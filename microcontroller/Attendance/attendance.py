import I2C_LCD_driver
import socket
import threading
import time
from pyfingerprint.pyfingerprint import PyFingerprint
import grpc
import AttendancePodsInterface_pb2
import AttendancePodsInterface_pb2_grpc

server_endpoint='192.168.88.245:2002'

def get_ip():
    return [l for l in ([ip for ip in socket.gethostbyname_ex(socket.gethostname())[2] if not ip.startswith("127.")][:1], [[(s.connect(('8.8.8.8', 53)), s.getsockname()[0], s.close()) for s in [socket.socket(socket.AF_INET, socket.SOCK_DGRAM)]][0][1]]) if l][0][0]


def ip_refresh(refresh_rate, lcd, run_event):
    while(run_event.is_set()):
        ip_addr = get_ip()
        print(ip_addr)
        lcd.lcd_display_string(ip_addr, 1)
        time.sleep(refresh_rate)

def receive_message(request):
    with grpc.insecure_channel(server_endpoint) as channel:
        stub = AttendancePodsInterface_pb2_grpc.StudentRecordsServiceStub(channel)
        response = stub.produceRecord(request)
        print("Received: " + str(response.status) + " " + response.student_id)
        return response

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

def shutdown_all(run_event, lcd, thread):
    print("Closing Threads")
    run_event.clear()
    ipThread.join()
    lcd.lcd_clear()
    print("Closed")
    lcd.lcd_clear()
    exit(0)


def get_attendance_request(finger_id):
        request = AttendancePodsInterface_pb2.AttendanceRecord()
        request.room = "test"
        request.finger_id = str(finger_id)
        return request


if __name__ == "__main__":
    run_event = threading.Event()
    run_event.set()
    lcd = I2C_LCD_driver.lcd()
    ipThread = threading.Thread(target=ip_refresh, args=(6000, lcd, run_event))
    ipThread.start()
    b = True
    try:
        while(b):
#            scanner = get_scanner()
#            if (scanner == -1):
                #Couldnt find scanner, shutdown
#                shutdown_all(run_event, lcd, ipThread)
#            finger_id = get_finger_id(scanner)
            finger_id = 1;
            if (finger_id == -1):
                print("Bad Finger")
                continue
            request = get_attendance_request(finger_id)
            response = receive_message(request)
            if (response.status == 200):
                lcd.lcd_display_string("OK " + response.student_id, 2)
            else:
                lcd.lcd_display_string("ERR " + str(response.status), 2) 
            b = False
        while(True):
            time.sleep(.1)
            # Send finger id
    except KeyboardInterrupt:
        shutdown_all(run_event, lcd, ipThread)
