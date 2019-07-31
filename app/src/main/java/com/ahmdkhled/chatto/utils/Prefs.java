package com.ahmdkhled.chatto.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ahmed Khaled on 6/12/2018.
 */

public class Prefs {
    private Context context;
    private String FRIEND_REQUEST_KEY="friend_reqq";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public Prefs(Context context){
        this.context=context;
        sharedPreferences=context.getSharedPreferences("shared",Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public  ArrayList<String> getFriendReqList(){
        ArrayList<String> friendReqs=new ArrayList<>();
        Set<String> set=sharedPreferences.getStringSet(FRIEND_REQUEST_KEY,null);
        if (set!=null){
        friendReqs.addAll(set);
        }
        return friendReqs;
    }

    public void saveData(ArrayList<String> friendReqArrayList){
        Set<String> set=new HashSet<>();
        set.addAll(friendReqArrayList);
        editor.putStringSet(FRIEND_REQUEST_KEY,set);
        editor.commit();

    }

}
