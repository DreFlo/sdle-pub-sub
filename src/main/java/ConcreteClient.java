import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class ConcreteClient implements Client {
    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            System.out.println("Connecting to hello world server");

            //  Socket to talk to server
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://localhost:5555");

            for (int requestNbr = 0; requestNbr != 10; requestNbr++) {
                String request = "Hello";
                System.out.println("Sending Hello " + requestNbr);
                socket.send(request.getBytes(ZMQ.CHARSET), 0);

                byte[] reply = socket.recv(0);
                System.out.println(
                        "Received " + new String(reply, ZMQ.CHARSET) + " " +
                                requestNbr
                );
            }
        }
    }

    @Override
    public String get() {
        return null;
    }

    @Override
    public String get(String topic) {
        return null;
    }

    @Override
    public void put(String topic, byte[] message) {

    }

    @Override
    public void subscribe(String topic) {

    }

    @Override
    public void unsubscribe(String topic) {

    }

    @Override
    public void send() {

    }

    @Override
    public void receive() {

    }
}
