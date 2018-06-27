package com.ahmdkhled.wechat.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.model.FriendReq;
import com.ahmdkhled.wechat.utils.Prefs;

import java.util.ArrayList;

/**
 * Created by Ahmed Khaled on 6/12/2018.
 */

public class WidgetAdapter implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<String> friendReqList;
    private Context context;
    Prefs prefs;
    public static final String ITEM_CLICK_ACTION="item_click_action";

    public WidgetAdapter(Context context) {
        this.context = context;
        prefs=new Prefs(context);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        friendReqList= prefs.getFriendReqList();
        Log.d("WIDGETT","on data change ");

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (friendReqList==null)
            return 0;
        return friendReqList.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews remoteViews=new RemoteViews(context.getPackageName(), R.layout.friendreq_row_widget);
        remoteViews.setTextViewText(R.id.widget_friendReqSender_TV,friendReqList.get(i));
        remoteViews.setTextViewText(R.id.widget_friendReqLetter_TV,friendReqList.get(i).charAt(0)+"");
        Intent intent=new Intent();
        intent.setAction(ITEM_CLICK_ACTION);
        remoteViews.setOnClickFillInIntent(R.id.friendReq_row_item,intent);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
