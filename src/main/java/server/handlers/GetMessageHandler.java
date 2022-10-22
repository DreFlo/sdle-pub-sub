package server.handlers;

import messages.clientMessages.GetMessage;
import messages.serverMessages.EmptyTopicMessage;
import messages.serverMessages.NonExistentTopicMessage;
import messages.serverMessages.NotSubscribedMessage;
import messages.serverMessages.TopicArticleMessage;
import server.ConcreteServer;

public class GetMessageHandler extends Handler<GetMessage, ConcreteServer>{

    public GetMessageHandler(byte[] address, GetMessage message, ConcreteServer server) {
        super(address, message, server);
    }

    @Override
    public void run() {
        if(this.server.getTopic(this.message.getTopic()) == null) {
           this.server.send(this.address, new NonExistentTopicMessage());
           return;
        }
        System.out.println(server.getTopic(message.getTopic()));
        if(!this.server.clientInTopic(this.message.getTopic(), new String(address))){
            this.server.send(this.address, new NotSubscribedMessage());
            return;
        }
        byte[] article = this.server.getTopic(this.message.getTopic()).getMessage(new String(address));
        if(article == null){
            this.server.send(address, new EmptyTopicMessage());
        }
        else{
            System.out.println(server.getTopic(message.getTopic()));
            this.server.send(address, new TopicArticleMessage(article));
        }
    }
}