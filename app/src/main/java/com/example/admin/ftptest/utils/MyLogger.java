package com.example.admin.ftptest.utils;

import android.util.Log;

public class MyLogger {
    private static boolean debug;
    private static String TAG="personCloud";
    public static void setDebug(){
        debug=true;
    }
    public static void d(String s){
        if (debug){
            Log.d(TAG,s);
        }
    }
    public static void e(String s){
        if (debug){
            Log.e(TAG,s);
        }
    }
}
