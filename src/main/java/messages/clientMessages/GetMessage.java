package messages.clientMessages;

import messages.Message;

public class GetMessage extends Message {
    private final String topic;

    public GetMessage(Integer clientId, String topic) {
        super(clientId);
        this.topic = topic;
    }


    public String getTopic() {
        return topic;
    }
}
