package com.ahmdkhled.wechat.fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ahmed Khaled on 6/2/2018.
 */

public class ProfileFrag extends Fragment {
    ImageView profileImg;
    TextView nameTV, bioTV;
    CardView addcontainer;
    Button addBU,messageBU;
    RecyclerView postRecycler;
    NestedScrollView scrollView;
    DatabaseReference root;
    FirebaseUser currentuser;
    User user;
    ArrayList<Post> postsList;
    PostsAdapter postsAdapter;
    String uid;
    int friendshiip_state=-2;
    boolean isMyProfile=true;
    public static final String POSTS_KEY="posts_key";
    public static final String User_KEY="user_key";
    private int STORAGE_PERMISSION_CODE=44;
    private final int PICK_IMAGE_CODE=23;
    private static final int ACCEPT_STATE=2;
    private static final int ADD_STATE=1;
    private static final int CANCEL_REQUEST_STATE=4;
    private static final int FRIENDS_STATE=3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.profile_fragment,container,false);
        profileImg=v.findViewById(R.id.profileImg_IV);
        nameTV =v.findViewById(R.id.profileName_TV);
        bioTV =v.findViewById(R.id.profileBio_TV);
        addBU=v.findViewById(R.id.addFriend_BU);
        messageBU=v.findViewById(R.id.message_BU);
        addcontainer=v.findViewById(R.id.addContainer);
        postRecycler=v.findViewById(R.id.userPostsRecycler);
        scrollView=v.findViewById(R.id.scroll);
        postsList=new ArrayList<>();
        root= FirebaseDatabase.getInstance().getReference().getRoot();
        currentuser= FirebaseAuth.getInstance().getCurrentUser();


        Bundle b=getArguments();
        if (b!=null){
             uid=b.getString(ProfileActivity.PROFILEUID_TAG);
            if (getUserUid().equals(uid)){
                addcontainer.setVisibility(View.INVISIBLE);
                bioTV.setEnabled(true);
                profileImg.setEnabled(true);
            }else{
                addcontainer.setVisibility(View.VISIBLE);
                isMyProfile=false;
                bioTV.setEnabled(false);
                profileImg.setEnabled(false);
            }
        }else{
            uid=currentuser.getUid();
            addcontainer.setVisibility(View.INVISIBLE);
            bioTV.setEnabled(true);
            profileImg.setEnabled(true);
        }

        addBU.setEnabled(false);
        addBU.setText("....");

            handleAddButton();
            fetchData(uid);


        bioTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference bioRef=root.child("users").child(uid).child("bio");
                LayoutInflater inflater=getLayoutInflater();
                final View alertDialog=inflater.inflate(R.layout.bio_alert_dialog,null);
                final EditText bioUpdate_ET=alertDialog.findViewById(R.id.bioUpdate_ET);
                Button bioUpdate_BU=alertDialog.findViewById(R.id.bioUpdate_BU);
                final AlertDialog dialog=new AlertDialog.Builder(getContext()).create();
                dialog.setView(alertDialog);
                dialog.show();
                bioUpdate_BU.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String bio=bioUpdate_ET.getText().toString();
                        bioRef.setValue(bio);
                        dialog.dismiss();
                    }
                });

            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grantPermission();
            }
        });

        addBU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FRIENDSHIP", "testing now "+friendshiip_state);
                if (friendshiip_state==ADD_STATE){
                    addFriend();
                }else if (friendshiip_state==CANCEL_REQUEST_STATE){
                    cancelRequest();
                }else if (friendshiip_state==ACCEPT_STATE){
                    acceptRequest();
                }else if (friendshiip_state==FRIENDS_STATE){
                    unFriend();
                }
            }
        });

        DatabaseReference userRef=root.child("users").child(uid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user=dataSnapshot.getValue(User.class);
                populateData(user);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        return v;
    }

    void fetchData(final String userUid){
        DatabaseReference postsRef=root.child("posts");
        postsRef.orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postsList.clear();
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    final Post post=data.getValue(Post.class);
                    if (post.getUid().equals(userUid)){
                        Log.d("PROFILEE",post.getUid()+" "+currentuser.getUid());

                        DatabaseReference users=root.child("users").child(post.getUid());
                        users.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user=dataSnapshot.getValue(User.class);
                                post.setUser(user);
                                postsAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                        postsList.add(post);
                    }
                }
                showPosts(postsList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    void handleAddButton(){
        if (!isMyProfile) {
            final DatabaseReference friendReqRef = root.child("friendRequests");
            friendReqRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(uid) && dataSnapshot.child(uid).hasChild(getUserUid())) {
                        Log.d("FRIENDSHIP", "cancel request");
                        addBU.setEnabled(true);
                        addBU.setText("cancel request ");
                        friendshiip_state = CANCEL_REQUEST_STATE;
                    } else if (dataSnapshot.hasChild(getUserUid()) && dataSnapshot.child(getUserUid()).hasChild(uid)) {
                        Log.d("FRIENDSHIP", "accept");
                        addBU.setEnabled(true);
                        addBU.setText("Accept");
                        friendshiip_state = ACCEPT_STATE;
                    } else {
                        DatabaseReference friendsRef = root.child("friends");
                        friendsRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d("FRIENDSHIP", "real time work..........");
                                friendshiip_state=-2;
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    Friend friend = data.getValue(Friend.class);
                                    String user1 = friend.getUser1();
                                    String user2 = friend.getUser2();
                                    if ((user1.equals(uid) && user2.equals(getUserUid())) ||
                                            (user1.equals(getUserUid()) && user2.equals(uid))) {
                                        Log.d("FRIENDSHIP", "friendssssss......"+user1+"---"+user2);

                                        friendshiip_state = FRIENDS_STATE;
                                        break;
                                    }else {
                                        friendshiip_state = ADD_STATE;
                                    }
                                }
                                if (friendshiip_state == FRIENDS_STATE) {
                                    Log.d("FRIENDSHIP", "friends");
                                    addBU.setEnabled(true);
                                    addBU.setText("friends");
                                }else if (friendshiip_state==ADD_STATE ||dataSnapshot.getChildrenCount()==0){
                                    friendshiip_state=ADD_STATE;
                                    Log.d("FRIENDSHIP", "add");
                                    addBU.setEnabled(true);
                                    addBU.setText("Add");
                                }
                                Log.d("FRIENDSHIP", "state now "+friendshiip_state);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                friendshiip_state = -1;
                                Log.d("FRIENDSHIP", "" + databaseError.getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("FRIENDSHIP", "" + databaseError.getMessage());
                    friendshiip_state = -1;
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_CODE &&data!=null){
            Uri imageUri=data.getData();
            uploadprofileImg(imageUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==STORAGE_PERMISSION_CODE){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                pickImage();
            }
        }
    }


    void populateData(User user){
        if (Utils.isEmpty(user.getProfileImg())){
            profileImg.setImageResource(R.drawable.user);
        }else {
            Glide.with(getContext()).load(user.getProfileImg()).into(profileImg);
        }
        nameTV.setText(user.getName());
        if (Utils.isEmpty(user.getBio())&&isMyProfile){
            bioTV.setText("set you bio :)");
        }else{
            bioTV.setText(user.getBio());
        }
    }

    void showPosts(ArrayList<Post> posts){
        postsAdapter=new PostsAdapter(getContext(),posts);
        postRecycler.setAdapter(postsAdapter);
        postRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        if (postRecycler.getAdapter().getItemCount()==0){
            Toast.makeText(getContext(),"zero",Toast.LENGTH_SHORT).show();
            ViewCompat.setNestedScrollingEnabled(scrollView,false);
        }

    }

    String getUserUid(){
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        return currentUser.getUid();
    }

    void addFriend(){
        final DatabaseReference root=FirebaseDatabase.getInstance().getReference().getRoot();
        DatabaseReference uidRef=root.child("friendRequests").child(uid).child(getUserUid());
                Map<String,Object> reqMap =new HashMap<>();
                reqMap.put("date",-System.currentTimeMillis());
                uidRef.updateChildren(reqMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getContext(),"your request have been sent "
                                            ,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
    }

    void cancelRequest(){
        DatabaseReference friendReqRef=root.child("friendRequests").child(uid).child(getUserUid());
        friendReqRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(),"request cancelled ",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"failed to cancel request ",Toast.LENGTH_SHORT).show();
            }
        });
    }

    void unFriend(){
        final DatabaseReference friendRef=root.child("friends");
        friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    Friend friend=data.getValue(Friend.class);
                    String user1 = friend.getUser1();
                    String user2 = friend.getUser2();
                    if ((user1.equals(uid) && user2.equals(getUserUid())) ||
                            (user1.equals(getUserUid()) && user2.equals(uid))) {
                        friendRef.child(data.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getContext(),"unfriend ",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"failed ",Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    void acceptRequest(){
        DatabaseReference friendReqRef=root.child("friendRequests").child(getUserUid()).child(uid);
        friendReqRef.removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            DatabaseReference friendsRef=root.child("friends");
                            String key=friendsRef.push().getKey();
                            DatabaseReference keyRef=friendsRef.child(key);
                            Map<String,Object> friendMap=new HashMap<>();
                            friendMap.put("user1",uid);
                            friendMap.put("user2",getUserUid());
                            keyRef.updateChildren(friendMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(getContext(),"you are friends now ",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"failed to accept",Toast.LENGTH_SHORT).show();

            }
        });
    }

    void uploadprofileImg(final Uri imageUri){
        final StorageReference storageRef=FirebaseStorage.getInstance().getReference();
        storageRef.child("profileImages/"+uid).putFile(imageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            DatabaseReference profileImgRef=root.child("users")
                                    .child(uid).child("profileImg");
                            String url=task.getResult().getDownloadUrl().toString();
                            Log.d("STORAGEEE",""+url);
                            profileImgRef.setValue(url)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        profileImg.setImageURI(imageUri);
                                        Toast.makeText(getContext(),"profile image updated",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("STORAGEEE",""+e.getMessage());
                                    Toast.makeText(getContext(),"failed to update photo ",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("STORAGEEE",""+e.getMessage());
                Toast.makeText(getContext(),"failed to update photo ",Toast.LENGTH_SHORT).show();
            }
        });
        //storage.putFile("");
    }

    void pickImage(){
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,PICK_IMAGE_CODE);
    }

    void grantPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    AlertDialog.Builder alertDialog=new AlertDialog.Builder(getContext());
                    alertDialog.setTitle("Storage Permission");
                    alertDialog.setMessage("we need this permission to upload your profile photo :)");
                    alertDialog.setPositiveButton("ok ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                        }
                    });
                    alertDialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getContext(),"ok thanks",Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.show();
                }else{
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                }
            }else {
                pickImage();
            }
        }else {
            pickImage();
        }
    }

}
