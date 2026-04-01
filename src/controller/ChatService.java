package controller;

import model.Message;
import model.TranscriptStore;

import java.util.Date;
import java.util.UUID;

public class ChatService {

    private final TranscriptStore store;
    private ConnectionHandler connection;
    private String localId;
    private String peerId;

    public ChatService(TranscriptStore store) {
        this.store = store;
    }
    
    public void connect(String localId, String peerId, ConnectionHandler connection) {
        this.localId = localId;
        this.peerId = peerId;
        this.connection = connection;
    }

    public void disconnect() {
        connection = null;
        localId = null;
        peerId = null;
    }

    public void sendMessage(String content) {
        if (connection == null)
            return;

        Message msg = new Message(
                UUID.randomUUID().toString(),
                localId,
                peerId,
                new Date(),
                content);

        store.addMessage(msg);
        connection.send(content);
    }

    // Called when a raw message string arrives from the peer
    public Message receiveMessage(String content) {
        Message msg = new Message(
                UUID.randomUUID().toString(),
                peerId,
                localId,
                new Date(),
                content);

        store.addMessage(msg);
        return msg;
    }
}
