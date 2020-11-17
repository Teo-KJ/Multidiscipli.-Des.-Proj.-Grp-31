from bluetooth import *
from config import *
import time

class AndroidApplication(object):

    def __init__(self):
        self.serverSocket = None
        self.clientSocket = None
        self.connection = False

    # Our traditional assessor method of OOP
    def isItConnected (self):
        return self.connection

    def connectToTablet (self, uuid):
        try:
            # Endpoint for Bluetooth connection
            print(BLUETOOTH_PORT)
            self.serverSocket = BluetoothSocket(RFCOMM)
            self.serverSocket.bind(("", 7))
            self.serverSocket.listen(1)
            self.port = 7

            advertise_service(self.serverSocket, "Group 31 Server",
                            service_id = uuid,
                            service_classes = [uuid, SERIAL_PORT_CLASS],
                            profiles = [SERIAL_PORT_PROFILE],)

            print ("Connecting to Bluetooth RFCOMM channel %d" % 7)
            self.clientSocket, clientInfo = self.serverSocket.accept()

            print ("Accepted connection from ", clientInfo)
            print ("Connected to Android :)")
            self.connection = True

        except Exception as e:
            print ("android.py - Bluetooth connection failed :(")
            self.serverSocket.close()
            print ("Closing server socket")
            self.connection = False # Set connection to true

    def disconnectFromTablet (self):
        self.clientSocket.close()
        print ("Closing client socket")
        self.serverSocket.close()
        print ("Closing server socket")
        self.connection = False # Set connection to true

    # "msg" refers to the exact string parameter passed
    def writeToTablet (self, msg):
        try:
            self.clientSocket.send(msg)
            print ("Sent to Android: %s" %(msg))

        except Exception as e:
            print("android.py - There was an error with Bluetooth. RPi trying to reconnect :(")
            self.connectToTablet()

    def readFromTablet (self):
        try:
            msg = self.clientSocket.recv(1024)
            msg = msg.decode('utf-8')
            print("Received from Android: %s" % str(msg))
            return (msg)

        except Exception as e:
            print("android.py - There was an error with Bluetooth. RPi trying to reconnect :(")
            self.connectToTablet()
