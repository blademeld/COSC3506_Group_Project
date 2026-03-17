public class Message {
	// Adding this class early so it starts aligning with our UML design. 
    // This will be the data structure sent between peers. 
    // Class is not being used yet. 

	private String messageId;
	private String senderId;
	private String receiverId;
	private String timestamp;
	private String content;

	// Constructor for creating a new message instance with all fields
	public Message(String messageId, String senderId, String receiverId, String timestamp, String content)
	{
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

	public String getTimestamp() {
		return timestamp;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return "Message ID: " + messageId
				+ ", Sender ID: " + senderId
				+ ", Receiver ID: " + receiverId
				+ ", Timestamp: " + timestamp
				+ ", Content: " + content;
	}
}