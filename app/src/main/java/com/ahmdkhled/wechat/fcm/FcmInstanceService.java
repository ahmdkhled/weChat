package com.ahmdkhled.wechat.fcm;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Ahmed Khaled on 8/21/2018.
 */

public class FcmInstanceService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String token= FirebaseInstanceId.getInstance().getToken();
        Log.d("FCMM", "Refreshed token: " + token);
        if(getCurrentUserUid()!=null){
            updateToken(token);
        }


    }

    void updateToken(String token){
        DatabaseReference root= FirebaseDatabase.getInstance().getReference().getRoot();
        DatabaseReference tokenRef=root.child("users")
                .child(getCurrentUserUid())
                .child("notification_token");
        tokenRef.setValue(token);
    }
    String getCurrentUserUid(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            return user.getUid();
        }
        return null;
    }

}
