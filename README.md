# COSC3506: Course Project (Group 3)

## Project Description
For our project, we chose to go with the **Peer-to-peer chat application**. This application allows users on the same network to communicate directly with each other through a dedicated TCP socket, with no central server required. A user hosts a session by entering a port to listen on, and a Peer can connect to that Host using their IP address + port. Once the connection is established, users can send messages to each other in real time. 

To securely identify users, an RSA challenge-response authentication handshake is performed between the Host and the Peer before the session is established. The asymmetric flow is as follows when a connection attempt is made:
- Each user generates an RSA keypair, 
- The Host creates a random challenge string and sends it to the Peer,
- The Peer signs the challenge string using their private key,
- The host verifies the signature using the Peer's public key,
- Once the Peer's identity is verified, the Host then shares its username + public key,
- The Peer uses the Host's public key to confirm the Host's identity.

If successful, the connection then gets established and they can now chat with each other. A unique short ID is then generated from their public key, used to securely identify the Peer and the Host. Our application also has audio calling functionality. Once a chat session is established between users, they have the option of clicking on "Call" to start a live audio call and clicking on "End" to cancel it. The audio is streamed on a separate TCP connection, allowing for users to continue chatting through text while also being in a call.

## Implementation 

The system was implemented using Java, with JavaFX and FXML used for the graphical user interface. Communication between users is implemented using TCP sockets.

## Running The App
```bash
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar COSC3506Project.jar
```
