package com.ahmdkhled.wechat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.model.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by Ahmed Khaled on 8/7/2018.
 */

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<Message> messagesList;
    public static final int SENT_MESSAGE_TYPE=1;
    public static final int RECEIVED_MESSAGE_TYPE=2;

    public MessagesAdapter(Context context, ArrayList<Message> messagesList) {
        this.context = context;
        this.messagesList = messagesList;
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
            sentMessageHolder.message.setText(messagesList.get(position).getContent());
        }else if (getItemViewType(position)==RECEIVED_MESSAGE_TYPE){
            ReceivedMessageHolder receivedMessageHolder= (ReceivedMessageHolder) holder;
            receivedMessageHolder.message.setText(messagesList.get(position).getContent());
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
        public SentMessageHolder(View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.sentMessage);
        }
    }
    class ReceivedMessageHolder extends RecyclerView.ViewHolder{
        TextView message;
        public ReceivedMessageHolder(View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.receivedMessage);
        }
    }

    private String getcurrentUserUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}
