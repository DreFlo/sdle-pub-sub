package server;

import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Topic {
    private String name;
    private List<byte[]> messages;
    private HashMap<Integer, Integer> clients;

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
        for(Map.Entry<Integer, Integer> client : this.clients.entrySet()){
            this.clients.replace(client.getKey(), client.getValue());
        }
    }

    public void addClient(Integer clientID){
        this.clients.put(clientID, this.messages.size() - 1);
    }

    public byte[] getMessage(Integer clientID){
        Integer idx = this.clients.get(clientID);

        if(idx == null) throw new RuntimeException("No Client with ID: " + clientID);

        if(idx == this.messages.size()){
            return new String("Already got everything in topic.").getBytes(ZMQ.CHARSET);
        } else {
            return this.messages.get(idx);
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
}
