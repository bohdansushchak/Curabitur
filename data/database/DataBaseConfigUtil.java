package sushchak.bohdan.curabitur.data.database;


import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

public class DataBaseConfigUtil extends OrmLiteConfigUtil{

    private static final Class<?>[] classes = new Class[] {ChatDB.class, MessageDB.class, UserDB.class};

    public static void main(String[] args) throws IOException, SQLException {
        writeConfigFile("ormlite_config.txt", classes);
    }
}
