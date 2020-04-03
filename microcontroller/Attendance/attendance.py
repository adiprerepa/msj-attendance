import I2C_LCD_driver
import socket
import threading
import time
from pyfingerprint.pyfingerprint import PyFingerprint
import grpc
import AttendancePodsInterface_pb2
import AttendancePodsInterface_pb2_grpc
import sys

"""
Some things:
    The LCD is 16*2
    The Current Implementation of the LCD driver is using
        I2C, and we have that driver in this file's path.
    We use gRPC to recieve messages, and the server is run 
        locally on the raspberry pi.
    Saved Address 0x27 in I2C_LCD_driver.py after running
        `i2cdetect -y 1` with the LCD connected.
    We are running the LCD IP address updater on a separate non-daemon
        thread, and shudown_all() kills the lcd and daemon thread.
    The fingerprint sensor has to be attached via a usb converter with
        RX and TX pins in order for the setup to work.
        This is because the Fingerprint sensor's RX and TX pins cannot 
        sense 3.3V inputs, which is what the raspberry pi and esp8266 provide.
Author: Aditya Prerepa (adiprerepa@gmail.com)
"""

server_endpoint='localhost:2002'

""" Get the Raspberry Pi's IP address to display on the lcd. Used for ssh and website access."""
def get_ip():
    return [l for l in ([ip for ip in socket.gethostbyname_ex(socket.gethostname())[2] if not ip.startswith("127.")][:1], [[(s.connect(('8.8.8.8', 53)), s.getsockname()[0], s.close()) for s in [socket.socket(socket.AF_INET, socket.SOCK_DGRAM)]][0][1]]) if l][0][0]

""" Run by IP updater thread - updates LCD's first line (16*1) with network ip every <refresh rate> seconds"""
def ip_refresh(refresh_rate, lcd, run_event):
    while(run_event.is_set()):
        ip_addr = get_ip()
        #print(ip_addr)
        lcd.lcd_display_string(ip_addr, 1)
        time.sleep(refresh_rate)

""" Clear the bottom line of the lcd """
def clr_b(lcd):
    lcd.lcd_display_string(" "*16, 2)


""" Receive and return status of attendance insertion """
def receive_message(request):
    with grpc.insecure_channel(server_endpoint) as channel:
        stub = AttendancePodsInterface_pb2_grpc.StudentRecordsServiceStub(channel)
        response = stub.produceRecord(request)
        print("Received: " + str(response.status) + " " + response.student_id)
        return response

def receive_enroll_message(request):
    with grpc.insecure_channel(server_endpoint) as channel:
        stub = AttendancePodsInterface_pb2_grpc.StudentRecordsServiceStub(channel)


""" Get the fingerprint Scanner object by accessing /dev/ttyUSB0. Return -1 if not available. """
def get_scanner():
    try:
        f = PyFingerprint('/dev/ttyUSB0', 57600, 0xFFFFFFFF, 0x00000000)
        if (f.verifyPassword() == False):
            raise ValueError('The fingerprint sensor password is wrong :(')
        return f
    except Exception as e:
        print('cant initialize. exiting.')
        print(str(e))
        return -1;

""" 
Given the scanner object, read the fingerprint and get the id of that fingerprint.
Blocking function, TODO implement accuracy threshold 
"""
def get_finger_id(f):
    print('Waiting for finger...')
    while (f.readImage() == False):
        pass
    # Converts image to characteristics and stores in buffer 1
    f.convertImage(0x01)
    result = f.searchTemplate()
    fingerId = result[0]
    accuracy = result[1]
    if (fingerId == -1):
        print("No Match!")
        return -1
    else:
        print('Found finger id #' + str(fingerId))
        print('Accuracy ' + str(accuracy))
        return fingerId

def register_finger(f, lcd):
    while (f.readImage() == False):
        pass
    f.convertImage(0x01)
    # check if it is already registered
    result = f.searchTemplate()
    position = result[0]
    if (positionNumber >= 0):
        print("Template already exists at pos " + str(position))
        clr_b(lcd)
        lcd.lcd_display_string("err: exists@" + str(position), 2)
        return -1
    lcd.lcd_display_string("Remove Finger", 2)
    time.sleep(2)
    clr_b(lcd)
    lcd.lcd_display_string("Re-place finger", 2)
    # Wait for finger
    while(f.readImage() == False):
        pass
    f.convertImage(0x02)
    if (f.compareCharacteristics() == 0):
        clr_b(lcd)
        lcd.lcd_display_string("no match", 2)
        return -1
    f.createTemplate()
    position = f.storeTemplate()
    clr_b(lcd)
    lcd.lcd_display_string("enroll OK", 2)
    return position;

"""
Shutdown all of the operations - lcd operations and thread operations.
We shutdown the thread by passing a runEvent to the thread and unset it 
    when we need to stop the thread. The max delay is the refresh_rate of the 
    LCD updater function.
"""
def shutdown_all(run_event, lcd, thread):
    print("Closing Threads")
    run_event.clear()
    ipThread.join()
    lcd.lcd_clear()
    print("Closed")
    lcd.lcd_clear()
    exit(0)

""" Build an attendance request. Room changes! """
def get_attendance_request(finger_id):
        request = AttendancePodsInterface_pb2.AttendanceRecord()
        request.room = "test"
        request.finger_id = str(finger_id)
        return request

def get_register_request(finger_id, student_id):
    request = AttendancePodsInterface_pb2.AttendanceRecord()
    request.room = "test"
    request.finger_id = str(finger_id)
    request.student_id=str(student_id)
    return request

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Bad Usage. Usage: \npython attendance.py <arg1>\n<arg1> represents enrollment, deletion, or recognition mode. \nFor example, to enroll a new class, run:\npython attendance.py enroll\nOr to recognize fingerprints, run:\npython attendance.py recognize \nPlease try again...")
        exit(1)
    run_event = threading.Event()
    run_event.set()
    lcd = I2C_LCD_driver.lcd()
    lcd.lcd_clear()
    """ Current refresh rate of 10 seconds """
    ipThread = threading.Thread(target=ip_refresh, args=(10, lcd, run_event))
    ipThread.start()
    scanner = get_scanner()
    if (scanner == -1):
        print("Couldnt find scanner :(")
        shutdown_all(run_event, lcd, ipThread)
    try:
        if (sys.argv[1] == "recognize"):  
            while(True):
                finger_id = get_finger_id(scanner)
                lcd.lcd_display_string(" "*16, 2)
                print(finger_id)
                if (finger_id == -1):
                    lcd.lcd_display_string("Bad Finger", 2)
                    continue
                request = get_attendance_request(finger_id+1)
                response = receive_message(request)
                if (response.status == 200):
                    time.sleep(2);
                    lcd.lcd_display_string("OK " + response.student_id, 2)
                else:
                    lcd.lcd_display_string("ERR " + str(response.status), 2)
                time.sleep(2)
                lcd.lcd_display_string("Place Finger", 2)
        else if (sys.argv[1] == "enroll"):
            while(True):
                lcd.lcd_display_string("Place Finger", 2)
                position = register_finger(scanner, lcd)
                if position >= 0:

                    # send to server & recieve
                else:
                    print("There was an error, returned -1")
        while(True):
            time.sleep(.1)
    except KeyboardInterrupt:
        """ Kill threads and lcd """
        shutdown_all(run_event, lcd, ipThread)
