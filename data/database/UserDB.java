package sushchak.bohdan.curabitur.data.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "user")
public class UserDB {

    @DatabaseField(canBeNull = false) String userId;
    @DatabaseField String avatar;
    @DatabaseField String email;
    @DatabaseField String name;
    @DatabaseField String phone;


    public UserDB(){}

    public UserDB(String userId) {
        super();
        this.userId = userId;
    }

    public UserDB(String userId, String avatar, String email, String name, String phone) {
        super();
        this.userId = userId;
        this.avatar = avatar;
        this.email = email;
        this.name = name;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "UserDB{" +
                "userId='" + userId + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
