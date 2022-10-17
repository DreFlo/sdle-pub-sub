package messages.clientMessages;

import messages.Message;

public class GetMessage extends Message {
    private final String topic;

    public GetMessage(String topic) {
        this.topic = topic;
    }


    public String getTopic() {
        return topic;
    }
}
