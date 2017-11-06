package sushchak.bohdan.curabitur.data.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "message")
public class MessageDB {

    @DatabaseField(generatedId = true, canBeNull = false) int id;
    @DatabaseField(canBeNull = false) String idSender;
    @DatabaseField(canBeNull = false) String idText;
    @DatabaseField(canBeNull = false) long timeStamp;
    @DatabaseField(canBeNull = false) String chatId;

    public MessageDB(){}

    public MessageDB(String chatId, String idSender, String idText, long timeStamp) {
        super();
        this.chatId = chatId;
        this.idSender = idSender;
        this.idText = idText;
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "MessageDB{" +
                "id=" + id +
                ", idSender='" + idSender + '\'' +
                ", idText='" + idText + '\'' +
                ", timeStamp=" + timeStamp +
                ", chatId='" + chatId + '\'' +
                '}';
    }
}
