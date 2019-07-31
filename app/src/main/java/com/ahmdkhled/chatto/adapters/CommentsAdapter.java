package com.ahmdkhled.chatto.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmdkhled.chatto.R;
import com.ahmdkhled.chatto.model.Comment;
import com.ahmdkhled.chatto.model.Notification;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ahmed Khaled on 8/5/2018.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentHolder>{

    private Context context;
    private ArrayList<Comment> commentsList;
    private String postUid;
    private DatabaseReference root;
    private static final int LIKED_STATE=1;
    private static final int UNLIKED_STATE=2;

    public CommentsAdapter(Context context, ArrayList<Comment> commentsList,String postUid) {
        this.context = context;
        this.commentsList = commentsList;
        this.postUid=postUid;
        root = FirebaseDatabase.getInstance().getReference().getRoot();
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.comment_row,parent,false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        holder.populateComment(position,holder);
    }

    @Override
    public int getItemCount() {
        if (commentsList==null)
            return 0;
        return commentsList.size();
    }

    class CommentHolder extends RecyclerView.ViewHolder{
        ImageView authorImg;
        TextView author,content;
        Button like;

        CommentHolder(View itemView) {
            super(itemView);
            author=itemView.findViewById(R.id.commentAuthor);
            authorImg=itemView.findViewById(R.id.commentAuthorImg);
            content=itemView.findViewById(R.id.commentContent);
            like=itemView.findViewById(R.id.likeComment);

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (commentsList.get(getAdapterPosition()).getLikeState()==UNLIKED_STATE){
                        likeComment(getAdapterPosition());
                    }else if (commentsList.get(getAdapterPosition()).getLikeState()==LIKED_STATE){
                        unLikeComment(getAdapterPosition());
                    }
                }
            });
        }


        void populateComment(int position, CommentHolder holder){
            if (commentsList.get(position).getContent()!=null){
                content.setText(commentsList.get(position).getContent());
            }
            if (commentsList.get(position).getUser()!=null) {
                author.setText(commentsList.get(position).getUser().getName());
                Glide.with(context).load(commentsList.get(position).getUser().getProfileImg()).into(authorImg);
            }
            handleLikeButton(position,holder);
        }
    }

    private void handleLikeButton(final int pos, final CommentHolder holder){
        DatabaseReference commentLikeRef=root.child("commentsLikes");
        commentLikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String commentUid=commentsList.get(pos).getUid();
                if (dataSnapshot.hasChild(commentUid)&&dataSnapshot.child(commentUid).hasChild(getUserUid())){
                        commentsList.get(pos).setLikeState(LIKED_STATE);
                        holder.like.setTextColor(Color.parseColor("#03A9F4"));
                }else {
                    commentsList.get(pos).setLikeState(UNLIKED_STATE);
                    holder.like.setTextColor(Color.parseColor("#424242"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void likeComment(final int pos){
        DatabaseReference commentLikeRef=root.child("commentsLikes")
                .child(commentsList.get(pos).getUid())
                .child(getUserUid());
        HashMap<String,Object> likeMap=new HashMap<>();
        likeMap.put("date",-System.currentTimeMillis());
        commentLikeRef.updateChildren(likeMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        showNotification(pos);
                    }
                });
    }
    private void unLikeComment(int pos){
        DatabaseReference commentLikeRef=root.child("commentsLikes")
                .child(commentsList.get(pos).getUid())
                .child(getUserUid());
        commentLikeRef.removeValue();
    }

    private void showNotification(int pos){
        DatabaseReference notificationRef=root.child("notifications")
                .child(commentsList.get(pos).getAuthorUid())
                .push();
        Notification notification=new Notification(getUserUid()
                ,"comment like",System.currentTimeMillis());
        HashMap<String,Object> target=new HashMap<>();
        target.put("postUid",postUid);
        target.put("commentUid",commentsList.get(pos).getUid());
        notification.setTarget(target);
        notificationRef.setValue(notification);

    }

    private String getUserUid(){
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        return currentUser.getUid();
    }

}
