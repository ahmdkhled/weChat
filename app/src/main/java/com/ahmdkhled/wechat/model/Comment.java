package com.ahmdkhled.wechat.model;

/**
 * Created by Ahmed Khaled on 8/5/2018.
 */

public class Comment {
    private String uid;
    private String content;
    private String authorUid;
    private long date;
    private User user;

    public Comment(String uid, String content, String authorUid, long date, User user) {
        this.uid = uid;
        this.content = content;
        this.authorUid = authorUid;
        this.date = date;
        this.user = user;
    }

    public Comment() {
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

    public String getAuthorUid() {
        return authorUid;
    }

    public void setAuthorUid(String authorUid) {
        this.authorUid = authorUid;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Comment)) return false;
        Comment c = (Comment) obj;
        return c.getUid().equals(this.uid);
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }
}
