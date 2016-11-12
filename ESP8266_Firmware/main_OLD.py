import dht
import machine
import time
d = dht.DHT22(machine.Pin(2))
adc = machine.ADC(0)

html = """<!DOCTYPE html>
<html>
    <head> <title>ESP8266 Weather Station</title> </head>
    <body> <h1>ESP8266 Weather Station</h1>
        <table border="1"> <tr><th>Pin</th><th>Value</th></tr> %s </table>
    </body>
</html>
"""

import socket
addr = socket.getaddrinfo('0.0.0.0', 80)[0][-1]

s = socket.socket()
s.bind(addr)
s.listen(1)

print('listening on', addr)

while True:
    cl, addr = s.accept()
    print('client connected from', addr)
    cl_file = cl.makefile('rwb', 0)
    while True:
        line = cl_file.readline()
        if not line or line == b'\r\n':
            break
    time.sleep(1)
    d.measure()
    time.sleep(1)
    rows = ['<tr><td>%s</td><td>%.2f</td></tr><tr><td>%s</td><td>%.2f</td></tr><tr><td>%s</td><td>%d</td></tr>' % ('Temperature (C)', d.temperature(), 'Humidity', d.humidity(), 'Ambient light (%)', adc.read()/10.24)]
    response = html % '\n'.join(rows)
    cl.send(response)
    cl.close()
