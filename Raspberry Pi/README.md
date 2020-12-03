# Raspberry Pi

## Overview
The Raspberry Pi (RPi) acts as the centre for communications between the other devices, namely the Android tablet and the Arduino Uno board.  Additionally, it also acts as a medium for the PiCamera module to capture the footage for image recognition. As shown in the diagram below, the RPi uses WiFi for robust connectivity between the PC and itself, Bluetooth 4.1 for the Android and Universal Serial Bus connection with Arduino Uno. uter and Arduino Uno) connected to RPi, while data is shared in the RPi between the threads.

## About the Files
### RPi Camera
Python scripts to connect the PC with the RPi for image recognition.

### Multithreading
Scripts used for multithreading operations in the RPi with the other devices: Android tablet, PC and Arduino Uno board

## Multithreading Communication Details
### RPi to Computer
The RPi connects to the computer using socket programming and with a secure shell. The PC is able to recognise the RPi by connecting to the customised IP address of our RPi. With this feature, the PC can also access the desktop and terminal of the RPi to run the multithreading program. Data is this transmitted through wireless socket programming.

### RPi to Android Tablet
The RPi connects with the tablet using Bluetooth RFCOMM protocol. The connection begins by first listening for available devices. The connection between both devices are finally verified with the UUID variable, which is a string variable indicated in both the Android application and multithreading program. The UUID serves as a password for connection. The messages are transmitted via Bluetooth.

### RPi to Arduino Uno
The RPi is connected to the board by wired Universal Serial Bus (USB) at a baud rate of 9600, since the location of the board is right beside the RPi. Hence, data is transmitted via USB.
