package com.ahmdkhled.wechat.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ahmed Khaled on 6/3/2018.
 */

public class Post implements Parcelable{

    private String content;
    private long date;
    private String uid;
    private String postUid;
    private User user;
    private int likeState;
    private int commentsCount;
    public Post(String uid,String content, long date,User user ) {
        this.content = content;
        this.date = date;
        this.uid = uid;
        this.user = user;
    }

    public Post() {
    }

    public Post(Parcel parcel) {
        content=parcel.readString();
        date=parcel.readLong();
        uid=parcel.readString();
        postUid=parcel.readString();
        likeState=parcel.readInt();
        commentsCount=parcel.readInt();
        user=parcel.readParcelable(User.class.getClassLoader());
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {return date;}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getPostUid() {
        return postUid;
    }

    public void setPostUid(String postUid) {
        this.postUid = postUid;
    }

    public int getLikeState() {
        return likeState;
    }

    public void setLikeState(int likeState) {
        this.likeState = likeState;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(content);
        parcel.writeLong(date);
        parcel.writeString(uid);
        parcel.writeString(postUid);
        parcel.writeInt(likeState);
        parcel.writeInt(commentsCount);
        parcel.writeParcelable(user,i);
    }

    public static Creator<Post> CREATOR =new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel parcel) {
            return new Post(parcel);
        }

        @Override
        public Post[] newArray(int i) {
            return new Post[i];
        }
    };
}
