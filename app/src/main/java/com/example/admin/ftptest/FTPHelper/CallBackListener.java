package com.example.admin.ftptest.FTPHelper;

import org.apache.commons.net.ftp.FTPFile;

import java.util.List;

/**
 * Created by admin on 2018/5/23.
 */

public interface CallBackListener {
    //void onProgress();
    void getFTPFileList(List<FTPFile> ftpFileList);
    void onFinsh(Boolean b);
}
