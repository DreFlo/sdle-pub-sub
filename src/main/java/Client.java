public interface Client {
    String get();
    String get(String topic);
    void put(String topic, byte[] message);
    void subscribe(String topic);
    void unsubscribe(String topic);
    void send();
    void receive();
}
