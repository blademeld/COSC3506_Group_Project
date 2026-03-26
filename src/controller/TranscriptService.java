package controller;

import model.Message;
import model.TranscriptStore;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TranscriptService {

    private final TranscriptStore store;

    public TranscriptService(TranscriptStore store) {
        this.store = store;
    }

    public List<Message> getMessages() {
        return store.getMessages();
    }

    // Save the transcript to a local text file
    public void saveToFile(String filename) {
        try {
            FileWriter writer = new FileWriter(filename);
            for (Message msg : store.getMessages()) {
                writer.write("[" + msg.getTimestamp() + "] "
                        + msg.getSenderId() + ": "
                        + msg.getContent() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not save transcript: " + e.getMessage());
        }
    }
}
