package server.handlers;

import messages.clientMessages.SubscribeMessage;
import messages.serverMessages.SubscriptionReplyMessage;
import messages.serverMessages.SubscriptionState;
import server.ConcreteServer;

public class SubscribeMessageHandler extends Handler<SubscribeMessage, ConcreteServer> {

    public SubscribeMessageHandler(byte[] address, SubscribeMessage subscribeMessage, ConcreteServer concreteServer) {
        super(address, subscribeMessage, concreteServer);
    }

    @Override
    public void run() {
        this.server.addClientToTopic(this.message.getTopic(), new String(address));

        System.out.println("Client subscribed to topic");

        SubscriptionReplyMessage replyMessage = new SubscriptionReplyMessage(SubscriptionState.SUBSCRIBED);

        this.server.send(this.address, replyMessage);
    }
}
