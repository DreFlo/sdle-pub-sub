package client;

import messages.clientMessages.GetMessage;
import messages.Message;
import messages.clientMessages.PutMessage;
import messages.clientMessages.SubscribeMessage;
import messages.clientMessages.UnsubscribeMessage;
import messages.serverMessages.*;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import server.ReceivePair;

import java.io.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConcreteClient implements Client {
    private ZContext context;
    private ZMQ.Socket socket;
    private List<String> subscribed;


    public ConcreteClient(String id) {
        this.subscribed = new ArrayList<>();
        try {
            this.context = new ZContext();
            this.socket = context.createSocket(SocketType.REQ);
            this.socket.setIdentity(id.getBytes());
            socket.connect("tcp://localhost:5555");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //client id

    //client put topic "1321312"

    public static void main(String[] args) {
        System.out.println(args.length);
        if(args.length == 3){ //client put topic msg
            ConcreteClient client = new ConcreteClient("0");
            client.put(args[1], args[2].getBytes());
        }
        else if (args.length == 1){ //client id
            ConcreteClient client = new ConcreteClient(args[0]);
            client.run();
        }
        else {
            System.out.println("Invalid arguments.");
            System.out.println("Either 'id' or 'put topic msg'");
        }
    }

    private void run(){
        Console cnsl = System.console();
        boolean exit = false;
        while (!exit){
            String line = cnsl.readLine("Enter command: ");
            String[] args = line.split(" ");

            switch(args[0]){
                case "put":
                    this.put(args[1], args[2].getBytes());
                    break;
                case "get":
                    if(args.length == 1)
                        this.get();
                    else
                        this.get(args[1]);
                    break;
                case "subscribe":
                    this.subscribe(args[1]);
                    break;
                case "unsubscribe":
                    break;
                case "exit":
                    exit = true;
                    break;
            }
        }

    }

    @Override
    public void get() {
        subscribed.forEach(this::get);
    }

    @Override
    public void get(String topic) {
        try {
            send(new GetMessage(topic));
        } catch (MessageTypeNotSupportedException e) {
            throw new RuntimeException(e);
        }

        Message receivedMessage = this.receive();
        if (receivedMessage instanceof TopicArticleMessage){
            System.out.println("Got message:");
            System.out.println(new String(((TopicArticleMessage) receivedMessage).getArticle()));
        }
        else if (receivedMessage instanceof NotSubscribedMessage) {
            System.out.println("Not subscribed to topic: " + topic);
        }
        else System.out.println("Invalid response from server in put attempt.");
    }

    @Override
    public void put(String topic, byte[] message) {
        try {
            send(new PutMessage(topic, message));
        } catch (MessageTypeNotSupportedException e) {
            throw new RuntimeException(e);
        }

        Message receivedMessage = this.receive();

        if(receivedMessage instanceof PutReplyMessage){
            System.out.println("Successful put in topic: " + topic);
        }
        else System.out.println("Invalid response from server in put attempt.");
    }

    @Override
    public void subscribe(String topic) {
        subscribed.add(topic);
        try {
            send(new SubscribeMessage(topic));
        } catch (MessageTypeNotSupportedException e) {
            throw new RuntimeException(e);
        }

        Message receivedMessage = this.receive();
        if(receivedMessage instanceof SubscriptionReplyMessage){
            if(((SubscriptionReplyMessage) receivedMessage).getSubscriptionState() == SubscriptionState.SUBSCRIBED)
                System.out.println("Successful subscription to topic: " + topic);
            else System.out.println("Subscription wasn't successful");
        }
        else System.out.println("Invalid response from server in subscription attempt.");
    }

    @Override
    public void unsubscribe(String topic) {
        subscribed.remove(topic);

        try {
            send(new UnsubscribeMessage(topic));
        } catch (MessageTypeNotSupportedException e) {
            throw new RuntimeException(e);
        }

        Message receivedMessage = this.receive();
        if(receivedMessage instanceof SubscriptionReplyMessage){
            if(((SubscriptionReplyMessage) receivedMessage).getSubscriptionState() == SubscriptionState.UNSUBSCRIBED)
                System.out.println("Successful unsubscription to topic: " + topic);
            else System.out.println("Unsubscription wasn't successful");
        }
        else System.out.println("Invalid response from server in unsubscription attempt.");
    }

    @Override
    public void send(Message message) throws MessageTypeNotSupportedException {
        socket.send(message.toBytes(), 0);
    }

    @Override
    public Message receive() {
        byte[] receivedMsgBytes = socket.recv(0);

        try {
            System.out.println(Message.fromBytes(receivedMsgBytes));
            return Message.fromBytes(receivedMsgBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class MessageTypeNotSupportedException extends Exception {
    public MessageTypeNotSupportedException(String errorMsg) {
        super(errorMsg);
    }
}
