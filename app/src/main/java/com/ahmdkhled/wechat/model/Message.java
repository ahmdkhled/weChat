package com.ahmdkhled.wechat.model;

/**
 * Created by Ahmed Khaled on 8/6/2018.
 */

public class Message {

    private String uid;
    private String content;
    private long date;
    private boolean seen;

    public Message(String content, long date, boolean seen) {
        this.content = content;
        this.date = date;
        this.seen = seen;
    }

    public Message() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
