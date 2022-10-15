package server.handlers;

import messages.clientMessages.PutMessage;
import server.ConcreteServer;

public class PutMessageHandler extends Handler<PutMessage, ConcreteServer>{

    public PutMessageHandler(byte[] address, PutMessage message, ConcreteServer server) {
        super(address, message, server);
    }

    @Override
    public void run() {
        if(!this.server.clientInTopic(this.message.getTopic(), this.message.getClientId())){
            // TODO: send NotSubscribed Message
            return;
        }
        this.server.putMessageInTopic(this.message.getTopic(), this.message.getArticle());

    }
}
