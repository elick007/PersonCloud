package com.example.admin.ftptest.ftphelper;

/**
 * Created by admin on 2018/6/5.
 */

public class ServiceState {
    public static String host="178.128.72.24";
    public static String loginName;
    public static String passwd;

    public static void setLoginName(String name){
        ServiceState.loginName=name;
    }
    public static void setPasswd(String passwd){
        ServiceState.passwd=passwd;
    }
}
