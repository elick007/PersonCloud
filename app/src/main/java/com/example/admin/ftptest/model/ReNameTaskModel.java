package com.example.admin.ftptest.model;

import android.os.AsyncTask;

import com.example.admin.ftptest.Presenter.BasePresenter;
import com.example.admin.ftptest.Presenter.OperatePresenter;
import com.example.admin.ftptest.ftphelper.FTPHelper;

public class ReNameTaskModel extends AsyncTask<Void,Void,Boolean> implements BaseModel<Boolean> {
    private String oldName;
    private String newName;
    private OperatePresenter basePresenter;
    public ReNameTaskModel(BasePresenter basePresenter,String oldName, String newName) {
        this.basePresenter= (OperatePresenter) basePresenter;
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return FTPHelper.getInstance().reNameOrMove(oldName,newName);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        onSuccess(aBoolean);
    }

    @Override
    public void onSuccess(Boolean aBoolean) {
        if (aBoolean){
            basePresenter.onModelSuccess("操作成功");
        }else {
            basePresenter.onModelSuccess("操作失败");
        }
    }

    @Override
    public void onFail() {

    }
}
