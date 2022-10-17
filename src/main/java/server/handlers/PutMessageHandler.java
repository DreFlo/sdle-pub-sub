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
        if(!this.server.clientInTopic(this.message.getTopic(), new String(address))){
            this.server.send(this.address, new NotSubscribedMessage());
            return;
        }
        this.server.putMessageInTopic(this.message.getTopic(), this.message.getArticle());
        this.server.send(this.address, new PutReplyMessage());
    }
}
