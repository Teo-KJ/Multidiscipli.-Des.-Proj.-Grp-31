import os, sys
import serial
import time
from config import *

class ArduinoRobot():

	def __init__(self):
		# Assign a default port number first
		self.port = "/dev/serial/by-id/usb-Arduino__www.arduino.cc__0043_7583335393435131C0B1-if00"
		
		self.baudRate = BAUD
		self.ser = 0
		self.isConnected = False

	def isItConnected (self):
		return self.isConnected

	def connectToArduino (self):
		#connect to serial port
		attemptConnect = True
		
		try:
			while attemptConnect:
				print ("Trying to connect to our robot through Arduino ...")
				self.ser = serial.Serial(SER_PORT0, self.baudRate, timeout=3)
				time.sleep(1)

				if(self.ser != 0):
					print ("Connected to Arduino! :)")
					self.isConnected = True
					attemptConnect = False
					break
				
				self.ser = serial.Serial(SER_PORT1, self.baudRate, timeout=3)
				if(self.ser != 0):
					print ("Connected to Arduino! :)")
					self.isConnected = True
					attemptConnect = False
					break

		except Exception as e:
			print ("arduino.py - Arduino connection failed :(")
			traceback.print_exc(limit=10, file=sys.stdout)

	def disconnectFromArduino (self):
		self.ser.close()
		self.isConnected = False
		print("Disconnect from Arduino.")

	def writeToArduino (self, msg):
		try:
			self.ser.write(str.encode(msg))
			print ("Sent to arduino: %s" % msg)
		
		except Exception as e:
			print ("arduino.py - Failed to send message to Arduino: %s" %str(e))
			self.connectToArduino()

	def readFromArduino (self):
		try:
			msg = self.ser.readline() # read msg from arduino sensors
			receivedMsg = msg.decode('utf-8')
			receivedMsg = str(receivedMsg)
			print ("Received from Arduino: %s" % receivedMsg)
			return receivedMsg

		except Exception as e:
			print ("arduino.py - Failed to receive message from Arduino")
			self.connectToArduino()
