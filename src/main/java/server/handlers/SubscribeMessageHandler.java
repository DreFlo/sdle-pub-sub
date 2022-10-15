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
        this.server.addClientToTopic(this.message.getTopic(), this.message.getClientID());

        System.out.println("Client subscribed to topic");

        SubscriptionReplyMessage replyMessage = new SubscriptionReplyMessage(this.message.getClientID(), SubscriptionState.SUBSCRIBED);

        this.server.send(this.address, replyMessage);
    }
}
