package messages.clientMessages;

import messages.Message;

public class UnsubscribeMessage extends Message {

    private final String topic;

    public UnsubscribeMessage(String topic){
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }


}
