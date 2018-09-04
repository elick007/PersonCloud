package com.example.admin.ftptest.Presenter;

import com.example.admin.ftptest.model.ListFilesTaskModel;
import com.example.admin.ftptest.view.BaseView;
import com.example.admin.ftptest.view.MyActivity;

import org.apache.commons.net.ftp.FTPFile;

import java.util.List;

public class ListFilesPresenter implements BasePresenter<List<FTPFile>> {
    private BaseView myActivity;
    private String path;
    private ListFilesTaskModel listFilesTaskModel;
    public ListFilesPresenter(BaseView myActivity, String path) {
        this.myActivity = myActivity;
        this.path = path;
        this.listFilesTaskModel = new ListFilesTaskModel(this);
    }

    public void doListFiles() {
        listFilesTaskModel.execute(path);
    }
    public void doSortFiles(MyActivity.FileSortWay fileSortWay,List<FTPFile> list){
        listFilesTaskModel.doFileSort(fileSortWay,list);}
    @Override
    public void onModelSuccess(List<FTPFile> list) {
        myActivity.showRV(list);
       // MyLogger.d(list.isEmpty()?"yes":"no");
    }

    @Override
    public void onModelFail() {
        myActivity.showToast("刷新列表失败");
    }
}
