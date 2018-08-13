package com.example.admin.ftptest.Presenter;
import com.example.admin.ftptest.model.LoginTaskModel;
import com.example.admin.ftptest.utils.MyLogger;
import com.example.admin.ftptest.view.BaseView;
import com.example.admin.ftptest.view.MyActivity;

public class LoginPresenter implements BasePresenter<Void> {
    private BaseView mainAct;

    public LoginPresenter(MyActivity myActivity) {
        this.mainAct = myActivity;
        LoginTaskModel loginTaskModel = new LoginTaskModel(this);
        loginTaskModel.execute();
    }

    @Override
    public void onModelSuccess(Void aVoid) {
        //登录成功则刷新recyclerView,登录成功初始化
        //mainAct.showToast("登录成功");
        MyLogger.d("登录成功");
        mainAct.onLoginSuccessInit();
    }

    @Override
    public void onModelFail() {
        MyLogger.d("登录失败");
    }
}
