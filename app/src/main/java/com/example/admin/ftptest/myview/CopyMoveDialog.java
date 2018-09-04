package com.example.admin.ftptest.myview;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;

import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.admin.ftptest.adapter.DirAdapter;
import com.example.admin.ftptest.ftphelper.CallBackListener;

import com.example.admin.ftptest.model.ListPathDirTask;

import com.example.admin.ftptest.R;

import org.apache.commons.net.ftp.FTPFile;

import java.util.ArrayList;
import java.util.List;


/**复制或移动Dialog
 * Created by admin on 2018/6/5.
 */

public class CopyMoveDialog extends Dialog implements CallBackListener{
    private Context context;
    private DirAdapter dirAdapter;
    private List<FTPFile> list = new ArrayList<>();
    private TextView choosePath;
    private ChooseListener chooseListener;
    private String path="";
    public CopyMoveDialog(@NonNull Context context,ChooseListener chooseListener) {
        super(context);
        this.context = context;
        this.chooseListener=chooseListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_copy_move);
        RecyclerView recyclerView = findViewById(R.id.dialog_recyclerView);
        TextView cancel = findViewById(R.id.dialog_copy_move_cancel);
        TextView confirm = findViewById(R.id.dialog_copy_move_sure);
        choosePath=findViewById(R.id.dialog_copy_move_choosePath);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                chooseListener.chooseConfirm(path);
            }
        });
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth() * 8 / 9; // 设置dialog宽度为屏幕的8/9
        lp.height = display.getHeight() * 6 / 7;
        getWindow().setAttributes(lp);
        final ListPathDirTask listPathDirTask = new ListPathDirTask(path, this);
        listPathDirTask.execute();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        dirAdapter = new DirAdapter(list);
        recyclerView.setAdapter(dirAdapter);//设置适配器
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));//设置分割线
        dirAdapter.setOnItemClickListener(new DirAdapter.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(View itemView, int position) {
                if (position==0){
                    if (!path.equals("")){
                        path=path.substring(0, path.lastIndexOf("/"));
                    }
                }else {
                    path += "/"+list.get(position).getName();
                }
                new ListPathDirTask(path, CopyMoveDialog.this).execute();
                //Toast.makeText(context, path, Toast.LENGTH_SHORT).show();
                choosePath.setText("选择为："+path);
            }
        });
    }

    public interface ChooseListener {
        void chooseConfirm(String path);
        void chooseCancel();
    }

    @Override
    public void getFTPFileList(List<FTPFile> ftpFileList) {
        list.clear();
        list.addAll(ftpFileList);
        dirAdapter.notifyDataSetChanged();
//        for (FTPFile ftpFile : list) {
//            Log.e("sfj", ftpFile.getName());
//        }
    }

    @Override
    public void onFinsh(Boolean b) {

    }

}
