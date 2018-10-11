package com.example.admin.ftptest.myview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.admin.ftptest.Presenter.OperatePresenter;
import com.example.admin.ftptest.R;
import com.example.admin.ftptest.view.BaseView;

/**
 * Created by admin on 2018/5/25.
 */

public class NewDirDialog extends Dialog implements View.OnClickListener {
    private Context context;      // 上下文
    private EditText editText;
    public NewDirDialog(Context context) {
        super(context, R.style.dialog_new_dir); //dialog的样式
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置为居中
        }
        View contentView= LayoutInflater.from(context).inflate(R.layout.custom_dialog_layout,null);
        setContentView(contentView);
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth()*4/5; // 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);// 点击Dialog外部消失
        TextView title=contentView.findViewById(R.id.dialog_title);
        title.setText("请输入新建文件名");
        TextView sure = contentView.findViewById(R.id.dialog_sure);
        TextView cancel = contentView.findViewById(R.id.dialog_cancel);
        editText=contentView.findViewById(R.id.edit_new_dir);
        sure.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        switch (view.getId()){
            case R.id.dialog_sure:
               String dirName= editText.getText().toString().trim();
                OperatePresenter createNewDir=new OperatePresenter((BaseView) context);
                createNewDir.doNewDirTask(dirName);
                break;
            case R.id.dialog_cancel:
                break;
        }
    }
}
