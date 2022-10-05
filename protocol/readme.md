# RAT Server/Client Communication Protocol
This is the documentation for the communication protocol between the Client and Server programs of the Remote Access Tool.

## Client Side
The client initiates a connection with the server before issuing a command.

### Remote Commands
The command is no more than just a single word string. The server can recognize one of three comands:
- *REBOOT*[Line Feed]
- *SCREENSHOT*[Line Feed]
- *PROCESSES*[Line Feed]

The commands are **not** case sensitive.

## Server Side
Upon receiving a connection request. The server reads and parses the header line to get the commmand. The server will assume a default port of 8000.

### Server Response

#### REBOOT
If the REBOOT command is issued, the server will send back an acknowledgement in the form:

*OK*[Line Feed]

then proceed to restart.

#### SCREENSHOT
If the SCREENSHOT command is issued, the server will take a screenshot of the screen then send a header response in the form:

*OK*[One Space][Image Size][One Space][File Type][Line Feed] 

before sending the actual bytes of the image.

#### PROCESSES
If the PROCESSES command is issued, the server will send back an acknowledgement in for from:

*OK*[One Space]
followed by a string corresponding to the list of processes currently running on the server. Each line in the response will look like the following:

[Image Name][Some White Space][Process ID][Some White Space][Session Number][Some White Space][Memory Usage][Line Feed]

The server then sends the following to mark the end of the output stream:

*END*[Line Feed]

### Exception Handling
If the client sends a command with multiple words separated by white spaces, the server will only read the first word and attempt to interpret it as a command.

If the client sends a command that is not recognized by the server or that is not formatted the right way, then the server sends back the following:

*BAD*[One Space]*COMMAND*[Line Feed]

If, for some reason, an exception is raised at the level of the server, the following response will be sent back:

*INTERNAL*[One Space]*SERVER*[ONE Space]*ERROR*[Line Feed]
