package com.example.admin.ftptest.ftphelper;

import android.os.AsyncTask;
import org.apache.commons.net.ftp.FTPFile;
import java.util.List;

/**列出服务器path下所有文件夹
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
            list= FTPHelper.getInstance().listFTPDir(filePath);
        return list;
    }

    @Override
    protected void onPostExecute(List<FTPFile> list) {
        super.onPostExecute(list);
        callBackListener.getFTPFileList(list);
    }
}
