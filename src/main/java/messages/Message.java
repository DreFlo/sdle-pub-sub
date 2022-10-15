package messages;

import java.io.*;

public abstract class Message implements Serializable{
    private final Integer clientId;

    public Message(Integer clientId){
        this.clientId = clientId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public final byte[] toBytes() {
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        )
        {
            oos.writeObject(this);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message fromBytes(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(bais));
        Object o;
        try {
            o = ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (Message) o;
    }
}
