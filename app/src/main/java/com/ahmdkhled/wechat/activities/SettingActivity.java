package com.ahmdkhled.wechat.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ahmdkhled.wechat.R;

public class SettingActivity extends AppCompatActivity {

    TextView findFriends,friends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        findFriends=findViewById(R.id.findFriends);
        friends=findViewById(R.id.friends);

        findFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),UsersActivity.class));
            }
        });

        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),FriendsActivity.class));
            }
        });

    }
}
