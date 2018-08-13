package com.example.admin.ftptest.model;

import android.os.AsyncTask;

import com.example.admin.ftptest.Presenter.OperatePresenter;
import com.example.admin.ftptest.ftphelper.FTPHelper;
import com.example.admin.ftptest.utils.MyLogger;

import org.apache.commons.net.ftp.FTPFile;

import java.util.ArrayList;
import java.util.List;

public class RemoveTaskModel extends AsyncTask<String, Void, Void> implements BaseModel<Void> {
    private OperatePresenter operatePresenter;
    private List<String> mCheckList = new ArrayList<>();
    private List<FTPFile> mList = new ArrayList<>();


    public RemoveTaskModel(OperatePresenter operatePresenter, List<String> checkList, List<FTPFile> list) {
        this.operatePresenter = operatePresenter;
        mCheckList.addAll(checkList);
        mList.addAll(list);
        MyLogger.d(mCheckList.toString());
    }

    @Override
    protected Void doInBackground(String... strings) {
        for (String position : mCheckList) {
            MyLogger.d(mCheckList.toString());
            FTPHelper.getInstance().reNameOrMove(FTPHelper.getInstance().getCurrentPath() + "/" + mList.get(Integer.parseInt(position) - 1).getName(), strings[0] + "/" + mList.get(Integer.parseInt(position) - 1).getName());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onSuccess(aVoid);
    }

    @Override
    public void onSuccess(Void aVoid) {
        operatePresenter.onModelSuccess("操作成功");
    }

    @Override
    public void onFail() {

    }
}
