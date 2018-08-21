package com.ahmdkhled.wechat.fcm;

import android.util.Log;

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

    }

}
