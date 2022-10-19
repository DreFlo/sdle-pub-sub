package messages.clientMessages;

import messages.Message;

public class GetMessageAck extends Message {
    private final String topic;

    public GetMessageAck(String topic){
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
