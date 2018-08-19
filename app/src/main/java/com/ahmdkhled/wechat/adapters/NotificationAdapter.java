package com.ahmdkhled.wechat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.model.Notification;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Ahmed Khaled on 8/19/2018.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder>{

    private Context context;
    private ArrayList<Notification> notificationList;

    public NotificationAdapter(Context context, ArrayList<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
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

    class NotificationHolder extends RecyclerView.ViewHolder{
        ImageView notificationImg;
        TextView notificationBody;
        NotificationHolder(View itemView) {
            super(itemView);
            notificationImg=itemView.findViewById(R.id.notificationImg);
            notificationBody=itemView.findViewById(R.id.notificationBody);
        }

        void populateNotification(){
            notificationBody.setText(notificationList.get(getAdapterPosition()).getBody());
            if (!TextUtils.isEmpty(notificationList.get(getAdapterPosition()).getImage())){
                Glide.with(context).load(notificationList.get(getAdapterPosition()).getImage())
                        .into(notificationImg);
            }
        }
    }
}
