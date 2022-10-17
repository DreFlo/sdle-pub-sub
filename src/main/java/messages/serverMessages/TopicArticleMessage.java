package messages.serverMessages;

import messages.Message;

public class TopicArticleMessage extends Message {
    private final byte[] article;

    public TopicArticleMessage(byte[] article) {
        this.article = article;
    }

    public byte[] getArticle() {
        return article;
    }
}
