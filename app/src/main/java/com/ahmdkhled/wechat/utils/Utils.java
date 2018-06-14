package com.ahmdkhled.wechat.utils;

import java.util.ArrayList;

/**
 * Created by Ahmed Khaled on 6/3/2018.
 */

public class Utils {

    public static boolean isEmpty(String string){
        return string.length()==0;
    }
    public static boolean isEmpty(String name,String email,String password){
        return name.length()==0&&email.length()==0&&password.length()==0;
    }
    public static boolean isEmpty(String email,String password){
        return email.length()==0&&password.length()==0;
    }
}
