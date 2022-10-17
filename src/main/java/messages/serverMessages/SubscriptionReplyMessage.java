package messages.serverMessages;

import messages.Message;

public class SubscriptionReplyMessage extends Message {
    private final SubscriptionState subscriptionState;

    public SubscriptionReplyMessage(SubscriptionState subscriptionState) {
        this.subscriptionState = subscriptionState;
    }

    public SubscriptionState getSubscriptionState() {
        return subscriptionState;
    }
}
