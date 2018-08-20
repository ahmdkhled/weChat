package com.ahmdkhled.wechat.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.activities.CommentsActivity;
import com.ahmdkhled.wechat.model.Post;
import com.ahmdkhled.wechat.model.User;
import com.ahmdkhled.wechat.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ahmed Khaled on 6/24/2018.
 */

public class ProfilePostsAdapter extends RecyclerView.Adapter<ProfilePostsAdapter.PostHolder>{
    private ArrayList<Post> posts;
    private Context context;
    private DatabaseReference root;
    private static final int LIKED_STATE=1;
    private static final int UNLIKED_STATE=2;

    public ProfilePostsAdapter(ArrayList<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
        root = FirebaseDatabase.getInstance().getReference().getRoot();
    }


    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(context).inflate(R.layout.post_row,parent,false);
        return new PostHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.populateData(position,holder);
    }

    @Override
    public int getItemCount() {
        if (posts==null) return 0;
        return posts.size();
    }


    class PostHolder extends RecyclerView.ViewHolder{
        TextView postContent,author,commentsCount;
        ImageView profileImg;
        Button like,comment;
        PostHolder(View itemView) {
            super(itemView);
            postContent=itemView.findViewById(R.id.postContent_TV);
            author=itemView.findViewById(R.id.postAuthor_TV);
            commentsCount=itemView.findViewById(R.id.commentsCount);
            profileImg=itemView.findViewById(R.id.postImg_IV);
            like=itemView.findViewById(R.id.like_BU);
            comment=itemView.findViewById(R.id.comment_BU);

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (posts.get(getAdapterPosition()).getLikeState()==UNLIKED_STATE){
                        likePost(posts.get(getAdapterPosition()).getUid());
                    }else if (posts.get(getAdapterPosition()).getLikeState()==LIKED_STATE){
                        unLikePost(posts.get(getAdapterPosition()).getUid());
                    }
                }
            });

            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, CommentsActivity.class);
                    intent.putExtra(CommentsActivity.POST_KEY,posts.get(getAdapterPosition()).getUid());
                    context.startActivity(intent);
                }
            });


        }

        void populateData(int position,PostHolder holder){
            postContent.setText(posts.get(position).getContent());
            if (posts.get(position).getUser()!=null){
                User user=posts.get(position).getUser();
                author.setText(posts.get(position).getUser().getName());
                if (Utils.isEmpty(user.getProfileImg())){
                    profileImg.setImageResource(R.drawable.user);
                }else{
                    Glide.with(context).load(user.getProfileImg()).into(profileImg);
                }
                String postUid=posts.get(position).getUid();
                handleLikeButton(postUid,position,holder);
                getCommentsCount(postUid,holder);

            }
        }
    }

    private void likePost(String postId){
        DatabaseReference postRef=root.child("likes")
                .child(postId).child(getUserUid());
        Map<String,Object> likeMap=new HashMap<>();
        likeMap.put("date",-System.currentTimeMillis());
        postRef.updateChildren(likeMap);
    }
    private void unLikePost(String postUid) {
        DatabaseReference postRef=root.child("likes")
                .child(postUid);
        postRef.removeValue();
    }

    private void getCommentsCount(String postUid, final PostHolder holder){
        DatabaseReference commentsRef=root.child("comments").child(postUid);
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.commentsCount.setText(dataSnapshot.getChildrenCount()+" comments");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void handleLikeButton(final String postUid, final int pos, final PostHolder holder){
        DatabaseReference likesRf=root.child("likes");
        likesRf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(postUid)&&dataSnapshot.child(postUid).hasChild(getUserUid())){
                    holder.like.setTextColor(Color.parseColor("#03A9F4"));
                    posts.get(pos).setLikeState(LIKED_STATE);
                    Drawable mDrawable = context.getResources().getDrawable(R.drawable.ic_thumb_up_blue_24dp);
                    holder.like.setCompoundDrawablesWithIntrinsicBounds(mDrawable,null,null,null);
                }else {
                    holder.like.setTextColor(Color.parseColor("#FF424242"));
                    posts.get(pos).setLikeState(UNLIKED_STATE);
                    Drawable mDrawable = context.getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp);
                    holder.like.setCompoundDrawablesWithIntrinsicBounds(mDrawable,null,null,null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getUserUid(){
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        return currentUser.getUid();
    }

}
