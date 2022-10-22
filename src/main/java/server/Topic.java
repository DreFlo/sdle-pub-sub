package server;

import org.zeromq.ZMQ;

import java.io.Serializable;
import java.util.*;

public class Topic implements Serializable {
    private final String name;
    private List<byte[]> messages;
    private final HashMap<String, Integer> clients; // ClientID: "messages length"

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

    public HashMap<String, Integer> getClients() {
        return clients;
    }

    public void addMessage(byte[] message){
        this.messages.add(message);
    }

    public void addClient(String clientID){
        if(!this.clients.containsKey(clientID))
            this.clients.put(clientID, this.messages.size() == 0 ? 0 :  this.messages.size());
    }

    public void removeClient(String clientID) {
        this.clients.remove(clientID);
        deleteOldMessages();
    }

    public boolean hasClient(String clientId) {
        return this.clients.containsKey(clientId);
    }

    public byte[] getMessage(String clientID){
        Integer idx = this.clients.get(clientID);

        if(idx == null) throw new RuntimeException("No Client with ID: " + clientID);

        if(idx == this.messages.size()){
            return null;
        } else {
            byte[] message = this.messages.get(idx);
            return message;
        }
    }

    public void deleteOldMessages() {
        OptionalInt minIndex = clients.values().stream().mapToInt(v -> v).min();

        if (minIndex.isPresent()) {
            this.messages.subList(0, minIndex.getAsInt()).clear();
            for(Map.Entry<String, Integer> client : this.clients.entrySet()){
                this.clients.replace(client.getKey(), client.getValue() - minIndex.getAsInt());
            }
        }
    }

    public void incrementClientIndex(String clientID){
        this.clients.replace(clientID, this.clients.get(clientID) + 1);
    }

    public void removeMessages(Integer num){
        this.messages = this.messages.subList(num, this.messages.size());
        for(Map.Entry<String, Integer> client : this.clients.entrySet()){
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

    @Override
    public String toString() {
        return "Topic{" +
                "name='" + name + '\'' +
                ", messages=" + messages +
                ", clients=" + clients +
                '}';
    }
}
