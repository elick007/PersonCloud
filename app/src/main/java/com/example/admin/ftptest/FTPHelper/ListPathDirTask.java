package com.example.admin.ftptest.FTPHelper;

import android.os.AsyncTask;

import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/6/6.
 */

public class ListPathDirTask extends AsyncTask<String,Void,List<FTPFile>> {
    private String filePath;
    private CallBackListener callBackListener;

    public ListPathDirTask(String filePath, CallBackListener callBackListener) {
        this.filePath = filePath;
        this.callBackListener=callBackListener;

    }

    @Override
    protected List<FTPFile> doInBackground(String... strings) {
        List<FTPFile> list;
            list=FTP.listFTPDir(filePath);
        return list;
    }

    @Override
    protected void onPostExecute(List<FTPFile> list) {
        super.onPostExecute(list);
        callBackListener.getFTPFileList(list);
    }
}
