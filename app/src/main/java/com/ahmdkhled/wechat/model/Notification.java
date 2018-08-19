package com.ahmdkhled.wechat.model;

/**
 * Created by Ahmed Khaled on 8/19/2018.
 */

public class Notification {

    private String id;
    private String Image;
    private String body;

    public Notification(String id, String image, String body) {
        this.id = id;
        Image = image;
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
