package server;

import org.zeromq.ZMQ;

import java.io.Serializable;
import java.util.*;

public class Topic implements Serializable {
    private final String name;
    private List<byte[]> messages;
    private final HashMap<Integer, Integer> clients;

    public Topic(String name){
        this.name = name;
        this.messages = new ArrayList<>();
        this.clients = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public List<byte[]> getMessages() {
        return messages;
    }

    public HashMap<Integer, Integer> getClients() {
        return clients;
    }

    public void addMessage(byte[] message){
        this.messages.add(message);
    }

    public void addClient(int clientID){
        this.clients.put(clientID, this.messages.size() - 1);
    }

    public void removeClient(int clientID) { this.clients.remove(clientID); }

    public boolean hasClient(int clientId) {
        return this.clients.containsKey(clientId);
    }

    public byte[] getMessage(int clientID){
        Integer idx = this.clients.get(clientID);

        if(idx == null) throw new RuntimeException("No Client with ID: " + clientID);

        if(idx == this.messages.size()){
            return new String("Already got everything in topic.").getBytes(ZMQ.CHARSET);
        } else {
            byte[] message = this.messages.get(idx);
            incrementClientIndex(clientID);
            deleteOldMessages();
            return message;
        }
    }

    private void deleteOldMessages() {
        OptionalInt minIndex = clients.values().stream().mapToInt(v -> v).min();

        if (minIndex.isPresent()) {
            this.messages = this.messages.subList(minIndex.getAsInt(), this.messages.size());
            for(Map.Entry<Integer, Integer> client : this.clients.entrySet()){
                this.clients.replace(client.getKey(), client.getValue() - minIndex.getAsInt());
            }
        }
    }

    public void incrementClientIndex(Integer clientID){
        this.clients.replace(clientID, this.clients.get(clientID) + 1);
    }

    public void removeMessages(Integer num){
        this.messages = this.messages.subList(num, this.messages.size());
        for(Map.Entry<Integer, Integer> client : this.clients.entrySet()){
            this.clients.replace(client.getKey(), client.getValue() - num);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Topic)) return false;
        Topic topic = (Topic) o;
        return getName().equals(topic.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
