package application;

import java.io.Serializable;
import java.sql.Timestamp;

public class ChatMessage implements Serializable {
    private String sender;
    private String message;
    private String chatroomID;
    private Timestamp timestamp;

    public ChatMessage(String sender, String message, String chatroomID, Timestamp timestamp) {
        this.sender = sender;
        this.message = message;
        this.chatroomID=chatroomID;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getChatroomID() {
        return chatroomID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
