package messages.clientMessages;

import messages.Message;

public class PutMessage extends Message {
    private final String topic;
    private final byte[] article;

    public PutMessage(String topic, byte[] article) {
        this.topic = topic;
        this.article = article;
    }

    public String getTopic() {
        return topic;
    }

    public byte[] getArticle() {
        return article;
    }
}
