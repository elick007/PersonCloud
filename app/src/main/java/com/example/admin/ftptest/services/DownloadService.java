package com.example.admin.ftptest.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.admin.ftptest.ftphelper.FTPHelper;
import com.example.admin.ftptest.utils.MyLogger;

public class DownloadService extends Service {
    private String localPath;
    private int MAX_THREAD = 3;

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
    private void _startDownDir(final String remotePath){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FTPHelper.getInstance().downloadDir(localPath,remotePath);
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
        public void startDownDir(String url){
            _startDownDir(url);
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
