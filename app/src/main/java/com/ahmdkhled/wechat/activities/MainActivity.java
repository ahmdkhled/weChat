package com.ahmdkhled.wechat.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ahmdkhled.wechat.fragments.ConnectivityEvent;
import com.ahmdkhled.wechat.utils.Connection;
import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.adapters.MainPagerAdapter;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    FirebaseAnalytics firebaseAnalytics;

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
        viewPager.setOffscreenPageLimit(3);

        firebaseAnalytics=FirebaseAnalytics.getInstance(this);

        if (!Connection.isConnected(this)){
            showSnackBar();
        }
    }
    void showSnackBar(){
        Snackbar snackbar=Snackbar.make(findViewById(R.id.activityMainContainer)
                , R.string.no_connection,Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.retry, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Connection.isConnected(getApplicationContext())){
                    EventBus.getDefault().post(new ConnectivityEvent());
                }else {
                    showSnackBar();
                }
            }
        });
        snackbar.show();
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.settings){
            startActivity(new Intent(getApplicationContext(),SettingActivity.class));
        }else if (item.getItemId()==R.id.startChat){
            startActivity(new Intent(getApplicationContext(),MessagesListActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


}
