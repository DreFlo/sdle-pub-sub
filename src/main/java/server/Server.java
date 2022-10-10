package server;

import messages.Message;

public interface Server {
    Message receive();
    void send();
}
