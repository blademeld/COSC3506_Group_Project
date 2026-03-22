package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TranscriptStore {
    private final List<Message> messages = new ArrayList<>();
    private final List<CallRecord> callRecords = new ArrayList<>();

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public List<CallRecord> getCallRecords() {
        return Collections.unmodifiableList(callRecords);
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void addCallRecord(CallRecord record) {
        callRecords.add(record);
    }
}
