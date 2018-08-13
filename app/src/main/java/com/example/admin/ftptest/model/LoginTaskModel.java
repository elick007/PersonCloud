package com.example.admin.ftptest.model;

import android.os.AsyncTask;

import com.example.admin.ftptest.Presenter.BasePresenter;
import com.example.admin.ftptest.Presenter.LoginPresenter;
import com.example.admin.ftptest.ftphelper.FTPHelper;

public class LoginTaskModel extends AsyncTask<Void,Void,Boolean> implements BaseModel<Boolean> {
    private LoginPresenter basePresenter;

    public LoginTaskModel(BasePresenter basePresenter) {
        this.basePresenter = (LoginPresenter) basePresenter;
    }

    @Override
    public void onSuccess(Boolean aBoolean) {
        if (aBoolean){
            basePresenter.onModelSuccess(null);
        }else basePresenter.onModelFail();
    }

    @Override
    public void onFail() {

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return FTPHelper.getInstance().openConnect();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
            onSuccess(aBoolean);
    }
}
