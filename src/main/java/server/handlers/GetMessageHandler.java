package server.handlers;

import messages.clientMessages.GetMessage;
import server.ConcreteServer;

public class GetMessageHandler extends Handler<GetMessage, ConcreteServer>{

    public GetMessageHandler(byte[] address, GetMessage message, ConcreteServer server) {
        super(address, message, server);
    }

    @Override
    public void run() {
        if(!this.server.clientInTopic(this.message.getTopic(), this.message.getClientId())){
            // TODO: send NotSubscribed Message
            return;
        }
        this.server.getClientMessagesPerTopic(this.message.getTopic(), this.message.getClientId());

    }
}