package client;

import messages.Message;

import java.util.List;

public interface Client {
    void get();
    void get(String topic);
    void put(String topic, byte[] message);
    void subscribe(String topic);
    void unsubscribe(String topic);
    void send(Message message) throws MessageTypeNotSupportedException;
    Message receive();
}
