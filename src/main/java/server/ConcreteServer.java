package server;

import messages.Message;
import org.zeromq.SocketType;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;
import server.handlers.Handler;
import server.handlers.MessageHandlerBuilder;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcreteServer implements Server {
    private ZContext context;
    private Socket socket;
    private Map<String, Topic> topics;
    private ExecutorService executorService;

    public ConcreteServer() {
        try {
            this.context = new ZContext();

            this.socket = context.createSocket(SocketType.ROUTER);

            socket.bind("tcp://*:5555");

            this.executorService = Executors.newFixedThreadPool(NTHREADS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.topics = new HashMap<>();
    }

    public static void main(String[] args) {
        ConcreteServer server = new ConcreteServer();
        server.run();
    }

    private void run(){
        while (!Thread.currentThread().isInterrupted()) {
            Poller items = this.context.createPoller(1);

            items.register(socket, Poller.POLLIN);

            items.poll();

            if(items.pollin(0)){

                ReceivePair received = this.receive();

                Handler<? extends Message, ? extends Server> handler = MessageHandlerBuilder.getHandler(
                        received.address(), received.message(), this
                );

                executorService.submit(handler);
            }
        }
    }

    @Override
    public Message receive() {
        byte[] reply = socket.recv(0);
        try {
            return new ReceivePair(client_addr, Message.fromBytes(receivedMsgBytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send() {
        String response = "world";
        socket.send(response.getBytes(ZMQ.CHARSET), 0);
    }

    public boolean clientInTopic(String topic, Integer clientID) {
        // TODO: exception -> topic does not exist
        if(!this.topics.containsKey(topic)) return false;
        return topics.get(topic).hasClient(clientID);
    }

    public void addClientToTopic(String topic, Integer clientId) {
        // TODO Data permanence
        if (!topics.containsKey(topic)) {
            topics.put(topic, new Topic(topic));
        }
        topics.get(topic).addClient(clientId);
    }

    public void rmClientFromTopic(String topic, Integer clientId) {
        // TODO Data permanence
        if (!this.topics.containsKey(topic)) {
            return;
        }
        this.topics.get(topic).removeClient(clientId);
    }

    public byte[] getClientMessagesPerTopic(String topic, int clientID) {
        return this.topics.get(topic).getMessage(clientID);
    }

    public void putMessageInTopic(String topic, byte[] message) {
        if (topics.containsKey(topic)) {
            topics.get(topic).addMessage(message);
        }
    }
}
