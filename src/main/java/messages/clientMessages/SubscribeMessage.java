package messages.clientMessages;

import messages.Message;

public class SubscribeMessage extends Message {
    private final String topic;

    public SubscribeMessage(String topic){
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }


}
