package com.example.admin.ftptest.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bm.library.PhotoView;
import com.example.admin.ftptest.R;

public class ShowPhotoActivity extends AppCompatActivity {
private PhotoView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);
        initView();
    }

    private void initView() {
        imageView=findViewById(R.id.photo);
        Intent intent=getIntent();
        Bitmap bitmap= BitmapFactory.decodeFile(intent.getStringExtra("path"));
        imageView.enable();
        imageView.setImageBitmap(bitmap);
    }
}
