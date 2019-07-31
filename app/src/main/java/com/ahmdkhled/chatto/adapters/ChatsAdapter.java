package com.ahmdkhled.chatto.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmdkhled.chatto.R;
import com.ahmdkhled.chatto.activities.ChatActivity;
import com.ahmdkhled.chatto.model.Chat;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Ahmed Khaled on 8/6/2018.
 */

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatHolder>{

    private Context context;
    private ArrayList<Chat> chatList;

    public ChatsAdapter(Context context, ArrayList<Chat> chatList) {
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uid =chatList.get(getAdapterPosition()).getUser().getUid();
                    Log.d("CHATT","from adapter is : " +uid);
                    Intent intent=new Intent(context, ChatActivity.class);
                    intent.putExtra(ChatActivity.USER_TAG,chatList.get(getAdapterPosition()).getUser());
                    context.startActivity(intent);
                }
            });
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
