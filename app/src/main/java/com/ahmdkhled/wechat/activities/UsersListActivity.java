package com.ahmdkhled.wechat.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.adapters.UsersListAdapter;
import com.ahmdkhled.wechat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersListActivity extends AppCompatActivity implements UsersListAdapter.OnUserClickd {

    DatabaseReference root;
    ArrayList<User> usersList;
    UsersListAdapter usersAdapter;
    RecyclerView usersRecycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        usersRecycler=findViewById(R.id.userRecycler);
        usersList=new ArrayList<>();
        root= FirebaseDatabase.getInstance().getReference().getRoot();

        getUsers();
    }

    void getUsers(){
        DatabaseReference users=root.child("users");
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    String uid=data.getKey();
                    if (!uid.equals(getCurrentUserUid())){
                        User user=data.getValue(User.class);
                        user.setUid(uid);
                        usersList.add(user);
                        usersAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        showUsers(usersList);
    }

    private void showUsers(ArrayList<User> usersList) {
        usersAdapter=new UsersListAdapter(usersList,this,this);
        usersRecycler.setAdapter(usersAdapter);
        DividerItemDecoration itemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
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

    @Override
    public void onUserClicked(int position) {
        showProfile(usersList.get(position).getUid());
    }

}
