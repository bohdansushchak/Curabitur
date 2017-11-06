package sushchak.bohdan.curabitur.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import sushchak.bohdan.curabitur.R;


public class DataBaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "Chat.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<UserDB, Integer> userDao = null;
    private Dao<ChatDB, Integer> chatDao = null;
    private Dao<MessageDB, Integer> messageDao = null;

    private RuntimeExceptionDao<UserDB, Integer> userRuntimeDao = null;
    private RuntimeExceptionDao<ChatDB, Integer> chatRuntimeDao = null;
    private RuntimeExceptionDao<MessageDB, Integer> messageRuntimeDao = null;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, UserDB.class);
            TableUtils.createTable(connectionSource, ChatDB.class);
            TableUtils.createTable(connectionSource, MessageDB.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, UserDB.class, true);
            TableUtils.dropTable(connectionSource, ChatDB.class, true);
            TableUtils.dropTable(connectionSource, MessageDB.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Dao<UserDB, Integer> getUserDao() throws SQLException {
        if(userDao == null){
            userDao = getDao(UserDB.class);
        }
        return userDao;
    }

    public RuntimeExceptionDao<UserDB, Integer> getUserRuntimeDao(){
        if(userRuntimeDao == null){
            userRuntimeDao = getRuntimeExceptionDao(UserDB.class);
        }
        return userRuntimeDao;
    }

    public Dao<ChatDB, Integer> getChatDao() throws SQLException {
        if(chatDao == null){
            chatDao = getDao(ChatDB.class);
        }
        return chatDao;
    }

    public RuntimeExceptionDao<ChatDB, Integer> getChatRuntimeDao(){
        if(chatRuntimeDao == null){
            chatRuntimeDao = getRuntimeExceptionDao(ChatDB.class);
        }
        return chatRuntimeDao;
    }

    public Dao<MessageDB, Integer> getMessageDao() throws SQLException {
        if(messageDao == null){
            messageDao = getDao(MessageDB.class);
        }
        return messageDao;
    }

    public RuntimeExceptionDao<MessageDB, Integer> getMessageRuntimeDao(){
        if(messageRuntimeDao == null){
            messageRuntimeDao = getRuntimeExceptionDao(MessageDB.class);
        }
        return messageRuntimeDao;
    }


}
