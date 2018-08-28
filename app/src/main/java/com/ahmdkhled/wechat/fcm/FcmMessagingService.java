package com.ahmdkhled.wechat.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.activities.MainActivity;
import com.ahmdkhled.wechat.activities.PostActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Ahmed Khaled on 8/21/2018.
 */

public class FcmMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("NORITFF","message "+remoteMessage.getData());
        String title=remoteMessage.getNotification().getTitle();
        String body=remoteMessage.getNotification().getBody();
        Intent intent=null;
        Map<String,String> data=remoteMessage.getData();
        if (data.size()>0){
            Bundle bundle=new Bundle();
            if (data.get("notificationType").equals("post comment")){
                bundle.putString("postUid",data.get("postUid"));
                bundle.putString("notificationType",data.get("notificationType"));
                intent=new Intent(this, PostActivity.class);
                intent.putExtras(bundle);
            }if (data.get("notificationType").equals("sent request")){
                bundle.putString("notificationType",data.get("notificationType"));
                intent=new Intent(this, MainActivity.class);
                intent.putExtras(bundle);
            }
        }

        showNotification(title,body,intent);
    }

    void showNotification(String title, String body, Intent intent){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "fcm_channel";
            String description = getString(R.string.channelDescription);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
        PendingIntent pendingIntent=PendingIntent.getActivity(this,211
                ,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder nBuilder=new NotificationCompat.Builder(getApplicationContext(),"1");
        nBuilder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.sent_message_bg)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(174,nBuilder.build());
        }
    }
}
