import socket
import sys
import traceback
import errno
from config import *

class PCinterface(object):

    def __init__(self):
        self.host = WIFI_IP
        self.port = WIFI_PORT
        self.isConnected = False

    def isItConnected (self):
        return self.isConnected

    def connectToPC (self):
        try:
            # Assign a new socket variable for the Socket library, both are 2 seperate things
            self.connection = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.connection.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            self.connection.bind((self.host, self.port))
            print("Bind completed") # Bind socket

            # Let the backlog be 3, which is the number of unaccepted connections
            self.connection.listen(3)
            print ("Waiting for connection from PC ...")

            # Assign new variables clientSocket and address
            self.clientSocket, self.address = self.connection.accept()
            print ("Connected to PC with the IP Address: ", self.address, ":)")
            self.isConnected = True # Set connection to true

        except Exception as e:
            print ("pc.py - Error: %s" % str(e))
            print ("pc.py - Please try again :(")

    def disconnectFromPC (self):
        try:
            if self.connection:
                self.connection.close()
                print ('Terminating server socket')

            if self.clientSocket:
                self.clientSocket.close()
                print ('Terminating client socket')

            self.isConnected = False

        except Exception as e:
            print ("pc.py - PC disconnection failed: %s :(" %str(e))

    def writeToPC (self, msg):
        try:
            # Clean message string
            msg = str(msg)
            messageForPC = str.encode(msg + '\n')
            self.clientSocket.sendto(messageForPC, self.address)
            print ('Sent to PC: ' + msg)

        except Exception as e:
            print ('pc.py - PC Write Error: %s' % str(e))
            self.connectToPC()

    def readFromPC (self):
        try:
            msg = self.clientSocket.recv(1024)
            msg = msg.decode('utf-8')
            print ("Read from PC: %s" %(msg))

            if (not msg):
                self.disconnectFromPC()
                print('There is no transmission from PC, hence attempting to reconnect.')
                self.connectToPC()
                return msg

            return msg

        except Exception as e:
            print ('pc.py - PC Read Error: %s' % str(e))
            self.connectToPC()