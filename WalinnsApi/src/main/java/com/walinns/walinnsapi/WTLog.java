package com.walinns.walinnsapi;

import android.util.Log;

/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class WTLog {
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int NONE = 2147483647;
    private static int sMinLevel = 5;

    public WTLog() {
    }

    public static void setLevel(int minLevel) {
        sMinLevel = minLevel;
    }

    public static void v(String tag, String message) {
        if(shouldLog(2)) {
            Log.v(tag, message);
        }

    }

    public static void v(String tag, String message, Throwable throwable) {
        if(shouldLog(2)) {
            Log.v(tag, message, throwable);
        }

    }

    public static void d(String tag, String message) {
        if(shouldLog(3)) {
            Log.d(tag, message);
        }

    }

    public static void d(String tag, String message, Throwable throwable) {
        if(shouldLog(3)) {
            Log.d(tag, message, throwable);
        }

    }

    public static void i(String tag, String message) {
        if(shouldLog(4)) {
            Log.i(tag, message);
        }

    }

    public static void i(String tag, String message, Throwable throwable) {
        if(shouldLog(4)) {
            Log.i(tag, message, throwable);
        }

    }

    public static void w(String tag, String message) {
        if(shouldLog(5)) {
            Log.w(tag, message);
        }

    }

    public static void w(String tag, String message, Throwable throwable) {
        if(shouldLog(5)) {
            Log.w(tag, message, throwable);
        }

    }

    public static void e(String tag, String message) {
        if(shouldLog(6)) {
            Log.e(tag, message);
        }

    }

    public static void e(String tag, String message, Throwable throwable) {
        if(shouldLog(6)) {
            Log.e(tag, message, throwable);
        }

    }

    public static void wtf(String tag, String message) {
        if(shouldLog(6)) {
            Log.wtf(tag, message);
        }

    }

    public static void wtf(String tag, String message, Throwable throwable) {
        if(shouldLog(6)) {
            Log.wtf(tag, message, throwable);
        }

    }

    private static boolean shouldLog(int level) {
        return sMinLevel <= level;
    }
}
