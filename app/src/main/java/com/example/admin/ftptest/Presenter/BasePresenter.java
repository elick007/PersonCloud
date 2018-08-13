package com.example.admin.ftptest.Presenter;

public interface BasePresenter<T> {
    void onModelSuccess(T t);
    void onModelFail();
}
