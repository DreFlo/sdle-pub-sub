package client;

import messages.clientMessages.GetMessage;
import messages.Message;
import messages.clientMessages.PutMessage;
import messages.clientMessages.SubscribeMessage;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConcreteClient implements Client {
    private Integer id;
    private ZContext context;
    private ZMQ.Socket socket;
    private List<String> subscribed;

    public ConcreteClient() {
        this.subscribed = new ArrayList<>();

        try {
            this.context = new ZContext();
            this.socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://localhost:5555");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            System.out.println("Connecting to hello world server");

            //  Socket to talk to server
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://localhost:5555");

            for (int requestNbr = 0; requestNbr != 10; requestNbr++) {
                String request = "Hello";

                byte[] reply = socket.recv(0);
                System.out.println(
                        "Received " + new String(reply, ZMQ.CHARSET) + " " +
                                requestNbr
                );
            }
        }
    }

    @Override
    public List<Message> get() {    
        return subscribed.stream().map(this::get).collect(Collectors.toList());
    }

    @Override
    public Message get(String topic) {
        try {
            send(new GetMessage(this.id, topic));
        } catch (MessageTypeNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return receive();
    }

    @Override
    public void put(String topic, byte[] message) {
        try {
            send(new PutMessage(this.id, topic, message));
        } catch (MessageTypeNotSupportedException e) {
            throw new RuntimeException(e);
        }
        receive();
        // TODO Check ack
    }

    @Override
    public void subscribe(String topic) {
        subscribed.add(topic);
        // TODO Other stuff
        try {
            send(new SubscribeMessage(this.id, topic));
        } catch (MessageTypeNotSupportedException e) {
            throw new RuntimeException(e);
        }
        receive();
        // TODO Check ack
    }

    @Override
    public void unsubscribe(String topic) {
        subscribed.remove(topic);
        // TODO Other stuff
    }

    @Override
    public void send(Message message) throws MessageTypeNotSupportedException {
        if (message instanceof GetMessage getMessage) {
            System.out.println("Sending Hello " + getMessage.getTopic());
            socket.send(getMessage.toBytes(), 0);
        }
        else {
            throw new MessageTypeNotSupportedException("Expected GetMessage Type");
        }
    }

    @Override
    public Message receive() {
        byte[] reply = socket.recv(0);
        try {
            return Message.fromBytes(reply);
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
