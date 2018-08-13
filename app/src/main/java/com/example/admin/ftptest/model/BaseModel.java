package com.example.admin.ftptest.model;

public interface BaseModel<T>{
    void onSuccess(T t);
    void onFail();
}
