package sushchak.bohdan.curabitur.data.database;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "chat")
public class ChatDB {

    @DatabaseField(canBeNull = false) String id;
    @DatabaseField(canBeNull = false) String creationDate;
    @DatabaseField(canBeNull = false) String creatorUserId;
    @DatabaseField(canBeNull = false) String textLastMessage;
    @DatabaseField(canBeNull = false) long timeLastMessage;
    @DatabaseField(canBeNull = false) String userId;

    public ChatDB(){}

    public ChatDB(String userId, String id, String creationDate, String creatorUserId){
        super();
        this.userId = userId;
        this.id = id;
        this.creationDate = creationDate;
        this.creatorUserId = creatorUserId;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id='" + id + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", creatorUserId='" + creatorUserId + '\'' +
                ", textLastMessage='" + textLastMessage + '\'' +
                ", timeLastMessage=" + timeLastMessage +
                ", userId='" + userId + '\'' +
                '}';
    }
}
