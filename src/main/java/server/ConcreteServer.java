package server;

import messages.Message;
import messages.clientMessages.ShutdownServerMessage;
import messages.serverMessages.ShutdownReplyMessage;
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

    private final int NTHREADS = 4;
    private final ZContext context;
    private final Socket socket;
    private final Map<String, Topic> topics;
    private final ExecutorService executorService;
    private final ServerSerializer serverSerializer;

    public ConcreteServer() {
        try {
            this.context = new ZContext();

            this.socket = context.createSocket(SocketType.ROUTER);

            socket.bind("tcp://*:5555");

            this.executorService = Executors.newFixedThreadPool(NTHREADS);

            this.serverSerializer = new ServerSerializer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.topics = serverSerializer.readTopics();
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

                System.out.println(received.message());

                if (received.message() instanceof ShutdownServerMessage) {
                    send(receive().address(), new ShutdownReplyMessage());
                    items.close();
                    break;
                }

                Handler<? extends Message, ? extends Server> handler = MessageHandlerBuilder.getHandler(
                        received.address(), received.message(), this
                );

                if (handler == null) continue;

                executorService.submit(handler);
            }
        }
        executorService.shutdown();
        System.out.println("Exit");
    }

    @Override
    public ReceivePair receive() {
        byte[] client_addr = socket.recv(0);

        byte[] empty = socket.recv(0);
        if(empty.length != 0) throw new RuntimeException("Problem in message");

        System.out.println(new String(client_addr));
        byte[] receivedMsgBytes = socket.recv(0);

        try {
            return new ReceivePair(client_addr, Message.fromBytes(receivedMsgBytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(byte[] clientAddress, Message message) {
        this.socket.sendMore(clientAddress);
        this.socket.sendMore("".getBytes());
        this.socket.send(message.toBytes(), 0);
    }

    public boolean clientInTopic(String topic, String clientID) {

        if(!this.topics.containsKey(topic)) return false;
        return topics.get(topic).hasClient(clientID);
    }

    public synchronized void addClientToTopic(String topic, String clientId) {
        if (!topics.containsKey(topic)) {
            topics.put(topic, new Topic(topic));
        }
        if(!topics.get(topic).hasClient(clientId)){
            topics.get(topic).addClient(clientId);
            saveTopic(topic);
        }
    }

    public synchronized void rmClientFromTopic(String topic, String clientId) {
        if (!this.topics.containsKey(topic)) {
            return;
        }
        this.topics.get(topic).removeClient(clientId);
        saveTopic(topic);
    }

    public synchronized void updateTopic(String topic) {
        System.out.println("Saving topic : " + topic);
        saveTopic(topic);
    }

    public synchronized void putMessageInTopic(String topic, byte[] message) {
        if (!topics.containsKey(topic)) {
            System.out.println("Creating new topic " + topic);
            topics.put(topic, new Topic(topic));
        }
        System.out.println("Adding message to topic");
        topics.get(topic).addMessage(message);
        System.out.println("Saving topic");
        saveTopic(topic);
    }

    private void saveTopic(String topic) {
        serverSerializer.writeTopic(topics.get(topic));
        System.out.println("Saved topic");
    }

    public Topic getTopic(String topic) {
        return topics.get(topic);
    }
}
