package server.handlers;

import messages.clientMessages.GetMessage;
import messages.serverMessages.NotSubscribedMessage;
import messages.serverMessages.TopicArticleMessage;
import server.ConcreteServer;
import server.Topic;

public class GetMessageHandler extends Handler<GetMessage, ConcreteServer>{

    public GetMessageHandler(byte[] address, GetMessage message, ConcreteServer server) {
        super(address, message, server);
    }

    @Override
    public void run() {
        if(!this.server.clientInTopic(this.message.getTopic(), new String(address))){
            this.server.send(this.address, new NotSubscribedMessage());
            return;
        }
        byte[] article = this.server.getClientMessagesPerTopic(this.message.getTopic(), new String(address));
        this.server.send(address, new TopicArticleMessage(article));
    }
}