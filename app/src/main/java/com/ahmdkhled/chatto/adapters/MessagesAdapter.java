package com.ahmdkhled.chatto.adapters;

import android.content.Context;
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
import com.ahmdkhled.chatto.model.Message;
import com.ahmdkhled.chatto.model.User;
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
    private static final int SENT_TEXT_MESSAGE_TYPE =1;
    private static final int RECEIVED_TEXT_MESSAGE_TYPE =2;
    private static final int SENT_IMAGE_MESSAGE_TYPE =3;
    private static final int RECEIVED_IMAGE_MESSAGE_TYPE =4;


    public MessagesAdapter(Context context, ArrayList<Message> messagesList, User user) {
        this.context = context;
        this.messagesList = messagesList;
        this.user = user;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("VIEWTYPEE",""+viewType);
        if (viewType== SENT_TEXT_MESSAGE_TYPE){
            View row= LayoutInflater.from(context).inflate(R.layout.sent_message_row,parent,false);
            return new SentTextMessageHolder(row);
        }else  if (viewType== RECEIVED_TEXT_MESSAGE_TYPE){
            View row= LayoutInflater.from(context).inflate(R.layout.received_message_row,parent,false);
            return new ReceivedTextMessageHolder(row);
        }
        if (viewType== SENT_IMAGE_MESSAGE_TYPE){
            View row= LayoutInflater.from(context).inflate(R.layout.sent_image_message_row,parent,false);
            return new SentImageMessageHolder(row);
        }else  if (viewType== RECEIVED_IMAGE_MESSAGE_TYPE){
            View row= LayoutInflater.from(context).inflate(R.layout.received_image_message_row,parent,false);
            return new ReceivedImageMessageHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position)== SENT_TEXT_MESSAGE_TYPE){
            SentTextMessageHolder sentTextMessageHolder = (SentTextMessageHolder) holder;
            sentTextMessageHolder.populateMessage();
        }else if (getItemViewType(position)== RECEIVED_TEXT_MESSAGE_TYPE){
            ReceivedTextMessageHolder receivedTextMessageHolder = (ReceivedTextMessageHolder) holder;
            receivedTextMessageHolder.populateMessage();
        }else if (getItemViewType(position)== SENT_IMAGE_MESSAGE_TYPE){
            SentImageMessageHolder sentImageMessageHolder = (SentImageMessageHolder) holder;
            sentImageMessageHolder.populateMessage();
        }else if (getItemViewType(position)== RECEIVED_IMAGE_MESSAGE_TYPE){
            ReceivedImageMessageHolder receivedImageMessageHolder = (ReceivedImageMessageHolder) holder;
            receivedImageMessageHolder.populateMessage();
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messagesList.get(position).getSenderUid().equals(getcurrentUserUid())){
            if (TextUtils.isEmpty(messagesList.get(position).getImageUrl())){
                return SENT_TEXT_MESSAGE_TYPE;
            }else {
                return SENT_IMAGE_MESSAGE_TYPE;
            }
        }else {
            if (TextUtils.isEmpty(messagesList.get(position).getImageUrl())){
                return RECEIVED_TEXT_MESSAGE_TYPE;
            }else {
                return RECEIVED_IMAGE_MESSAGE_TYPE;
            }
        }
    }

    class SentTextMessageHolder extends RecyclerView.ViewHolder{
        TextView message;
        CircleImageView seenImg;
        SentTextMessageHolder(View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.sentMessage);
            seenImg=itemView.findViewById(R.id.seenImg);
        }
        void populateMessage(){
            message.setText(messagesList.get(getAdapterPosition()).getContent());
            if (messagesList.get(getAdapterPosition()).isSeen()&&getAdapterPosition()==messagesList.size()-1){
                if (TextUtils.isEmpty(user.getProfileImg())){
                    seenImg.setImageResource(R.drawable.user);
                }else {
                Glide.with(context).load(user.getProfileImg())
                        .apply(new RequestOptions().override(25,25)
                        .centerCrop())
                        .into(seenImg);
                }
            }
        }
    }

    class ReceivedTextMessageHolder extends RecyclerView.ViewHolder{
        TextView message;
        CircleImageView messageSenderImg;
        ReceivedTextMessageHolder(View itemView) {
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

    class SentImageMessageHolder extends RecyclerView.ViewHolder{
        ImageView sentImage;
        CircleImageView seenImg;
        public SentImageMessageHolder(View itemView) {
            super(itemView);
            sentImage=itemView.findViewById(R.id.sentImageMessage);
            seenImg=itemView.findViewById(R.id.isSeenImg);
        }

        void populateMessage(){
            if (messagesList.get(getAdapterPosition()).getImageUrl()!=null){
                Glide.with(context).load(messagesList.get(getAdapterPosition()).getImageUrl())
                        .into(sentImage);
            }
            if (messagesList.get(getAdapterPosition()).isSeen()){
                if (TextUtils.isEmpty(user.getProfileImg())){
                    seenImg.setImageResource(R.drawable.user);
                }else {
                    Glide.with(context).load(user.getProfileImg()).into(seenImg);
                }
            }
        }
    }

    class ReceivedImageMessageHolder extends RecyclerView.ViewHolder{
        ImageView receivedMessage;
        public ReceivedImageMessageHolder(View itemView) {
            super(itemView);
            receivedMessage=itemView.findViewById(R.id.receivedImageMessage);
        }

        void populateMessage(){
            if (messagesList.get(getAdapterPosition()).getImageUrl()!=null){
                Glide.with(context).load(messagesList.get(getAdapterPosition()).getImageUrl())
                        .into(receivedMessage);
            }
        }
    }

    private String getcurrentUserUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}
