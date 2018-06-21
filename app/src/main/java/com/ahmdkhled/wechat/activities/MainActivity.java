package com.ahmdkhled.wechat.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ahmdkhled.wechat.utils.NotificationService;
import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.adapters.MainPagerAdapter;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    FirebaseAnalytics firebaseAnalytics;
    FirebaseJobDispatcher jobDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);
        setContentView(R.layout.activity_main);
        viewPager=findViewById(R.id.viewPager);
        tabLayout=findViewById(R.id.tabLayout);
        String unitId=getResources().getString(R.string.adunit_id);
        MobileAds.initialize(this,unitId);

        MainPagerAdapter pagerAdapter=new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);

        firebaseAnalytics=FirebaseAnalytics.getInstance(this);
        startJob();
    }

    public void startJob() {
        jobDispatcher=new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job job=jobDispatcher.newJobBuilder()
                .setService(NotificationService.class)
                .setTag("MYTAG")
                .setTrigger(Trigger.executionWindow(12*3600,12*3600))
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .build();

        jobDispatcher.mustSchedule(job);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.logOut){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),SignupActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
