package com.ahmdkhled.wechat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.model.User;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ahmed Khaled on 6/27/2018.
 */

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UsersHolder>{

    private ArrayList<User> users;
    private Context context;
    private OnUserClickd onUserClickd;

    public UsersListAdapter(ArrayList<User> users, Context context, OnUserClickd onUserClickd) {
        this.users = users;
        this.context = context;
        this.onUserClickd = onUserClickd;
    }

    @NonNull
    @Override
    public UsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list_row,parent,false);
        return new UsersHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersHolder holder, int position) {
        holder.populateData(users.get(position));
    }

    @Override
    public int getItemCount() {
        if (users==null)
            return 0;
        return users.size();
    }

    class UsersHolder extends RecyclerView.ViewHolder{
        TextView name,bio;
        CircleImageView userImg;
        UsersHolder(View itemView) {
            super(itemView);
            userImg=itemView.findViewById(R.id.userImg_IV);
            name=itemView.findViewById(R.id.userName_TV);
            bio=itemView.findViewById(R.id.userBio_TV);

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onUserClickd.onUserClicked(getAdapterPosition());
                }
            });
            userImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onUserClickd.onUserClicked(getAdapterPosition());
                }
            });
        }

        void populateData(User user){
            if (TextUtils.isEmpty(user.getProfileImg())){
                userImg.setImageResource(R.drawable.user);
            }else{
                Glide.with(context).load(user.getProfileImg()).into(userImg);
            }
            name.setText(user.getName());
            bio.setText(user.getBio());
        }
    }

    public interface OnUserClickd{
        void onUserClicked(int position);
    }

}
