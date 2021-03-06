package com.example.admin.ftptest.utils;

import org.apache.commons.net.ftp.FTPFile;

import java.util.List;

/**
 * Created by admin on 2018/5/23.
 */

public class SortWayFuntion {

    //按时间降序
    public static List<FTPFile> descByTime(List<FTPFile> list) {
        FTPFile temp;
        for (int i = 1; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++)
                if (list.get(i).getTimestamp().before(list.get(j).getTimestamp())) {
                    temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
        }
        return list;
    }

    //按文件名降序
    public static List<FTPFile> descByName(List<FTPFile> list) {
        FTPFile temp;
        int length = list.size() - 1;
        for (int i = 1; i <= list.size() / 2; i++) {
            temp = list.get(i);
            list.set(i, list.get(length));
            list.set(length, temp);
            length--;
        }
        return list;
    }

    //按文件大小升序
    public static List<FTPFile> ascByFileSize(List<FTPFile> list) {
        FTPFile temp;
        for (int i = 1; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++)
                if (list.get(i).getSize() > list.get(j).getSize()) {
                    temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
        }
        return list;
    }

    //按文件大小升序
    public static List<FTPFile> descByFileSize(List<FTPFile> list) {
        FTPFile temp;
        for (int i = 1; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++)
                if (list.get(i).getSize() < list.get(j).getSize()) {
                    temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
        }
        return list;
    }
}
