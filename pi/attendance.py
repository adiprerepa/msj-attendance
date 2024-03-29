import I2C_LCD_driver
import socket
import select
import threading
import time
from pyfingerprint.pyfingerprint import PyFingerprint
from Queue import Queue
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
    No one can use sensor in deletion
Author: Aditya Prerepa (adiprerepa@gmail.com)
"""

server_endpoint='localhost:2002'
room_name = "test"
scanner_access_queue = Queue()

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

def grpc_receive_finger_id(request):
    with grpc.insecure_channel(server_endpoint) as channel:
        stub = AttendancePodsInterface_pb2_grpc.StudentRecordsServiceStub(channel)
        response = stub.lookupFingerId(request)
        print("Received: " + str(response.status) + " " + response.finger_id)
        return response

def get_serv_sock(port):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    sock.bind(('0.0.0.0', port))
    return sock

def nc_receive_id(port):
   sock = get_serv_sock(port)
    # Max queued connections 1
   sock.listen(1)
   client_sock, client_addr = sock.accept()
   try:
       ready_read, ready_write, err = select.select([client_sock,], [], [])
   except select.error:
       print('Select() failed on socket with {}'.format(client_addr))
       return '-1'
   if len(ready_read) > 0:
       read_data = client_sock.recv(255)
       if len(read_data) == 0:
           print('{} closed the socket. '.format(client_addr))
           return '-1'
       else:
           print('Received: {}'.format(read_data.rstrip()))
           return read_data.rstrip()
   else:
        return '-1';
   client_sock.close()
   sock.close()

def nc_receive_conf(port):
    sock = get_serv_sock(port)
    sock.listen(1)
    client_sock, client_addr = sock.accept()
    try:
        ready_read, ready_write, err = select.select([client_sock,], [], [])
    except select.error:
        print('Select() Failed on conf with {}'.format(client_addr))
        return False
    if len(ready_read) > 0:
        read_data = client_sock.recv(256)
        if len(read_data) == 0:
            print('{} closed sock in conf.'.format(client_addr))
            return False
        else:
            print('Received conf: {}'.format(read_data.rstrip()))
            if read_data.rstrip() == 'accept':
                sock.close()
                client_sock.close()
                return True
            else:
                sock.close()
                client_sock.close()
                return False


"""
Runs on separate thread & Separate port
to shutdown in shutdown_all, make a connection 
to local socket and make it pass

nc conn sends id wanted to delete
grpc lookup for fingerid
delete finger id
if queue.pop() == main thread using:
    queue.put("finger")
    time.sleep(1000)
"""
def nc_finger_delete(run_event, port, f):
    sock = get_serv_sock(port)
    sock.listen(1)
    while (run_event.is_set()):
        client_sock, client_addr = sock.accept()
        try:
            ready_read, ready_write, err = select.select([client_sock,], [], [])
        except select.error:
            print('Select() failed on delete')
            return False
        if len(ready_read) > 0:
            read_data = client_sock.recv(256).rstrip()
            if len(read_data) == 0:
                print('err closed sock in nc_finger_delete()')
                return False
            # Thread Interrupt
            if read_data.rstrip() == "exit":
                continue
            print('Recieved Delete Request: {}'.format(read_data))
            request = get_finger_lookup_request(read_data)
            response = grpc_receive_finger_id(request)
            if response.status != 200:
                print("Server Failed status != 200")
                # send nc ERR
                client_sock.send("Server Failed, Student ID doesnt exist?")
                continue
            fingerId = int(response.finger_id) - 1
            if scanner_access_queue.get() == "main":
                scanner_access_queue.put("nc_del")
                # Wait for while iteration to finish
                time.sleep(0.1)
                # if this doesnt work get a toggle for if the work is done on the main thread
                if (f.deleteTemplate(fingerId)):
                    print("Finger Deleted!")
                    client_sock.send("OK")
                else:
                    print("Unable to delete..")
                    client_sock.send("Couldnt delete")
            

""" 
Interrupts local finger delete thread
"""
def local_interrupt_finger_delete(port):
    mock_client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    mock_client.connect(('0.0.0.0', port))
    mock_client.send("exit")
    mock_client.close()

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
    scanner_access_queue.put("main")
    while (f.readImage() == False):
        if scanner_access_queue.get() == "main":
            scanner_access_queue.put("main")
            pass
        else:
            print("SOMEONE WANTS TO USE???!!!")
            time.sleep(2)
            scanner_access_queue.put("main")
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

""" 
Register a finger to the scanner, to checking and save to lcd & finger db 
Returns position of enrolled finger or -1
"""
def register_finger(f, lcd):
    scanner_access_queue.put("main")
    while (f.readImage() == False):
        if scanner_access_queue.get() == "main":
            scanner_access_queue.put("main")
            pass
        else:
            print("Crapshoot, shucks, someone else wants to use the scanner")
            time.sleep(2)
            scanner_access_queue.put("main")
            pass
    f.convertImage(0x01)
    # check if it is already registered
    result = f.searchTemplate()
    # This SHOULD return -1 if a finger isn't already registered
    position = result[0]
    if (position >= 0):
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
    # Store to charbuffer 2
    f.convertImage(0x02)
    # Compares charbuffer 0x01 & 0x02
    if (f.compareCharacteristics() == 0):
        clr_b(lcd)
        lcd.lcd_display_string("no match", 2)
        return -1
    # Create and store finger
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
def shutdown_all(ip_run_event, finger_run_event, lcd, finger_thread_started, ipThread, fingerThread, finger_thread_port):
    print("Closing Threads")
    ip_run_event.clear()
    ipThread.join()
    if (finger_thread_started):
        finger_run_event.clear()
        fingerThread.join()
        local_interrupt_finger_delete(finger_thread_port)
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

""" For enrollment """
def get_register_request(finger_id, student_id):
    request = AttendancePodsInterface_pb2.AttendanceRecord()
    request.room = "test"
    request.finger_id = str(finger_id)
    request.student_id=str(student_id)
    return request

""" For looking up a finger id """
def get_finger_lookup_request(student_id):
    request = AttendancePodsInterface_pb2.FingerLookupRequest()
    request.room = "test"
    request.student_id = student_id
    return request

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Bad Usage. Usage: \npython attendance.py <arg1>\n<arg1> represents enrollment, deletion, or recognition mode. \nFor example, to enroll a new class, run:\npython attendance.py enroll\nOr to recognize fingerprints, run:\npython attendance.py recognize \nPlease try again...")
        exit(1)
    lcd = I2C_LCD_driver.lcd()
    lcd.lcd_clear()
    # finger run event to interrupt
    finger_run_event = threading.Event()
    finger_run_event.set()
    # IP run event set and start
    ip_run_event = threading.Event()
    ip_run_event.set()
    ipThread = threading.Thread(target=ip_refresh, args=(5, lcd, ip_run_event))
    ipThread.start()
    # get scanner object
    scanner = get_scanner()
    if (scanner == -1):
        print("No Scanner ")
        # shutdown
        shutdown_all(ip_run_event, finger_run_event, lcd, False, ipThread, 0, 0)
    # Start finger delete thread server
    fingerDeleteThread = threading.Thread(target=nc_finger_delete, args=(finger_run_event, 2021, scanner))
    fingerDeleteThread.start()
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
        elif (sys.argv[1] == "enroll"):
            while(True):
                clr_b(lcd)
                lcd.lcd_display_string("Place Finger", 2)
                # Get the position of a newly registered finger
                position = register_finger(scanner, lcd)
                # Success
                if position >= 0:
                    clr_b(lcd)
                    lcd.lcd_display_string("Waiting for ID", 2)
                    # Recieve id from netcat
                    data = nc_receive_id(2020)
                    if data == '-1':
                        clr_b(lcd)
                        lcd.lcd_display_string('Err with nc', 2)
                        continue
                    clr_b(lcd)
                    lcd.lcd_display_string(str(data) + ' OK?', 2)
                    # Recieve confirmation for ID
                    if nc_receive_conf(2020):
                        request = get_register_request(position, data)
                        # Register finger with server
                        response = receive_message(request)
                        if (response.status):
                            clr_b(lcd)
                            lcd.lcd_display_string('Enroll Success', 2)
                        else:
                            clr_b(lcd)
                            lcd.lcd_display_string('Enroll Failed', 2)
                    else:
                        lcd.lcd_display_string('Conf Failed', 2)
                        time.sleep(2)
                        continue

                    # send to server & recieve
                else:
                    lcd.lcd_display_string('Registration Fail', 2)
                    print("There was an error, returned -1")
        while(True):
            time.sleep(.1)
    except KeyboardInterrupt:
        """ Kill threads and lcd """
        shutdown_all(ip_run_event, finger_run_event, lcd, True, ipThread, fingerDeleteThread, 2021)
