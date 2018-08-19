package com.ahmdkhled.wechat.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahmdkhled.wechat.R;

/**
 * Created by Ahmed Khaled on 8/19/2018.
 */

public class NotificationFragment extends Fragment {

    RecyclerView notificationRecycler;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.notification_fragment,container,false);
        notificationRecycler=v.findViewById(R.id.notificationRecycler);
        return v;
    }


}
