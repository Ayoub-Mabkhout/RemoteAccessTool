# RemoteAccessTool
Remote Access Tool with custom Client/Server communication protocol. This is a repository for a programming assignment in my 
"SC 3374 Advanced & Distributed Programming Paradigms"course as part of the chapter on "Client/Server Programming Model".

## Demonstrated Learning Outcomes
- Programming for Communication
- Client/Server Programming Model
- Socket API Programming
- Designing a Custom Client/Server Communication Protocol 

## Description
The program allows a client process to access a local or remove server process and issue one of 3 commands. Either (1) take a screenshot of the main screen,
(2) record and send a list of all processes running on the Server machine, or (3) reboot the server machine.

This implementation should work on both Windows and Unix based machines.

## How To Run
First, clone the project to your own device. You can either run the server main class and then the client main class both on the same computer, or you can run 
the server program on one machine and run the client on another, in which case you will need the server's IPv4 address to be able to connect to it remotely.

- To run the server, cd to the root directory on your terminal and enter the command *gradle run*, with the server being the default *mainClass* in the Gradle build settings.

- To run the client, cd to *~\app\src\main\java\remote\access\tool\Client\\* and run the command *java RemoteAccessClient.java*. 
