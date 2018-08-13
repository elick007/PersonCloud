package com.example.admin.ftptest.model;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.admin.ftptest.Presenter.BasePresenter;
import com.example.admin.ftptest.Presenter.OperatePresenter;
import com.example.admin.ftptest.ftphelper.FTPHelper;
import com.example.admin.ftptest.utils.MyLogger;

import org.apache.commons.net.ftp.FTPFile;

import java.util.ArrayList;
import java.util.List;

public class DeleteFilesTaskModel extends AsyncTask<Void, Void, Void> implements BaseModel<Void> {
    private List<String> mCheckList=new ArrayList<>();
    private List<FTPFile> mList=new ArrayList<>();
    private ProgressDialog progressDialog;
    private OperatePresenter operatePresenter;

    public DeleteFilesTaskModel(BasePresenter basePresenter, List<String> checkList, List<FTPFile> list, ProgressDialog progressDialog) {
        this.operatePresenter = (OperatePresenter) basePresenter;
        mCheckList.addAll(checkList);
        mList.addAll(list);
        this.progressDialog = progressDialog;
        MyLogger.d(mCheckList.toString());
    }

    @Override
    protected Void doInBackground(Void... voids) {

        for (String listPosition : mCheckList) {
            MyLogger.d(listPosition);
            FTPFile ftpFile = mList.get(Integer.valueOf(listPosition) - 1);
            if (ftpFile.isFile()) {
                FTPHelper.getInstance().deleteFile(ftpFile.getName());
            } else {
                FTPHelper.getInstance().removeDirectoryALLFile(ftpFile.getName());
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onSuccess(aVoid);
        progressDialog.dismiss();
    }

    @Override
    public void onSuccess(Void aVoid) {
        operatePresenter.onModelSuccess("操作成功");
    }

    @Override
    public void onFail() {

    }
}
