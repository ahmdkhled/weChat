package com.ahmdkhled.wechat.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Ahmed Khaled on 6/12/2018.
 */

public class WidgetService extends RemoteViewsService{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetAdapter(this);
    }
}
