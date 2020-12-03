# Arduino

<img width="450" alt="image" src=https://user-images.githubusercontent.com/48685014/100953935-88932d00-354e-11eb-82f3-b43f3be23b38.png>

## Objectives 
The objectives of the Arduino sub-team is to handle the Arduino Uno board, and the motors and sensors in performing the following tasks, as well as the requirements for the project.

## Motor
* Capture and select suitable speed to revolutions per movement (RPM) data.
* Do proportional integral derivative (PID) on the selected speed to make sure the robot is moving straight.

## Sensors
* Select suitable placement for the sensor.
* Capture raw data from each individual sensor.
* Get the best fit curve formula for the sensor raw data and convert into cm.
* Test out whether the formula is accurate.

## Checklist 
* Ensure the robot is able to move straight up to 150cm.
* Ensure the robot is able to rotate accurately for up to 1080 degree.
* Ensure the robot is able to detect obstacles and do evasive movement.
* Ensure the sensor's reading is accurate and can measure up to 70cm.

## Initial implementation
### Motor
* Capture of speed to RPM data required full load to get a more accurate data so need to do sensor placement first.
* Implement code to capture speed to RPM data for speed of 100, 150, 200, 250, 300, 350, 400.
* From observations
  * Speed (200 to 300) and RPM (60 to 100) is more stable.
  * Right motor is more stable compared to the left motor.
  * Left motor is more powerful compared to the right motor.
* Find suitable speed and RPM for both motors to make sure the robot moves straight before implementing pid. 
* Work on PID for selected speed (206, 222) and RPM (59.9, 60.9) as it is more stable.
* Implemented 2 PID for both left and right motors to make sure both motors are moving straight.
  * Left PID Kp = 0.12, Ki and Kd was not required
  * Right PID Kp = 0.05, Ki and Kd was not required
* Capture the ticks required from the motor encoder to move per grid (10 cm), 90 degrees turn, 45 degrees turn and 10 degrees turn for checklist.
* Robot movements are based on the ticks from the motor encoder.

### Sensor
* Select and mount sensors on suitable position (required to communicate with the Algorithms sub-team on whether the algorithm is left or right wall hugging
  * The Algorithms sub-team ultimately decided on Right Wall hugging). 
* Decided to place 3 short sensors in front, 2 short sensors on the right side of the robot and 1 long sensor on the left side of the robot.
* Implemented code to capture raw data from every individual sensor.
* Record the data in Microsoft Excel, plot out the best fit curve and get the best fit formula for all sensors.
* Import and modify sensors library code by applying the best fit formula for the sensors and show in cm.
* Test out the sensors reading with actual obstacles blocks.

## Install ZsharpIR Library in Arduino
* Replace the cpp file in the library folder as there are a few changes required
* To use the library, indicate **#include <ZSharpIR.h>** at the start of the script

## To Run the Robot
Use the main script from the folder name "main"
