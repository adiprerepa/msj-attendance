# msj-attendance
msj-attendance is an fingerprint-based, decentralized attendance system for Mission San Jose High. 

## Motivation
Teachers should not have to take attendance. This chore which could be replaced easily by something automated, takes up the time of tons of teachers, especially the ones who forget to take attendance. This product solves that problem.

## How it works
Each classroom will be equipped with a raspberry pi attached to an FPM10A fingerprint sensor and an LCM1602 display. On the first day of school, students enroll themselves onto the system with the teachers help. Then, whenever a student enters the classroom, it is recorded, stored, and sent to the teacher of that classroom at their request.

## Author
Aditya Prerepa 2020

## Dependencies
Languages Required:
 - Java 11
 - Python 2.7
 - Javascript (npm)
 
Frameworks Required:
- gRPC (raspberry pi <-> api)
- Spring Boot (api <-> proxy <-> website)
- React (website)

Hardware:
- FPM10A Fingerprint Sensor
- LCD1602 (LiquidCrystal Display)
- LCM1602 (I2C Adapter)
- Raspberry Pi 3B v2
- CP2102 USB->TTL converter
## Execution
### Raspberry Pi
Run the Following script to install all the required dependencies onto the raspberry pi:
```bash
./pi/install_dependencies.sh
```
To start student enrollment, run:
```bash
./pi/scripts/run_pi_enroll.sh
```
To start the regular recognition program:
```bash
./pi/scripts/run_pi_main.sh
```
On the First Line of the LCD, the IP address of the raspberry PI is shown.
#### Enrollment Workflow
The system used a confirmation system during enrollment to make sure the right ID is entered. When you execute `run_pi_enroll.sh` and a student places their finger on the scanner, a message will appear on the scanner, saying "Waiting for ID". Obtain the ID of the student who just placed their finger and send the Id using `./pi/scipts/send_id.sh <ip> <id>`, where the ip is on the first line of the lcd. If all goes well, the LCD will display a "waiting for confirmation" message. If the ID displayed is correct, run `./pi/scripts/confirm_id.sh <ip>`. If not, run `./pi/scripts/reject_id.sh <ip>`. A simpler verison of the workflow:
 1) Run `./pi/scripts/run_pi_enroll.sh`.
 2) Have the student place their finger on the scanner, remove, and then re-place it.
 3) Send the ID of that student using the script `./pi/scripts/send_id.sh <ip> <id>`, where the IP is on the first line of the lcd, and ID is the student ID.
 4) Said ID will appear on the screen. If it is correct, run `./pi/scripts/confirm_id.sh <ip>`. If not, run `./pi/scripts/reject_id.sh <ip>`.
 5) Repeat steps 2-4 until all the students have enrolled.
 #### Deletion Workflow
 If an ID is not correct and is stored in the scanner, or if a student leaves the classroom permanently, run the following:
 ```bash
 ./pi/scripts/delete_id <ip> <id>
 ```
 This should be run in a separate process, not interrupting either the enrollment or recognition workflow.
 #### Recognition Workflow
 This workflow is the main workflow, the program running every day, which the students are scanning into. Script: 
 ```bash
 ./pi/scripts/run_pi_main.sh
 ```
## Architecture
This project has many components, all vital to it working. Here are the components:
- Attendance Server (`/api`)
- Spring Boot REST<->gRPC proxy (`/spring-proxy`)
- React webapp (`/web`)
- Raspberry pi application (`/pi`)

Here is the Architecture:
![architecture](https://github.com/adiprerepa/msj-attendance/blob/master/docs/msj-attendance%20architecure.png)
## What it should look like
![full_apparatus](https://github.com/adiprerepa/msj-attendance/blob/master/docs/full_scanner_apparatus.jpg)
![lcd_ip](https://github.com/adiprerepa/msj-attendance/blob/master/docs/lcd_interface.jpg)
