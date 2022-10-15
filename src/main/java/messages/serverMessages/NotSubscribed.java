package messages.serverMessages;

import messages.Message;

public class NotSubscribed extends Message {
    public NotSubscribed(Integer clientId) {
        super(clientId);
    }

}
