package com.example.admin.ftptest.ftphelper;

import org.apache.commons.net.ftp.FTPFile;

import java.util.List;

public interface CallBack {
    void onSuccess();
    void onFail();
}
