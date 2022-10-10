package messages.clientMessages;

import messages.Message;

public class UnsubscribeMessage extends Message {
    public UnsubscribeMessage(Integer clientId) {
        super(clientId);
    }
}
