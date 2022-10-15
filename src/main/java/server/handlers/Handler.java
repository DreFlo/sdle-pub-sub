package server.handlers;

import messages.Message;
import server.Server;

public abstract class Handler<M extends Message, S extends Server> implements Runnable{
    protected final M message;
    protected final S server;
    protected byte[] address;

    public Handler(byte[] address, M message, S server) {
        this.message = message;
        this.server = server;
        this.address = address;
    }

    public abstract void run();
}
