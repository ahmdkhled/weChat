package com.ahmdkhled.wechat.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ahmed Khaled on 6/3/2018.
 */

public  class User implements Parcelable{

    private String uid;
    private String name;
    private String email;
    private String password;
    private String profileImg;
    private String bio;
    private int friendShipState;

    public User(String uid, String name, String email, String password, String profileImg, String bio) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileImg = profileImg;
        this.bio = bio;
    }

    public User() {}

    private User(Parcel in) {
        uid = in.readString();
        name = in.readString();
        email = in.readString();
        password = in.readString();
        profileImg = in.readString();
        bio = in.readString();
        friendShipState=in.readInt();
    }


    public String getUid() {return uid;}

    public void setUid(String uid) {this.uid = uid;}

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getFriendShipState() {
        return friendShipState;
    }

    public void setFriendShipState(int friendShipState) {
        this.friendShipState = friendShipState;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(password);
        parcel.writeString(profileImg);
        parcel.writeString(bio);
        parcel.writeInt(friendShipState);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
