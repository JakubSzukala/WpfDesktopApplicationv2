#!/usr/bin/python
import socket
import random as rnd
import json
from sense_emu import SenseHat

sense = SenseHat()

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Bind the socket to the port
server_address = ('localhost', 10000)
print('starting up on {} port {}'.format(*server_address))
sock.bind(server_address)

# Listen for incoming connections
sock.listen(1)

while True:
    # Wait for a connection
    #print('waiting for a connection')
    connection, client_address = sock.accept()
    try:
        #print('connection from', client_address)

        while True:
            cmd = connection.recv(8)
            if cmd == b'get_env':
                data = [
                    {
                        "name": "TemmperatureFromHumidity",
                        "data": sense.get_temperature_from_humidity(),
                        "unit": "C"
                    },
                    {
                        "name": "TemmperatureFromPressure",
                        "data": sense.get_temperature_from_pressure(),
                        "unit": "C"
                    },
                    {
                        "name": "Pressure",
                        "data": sense.get_pressure(),
                        "unit": "hPa"
                    },
                    {
                        "name": "Humidity",
                        "data": sense.get_humidity(),
                        "unit": "%"
                    }
                ]
            if cmd == b'get_ori':
                t = sense.get_temperature()
                p = sense.get_pressure()
                h = sense.get_humidity()
                data = [
                    {
                        "name": "acceleration",
                        "data": sense.get_accelerometer(),
                        "unit": "deg"
                    },
                    {
                        "name": "magnetic",
                        "data": sense.get_compass_raw()
                    },
                    {
                        "name": "gyroscope",
                        "data": sense.get_gyroscope(),
                        "unit": "deg"
                    }
                ]
                dataJSON = json.dumps(data)
                msg = dataJSON.encode('utf-8') 
                connection.sendall(msg)
                dataJSON = json.dumps(data)
                msg = dataJSON.encode('utf-8') 
                connection.sendall(msg)
            else:
                break

    finally:
        # Clean up the connection
        connection.close()
