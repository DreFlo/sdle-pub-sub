package server.handlers;

import messages.Message;

public abstract class Handler {
    private final Message message;

    public Handler(Message message) {
        this.message = message;
    }

    abstract void run();
}
