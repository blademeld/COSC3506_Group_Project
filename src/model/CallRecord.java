package model;

import java.util.Date;

public class CallRecord {
    private String callId;
    private String senderId;
    private String receiverId;
    private Date startTime;
    private Date endTime;

    public CallRecord(String callId, String senderId, String receiverId, Date startTime, Date endTime) {
        this.callId = callId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getCallId() {
        return callId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void endCall(Date endTime) {
        this.endTime = endTime;
    }
}
