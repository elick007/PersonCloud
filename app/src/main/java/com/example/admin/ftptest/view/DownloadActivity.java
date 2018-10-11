package com.example.admin.ftptest.view;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.ftptest.R;
import com.example.admin.ftptest.adapter.ViewPagerAdapter;
import com.example.admin.ftptest.fragment.DownloadedFragment;
import com.example.admin.ftptest.fragment.DownloadingFragment;
import com.example.admin.ftptest.services.DownloadService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DownloadActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.downloading_tv)
    TextView downloadingTv;
    @BindView(R.id.downloaded_tv)
    TextView downloadedTv;

    private List<Fragment> fragmentList = new ArrayList<>();
    private DownloadService downloadService;
    private DownloadService.ServicesBinder servicesBinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);
        initView();
        initService();
    }

    private void initService() {
        ServiceConnection serviceConnection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                servicesBinder= (DownloadService.ServicesBinder) iBinder;
                downloadService=servicesBinder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        Intent intent=new Intent(this,DownloadService.class);
        this.bindService(intent,serviceConnection,BIND_AUTO_CREATE);
    }

    private void initView() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fragmentList.add(new DownloadingFragment());
        fragmentList.add(new DownloadedFragment());
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(this);
        downloadingTv.setTextColor(Color.BLACK);
        downloadedTv.setTextColor(Color.GRAY);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        if (i == 0) {
            downloadingTv.setTextColor(Color.BLACK);
            downloadedTv.setTextColor(Color.GRAY);
        } else {
            downloadedTv.setTextColor(Color.BLACK);
            downloadingTv.setTextColor(Color.GRAY);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @OnClick({ R.id.downloading_tv, R.id.downloaded_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.downloading_tv:
                viewPager.setCurrentItem(0);
                downloadingTv.setTextColor(Color.BLACK);
                downloadedTv.setTextColor(Color.GRAY);
                break;
            case R.id.downloaded_tv:
                viewPager.setCurrentItem(1);
                downloadedTv.setTextColor(Color.BLACK);
                downloadingTv.setTextColor(Color.GRAY);
                break;
        }
    }
}
