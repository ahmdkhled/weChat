package com.ahmdkhled.wechat.fragments;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.activities.ProfileActivity;
import com.ahmdkhled.wechat.adapters.FriendReqAdapter;
import com.ahmdkhled.wechat.model.FriendReq;
import com.ahmdkhled.wechat.model.User;
import com.ahmdkhled.wechat.utils.Connection;
import com.ahmdkhled.wechat.utils.Prefs;
import com.ahmdkhled.wechat.widget.WidgetProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ahmed Khaled on 6/2/2018.
 */

public class FriendReqFragment extends Fragment implements FriendReqAdapter.OnReqestclicked {

    ArrayList<FriendReq> friendReqList;
    ArrayList<String> reqSendersList;
    RecyclerView friendReqRecycler;
    FriendReqAdapter friendReqAdapter;
    Prefs prefs;
    public static final String POS_KEY="fr_pos_key";

    int pos=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.friendreq_frag,container,false);
        friendReqList =new ArrayList<>();
        reqSendersList =new ArrayList<>();
        friendReqRecycler=v.findViewById(R.id.friendReqRecycler);

        prefs=new Prefs(getContext());

        if (savedInstanceState!=null){
            pos=savedInstanceState.getInt(POS_KEY);
        }

        if (Connection.isConnected(getContext())){
            fetchData();
        }
        return v;
    }

    void fetchData(){
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=currentUser.getUid();
        final DatabaseReference root= FirebaseDatabase.getInstance().getReference().getRoot();

        DatabaseReference friendReqRef=root.child("friendRequests").child(uid);
        friendReqRef.orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendReqList.clear();
                reqSendersList.clear();
                prefs.saveData(reqSendersList);
                updateWidget();
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    String userId=data.getKey();
                    final FriendReq friendReq = data.getValue(FriendReq.class);
                    DatabaseReference userRef = root.child("users").child(userId);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                if (user != null) {
                                    user.setUid(dataSnapshot.getKey());
                                    if (friendReq != null)
                                        friendReq.setUser(user);
                                }
                                friendReqList.add(friendReq);
                                reqSendersList.add(user.getName());
                                prefs.saveData(reqSendersList);
                                updateWidget();
                                friendReqAdapter.notifyDataSetChanged();
                                friendReqRecycler.scrollToPosition(pos);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                }
                showData(friendReqList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void showData(ArrayList<FriendReq> friendReqs) {
        friendReqAdapter=new FriendReqAdapter(getContext(),friendReqs,this);
        friendReqRecycler.setAdapter(friendReqAdapter);
        friendReqRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    void showProfile(String uid){
        Intent intent=new Intent(getContext(), ProfileActivity.class);
        intent.putExtra(ProfileActivity.PROFILE_UID_TAG,uid);
        if (getContext()!=null)
            getContext().startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ConnectivityEvent event) {
        fetchData();
    };

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
        EventBus.getDefault().register(this);
    }
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        pos=((LinearLayoutManager)friendReqRecycler.getLayoutManager()).findFirstVisibleItemPosition();
        outState.putInt(POS_KEY,pos);
        Log.d("POSSS","onsave pos "+pos);
    }


    void updateWidget(){
        if (getContext()!=null) {
            Intent intent = new Intent(getContext(), WidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids = AppWidgetManager.getInstance(getContext())
                    .getAppWidgetIds(new ComponentName(getContext(), WidgetProvider.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            getContext().sendBroadcast(intent);
        }
    }

    @Override
    public void onRequestAccepted(final int position) {
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        final String uid=currentUser.getUid();
        final String senderUid=friendReqList.get(position).getUser().getUid();
        final DatabaseReference root= FirebaseDatabase.getInstance().getReference().getRoot();
        DatabaseReference friendReqRef=root.child("friendRequests").child(uid).child(senderUid);
        friendReqRef.removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    DatabaseReference friendsRef=root.child("friends");
                    String key=friendsRef.push().getKey();
                    DatabaseReference keyRef=friendsRef.child(key);
                    Map<String,Object> friendMap=new HashMap<>();
                    friendMap.put("user1",senderUid);
                    friendMap.put("user2",uid);
                    keyRef.updateChildren(friendMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        //reqSendersList.remove(friendReqList.get(position).getUser().getName());
                                        //updateWidget();
                                        Toast.makeText(getContext(),R.string.your_are_friends_now
                                                ,Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().post(new FRAcceptEvent());
                                    }
                                }
                            });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),R.string.failed_to_accept_request,Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onImageClicked(int position) {
        String uid=friendReqList.get(position).getUser().getUid();
        showProfile(uid);
    }

    @Override
    public void onNameClicked(int position) {
        String uid=friendReqList.get(position).getUser().getUid();
        showProfile(uid);

    }



}
