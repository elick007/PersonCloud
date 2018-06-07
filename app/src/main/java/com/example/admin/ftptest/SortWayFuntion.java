package com.example.admin.ftptest;

import org.apache.commons.net.ftp.FTPFile;

import java.util.List;

/**
 * Created by admin on 2018/5/23.
 */

public class SortWayFuntion {

//    public List<FTPFile> descByTime(List<FTPFile> list,int s,int t) {
//        int i=s,j=t;
//        FTPFile temp;
//        if (s<t){
//            temp=list.get(s);
//            while (i!=j){
//                while (j>i&&list.get(j).getTimestamp().before(temp.getTimestamp()));
//                j--;
//                list.set(i,list.get(j));
//                while (i<j&& list.get(i).getTimestamp().after(temp.getTimestamp()));
//                i++;
//                list.set(j,list.get(i));
//            }
//            list.set(i,temp);
//            descByTime(list,s,i-1);
//            descByTime(list,i+1,t);
//        }
//        return list;
//    }
    //按时间降序
    public List<FTPFile> descByTime(List<FTPFile> list) {
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
    public List<FTPFile> descByName(List<FTPFile> list) {
        FTPFile temp;
        int length = list.size() - 1;
        for (int i = 1; i <= list.size() / 2; i++) {
            temp = list.get(i);
            list.set(i, list.get(length));
            list.set(length, temp);
            length--;
        }
//        for (int i=0;i<tempList.size();i++){
//            Log.e("descbyname",tempList.get(i).getName());
//        }
        return list;
    }

    //按文件大小升序
    public List<FTPFile> ascByFileSize(List<FTPFile> list) {
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
    public List<FTPFile> descByFileSize(List<FTPFile> list) {
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
