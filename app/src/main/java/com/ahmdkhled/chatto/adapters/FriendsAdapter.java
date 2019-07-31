package com.ahmdkhled.chatto.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ahmdkhled.chatto.R;
import com.ahmdkhled.chatto.model.User;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ahmed Khaled on 6/27/2018.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendHolder>{

    private ArrayList<User> users;
    private Context context;
    private OnUserClickd onUserClickd;

    public FriendsAdapter(ArrayList<User> users, Context context, OnUserClickd onUserClickd) {
        this.users = users;
        this.context = context;
        this.onUserClickd = onUserClickd;
    }

    @NonNull
    @Override
    public FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_row,parent,false);
        return new FriendHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendHolder holder, int position) {
        holder.populateData(users.get(position));
    }

    @Override
    public int getItemCount() {
        if (users==null)
            return 0;
        return users.size();
    }

    class FriendHolder extends RecyclerView.ViewHolder{
        TextView name,bio;
        CircleImageView userImg;
        FriendHolder(View itemView) {
            super(itemView);
            userImg=itemView.findViewById(R.id.friendImg_IV);
            name=itemView.findViewById(R.id.friendName_TV);
            bio=itemView.findViewById(R.id.friendBio_TV);

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
