package controller;

import model.Message;
import model.TranscriptStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TranscriptService {

    private final TranscriptStore store;

    public TranscriptService(TranscriptStore store) {
        this.store = store;
    }

    public List<Message> getMessages() {
        return store.getMessages();
    }

    // Load a saved transcript file and add its messages to the store
    public void loadFromFile(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            List<String[]> parsed = new ArrayList<>();
            Set<String> senders = new LinkedHashSet<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("[")) continue;
                int closeBracket = line.indexOf("]");
                if (closeBracket == -1) continue;
                String rest = line.substring(closeBracket + 2);
                int colonIndex = rest.indexOf(": ");
                if (colonIndex == -1) continue;
                String senderId = rest.substring(0, colonIndex);
                String content = rest.substring(colonIndex + 2);
                parsed.add(new String[]{senderId, content});
                senders.add(senderId);
            }
            reader.close();
            String[] participants = senders.toArray(new String[0]);
            for (String[] entry : parsed) {
                String senderId = entry[0];
                String content = entry[1];
                String receiverId = "";
                for (String p : participants) {
                    if (!p.equals(senderId)) {
                        receiverId = p;
                        break;
                    }
                }
                Message msg = new Message(UUID.randomUUID().toString(), senderId, receiverId, new Date(), content);
                store.addMessage(msg);
            }
        } catch (IOException e) {
            System.out.println("Could not load transcript: " + e.getMessage());
        }
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
