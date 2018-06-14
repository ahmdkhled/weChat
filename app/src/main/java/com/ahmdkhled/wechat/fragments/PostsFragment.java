package com.ahmdkhled.wechat.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ahmdkhled.wechat.activities.ProfileActivity;
import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.adapters.PostsAdapter;
import com.ahmdkhled.wechat.model.Friend;
import com.ahmdkhled.wechat.model.Post;
import com.ahmdkhled.wechat.model.User;
import com.ahmdkhled.wechat.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.util.Map;

/**
 * Created by Ahmed Khaled on 6/2/2018.
 */

public class PostsFragment extends Fragment implements PostsAdapter.OnPostCLicked{

    private static final String POSTS_KEY = "posts_key";

    DatabaseReference root;
    FirebaseUser currentUser;
    ArrayList<Post> postsList;
    RecyclerView postRecycler;
    PostsAdapter postsAdapter;
    ImageView writePost,userImage;
    EditText postContent_ET;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.posts_frag,container,false);
        postsList=new ArrayList<>();
        postRecycler=v.findViewById(R.id.postsRecycler);
        writePost=v.findViewById(R.id.writePost_BU);
        userImage=v.findViewById(R.id.userImg);
        postContent_ET =v.findViewById(R.id.writePostContent_ET);
        root = FirebaseDatabase.getInstance().getReference().getRoot();
        writePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPost();
            }
        });
        fetchData();

        return v;
    }

    void fetchData(){
            DatabaseReference userRef=root.child("users").child(getCurrentUserUid());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String profileImg=dataSnapshot.child("profileImg").getValue(String.class);
                    if (!Utils.isEmpty(profileImg)){
                        Glide.with(getContext()).load(profileImg).into(userImage);
                    }else {
                        userImage.setImageResource(R.drawable.user);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

        DatabaseReference postsRef=root.child("posts");
        postsRef.orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postsList.clear();
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    final Post post=data.getValue(Post.class);
                    DatabaseReference friendsRef=root.child("friends");
                    friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot data:dataSnapshot.getChildren()){
                                Friend friend=data.getValue(Friend.class);
                                String user1=friend.getUser1();
                                String user2=friend.getUser2();
                                if ( (user1.equals(post.getUid())&&user2.equals(getCurrentUserUid()))
                                        ||(user1.equals(getCurrentUserUid())&&user2.equals(post.getUid()))){
                                    DatabaseReference userRef=root.child("users").child(post.getUid());
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            User user=dataSnapshot.getValue(User.class);
                                            user.setUid(dataSnapshot.getKey());
                                            post.setUser(user);
                                            postsList.add(post);
                                            postsAdapter.notifyDataSetChanged();
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });
                                    break;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
                showPosts(postsList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG","error " +databaseError.getMessage());
            }
        });
    }

    void uploadPost(){
        String post= postContent_ET.getText().toString();
        postContent_ET.setText("");
        postContent_ET.clearFocus();
        hideSoftKeyboard(this.getActivity());
        if (Utils.isEmpty(post)){
            Toast.makeText(getContext(),"post is Empty ",Toast.LENGTH_SHORT).show();
        }else{
            DatabaseReference postsRef=root.child("posts");
            String key=postsRef.push().getKey();
            DatabaseReference postRef=postsRef.child(key);
            Map<String,Object> postMap=new HashMap<>();
            postMap.put("content",post);
            postMap.put("date",-System.currentTimeMillis());
            postMap.put("uid",currentUser.getUid());
            postRef.updateChildren(postMap)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(),"post uploaded",Toast.LENGTH_SHORT).show();
                    Log.d("TAG","success ----------------------  ");

                }
                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"failed to upload post",Toast.LENGTH_SHORT).show();
                }
            });

    }
    }

    void showPosts(ArrayList<Post> posts){
        postsAdapter=new PostsAdapter(getContext(),posts,this);
        postRecycler.setAdapter(postsAdapter);
        postRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    String getCurrentUserUid(){
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        }
        return null;
    }

    void showProfile(String uid){
        Intent intent=new Intent(getContext(),ProfileActivity.class);
        intent.putExtra(ProfileActivity.PROFILEUID_TAG,uid);
        if (getContext()!=null)
        getContext().startActivity(intent);

    }

    @Override
    public void onImageClicked(int position) {
        String uid=postsList.get(position).getUser().getUid();
        showProfile(uid);
    }

    @Override
    public void onNameClicked(int position) {
        String uid=postsList.get(position).getUser().getUid();
        showProfile(uid);

    }


}
