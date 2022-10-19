package server.handlers;

import messages.clientMessages.UnsubscribeMessage;
import messages.serverMessages.SubscriptionReplyMessage;
import messages.serverMessages.SubscriptionState;
import server.ConcreteServer;

public class UnsubscribeMessageHandler extends Handler<UnsubscribeMessage, ConcreteServer> {
    public UnsubscribeMessageHandler(byte[] address, UnsubscribeMessage message, ConcreteServer server) {
        super(address, message, server);
    }

    @Override
    public void run() {
        this.server.rmClientFromTopic(this.message.getTopic(), new String(address));

        this.server.send(this.address, new SubscriptionReplyMessage(SubscriptionState.UNSUBSCRIBED));

        System.out.println("Client unsubscribed from topic");
    }
}
