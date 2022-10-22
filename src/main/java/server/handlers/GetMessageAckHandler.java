package server.handlers;

import messages.clientMessages.GetMessageAck;
import messages.serverMessages.AckMessage;
import server.ConcreteServer;

public class GetMessageAckHandler extends Handler<GetMessageAck, ConcreteServer>{
    public GetMessageAckHandler(byte[] address, GetMessageAck message, ConcreteServer server) {
        super(address, message, server);
    }

    @Override
    public void run(){
        this.server.getTopic(message.getTopic()).incrementClientIndex(new String(address));
        this.server.getTopic(message.getTopic()).deleteOldMessages();
        this.server.updateTopic(message.getTopic());
        this.server.send(this.address, new AckMessage());
    }

}
