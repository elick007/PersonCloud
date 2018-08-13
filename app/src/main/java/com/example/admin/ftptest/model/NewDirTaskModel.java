package com.example.admin.ftptest.model;

import android.os.AsyncTask;

import com.example.admin.ftptest.Presenter.OperatePresenter;
import com.example.admin.ftptest.ftphelper.FTPHelper;

public class NewDirTaskModel extends AsyncTask<String,Void,Integer> implements BaseModel<String> {
    private String string;
    private OperatePresenter operatePresenter;

    public NewDirTaskModel(String string, OperatePresenter operatePresenter) {
        this.string = string;
       this.operatePresenter=operatePresenter;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        return FTPHelper.getInstance().creatNewDir(string);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        switch (integer){
            case 0:
                onSuccess("创建成功");
                break;
            case 1:
                onSuccess("创建失败");
                break;
            case 2:
                onSuccess("文件已存在");
                break;
        }
    }

    @Override
    public void onSuccess(String s) {
        operatePresenter.onModelSuccess(s);
    }

    @Override
    public void onFail() {

    }
}
