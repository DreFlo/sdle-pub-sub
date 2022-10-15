package server;

import messages.Message;

import java.util.Map;

public interface Server {
    ReceivePair receive();
    void send(byte[] clientAddress, Message message);
}
