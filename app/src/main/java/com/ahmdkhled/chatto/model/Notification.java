package com.ahmdkhled.chatto.model;

import java.util.Map;

/**
 * Created by Ahmed Khaled on 8/19/2018.
 */

public class Notification {

    private String id;
    private String userUid;
    private String body;
    private String type;
    private long date;
    private User user;
    private Map<String,Object> target;


    public Notification( String userUid, String type,long date) {
        this.userUid = userUid;
        this.type = type;
        this.date=date;
    }

    public Notification() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Map<String, Object> getTarget() {
        return target;
    }

    public void setTarget(Map<String, Object> target) {
        this.target = target;
    }
}
