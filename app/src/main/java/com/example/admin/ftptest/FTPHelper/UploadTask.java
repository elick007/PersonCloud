package com.example.admin.ftptest.FTPHelper;

import android.app.Notification;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.example.admin.ftptest.MainActivity;

import java.io.File;
import java.io.IOException;

/**
 * Created by admin on 2018/5/23.
 */

public class UploadTask extends AsyncTask<String,Integer,Boolean>{
    private FTP ftp;
    private String filePath;
    private String currentPath;
    private CallBackListener callBackListener;
    public UploadTask(FTP ftp, String filePath, String currentPath,CallBackListener callBackListener) {
        this.ftp = ftp;
        this.filePath = filePath;
        this.currentPath = currentPath;
        this.callBackListener = callBackListener;
    }



    @Override
    protected Boolean doInBackground(String... strings) {
        boolean result=false;
        if (ftp!=null&&ftp.isConnected()){
            File localFile=new File(filePath);
            if (localFile.exists()&&localFile.isFile()){
                    try {
                        result=ftp.uploadFile(filePath,currentPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        callBackListener.onFinsh(aBoolean);
    }
}
