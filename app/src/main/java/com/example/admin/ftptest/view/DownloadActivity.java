package com.example.admin.ftptest.view;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.admin.ftptest.R;
import com.example.admin.ftptest.adapter.ViewPagerAdapter;
import com.example.admin.ftptest.fragment.DownloadedFragment;
import com.example.admin.ftptest.fragment.DownloadingFragment;

import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener {
    private List<Fragment> fragmentList = new ArrayList<>();
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        viewPager = findViewById(R.id.view_pager);
        fragmentList.add(new DownloadingFragment());
        fragmentList.add(new DownloadedFragment());
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.setCurrentItem(0);
        tabLayout = findViewById(R.id.tablayout);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        Toast.makeText(DownloadActivity.this,i+"",Toast.LENGTH_SHORT).show();
        tabLayout.getTabAt(i).select();
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Toast.makeText(DownloadActivity.this,tab.getPosition()+"",Toast.LENGTH_SHORT).show();
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
