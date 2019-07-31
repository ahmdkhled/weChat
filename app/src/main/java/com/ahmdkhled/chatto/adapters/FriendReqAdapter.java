package com.ahmdkhled.chatto.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmdkhled.chatto.R;
import com.ahmdkhled.chatto.model.FriendReq;
import com.ahmdkhled.chatto.utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Ahmed Khaled on 6/6/2018.
 */

public class FriendReqAdapter extends RecyclerView.Adapter<FriendReqAdapter.FriendReqHolder> {

    private Context context;
    private ArrayList<FriendReq> friendReqs;
    private OnReqestclicked onReqestClicked;

    public FriendReqAdapter(Context context, ArrayList<FriendReq> friendReqs,OnReqestclicked onReqestclicked) {
        this.context = context;
        this.friendReqs = friendReqs;
        this.onReqestClicked=onReqestclicked;
    }

    @NonNull
    @Override
    public FriendReqHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.friend_req_row,parent,false);
        return new FriendReqHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendReqHolder holder, int position) {
        if (friendReqs.get(position).getUser()!=null){
        holder.user_TV.setText(friendReqs.get(position).getUser().getName());
        String profileImg=friendReqs.get(position).getUser().getProfileImg();
        if (Utils.isEmpty(profileImg)){
            holder.userImg_IV.setImageResource(R.drawable.user);
        }else{
            Glide.with(context).load(profileImg).into(holder.userImg_IV);
        }
        }
    }

    @Override
    public int getItemCount() {
        return friendReqs.size();
    }

    class FriendReqHolder extends RecyclerView.ViewHolder{
        TextView user_TV;
        ImageView userImg_IV;
        Button accept_BU;
        FriendReqHolder(View itemView) {
            super(itemView);
            user_TV=itemView.findViewById(R.id.friendReqName_TV);
            accept_BU=itemView.findViewById(R.id.acceptRequest_BU);
            userImg_IV=itemView.findViewById(R.id.friendReqImg_IV);
            accept_BU.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onReqestClicked.onRequestAccepted(getAdapterPosition());
                }
            });

            user_TV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onReqestClicked.onNameClicked(getAdapterPosition());
                }
            });
            userImg_IV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onReqestClicked.onImageClicked(getAdapterPosition());
                }
            });
        }

    }

    public interface OnReqestclicked {
        void onRequestAccepted(int position);
        void onImageClicked(int position);
        void onNameClicked(int position);
    }


}
