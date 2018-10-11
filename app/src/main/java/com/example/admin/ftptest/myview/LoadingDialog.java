package com.example.admin.ftptest.myview;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.admin.ftptest.R;


/**
 * 自定义dialog
 */
public class LoadingDialog extends Dialog {

    private Context context;
    private static LoadingDialog dialog;
    private static ImageView ivProgress;


    public LoadingDialog(Context context) {
        super(context);
        this.context = context;
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;

    }
    //显示dialog的方法
    public static LoadingDialog showDialog(Context context){
        dialog = new LoadingDialog(context, R.style.LoadDialog);//dialog样式
        dialog.setContentView(R.layout.dialog_layout);//dialog布局文件
        ivProgress = (ImageView) dialog.findViewById(R.id.ivProgress);
        dialog.setCanceledOnTouchOutside(false);//点击外部不允许关闭dialog
        return dialog;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus && dialog != null){
            startAnimation();
        }else{
            endAnimation();
            this.cancel();
        }
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
        endAnimation();
    }

    private void startAnimation() {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.dialog_progress_anim);
        ivProgress.startAnimation(animation);
    }

    private void endAnimation() {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.dialog_progress_anim);
        animation.cancel();
    }
}
