package com.ahmdkhled.wechat.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.activities.MainActivity;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Ahmed Khaled on 6/19/2018.
 */

public class NotificationService extends JobService{

    private static final String CHANNEL_ID="120";

    @Override
    public boolean onStartJob(JobParameters job) {
        createNotificationChannel();
        NotificationCompat.Builder nBuilder=new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);

        nBuilder.setSmallIcon(R.drawable.ic_launcher_background);
        nBuilder.setContentText("hey!! write a post now and share it with your friends");
        nBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),222,
                new Intent(getApplicationContext(),MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager= getNotificationManager();
        notificationManager.notify(11,nBuilder.build());
        Log.d("TAGG","job started");

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    private void createNotificationChannel(){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"reminder notification channel"
                    ,NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager= getNotificationManager();
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }

        }
    }

    private NotificationManager getNotificationManager(){
       return (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
    }

}
