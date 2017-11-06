package sushchak.bohdan.curabitur.model;


public class Message {

    public static final String KEY_SENDER = "idSender";
    public static final String KEY_TEXT = "text";
    public static final String KEY_TIMESTAMP = "timestamp";

    public String idSender;
    public String text;
    public long timestamp;

    @Override
    public String toString() {
        return "MessageDB{" +
                "idSender='" + idSender + '\'' +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
