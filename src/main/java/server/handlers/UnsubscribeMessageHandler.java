package server.handlers;

import messages.clientMessages.UnsubscribeMessage;
import server.ConcreteServer;

public class UnsubscribeMessageHandler extends Handler<UnsubscribeMessage, ConcreteServer> {
    public UnsubscribeMessageHandler(byte[] address, UnsubscribeMessage message, ConcreteServer server) {
        super(address, message, server);
    }

    @Override
    public void run() {
        this.server.rmClientFromTopic(this.message.getTopic(), this.message.getClientID());

        System.out.println("Client unsubscribed from topic");
    }
}
