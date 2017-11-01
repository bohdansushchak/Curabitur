package sushchak.bohdan.curabitur.model;


import java.util.ArrayList;

public class ThreadData extends Thread{

    public ArrayList<User> listUserId;
    private String avatarUrl;
    private Message lastMessage;


    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public ThreadData(){
        listUserId = new ArrayList<>();
    }

    public ThreadData(Thread thread){
        super.setThread_id(thread.getThread_id());
        super.setTitle_name(thread.getTitle_name());
        listUserId = new ArrayList<>();
    }


    @Override
    public String toString() {
        return "ThreadData{" +
                "listUserId=" + listUserId +
                '}';
    }
}
