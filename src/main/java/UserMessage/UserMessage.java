package UserMessage;

public class UserMessage {
    private int sender_id;
    private int receiver_id;
    private String message;

    public UserMessage(int sender_id, int receiver_id, String message) {
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.message = message;
    }

    public UserMessage() {
    }

    public Integer getSender() {
        return sender_id;
    }

    public void setSender(int sender_id) {
        this.sender_id = sender_id;
    }

    public Integer getReceiver() {
        return receiver_id;
    }

    public void setReceiver(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
