package com.ahmdkhled.wechat.utils;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Ahmed Khaled on 6/4/2018.
 */

public class Encryption {

    private static final String ALGORITHM = "AES";

    public static String encrypt(String UID,String value)
    {
        try {
        Key key = generateKey(getKeyFromUID(UID));
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        String encryptedValue64 = Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
        return encryptedValue64;

    }catch (Exception e) {
            e.printStackTrace();
            Log.d("ENCRYPTT","encryption error "+e.getMessage());

            return null;
        }
    }

    public static String decrypt(String UID,String value)
    {
        try {
        Key key = generateKey(getKeyFromUID(UID));
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue64 = Base64.decode(value, Base64.DEFAULT);
        byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
        String decryptedValue = new String(decryptedByteValue,"utf-8");
        return decryptedValue;
        }catch (Exception e) {
            e.printStackTrace();
            Log.d("ENCRYPTT","decryption error "+e.getMessage());
            return null;
        }

    }

    private static Key generateKey(String KEY)
    {
        Key key = new SecretKeySpec(KEY.getBytes(),ALGORITHM);
        return key;
    }

    public static String getKeyFromUID(String uid){
        return uid.substring(0,16);
    }

}
