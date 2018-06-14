package com.ahmdkhled.wechat.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.fragments.ProfileFrag;

public class ProfileActivity extends AppCompatActivity {
    private static final String PROFILEFRAG_TAG = "profile_frag";
    public static final String PROFILEUID_TAG = "profile_uid";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent=getIntent();
        if (intent!=null&&intent.hasExtra(PROFILEUID_TAG)){
            showProfile(intent.getStringExtra(PROFILEUID_TAG));
        }
    }

    void showProfile(String uid){
        ProfileFrag profileFrag=new ProfileFrag();
        Bundle bundle=new Bundle();
        bundle.putString(PROFILEUID_TAG,uid);
        profileFrag.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.profile_activity,profileFrag,PROFILEFRAG_TAG)
                .commit();
    }
}
