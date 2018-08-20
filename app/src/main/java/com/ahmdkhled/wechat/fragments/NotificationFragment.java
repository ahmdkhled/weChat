package com.ahmdkhled.wechat.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.adapters.NotificationAdapter;
import com.ahmdkhled.wechat.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

/**
 * Created by Ahmed Khaled on 8/19/2018.
 */

public class NotificationFragment extends Fragment {

    RecyclerView notificationRecycler;
    DividerItemDecoration dividerItemDecoration;
    NotificationAdapter notificationAdapter;
    DatabaseReference root;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.notification_fragment,container,false);
        root= FirebaseDatabase.getInstance().getReference().getRoot();
        notificationRecycler=v.findViewById(R.id.notificationRecycler);
        dividerItemDecoration=new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);

        fetchNotification();

        return v;
    }

    void fetchNotification(){
        final ArrayList<Notification> notificationList=new ArrayList<>();
        Query notificationsRef =root.child("notifications").child(getCurrentUserUid())
                .orderByChild("date");
        notificationsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Notification notification=dataSnapshot.getValue(Notification.class);
                notification.setId(dataSnapshot.getKey());
                notificationList.add(0,notification);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        showNotification(notificationList);
    }

    void showNotification(ArrayList<Notification> notificationList){
        notificationAdapter=new NotificationAdapter(getContext(),notificationList);
        notificationRecycler.setAdapter(notificationAdapter);
        notificationRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationRecycler.removeItemDecoration(dividerItemDecoration);
        notificationRecycler.addItemDecoration(dividerItemDecoration);

    }

    private String getCurrentUserUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


}
