package messages.clientMessages;

import messages.Message;

public class UnsubscribeMessage extends Message {

    private final String topic;

    public UnsubscribeMessage(Integer clientId, String topic){
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
