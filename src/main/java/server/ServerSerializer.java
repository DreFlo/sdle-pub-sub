package server;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerSerializer {

    private String dir;

    public ServerSerializer() {
        this.dir = "/topics/";
    }

    public void writeTopic(Topic topic) {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(dir + topic.getName());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ObjectOutputStream oout = null;
        try {
            oout = new ObjectOutputStream(fout);
            oout.writeObject(topic);
            oout.close();
            fout.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Topic> getTopics() throws IOException {
        File directory = new File(dir);
        File[] topics = directory.listFiles();
        HashMap<String, Topic> ret = new HashMap<>();

        for(File topicFile : topics) {
            String name = topicFile.getName();
            Topic topic = readTopic(name);
            ret.put(name, topic);
        }

        return ret;
    }

    private Topic readTopic(String topic) {
        FileInputStream fin = null;
        ObjectInputStream oin = null;
        try {
            fin = new FileInputStream(topic);
            oin = new ObjectInputStream(fin);
            Topic ret = (Topic) oin.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            oin.close();
            fin.close();
        }
    }

}
