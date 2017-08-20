# A rudimentary Messaging and File Transfer Application
This is a rudimentary messaging application with the following features:
* Server and Client can send messages to each other(​ Half-Duplex​ communication, using TCP sockets)
* Server and Client can send ​ any kind​ of file to each other using both Datagram socket based communication (UDP socket) and Stream sockets based communication (TCP socket)
<br>
In this application, hostA is client and hostB is server. They communicate through a TCP socket for sending messages. File transfer can be done either through a TCP socket or UDP socket.
<br>

## Run application
* To compile: <br>
`javac hostA.java` <br>
`javac hostB.java`<br>
* To run:<br>
`java hostA` <br>
`java hostB`<br>

##### Note: Run hostA and hostB files in different directories

 

