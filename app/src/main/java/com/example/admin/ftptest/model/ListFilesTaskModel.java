package com.example.admin.ftptest.model;

import android.os.AsyncTask;

import com.example.admin.ftptest.Presenter.BasePresenter;
import com.example.admin.ftptest.Presenter.ListFilesPresenter;
import com.example.admin.ftptest.ftphelper.CallBack;
import com.example.admin.ftptest.ftphelper.FTPHelper;
import com.example.admin.ftptest.myview.SortWayPopup;
import com.example.admin.ftptest.utils.MyLogger;
import com.example.admin.ftptest.utils.SortWayFuntion;
import com.example.admin.ftptest.view.MyActivity;

import org.apache.commons.net.ftp.FTPFile;

import java.util.ArrayList;
import java.util.List;

public class ListFilesTaskModel extends AsyncTask<String, Void, List<FTPFile>> implements BaseModel<List<FTPFile>> {
    private ListFilesPresenter basePresenter;

    public ListFilesTaskModel(BasePresenter basePresenter) {
        this.basePresenter = (ListFilesPresenter) basePresenter;
    }

    private List<FTPFile> doListFiles(final String path) {
        if (FTPHelper.getInstance().isConnected()){
            MyLogger.d("!!isConnect!! doListFiles ");
            return FTPHelper.getInstance().listFTPFile(path);
        }else {
            MyLogger.d("!!is not Connect!! doListFiles fail");
            return null;
        }
    }

    public void doFileSort(MyActivity.FileSortWay fileSortWay, List<FTPFile> ftpFiles){
        List<FTPFile> list=new ArrayList<>();
        list.clear();
        switch (fileSortWay) {
            case DESC_BY_TIME:
                list.addAll(SortWayFuntion.descByTime(ftpFiles));
                break;
            case ASC_BY_FILENAME:
                list.addAll(ftpFiles);
                //basePresenter.doListFiles();
                break;
            case DESC_BY_FILESIZE:
                list.addAll(SortWayFuntion.descByFileSize(ftpFiles));
                break;
            case ASC_BY_FILESIZE:
                list.addAll(SortWayFuntion.ascByFileSize(ftpFiles));
                break;
//        case DESC_BY_FILENAME:
//            list.addAll(SortWayFuntion.descByName(ftpFiles));
//            break;
        }
        onSuccess(list);
    }

    @Override
    public void onSuccess(List<FTPFile> list) {
        basePresenter.onModelSuccess(list);
        //basePresenter.doSortFiles(SortWayPopup.getFileSortWay(),list);
    }

    @Override
    public void onFail() {
        basePresenter.onModelFail();
    }

    @Override
    protected List<FTPFile> doInBackground(String... strings) {
        MyLogger.d("path:"+strings[0]);
           return doListFiles(strings[0]);
    }

    @Override
    protected void onPostExecute(List<FTPFile> list) {
        super.onPostExecute(list);
        MyLogger.d("!!doListFiles  finish");
        doFileSort(SortWayPopup.getFileSortWay(),list);
    }
}
