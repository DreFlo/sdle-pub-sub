package messages.serverMessages;

import messages.Message;

enum SubscriptionState {
    SUBSCRIBED,
    UNSUBSCRIBED,
    ERROR
}

public class SubscriptionReplyMessage extends Message {
    private final SubscriptionState subscriptionState;

    public SubscriptionReplyMessage(Integer clientId, SubscriptionState subscriptionState) {
        super(clientId);
        this.subscriptionState = subscriptionState;
    }

    public SubscriptionState getSubscriptionState() {
        return subscriptionState;
    }
}