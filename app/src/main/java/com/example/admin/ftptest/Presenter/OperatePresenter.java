package com.example.admin.ftptest.Presenter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import com.example.admin.ftptest.adapter.FileAdapter;
import com.example.admin.ftptest.ftphelper.FTPHelper;
import com.example.admin.ftptest.model.DeleteFilesTaskModel;
import com.example.admin.ftptest.model.NewDirTaskModel;
import com.example.admin.ftptest.model.ReNameTaskModel;
import com.example.admin.ftptest.model.RemoveTaskModel;
import com.example.admin.ftptest.myview.CopyMoveDialog;
import com.example.admin.ftptest.utils.MyLogger;
import com.example.admin.ftptest.view.BaseView;
import com.example.admin.ftptest.view.MyActivity;

import org.apache.commons.net.ftp.FTPFile;

import java.util.ArrayList;
import java.util.List;

public class OperatePresenter implements BasePresenter<String> {
    private MyActivity myActivity;

    public OperatePresenter(BaseView baseView) {
        this.myActivity = (MyActivity) baseView;
    }

    /**
     * 新建文件夹
     * @param dirName 文件夹名
     */
    public void doNewDirTask(String dirName) {
        NewDirTaskModel newDirTaskModel = new NewDirTaskModel(dirName, this);
        newDirTaskModel.execute();
    }

    /**
     * 删除文件
     * @param checkList 选中的list
     * @param list 总list
     */
    public void doDeleteFile(final List<String> checkList, final List<FTPFile> list) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(myActivity);
        alertDialog.setTitle("删除");
        alertDialog.setMessage("删除这" + checkList.size() + "个文件");
        alertDialog.setCancelable(false);
        FileAdapter.setIsOnCheckChange(false);//设置CheckBox不响应监听
        alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final ProgressDialog progressDialog = new ProgressDialog(myActivity);
                progressDialog.setMessage("文件删除中");
                progressDialog.show();
                DeleteFilesTaskModel deleteFilesTaskModel = new DeleteFilesTaskModel(OperatePresenter.this, checkList, list, progressDialog);
                deleteFilesTaskModel.execute();
                myActivity.dissmissAnimator();
                dialogInterface.dismiss();
            }
        });
        alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                myActivity.dissmissAnimator();
                //FileAdapter.setIsOnCheckChange(true);//设置CheckBox响应监听
            }
        });
        alertDialog.show();
    }

    /**
     * 移动文件操作
     * @param checkList
     * @param list
     */
    public void doRemoveFiles(final List<String> checkList, final List<FTPFile> list) {
        final List<String> mCheck = new ArrayList<>(checkList);
        final List<FTPFile> mList = new ArrayList<>(list);
        FileAdapter.setIsOnCheckChange(false);//取消CheckBox监听
        CopyMoveDialog copyMoveDialog = new CopyMoveDialog(myActivity, new CopyMoveDialog.ChooseListener() {
            @Override
            public void chooseConfirm(final String path) {
                MyLogger.d("移动包含的列表："+mCheck.toString());
                RemoveTaskModel removeTaskModel = new RemoveTaskModel(OperatePresenter.this, mCheck, mList);
                removeTaskModel.execute(path);
            }

            @Override
            public void chooseCancel() {
                //FileAdapter.setIsOnCheckChange(true);
            }
        });
        copyMoveDialog.setCancelable(false);
        copyMoveDialog.show();
        myActivity.dissmissAnimator();
    }

    /**
     * 重命名操作
     * @param oldName
     * @param newName
     */
    public void doRenameFile(String oldName,String newName){
        myActivity.dissmissAnimator();
        new ReNameTaskModel(this,oldName, newName).execute();

}
    @Override
    public void onModelSuccess(String s) {
        myActivity.showToast(s);
        ListFilesPresenter listFilesPresenter = new ListFilesPresenter(myActivity, FTPHelper.getInstance().getCurrentPath());
        listFilesPresenter.doListFiles();
    }

    @Override
    public void onModelFail() {

    }
}
