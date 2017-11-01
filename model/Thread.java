package sushchak.bohdan.curabitur.model;


public class Thread {

    public static final String KEY_THREAD = "threads";

    private String thread_id;
    private String title_name;


    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public String getTitle_name() {
        return title_name;
    }

    public void setTitle_name(String title_name) {
        this.title_name = title_name;
    }
}

