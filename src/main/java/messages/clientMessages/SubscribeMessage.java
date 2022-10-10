package messages.clientMessages;

import messages.Message;

public class SubscribeMessage extends Message {
    private final String topic;

    public SubscribeMessage(Integer clientId, String topic){
        super(clientId);
        this.topic = topic;
    }

    public Integer getClientID() {
        return getClientId();
    }

    public String getTopic() {
        return topic;
    }


}
