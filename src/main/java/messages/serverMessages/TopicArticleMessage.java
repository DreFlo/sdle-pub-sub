package messages.serverMessages;

import messages.Message;

public class TopicArticleMessage extends Message {
    private final byte[] article;

    public TopicArticleMessage(Integer clientId, byte[] article) {
        super(clientId);
        this.article = article;
    }

    public byte[] getArticle() {
        return article;
    }
}
