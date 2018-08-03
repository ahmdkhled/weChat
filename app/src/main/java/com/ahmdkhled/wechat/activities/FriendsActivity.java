package com.ahmdkhled.wechat.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.EditText;

import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.adapters.FriendsAdapter;
import com.ahmdkhled.wechat.model.Friend;
import com.ahmdkhled.wechat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity implements FriendsAdapter.OnUserClickd{

    DatabaseReference root;
    ArrayList<User> friendsList;
    FriendsAdapter usersAdapter;
    RecyclerView usersRecycler;
    EditText searcBox;
    DividerItemDecoration itemDecoration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        usersRecycler=findViewById(R.id.userRecycler);
        searcBox=findViewById(R.id.userSearchBox_ET);
        friendsList=new ArrayList<>();
        root= FirebaseDatabase.getInstance().getReference().getRoot();

        getSupportActionBar().setElevation(0);
        SpannableString s = new SpannableString("Friends");
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffff"))
                , 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        itemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);

        getFriends();

        searcBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ArrayList<User> users= filterUsers(charSequence.toString());
                showUsers(users);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }



    void getFriends(){
        DatabaseReference friendsRef=root.child("friends");
        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendsList.clear();
                for (final DataSnapshot data:dataSnapshot.getChildren()){
                    Friend friend=data.getValue(Friend.class);
                    String friendUid=null;
                    if (friend.getUser1().equals(getCurrentUserUid())){
                        friendUid=friend.getUser2();
                    }else if (friend.getUser2().equals(getCurrentUserUid())){
                        friendUid=friend.getUser1();
                    }
                    if (friendUid!=null){
                        DatabaseReference usersRef=root.child("users").child(friendUid);
                        final String finalFriendUid = friendUid;
                        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user=dataSnapshot.getValue(User.class);
                                user.setUid(finalFriendUid);
                                friendsList.add(user);
                                usersAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        showUsers(friendsList);
    }

    private void showUsers(ArrayList<User> usersList) {
        usersAdapter=new FriendsAdapter(usersList,this,this);
        usersRecycler.setAdapter(usersAdapter);
        usersRecycler.removeItemDecoration(itemDecoration);
        usersRecycler.addItemDecoration(itemDecoration);
        usersRecycler.setLayoutManager(new LinearLayoutManager(this));

    }

    void showProfile(String uid){
        Intent profileIntent=new Intent(this,ProfileActivity.class);
        profileIntent.putExtra(ProfileActivity.PROFILE_UID_TAG,uid);
        startActivity(profileIntent);
    }
    String getCurrentUserUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    ArrayList<User> filterUsers(String search){
        if (TextUtils.isEmpty(search))
            return friendsList;
        ArrayList<User> users=new ArrayList<>();
        for (User user:friendsList){
            if (user.getName().contains(search)){
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public void onUserClicked(int position) {
        showProfile(friendsList.get(position).getUid());
    }

}
