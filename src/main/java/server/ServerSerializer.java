package server;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerSerializer {

    private String dir;

    public ServerSerializer() {
        this.dir = "topics/";
    }
    
    public void writeTopics(Iterable<Topic> topics) {
        for(Topic topic : topics) {
            writeTopic(topic);
        }
    }

    public Map<String, Topic> readTopics() {
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

    public void writeTopic(Topic topic) {
        try {
            File ftopic = new File(dir + topic.getName());
            ftopic.createNewFile();
            FileOutputStream fout = new FileOutputStream(ftopic);
            ObjectOutputStream oout = new ObjectOutputStream(fout);
            oout.writeObject(topic);
            oout.close();
            fout.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Topic readTopic(String topic) {
        try {
            FileInputStream fin = new FileInputStream(dir + topic);
            ObjectInputStream oin = new ObjectInputStream(fin);
            Topic ret = (Topic) oin.readObject();
            oin.close();
            fin.close();
            return ret;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
