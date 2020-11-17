from arduino import *
# from android import *
from pc import *
from config import *
# import Queue
# import thread
import threading
import os

class RaspberryPi(threading.Thread):

	def __init__(self):
		# Allow rpi bluetooth to be discoverable
		# os.system("sudo hciconfig hci0 piscan")

		# Define new connections
		# self.androidThread = AndroidApplication()
		self.pcThread = PCinterface()
		self.arduinoThread = ArduinoRobot()

		# Initialise connections
		# self.androidThread.connectToTablet(UUID)
		self.pcThread.connectToPC()
		self.arduinoThread.connectToArduino()

		time.sleep(2) # Set some delay for buffer

		print ("===========================")
		print ("===Starting Transmission===")
		print ("===========================")

	# Read/Write from Android via Bluetooth
	def read_Android (self):
		while True:
                    msgFromTablet = self.androidThread.readFromTablet()
                    msgFromTablet = str(msgFromTablet)

                    # Insert message logic here
                    btmsgArray = msgFromTablet.split(",")
                    header = (btmsgArray[0])

                    if (self.androidThread.isItConnected() and msgFromTablet != "None"):
                        if(header == 'PC'):
                            self.read_PC(btmsgArray[2])
                        elif(header == 'AR'):
                            self.read_Arduino(btmsgArray[2])
                        else:
                            print("main.py - Incorrect device selected from Android: %s" %(btmsg))

	def write_Android (self, message):
		if (self.androidThread.isItConnected() and message):            
			self.androidThread.writeToTablet(message)
			return True
		return False

	# Read/Write from Arduino via serial connection
	def read_Arduino (self):
		while True:
                    serialmsg = self.arduinoThread.readFromArduino()
                    serialmsgArray = serialmsg.split(",")
                    header = serialmsgArray[0]

                    # Insert message logic here
                    if (self.arduinoThread.isItConnected() and serialmsg):
                        if(header == 'PC'):
                            self.write_PC(serialmsgArray[2])
                        elif(header == 'AN'):
                            serialSent = self.write_Android(serialmsgArray[2])
                        else:
                            print("main.py - Incorrect device selected from Arduino: %s" %(serialmsg))

	def write_Arduino (self, message):
		if (self.arduinoThread.isItConnected and message):
			self.arduinoThread.writeToArduino(message)
			return True
		return False

	# Read/Write from PC through wifi socket programming
	def read_PC (self):
		while True:
			msgPC = self.pcThread.readFromPC()
			msgPC = str(msgPC)

			# Insert message logic here
			pcmsgArray = msgPC.split(",")
			header = pcmsgArray[0]
			if (self.pcThread.isItConnected() and msgPC):
                            if(header == 'AN'):
                                #self.write_Android(pcmsgArray[2])
                                continue
                            elif(header == 'AR'):
                                self.write_Arduino(pcmsgArray[2])
                            else:
                                print("main.py - Incorrect header from PC: %s" %(msgPC))

	def write_PC (self, message):
		if (self.pcThread.isItConnected() and message):
			self.pcThread.writeToPC(message)
			return True
		return False

	# Multi-threading function
	def multithread(self):
		# PC read and write thread
		readPCthread = threading.Thread(target = self.read_PC, args = (), name = "read_pc_thread")
		writePCthread = threading.Thread(target = self.write_PC, args = (), name = "write_pc_thread")

		# Android read and write thread
		# readAndroidThread = threading.Thread(target = self.read_Android, args = (), name = "read_android_thread")
		# writeAndroidThread = threading.Thread(target = self.write_Android, args = (), name = "write_android_thread")

		# Arduino read and write thread
		readArduinoThread = threading.Thread(target = self.read_Arduino, args = (), name = "read_arduino_thread")
		writeArudinoThread = threading.Thread(target = self.write_Arduino, args = (), name = "write_arudino_thread")

		# Set daemon for all thread
		readPCthread.daemon = True
		writePCthread.daemon = True

		# readAndroidThread.daemon = True
		# writeAndroidThread.daemon = True

		readArduinoThread.daemon = True
		writeArudinoThread.daemon = True

		# start running the thread for PC
		readPCthread.start()

		# Start running thread for Android
		# readAndroidThread.start()

		# Start running thread for Arduino
		readArduinoThread.start()

	# Disconnect all devices
	def disconnectAll(self):
		try:
			# self.androidThread.disconnectFromTablet()
			self.arduinoThread.disconnectFromArduino()
			self.pcThread.disconnectFromPC()

		except Exception as e:
			pass

if __name__ == "__main__":

	#Start the progam
	print("Starting the program...")
	main = RaspberryPi()

	try:
		main.multithread()
		while True:
			time.sleep(1)
	except KeyboardInterrupt:
		print("Exiting the program")
		main.disconnectAll()