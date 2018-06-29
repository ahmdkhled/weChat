package com.ahmdkhled.wechat.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
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
    EditText searcBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        usersRecycler=findViewById(R.id.userRecycler);
        searcBox=findViewById(R.id.userSearchBox_ET);
        usersList=new ArrayList<>();
        root= FirebaseDatabase.getInstance().getReference().getRoot();

        getSupportActionBar().setElevation(0);
        SpannableString s = new SpannableString("Users");
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        getUsers();

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

    ArrayList<User> filterUsers(String search){
        if (TextUtils.isEmpty(search))
            return usersList;
        ArrayList<User> users=new ArrayList<>();
        for (User user:usersList){
            if (user.getName().contains(search)){
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public void onUserClicked(int position) {
        showProfile(usersList.get(position).getUid());
    }

}
