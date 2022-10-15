package server.handlers;

import messages.Message;
import messages.clientMessages.GetMessage;
import messages.clientMessages.PutMessage;
import messages.clientMessages.SubscribeMessage;
import messages.clientMessages.UnsubscribeMessage;
import server.ConcreteServer;
import server.Server;

public class MessageHandlerBuilder {

    public static Handler<? extends Message, ? extends Server> getHandler(byte[] address, Message message, Server server) {
        if (server instanceof ConcreteServer concreteServer) {
            if (message instanceof SubscribeMessage subscribeMessage) {
                return new SubscribeMessageHandler(address, subscribeMessage, concreteServer);
            } else if (message instanceof GetMessage getMessage) {
                return new GetMessageHandler(address, getMessage, concreteServer);
            } else if (message instanceof PutMessage putMessage) {
                return new PutMessageHandler(address, putMessage, concreteServer);
            } else if (message instanceof UnsubscribeMessage unsubscribeMessage) {
                return new UnsubscribeMessageHandler(address, unsubscribeMessage, concreteServer);
            }
        }
        return null;
    }
}
