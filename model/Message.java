package sushchak.bohdan.curabitur.model;


public class Message {
    public String idSender;
    public String text;
    public long timestamp;

    @Override
    public String toString() {
        return "Message{" +
                "idSender='" + idSender + '\'' +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
