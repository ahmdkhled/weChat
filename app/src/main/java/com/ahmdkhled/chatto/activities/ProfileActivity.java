package com.ahmdkhled.chatto.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ahmdkhled.chatto.R;
import com.ahmdkhled.chatto.fragments.ProfileFrag;

public class ProfileActivity extends AppCompatActivity {
    public static final String PROFILE_UID_TAG="profile_uid";
    private final String profile_frag_tag="profile_frag_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent=getIntent();
        if (intent!=null&&intent.hasExtra(PROFILE_UID_TAG)){
            showProfile(intent.getStringExtra(PROFILE_UID_TAG));
        }

    }

    void showProfile(String uid){
        ProfileFrag profileFrag=new ProfileFrag();
        Bundle bundle=new Bundle();
        bundle.putString(PROFILE_UID_TAG,uid);
        profileFrag.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.profile_activity,profileFrag,profile_frag_tag)
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
