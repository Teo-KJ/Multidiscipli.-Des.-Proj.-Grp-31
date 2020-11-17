from bluetooth import * 

# os.system("sudo systemctl start rfcomm")
server_sock = BluetoothSocket(RFCOMM) 
server_sock.bind(("",7)) 
server_sock.listen(1) 
port = 7
uuid = "00001101-0000-1000-8000-00805F9B34FB"
advertise_service(server_sock, "MDP-Server", service_id = uuid,
                  service_classes = [ uuid, SERIAL_PORT_CLASS ], profiles = [ SERIAL_PORT_PROFILE ], 
                  # protocols = [ OBEX_UUID ]
                  ) 
print("Waiting for connection on RFCOMM channel %d" % port)

client_sock, client_info = server_sock.accept()
print("Accepted connection from ", client_info) 
 
try:     
    while True:         
        print ("In while loop...")         
        data = client_sock.recv(1024)         
        
        if len(data) == 0:
            break
          
        print("Received [%s]" % data)
        client_sock.send(data) 

except IOError:
    pass 

print("disconnected") 

client_sock.close()
server_sock.close() 
print("all done")