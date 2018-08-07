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
import com.ahmdkhled.wechat.model.Chat;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Ahmed Khaled on 8/6/2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder>{

    private Context context;
    private ArrayList<Chat> chatList;

    public ChatAdapter(Context context, ArrayList<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(context).inflate(R.layout.chat_row,parent,false);
        return new ChatHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        holder.populateChat(position);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder{
        ImageView chatImg;
        TextView chatUserName,chatLastMessage;
        ChatHolder(View itemView) {
            super(itemView);
            chatImg=itemView.findViewById(R.id.chatImg);
            chatUserName=itemView.findViewById(R.id.chatUserName);
            chatLastMessage=itemView.findViewById(R.id.chatLastMessage);
        }
        void populateChat(int pos){
            if (chatList.get(pos).getUser()!=null){
                chatUserName.setText(chatList.get(pos).getUser().getName());
                if (TextUtils.isEmpty(chatList.get(pos).getUser().getProfileImg())){
                    chatImg.setImageResource(R.drawable.user);
                }else {
                    Glide.with(context).load(chatList.get(pos).getUser().getProfileImg()).into(chatImg);
                }
            }
            if (chatList.get(pos).getLastMessage()!=null){
                chatLastMessage.setText(chatList.get(pos).getLastMessage());
            }
        }
    }
}
