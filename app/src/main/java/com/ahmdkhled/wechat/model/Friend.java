package com.ahmdkhled.wechat.model;

/**
 * Created by Ahmed Khaled on 6/10/2018.
 */

public class Friend {

    private String user1;
    private String user2;

    public Friend(String user1, String user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public Friend() {
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }
}
