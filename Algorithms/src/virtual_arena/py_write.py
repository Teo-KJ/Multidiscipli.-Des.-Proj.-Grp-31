import time
from time import sleep

start_time = time.time()
i = 0
while(time.time() - start_time < 150): 
    f = open("object_detection.txt", "w")
    if (i%2 == 0):
        f.write(str(time.time()) + "\n")
    else:
        f.write("Dummy Data")
        
    i += 1
    sleep(1)
