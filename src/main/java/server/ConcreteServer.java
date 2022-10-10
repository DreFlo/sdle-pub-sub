package server;

import messages.Message;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ConcreteServer implements Server {
    private ZContext context;
    private ZMQ.Socket socket;
    private HashMap<String, List<String>> messagesPerTopic;
    private HashMap<String, List<Integer>> clientsPerTopic;

    public ConcreteServer() {
        try {
            this.context = new ZContext();
            //  Socket to talk to clients
            this.socket = context.createSocket(SocketType.REP);
            socket.bind("tcp://*:5555");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        try (ZContext context = new ZContext()) {
            //  Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind("tcp://*:5555");

            while (!Thread.currentThread().isInterrupted()) {
                byte[] reply = socket.recv(0);
                System.out.println(
                        "Received " + ": [" + new String(reply, ZMQ.CHARSET) + "]"
                );
                String response = "world";
                socket.send(response.getBytes(ZMQ.CHARSET), 0);
                Thread.sleep(1000); //  Do some 'work'
            }
        }
    }

    private void run(){
        while (!Thread.currentThread().isInterrupted()) {
            Message message = this.receive();



            //Thread.sleep(1000); //  Do some 'work'
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

    @Override
    public void send() {
        String response = "world";
        socket.send(response.getBytes(ZMQ.CHARSET), 0);
    }
}
