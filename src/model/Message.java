package model;

import java.util.Date;

public class Message {
    private String messageId;
    private String senderId;
    private String receiverId;
    private Date timestamp;
    private String content;

    public Message(String messageId, String senderId, String receiverId, Date timestamp, String content) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
        this.content = content;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
