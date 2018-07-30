package com.ahmdkhled.wechat.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.model.User;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Ahmed Khaled on 7/30/2018.
 */

public class UsersAdapter  extends RecyclerView.Adapter<UsersAdapter.UserHolder>{

    private Context context;
    private ArrayList<User> usersList;
    private OnItemClickListener onItemClickListener;

    public UsersAdapter(Context context, ArrayList<User> usersList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.usersList = usersList;
        this.onItemClickListener = onItemClickListener;
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
                    onItemClickListener.onAddClicked(getAdapterPosition());
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
        void onAddClicked(int pos);
        void onItemClicked(int pos);
    }
}
