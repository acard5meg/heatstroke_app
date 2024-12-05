import time
import serial
# https://pyserial.readthedocs.io/en/latest/pyserial_api.html

import csv

"""
Example of serial monitor data with timestamp from Arduino
22:27:29.230 -> 80,958,745
22:27:29.263 -> 80,958,991
22:27:29.263 -> 80,958,991
22:27:29.296 -> 80,958,991
22:27:29.296 -> 80,958,991
22:27:29.329 -> 80,958,991
22:27:29.362 -> 80,958,991
22:27:29.362 -> 80,958,991
22:27:29.395 -> 80,958,991
22:27:29.395 -> 80,958,991
22:27:29.429 -> 80,958,991
22:27:29.462 -> 80,958,991
22:27:29.462 -> 80,958,991
22:27:29.495 -> 80,958,991
22:27:29.528 -> 80,958,991

https://github.com/WorldFamousElectronics/PulseSensorPlayground/blob/master/resources/PulseSensor%20Playground%20Tools.md

Based on pulse sensor documentation the library prints
BPM, IBI, PulseSensorRawSignal
BPM -> beats per minute
IBI -> interbeat interval


"""

# Testing how serial port interfaces with Python
""" 
if __name__ == "__main__":
    port = 
    # port immediately opened on object creation
    ser = serial.Serial(port, baud)
    idx = 0
    # time.sleep(5)
    while idx < 10:
    #     # print(ser.readline())
        idx += 1
        ans = ser.readline()
    # ser.close()
    start_time = time.time()
    # ans = ser.readline()
    ser.close()
    print(ans)
    # print(ans)
    # print(type(ans))
    # print(len(ans))
    # print(f"This is the ans byte: {ans}")
    # for i in range(len(ans)):
    #     print(f"This is index: {i} and byte: {ans[i]}")

    bpm_idx = 0
    for i in range(len(ans)):
        if ans[i] == 44:
            bpm_idx = i
            break
    print(ans[:bpm_idx].decode('utf-8'))
    print((time.time()-start_time))
    print(ans)

    ##################################
    output from ser.readline()
    b'65,994,491\r\n'
    the class is bytes
    length is 12

    The bytes at each index refer to the ascii values
    This is index: 0 and byte: 54
    This is index: 1 and byte: 53
    This is index: 2 and byte: 44
    This is index: 3 and byte: 57
    This is index: 4 and byte: 57
    This is index: 5 and byte: 52
    This is index: 6 and byte: 44
    This is index: 7 and byte: 52
    This is index: 8 and byte: 57
    This is index: 9 and byte: 49
    This is index: 10 and byte: 13
    This is index: 11 and byte: 10
    """

    
def read_bpm(port_output) -> int:
    """
    Converts serial port output to BPM
    Output of serial port format: b'65,994,491\r\n'   
    BPM,IBI,PulseSensor Raw Signal

    IF WE CAN MANIPULATE DATA BEFORE THE TRANSFER
    WE COULD SAVE TIME BY AVOIDING THE LOOP
    """
    bpm_idx = 0
    for i in range(len(port_output)):
        if port_output[i] == 44:
            bpm_idx = i
            break
    return port_output[:bpm_idx].decode('utf-8')

def open_port(port, baud) -> serial:
    """
    Opens serial connection to port at a bit per second rate of baud
    """

    return serial.Serial(port, baud)

def create_csv(file_name, header):
    """
    Creates a new csv file named file_name
    Adds the header which must be a list
    """
    with open(file_name, 'w', newline='') as file:
        csv_writer = csv.writer(file)
        csv_writer.writerow(header)

def write_to_csv(file_name, row):
    """
    Writes to an already created csv
    """
    with open(file_name, 'a', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(row)

def main():
    # ser = open_port('/dev/ttyACM0', 115200)
    ser = open_port('/dev/cu.usbmodem14201', 115200)

    # idx = 0

    create_csv("heart_beat_data.csv",["time_since_start", "BPM"])
    ## There is a time period at the beginning 
    ## regardless of using Arduino's serial monitor or Python
    ## where the monitor has 0 as BPM
    ## This seemed to reduce the number of 0s but it didn't fix the problem
    time.sleep(4) 
    start_time = time.time()
    interval_time = start_time
    elapsed_time = 0
    bpm = 0
    bpm_count = 0
    while elapsed_time < 182:
        bpm += int(read_bpm(ser.readline()))
        bpm_count += 1
        curr_time = time.time()

        elapsed_time = curr_time - start_time

        if curr_time - interval_time > 5:
            write_to_csv("heart_beat_data.csv", [int(elapsed_time), bpm//bpm_count])
            interval_time = curr_time
            bpm = 0
            bpm_count = 0


    ser.close()

if __name__ == "__main__":
    main()