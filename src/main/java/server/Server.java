package server;

import messages.Message;

public interface Server {
    ReceivePair receive();
    void send(byte[] clientAddress, Message message);
}
