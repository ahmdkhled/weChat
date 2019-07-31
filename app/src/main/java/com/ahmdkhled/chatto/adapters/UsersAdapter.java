package com.ahmdkhled.chatto.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmdkhled.chatto.R;
import com.ahmdkhled.chatto.model.Friend;
import com.ahmdkhled.chatto.model.Notification;
import com.ahmdkhled.chatto.model.User;
import com.bumptech.glide.Glide;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ahmed Khaled on 7/30/2018.
 */

public class UsersAdapter  extends RecyclerView.Adapter<UsersAdapter.UserHolder>{

    private Context context;
    private ArrayList<User> usersList;
    private OnItemClickListener onItemClickListener;
    private static final int ACCEPT_STATE=2;
    private static final int ADD_STATE=1;
    private static final int CANCEL_REQUEST_STATE=4;
    private static final int FRIENDS_STATE=3;
    private DatabaseReference root;

    public UsersAdapter(Context context, ArrayList<User> usersList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.usersList = usersList;
        this.onItemClickListener = onItemClickListener;
        root= FirebaseDatabase.getInstance().getReference().getRoot();

    }


    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.users_list_row,parent,false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        User user=usersList.get(position);
        holder.userName.setText(user.getName());
        if (TextUtils.isEmpty(user.getProfileImg())){
            holder.userImg.setImageResource(R.drawable.user);
        }else{
            Glide.with(context).load(user.getProfileImg()).into(holder.userImg);
        }

        handleAddButton(user.getUid(),position,holder);

    }



    @Override
    public int getItemCount() {
        if (usersList==null)
            return 0;
        return usersList.size();
    }

    class UserHolder extends RecyclerView.ViewHolder{
        TextView userName;
        ImageView userImg;
        Button add;
        UserHolder(View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.userName);
            userImg=itemView.findViewById(R.id.userImage);
            add=itemView.findViewById(R.id.add);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uid=usersList.get(getAdapterPosition()).getUid();
                    if (usersList.get(getAdapterPosition()).getFriendShipState()==ADD_STATE){
                        addFriend(uid);
                    }else if (usersList.get(getAdapterPosition()).getFriendShipState()==CANCEL_REQUEST_STATE){
                        cancelRequest(uid);
                    }else if (usersList.get(getAdapterPosition()).getFriendShipState()==ACCEPT_STATE){
                        acceptRequest(uid);
                    }else if (usersList.get(getAdapterPosition()).getFriendShipState()==FRIENDS_STATE){
                        unFriend(uid);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClicked(getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(int pos);
    }

    private void handleAddButton(final String uid,final int pos,UserHolder holder){
        usersList.get(pos).setFriendShipState(-2);
        final Button addBU=holder.add;
        //addBU.setEnabled(false);
        addBU.setText("...");

        final DatabaseReference friendReqRef = root.child("friendRequests");
        friendReqRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(uid) && dataSnapshot.child(uid).hasChild(getUserUid())) {
                    addBU.setEnabled(true);
                    addBU.setText(R.string.cancel_request);
                    usersList.get(pos).setFriendShipState(CANCEL_REQUEST_STATE);
                } else if (dataSnapshot.hasChild(getUserUid()) && dataSnapshot.child(getUserUid()).hasChild(uid)) {
                    addBU.setEnabled(true);
                    addBU.setText(R.string.accept);
                    usersList.get(pos).setFriendShipState(ACCEPT_STATE);
                } else {
                    DatabaseReference friendsRef = root.child("friends");
                    friendsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            usersList.get(pos).setFriendShipState(-2);
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Friend friend = data.getValue(Friend.class);
                                String user1 = friend.getUser1();
                                String user2 = friend.getUser2();
                                if ((user1.equals(uid) && user2.equals(getUserUid())) ||
                                        (user1.equals(getUserUid()) && user2.equals(uid))) {
                                    Log.d("FRIENDSHIP", "friendssssss......"+user1+"---"+user2);
                                    usersList.get(pos).setFriendShipState(FRIENDS_STATE);
                                    break;
                                }else {
                                    usersList.get(pos).setFriendShipState(ADD_STATE);
                                }
                            }
                            if (usersList.get(pos).getFriendShipState()==FRIENDS_STATE) {
                                Log.d("FRIENDSHIP", "friends");
                                addBU.setEnabled(true);
                                addBU.setText("friends");
                            }else if (usersList.get(pos).getFriendShipState()==ADD_STATE ||dataSnapshot.getChildrenCount()==0){
                                usersList.get(pos).setFriendShipState(ADD_STATE);
                                addBU.setEnabled(true);
                                addBU.setText("add");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            usersList.get(pos).setFriendShipState(-1);
                            Log.d("FRIENDSHIP", "" + databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FRIENDSHIP", "" + databaseError.getMessage());
                usersList.get(pos).setFriendShipState(-1);
            }
        });
        }

    private void addFriend(final String uid){
        final DatabaseReference root=FirebaseDatabase.getInstance().getReference().getRoot();
        DatabaseReference uidRef=root.child("friendRequests").child(uid).child(getUserUid());
        Map<String,Object> reqMap =new HashMap<>();
        reqMap.put("date",-System.currentTimeMillis());
        uidRef.updateChildren(reqMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            DatabaseReference notificationRef=root.child("notifications")
                                    .child(uid).push();
                            Notification notification=new Notification(getUserUid(),"sent request"
                                    ,System.currentTimeMillis());
                            notificationRef.setValue(notification)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(context, R.string.request_sent
                                                        ,Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void cancelRequest(String uid){
        DatabaseReference friendReqRef=root.child("friendRequests").child(uid).child(getUserUid());
        friendReqRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, R.string.request_cancelled,Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, R.string.failed_to_cancel_request,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unFriend(final String uid){
        final DatabaseReference friendRef=root.child("friends");
        friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    Friend friend=data.getValue(Friend.class);
                    String user1 = friend.getUser1();
                    String user2 = friend.getUser2();
                    if ((user1.equals(uid) && user2.equals(getUserUid())) ||
                            (user1.equals(getUserUid()) && user2.equals(uid))) {
                        friendRef.child(data.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(context, R.string.unfriend,Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context,R.string.failled+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void acceptRequest(final String uid){
        DatabaseReference friendReqRef=root.child("friendRequests").child(getUserUid()).child(uid);
        friendReqRef.removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            DatabaseReference friendsRef=root.child("friends");
                            String key=friendsRef.push().getKey();
                            DatabaseReference keyRef=friendsRef.child(key);
                            Map<String,Object> friendMap=new HashMap<>();
                            friendMap.put("user1",uid);
                            friendMap.put("user2",getUserUid());
                            keyRef.updateChildren(friendMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(context, R.string.your_are_friends_now,Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, R.string.failed_to_accept_request,Toast.LENGTH_SHORT).show();

            }
        });
    }

    private String getUserUid(){
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        return currentUser.getUid();
    }


    }


