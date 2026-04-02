# COSC3506: Course Project (Group 3)

## Project Description
For our project, we chose to go with the **Peer-to-peer chat application**. This application allows users on the same network to communicate directly with each other through a dedicated TCP socket, with no central server required. A user hosts a session by entering a port to listen on, and a Peer can connect to that Host using their IP address + port. Once the connection is established, users can send messages to each other in real time. 

To securely identify users, an RSA challenge-response authentication handshake is performed before the session is established. The flow is as follows when a connection attempt is made:
- Each user generates an RSA keypair, 
- The Host creates a random challenge string and sends it to the Peer,
- The Peer signs the challenge string using their public key,
- The host verifies the signature using the Peer's public key. 

If successful, the connection then gets established. A unique short ID is then generated from their public key, used to securely identify the Peer and the Host. 

## Implementation 

The system was implemented using Java, and with JavaFX used for the graphical user interface. Communication between users is implemented using TCP sockets.

## Running The App
```bash
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar PeerLink.jar
```
