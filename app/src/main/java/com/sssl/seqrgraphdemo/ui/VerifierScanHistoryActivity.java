package com.sssl.seqrgraphdemo.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.utils.MyAdapter;
import com.google.android.material.tabs.TabLayout;

public class VerifierScanHistoryActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifier_scan_history);

        tabLayout=(TabLayout)findViewById(R.id.tabLayout);
        viewPager= (ViewPager2) findViewById(R.id.viewPager);


        final MyAdapter adapter = new MyAdapter(this);
        viewPager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}