package com.ahmdkhled.wechat.model;

/**
 * Created by Ahmed Khaled on 8/6/2018.
 */

public class Message {

    private String uid;
    private String content;
    private String senderUid;
    private long date;
    private boolean seen;

    public Message(String content, String senderUid, long date, boolean seen) {
        this.content = content;
        this.senderUid = senderUid;
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

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
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


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Message)) return false;
        Message message= (Message) obj;
        return  (message.getUid().equals(this.uid));
    }

}
