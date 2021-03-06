package com.ahmdkhled.chatto.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.ahmdkhled.chatto.R;
import com.ahmdkhled.chatto.adapters.CommentsAdapter;
import com.ahmdkhled.chatto.model.Comment;
import com.ahmdkhled.chatto.model.Notification;
import com.ahmdkhled.chatto.model.Post;
import com.ahmdkhled.chatto.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    public static final String POST_KEY ="post_key";
    Post post;
    DatabaseReference root;
    RecyclerView commentsRecycler;
    EditText writeComment;
    ImageView submitComment;
    ArrayList<Comment> commentsList;
    CommentsAdapter commentsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        commentsRecycler=findViewById(R.id.commentsRecycler);
        writeComment=findViewById(R.id.writeComment);
        submitComment=findViewById(R.id.submitComment);
        commentsList=new ArrayList<>();

        root= FirebaseDatabase.getInstance().getReference().getRoot();
        if (getIntent()!=null&&getIntent().hasExtra(POST_KEY)){
            post=getIntent().getParcelableExtra(POST_KEY);
            fetchComment(post.getUid());
        }

        submitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment=writeComment.getText().toString();
                writeComment(comment);
                writeComment.setText("");

            }
        });

    }

    private void fetchComment(String postUid){
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
        commentMap.put("authorUid",getCurrentUserUid());
        commentMap.put("date",-System.currentTimeMillis());
        commentsRef.updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    DatabaseReference notificationRef=root.child("notifications")
                            .child(post.getAuthorUid())
                            .push();
                    Notification notification=new Notification(getCurrentUserUid(),
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

    private String getCurrentUserUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
