package com.ahmdkhled.wechat.model;

/**
 * Created by Ahmed Khaled on 6/6/2018.
 */

public class FriendReq {

    private long date;
    private User user;

    public FriendReq() {}

    public FriendReq(long date, String senderUid, User user) {
        this.date = date;
        this.user = user;
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


}
