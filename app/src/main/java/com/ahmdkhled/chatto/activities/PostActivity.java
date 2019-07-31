package com.ahmdkhled.chatto.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmdkhled.chatto.R;
import com.ahmdkhled.chatto.adapters.CommentsAdapter;
import com.ahmdkhled.chatto.model.Comment;
import com.ahmdkhled.chatto.model.Notification;
import com.ahmdkhled.chatto.model.Post;
import com.ahmdkhled.chatto.model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {
    TextView author,content;
    ImageView authorImg,submitComment;
    Button like,comment;
    EditText commentContent;
    RecyclerView commentsRecycler;
    DatabaseReference root;
    String postUid;
    Post post;
    ArrayList<Comment> commentsList;
    CommentsAdapter commentsAdapter;
    int likeState;
    public static final int LIKED_STATE=1;
    public static final int UNLIKED_STATE=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        author=findViewById(R.id.singlePostAuthor);
        content=findViewById(R.id.singlePostContent);
        authorImg=findViewById(R.id.singlePostImg);
        submitComment=findViewById(R.id.submitComment_SP);
        commentContent=findViewById(R.id.commentContent_SP);
        like=findViewById(R.id.singlePostLike);
        comment=findViewById(R.id.singlePostComment);
        commentsRecycler=findViewById(R.id.singlePostCommentsRecycler);
        root= FirebaseDatabase.getInstance().getReference().getRoot();
        commentsList=new ArrayList<>();


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b=getIntent().getExtras();
        if (b!=null){
            postUid=b.getString("postUid");
            fetchPost(postUid);
            handleLikeButton(postUid);
            Log.d("POSTTS","postuid : "+postUid);
        }

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (likeState==UNLIKED_STATE){
                    likePost(postUid);
                }else if (likeState==LIKED_STATE){
                    unLikePost(postUid);
                }

            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), CommentsActivity.class);
                intent.putExtra(CommentsActivity.POST_KEY,post);
                startActivity(intent);
            }
        });

        submitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment=commentContent.getText().toString();
                writeComment(comment);
                commentContent.setText("");
            }
        });

    }

    void fetchPost(final String postUid){
        DatabaseReference postRef=root.child("posts")
                .child(postUid);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post=dataSnapshot.getValue(Post.class);
                post.setUid(dataSnapshot.getKey());
                content.setText(post.getContent());
                DatabaseReference authorRef=root.child("users")
                        .child(post.getAuthorUid());
                authorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user=dataSnapshot.getValue(User.class);
                        author.setText(user.getName());
                        post.setUser(user);
                        if (TextUtils.isEmpty(user.getProfileImg())){
                            authorImg.setImageResource(R.drawable.user);
                        }else {
                            Glide.with(getApplicationContext()).load(user.getProfileImg())
                                    .into(authorImg);
                        }
                        fetchComments(postUid);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
                .child(postUid).child(getUserUid());
        postRef.removeValue();
    }

    void handleLikeButton(String postUid){
        if (postUid !=null){
            DatabaseReference likesRef=root.child("likes");
            likesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(PostActivity.this.postUid)&&dataSnapshot.child(PostActivity.this.postUid).hasChild(getUserUid())){
                        likeState=LIKED_STATE;
                        like.setTextColor(Color.parseColor("#03A9F4"));
                        //Drawable mDrawable = getResources().getDrawable(R.drawable.ic_thumb_up_blue_24dp);
                        Drawable mDrawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_thumb_up_blue_24dp);
                        like.setCompoundDrawablesWithIntrinsicBounds(mDrawable,null,null,null);
                    }else {
                        likeState=UNLIKED_STATE;
                        like.setTextColor(Color.parseColor("#FF424242"));
                        //Drawable mDrawable = getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp);
                        Drawable mDrawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_thumb_up_black_24dp);
                        like.setCompoundDrawablesWithIntrinsicBounds(mDrawable,null,null,null);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void fetchComments(String postUid){
        Log.d("COMMENT",post.getUser().getUid()+"   "+post.getAuthorUid());
        DatabaseReference postRef=root.child("comments").child(postUid);
        postRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Comment comment=dataSnapshot.getValue(Comment.class);
                comment.setUid(dataSnapshot.getKey());
                commentsList.add(comment);
                commentsAdapter.notifyDataSetChanged();
                DatabaseReference userRef=root.child("users")
                        .child(comment.getAuthorUid());
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user =dataSnapshot.getValue(User.class);
                        user.setUid(dataSnapshot.getKey());
                        comment.setUser(user);
                        commentsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Comment comment=dataSnapshot.getValue(Comment.class);
                comment.setUid(dataSnapshot.getKey());
                int index=commentsList.indexOf(comment);
                commentsList.set(index,comment);
                commentsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Comment comment=dataSnapshot.getValue(Comment.class);
                comment.setUid(dataSnapshot.getKey());
                commentsList.remove(comment);
                commentsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        populateComments();
    }

    private void writeComment(String comment){
        final DatabaseReference commentsRef=root.child("comments")
                .child(post.getUid()).push();
        final String key=commentsRef.getKey();
        Map<String,Object> commentMap=new HashMap<>();
        commentMap.put("content",comment);
        commentMap.put("authorUid",getUserUid());
        commentMap.put("date",-System.currentTimeMillis());
        commentsRef.updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    DatabaseReference notificationRef=root.child("notifications")
                            .child(post.getAuthorUid())
                            .push();
                    Notification notification=new Notification(getUserUid(),
                            "post comment",System.currentTimeMillis());
                    Map<String,Object> targetMap=new HashMap<>();
                    targetMap.put("postUid",post.getUid());
                    targetMap.put("commentUid",key);
                    notification.setTarget(targetMap);
                    notificationRef.setValue(notification);
                }
            }
        });
    }

    void populateComments(){
        commentsAdapter=new CommentsAdapter(this,commentsList,post.getUid());
        commentsRecycler.setAdapter(commentsAdapter);
        commentsRecycler.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        commentsRecycler.addItemDecoration(dividerItemDecoration);
    }

    private String getUserUid(){
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        return currentUser.getUid();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
