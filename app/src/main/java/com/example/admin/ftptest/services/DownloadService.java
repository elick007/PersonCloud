package com.example.admin.ftptest.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import com.example.admin.ftptest.ftphelper.FTPHelper;
import com.example.admin.ftptest.utils.MyLogger;
import com.example.admin.ftptest.view.ShowPhotoActivity;

public class DownloadService extends Service {
    private String localPath;
    private int MAX_THREAD = 3;
    private Handler handler;

    public DownloadService() {
    }

    private void _setMAX_THREAD(int MAX_THREAD) {
        this.MAX_THREAD = MAX_THREAD;
    }

    private void _setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    @Override
    public IBinder onBind(Intent intent) {
        MyLogger.d("bind service suceess");
        handler=new Handler(getMainLooper());
        return new ServicesBinder();
    }

    private void _startDownFile(final String remotePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FTPHelper.getInstance().dowloadFile(localPath, remotePath);
            }
        }).start();
    }

    private void _startDownDir(final String remotePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FTPHelper.getInstance().downloadDir(localPath, remotePath);
            }
        }).start();
    }

    private void _showPhoto(final String remotePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
if (FTPHelper.getInstance().dowloadFile(localPath,remotePath)){
    handler.post(new Runnable() {
        @Override
        public void run() {
            Intent intent=new Intent(DownloadService.this, ShowPhotoActivity.class);
            intent.putExtra("path",localPath+"/"+remotePath);
            DownloadService.this.startActivity(intent);
        }
    });
}
            }
        }).start();
    }

    public class ServicesBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }

        public void startDownFile(String url) {
            _startDownFile(url);
        }

        public void startDownDir(String url) {
            _startDownDir(url);
        }

        public void showPhoto(String url) {
            _showPhoto(url);
        }

        public void pause(String fileName) {

        }

        public void setMaxThread(int maxThread) {
            _setMAX_THREAD(maxThread);
        }

        public void setLocalPath(String localPath) {
            _setLocalPath(localPath);
        }
    }
}
