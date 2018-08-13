package com.example.admin.ftptest.view;

import android.app.Dialog;

import org.apache.commons.net.ftp.FTPFile;

import java.util.List;

public interface BaseView {
    void onPresenterSuccess();
    void onPresenterFail();
    void showRV(List<FTPFile> list);
    void showDialog(Dialog dialog);
    void dismissDialog(Dialog dialog);
    void showPopupWindow();
    void showAnimator();
    void dissmissAnimator();
    void showToast(String s);
    void onLoginSuccessInit();
}
