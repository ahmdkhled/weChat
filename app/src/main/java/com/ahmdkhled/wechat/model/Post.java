package com.ahmdkhled.wechat.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

/**
 * Created by Ahmed Khaled on 6/3/2018.
 */

public class Post implements Parcelable{

    private String content;
    private long date;
    private String authorUid;
    private String uid;
    private User user;
    private int likeState;
    private long commentsCount;

    public Post(String authorUid, String content, long date, User user ) {
        this.content = content;
        this.date = date;
        this.authorUid = authorUid;
        this.user = user;
    }

    public Post(String content, long date, String authorUid) {
        this.content = content;
        this.date = date;
        this.authorUid = authorUid;
    }

    public Post() {
    }

    public Post(Parcel parcel) {
        content=parcel.readString();
        date=parcel.readLong();
        authorUid =parcel.readString();
        uid =parcel.readString();
        likeState=parcel.readInt();
        commentsCount=parcel.readLong();
        user=parcel.readParcelable(User.class.getClassLoader());
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {return date;}

    public String getAuthorUid() {
        return authorUid;
    }

    public void setAuthorUid(String authorUid) {
        this.authorUid = authorUid;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Exclude
    public int getLikeState() {
        return likeState;
    }

    public void setLikeState(int likeState) {
        this.likeState = likeState;
    }

    @Exclude
    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(long commentsCount) {
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
        parcel.writeString(authorUid);
        parcel.writeString(uid);
        parcel.writeInt(likeState);
        parcel.writeLong(commentsCount);
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
