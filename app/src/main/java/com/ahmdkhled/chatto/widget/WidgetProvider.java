package com.ahmdkhled.chatto.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.ahmdkhled.chatto.R;
import com.ahmdkhled.chatto.activities.MainActivity;

/**
 * Created by Ahmed Khaled on 6/12/2018.
 */

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(WidgetAdapter.ITEM_CLICK_ACTION)){
            Intent i=new Intent(context,MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        int[] myAppWidgetIds =AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context,WidgetProvider.class));
        for (int id:myAppWidgetIds){
            RemoteViews remoteViews=new RemoteViews(context.getPackageName(), R.layout.widget);
            Intent serviceIntent=new Intent(context,WidgetService.class);
            remoteViews.setRemoteAdapter(R.id.widget_listView,serviceIntent);
            Log.d("WIDGETT","on update ");

            Intent intent=new Intent(context,WidgetProvider.class);
            intent.setAction(WidgetAdapter.ITEM_CLICK_ACTION);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,myAppWidgetIds);
            PendingIntent pendingIntent=PendingIntent.getBroadcast(context,151,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widget_listView,pendingIntent);
            appWidgetManager.notifyAppWidgetViewDataChanged(id,R.id.widget_listView);
            appWidgetManager.updateAppWidget(id,remoteViews);
        }
    }
}
