package com.ahmdkhled.chatto.model;

/**
 * Created by Ahmed Khaled on 8/6/2018.
 */

public class Chat {
    private String uid;
    private String lastMessage;
    private User user;


    public Chat(String lastMessage, User user) {
        this.lastMessage = lastMessage;
        this.user = user;
    }

    public Chat() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
