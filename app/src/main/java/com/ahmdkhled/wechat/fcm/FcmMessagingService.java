package com.ahmdkhled.wechat.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ahmdkhled.wechat.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Ahmed Khaled on 8/21/2018.
 */

public class FcmMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FCMM","message "+remoteMessage.getNotification().getBody());
        String title=remoteMessage.getNotification().getTitle();
        String body=remoteMessage.getNotification().getBody();
        showNotification(title,body);
    }

    void showNotification(String title,String body){
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationCompat.Builder nBuilder=new NotificationCompat.Builder(getApplicationContext(),"1");
        nBuilder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.received_message_bg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "fcm_channel";
            String description = getString(R.string.channelDescription);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(144,nBuilder.build());
    }
}
