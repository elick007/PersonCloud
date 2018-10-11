package com.example.admin.ftptest.model;

import android.os.AsyncTask;

import com.example.admin.ftptest.Presenter.BasePresenter;
import com.example.admin.ftptest.Presenter.UpLoadPresenter;
import com.example.admin.ftptest.ftphelper.CallBack;
import com.example.admin.ftptest.ftphelper.FTPHelper;
import com.example.admin.ftptest.utils.MyLogger;

/**
 * Created by admin on 2018/5/23.
 */

public class UploadTaskModel extends AsyncTask<String, Integer, Boolean> implements BaseModel<Boolean> {
    private String filePath;
    private String currentPath;
    private UpLoadPresenter basePresenter;
    public UploadTaskModel(String filePath, String currentPath, BasePresenter basePresenter) {
        this.filePath = filePath;
        this.currentPath = currentPath;
        this.basePresenter= (UpLoadPresenter) basePresenter;
    }


    @Override
    protected Boolean doInBackground(String... strings) {

        boolean result = false;
        if (FTPHelper.getInstance().isConnected()) {
            MyLogger.e("task is uploading");
            result=FTPHelper.getInstance().uploadFile(filePath, currentPath);
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
          if (aBoolean){
              onSuccess(aBoolean);
          }else onFail();
    }

    @Override
    public void onSuccess(Boolean aBoolean) {
        basePresenter.onModelSuccess(aBoolean);
    }

    @Override
    public void onFail() {

    }
}
