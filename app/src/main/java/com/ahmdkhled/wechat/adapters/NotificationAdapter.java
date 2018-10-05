package com.ahmdkhled.wechat.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.activities.MainActivity;
import com.ahmdkhled.wechat.activities.PostActivity;
import com.ahmdkhled.wechat.model.Notification;
import com.ahmdkhled.wechat.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Ahmed Khaled on 8/19/2018.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder>{

    private final DatabaseReference root;
    private Context context;
    private ArrayList<Notification> notificationList;

    public NotificationAdapter(Context context, ArrayList<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
        root= FirebaseDatabase.getInstance().getReference().getRoot();

    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(context).inflate(R.layout.notification_row,parent,false);
        return new NotificationHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        holder.populateNotification();
    }

    @Override
    public int getItemCount() {
        if (notificationList==null)
            return 0;
        return notificationList.size();
    }

    class NotificationHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView notificationImg;
        TextView notificationBody;
        NotificationHolder(View itemView) {
            super(itemView);
            notificationImg=itemView.findViewById(R.id.notificationImg);
            notificationBody=itemView.findViewById(R.id.notificationBody);
        }

        void populateNotification(){
            final Notification notification=notificationList.get(getAdapterPosition());
            DatabaseReference usersRef=root.child("users")
                    .child(notification.getUserUid());
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user=dataSnapshot.getValue(User.class);
                    user.setUid(dataSnapshot.getKey());
                    notification.setUser(user);
                    String body=getNotificationBody(user.getName(),notification.getType());
                    notificationBody.setText(body);
                    showUserImage(user.getProfileImg());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            itemView.setOnClickListener(this);

        }
        private String getNotificationBody(String name, String type){
            String body="";

            if (type.equals("post comment")){
                body=name+" has commented on your post";
            }else if (type.equals("sent request")){
                body=name+" sent you friend request";
            }else if (type.equals("comment like")){
                body=name+" likes you Comment ";
            }

                return body;
        }

        private void showUserImage(String imageUrl){
            if (TextUtils.isEmpty(imageUrl)){
                notificationImg.setImageResource(R.drawable.user);
            }else {
                Glide.with(context).load(imageUrl).into(notificationImg);
            }
        }

        @Override
        public void onClick(View view) {
            Log.d("NNOONP","clicked "+notificationList.get(getAdapterPosition()).getType());
            String type=notificationList.get(getAdapterPosition()).getType();
            if (type.equals("post comment")){
                Intent intent=new Intent(context, PostActivity.class);
                Bundle b=new Bundle();
                String postUid= (String) notificationList.get(getAdapterPosition())
                        .getTarget().get("postUid");
                b.putString("postUid",postUid);
                intent.putExtras(b);
                context.startActivity(intent);
            }else if (type.equals("sent request")){
                Intent intent=new Intent(context, MainActivity.class);
                Bundle b=new Bundle();
                String senderUid= notificationList.get(getAdapterPosition())
                        .getUserUid();
                b.putString("senderUid",senderUid);
                b.putString("notificationType","sent request");
                intent.putExtras(b);
                context.startActivity(intent);
            }
        }
    }


}
