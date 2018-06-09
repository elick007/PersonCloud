package com.example.admin.ftptest.FTPHelper;

import android.os.AsyncTask;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**获取服务器路径path下所有FTPFile
 * Created by admin on 2018/5/25.
 */

public class ListPathFileTask extends AsyncTask<String,Integer,List<FTPFile>> {
    private FTP ftp;
    private String filePath;
    private CallBackListener callBackListener;

    public ListPathFileTask(FTP ftp, String filePath,CallBackListener callBackListener) {
        this.ftp = ftp;
        this.filePath = filePath;
        this.callBackListener=callBackListener;

    }


    @Override
    protected  List<FTPFile> doInBackground(String... strings) {
        List<FTPFile> list=new ArrayList<>();
        try {
            list.addAll(ftp.listFTPFile(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<FTPFile> list) {
        super.onPostExecute(list);
        callBackListener.getFTPFileList(list);
    }
}
