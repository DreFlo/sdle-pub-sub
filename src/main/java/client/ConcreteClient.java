package client;

import messages.clientMessages.*;
import messages.Message;
import messages.serverMessages.*;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

import java.io.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConcreteClient implements Client {
    private ZContext context;
    private ZMQ.Socket socket;
    private List<String> subscribed;
    private byte[] id;
    private final static int receivedTimeout = 10000;

    public ConcreteClient(String id) {
        this.subscribed = new ArrayList<>();
        this.id = id.getBytes();
        try {
            this.context = new ZContext();
            this.createSocket();
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
            String line = cnsl.readLine("Enter command (type help for functionality): ");
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
                    this.unsubscribe(args[1]);
                    break;
                case "exit":
                    exit = true;
                    break;
                case "topics":
                    if(subscribed.isEmpty()){
                        System.out.println("No topics subscribed.");
                    } else {
                        System.out.println("Subscribed topics:");
                    }
                    for (String s : subscribed) {
                        System.out.println("\t" + s);
                    }
                    break;
                case "help":
                    printHelp();
                    break;
                default:
                    System.out.println("'" + args[0] + "' is not recognized as a command.");
                    break;
            }
        }
    }

    private void printHelp() {
        System.out.println("subscribe <topic> \t Subscribe to a topic");
        System.out.println("unsubscribe <topic> \t Unsubscribe from a topic");
        System.out.println("put <topic> <message> \t Put message in a topic");
        System.out.println("get [topic] \t\t Get message from all topics [or a single topic]");
        System.out.println("topics \t\t\t Print all topics currently subscribed");
        System.out.println("exit \t\t\t Exit the console");
    }

    @Override
    public void get() {
        subscribed.forEach(this::get);
    }

    @Override
    public void get(String topic) {

        Message receivedMessage = this.sendAndReceive(new GetMessage(topic));

        if (receivedMessage instanceof TopicArticleMessage){
            System.out.println("Got message:");
            System.out.println(new String(((TopicArticleMessage) receivedMessage).getArticle()));

            receivedMessage = this.sendAndReceive(new GetMessageAck(topic));

            if(receivedMessage instanceof AckMessage){
                System.out.println("Received correct acknowledgment.");
            }
            else System.out.println("Invalid response from server after acknowledging get message.");
        }
        else if(receivedMessage instanceof EmptyTopicMessage){
            System.out.println("There is no messages to get.");
        }
        else if (receivedMessage instanceof NonExistentTopicMessage) {
            System.out.println("There is no topic: " + topic);
        }
        else if (receivedMessage instanceof NotSubscribedMessage) {
            System.out.println("Not subscribed to topic: " + topic);
        }
        else System.out.println("Invalid response from server in put attempt.");
    }

    @Override
    public void put(String topic, byte[] message) {
        Message receivedMessage = this.sendAndReceive(new PutMessage(topic, message));

        if(receivedMessage instanceof PutReplyMessage){
            System.out.println("Successful put in topic: " + topic);
        }
        else System.out.println("Invalid response from server in put attempt.");
    }

    @Override
    public void subscribe(String topic) {
        subscribed.add(topic);

        Message receivedMessage = this.sendAndReceive(new SubscribeMessage(topic));

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

        Message receivedMessage = this.sendAndReceive(new UnsubscribeMessage(topic));

        if(receivedMessage instanceof SubscriptionReplyMessage){
            if(((SubscriptionReplyMessage) receivedMessage).getSubscriptionState() == SubscriptionState.UNSUBSCRIBED)
                System.out.println("Successful unsubscription to topic: " + topic);
            else System.out.println("Unsubscription wasn't successful");
        }
        else System.out.println("Invalid response from server in unsubscription attempt.");
    }

    public Message sendAndReceive(Message message){
        Message receivedMessage;

        do {
            try {
                this.send(message);
            } catch (MessageTypeNotSupportedException e) {
                throw new RuntimeException(e);
            }

            receivedMessage = this.receive();
        } while (receivedMessage == null);

        return receivedMessage;
    }

    @Override
    public void send(Message message) throws MessageTypeNotSupportedException {
        socket.send(message.toBytes());
    }

    @Override
    public Message receive() {
        byte[] receivedMsgBytes = socket.recv(0);

        if(receivedMsgBytes == null){
            this.recreateSocket();
            System.out.println("Receive timeout. Trying again.");
            return null;
        }

        try {
            System.out.println(Message.fromBytes(receivedMsgBytes));
            return Message.fromBytes(receivedMsgBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createSocket(){
        this.socket = context.createSocket(SocketType.REQ);
        this.socket.setIdentity(this.id);
        this.socket.setReceiveTimeOut(ConcreteClient.receivedTimeout);
        socket.connect("tcp://localhost:5555");
    }
    private void recreateSocket(){
        this.socket.close();
        this.createSocket();
    }
}

class MessageTypeNotSupportedException extends Exception {
    public MessageTypeNotSupportedException(String errorMsg) {
        super(errorMsg);
    }
}
