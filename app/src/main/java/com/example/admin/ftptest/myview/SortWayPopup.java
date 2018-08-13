package com.example.admin.ftptest.myview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.admin.ftptest.MainActivity;
import com.example.admin.ftptest.Presenter.ListFilesPresenter;
import com.example.admin.ftptest.R;
import com.example.admin.ftptest.view.MyActivity;

import org.apache.commons.net.ftp.FTPFile;

import java.util.List;

/**
 * Created by admin on 2018/5/16.
 */

public class SortWayPopup extends PopupWindow implements View.OnClickListener {
    private View mMenuView;
    private TextView descByTime,ascByFileName,ascByFileSize,descByFileSize;
    private static MyActivity.FileSortWay  fileSortWay=MyActivity.FileSortWay.ASC_BY_FILENAME;
    private MyActivity myAct;
    private TextView sortWay;
    private List<FTPFile> ftpFiles;
    public SortWayPopup(final MyActivity context, TextView sortWay, List<FTPFile> ftpFiles) {
        super(context);
        this.myAct=context;
        this.sortWay=sortWay;
        this.ftpFiles=ftpFiles;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            mMenuView = inflater.inflate(R.layout.header_sortway_choise_layout, null);
        }
        descByTime = mMenuView.findViewById(R.id.desc_by_time);
        ascByFileName = mMenuView.findViewById(R.id.asc_by_fileName);
        ascByFileSize = mMenuView.findViewById(R.id.asc_by_fileSize);
        descByFileSize = mMenuView.findViewById(R.id.desc_by_fileSize);
        descByTime.setOnClickListener(this);
        ascByFileName.setOnClickListener(this);
        ascByFileSize.setOnClickListener(this);
        descByFileSize.setOnClickListener(this);
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(260);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xFFE6E6E6);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

    public static MyActivity.FileSortWay getFileSortWay() {
        return fileSortWay;
    }

    @Override
    public void onClick(View v) {
          dismiss();
        ListFilesPresenter listFilesPresenter=new ListFilesPresenter(myAct,"");
            switch (v.getId()) {
                case R.id.desc_by_time:
                    fileSortWay = MyActivity.FileSortWay.DESC_BY_TIME;//更改标记按时间降序
                    sortWay.setText("按时间降序");
                    listFilesPresenter.doSortFiles(fileSortWay,ftpFiles);
                    break;
                case R.id.asc_by_fileName:
                    fileSortWay = MyActivity.FileSortWay.ASC_BY_FILENAME;//更改标记按文件名升序
                    sortWay.setText("文件名升序");
                    listFilesPresenter.doSortFiles(fileSortWay,ftpFiles);
                    //listFilesPresenter.doListFiles();
                    break;
                case R.id.asc_by_fileSize:
                    fileSortWay = MyActivity.FileSortWay.ASC_BY_FILESIZE;//更改标记按文件大小升序
                    sortWay.setText("按大小升序");
                    listFilesPresenter.doSortFiles(fileSortWay,ftpFiles);
                    break;
                case R.id.desc_by_fileSize:
                    fileSortWay = MyActivity.FileSortWay.DESC_BY_FILESIZE;//更改标记按文件大小降序
                    sortWay.setText("按大小降序");
                    listFilesPresenter.doSortFiles(fileSortWay,ftpFiles);
                    break;
            }
    }
}
