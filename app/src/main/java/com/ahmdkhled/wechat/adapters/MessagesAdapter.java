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
import com.ahmdkhled.wechat.model.Message;
import com.ahmdkhled.wechat.model.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ahmed Khaled on 8/7/2018.
 */

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<Message> messagesList;
    private User user;
    private static final int SENT_MESSAGE_TYPE=1;
    private static final int RECEIVED_MESSAGE_TYPE=2;


    public MessagesAdapter(Context context, ArrayList<Message> messagesList, User user) {
        this.context = context;
        this.messagesList = messagesList;
        this.user = user;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==SENT_MESSAGE_TYPE){
            View row= LayoutInflater.from(context).inflate(R.layout.sent_message_row,parent,false);
            return new SentMessageHolder(row);
        }else  if (viewType==RECEIVED_MESSAGE_TYPE){
            View row= LayoutInflater.from(context).inflate(R.layout.received_message_row,parent,false);
            return new ReceivedMessageHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position)==SENT_MESSAGE_TYPE){
            SentMessageHolder sentMessageHolder= (SentMessageHolder) holder;
            sentMessageHolder.populateMessage();
        }else if (getItemViewType(position)==RECEIVED_MESSAGE_TYPE){
            ReceivedMessageHolder receivedMessageHolder= (ReceivedMessageHolder) holder;
            receivedMessageHolder.populateMessage();
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messagesList.get(position).getSenderUid().equals(getcurrentUserUid())){
            return SENT_MESSAGE_TYPE;
        }
        return RECEIVED_MESSAGE_TYPE;
    }

    class SentMessageHolder extends RecyclerView.ViewHolder{
        TextView message;
        CircleImageView seenImg;
        SentMessageHolder(View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.sentMessage);
            seenImg=itemView.findViewById(R.id.seenImg);
        }
        void populateMessage(){
            message.setText(messagesList.get(getAdapterPosition()).getContent());
            if (messagesList.get(getAdapterPosition()).isSeen()&&getAdapterPosition()==messagesList.size()-1){
                Glide.with(context).load(user.getProfileImg())
                        .apply(new RequestOptions().override(25,25)
                        .centerCrop())
                        .into(seenImg);
            }
        }
    }
    class ReceivedMessageHolder extends RecyclerView.ViewHolder{
        TextView message;
        CircleImageView messageSenderImg;
        ReceivedMessageHolder(View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.receivedMessage);
            messageSenderImg=itemView.findViewById(R.id.messageSenderImg);
        }

        void populateMessage(){
            message.setText(messagesList.get(getAdapterPosition()).getContent());
            if (user!=null){
                if (TextUtils.isEmpty(user.getProfileImg())){
                    messageSenderImg.setImageResource(R.drawable.user);
                }else {
                    Glide.with(context).load(user.getProfileImg()).into(messageSenderImg);
                }
            }
        }
    }

    private String getcurrentUserUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}
