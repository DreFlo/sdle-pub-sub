package server.handlers;

import messages.clientMessages.PutMessage;
import messages.serverMessages.NotSubscribedMessage;
import messages.serverMessages.PutReplyMessage;
import server.ConcreteServer;

public class PutMessageHandler extends Handler<PutMessage, ConcreteServer>{

    public PutMessageHandler(byte[] address, PutMessage message, ConcreteServer server) {
        super(address, message, server);
    }

    @Override
    public void run() {
        this.server.putMessageInTopic(this.message.getTopic(), this.message.getArticle());
        System.out.println(server.getTopic(message.getTopic()));
        this.server.send(this.address, new PutReplyMessage());
    }
}
