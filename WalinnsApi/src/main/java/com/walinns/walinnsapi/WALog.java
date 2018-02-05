package com.walinns.walinnsapi;

import android.util.Log;

/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class WALog {
    private volatile boolean enableLogging = true;
    private volatile int logLevel = 4;
    protected static WALog instance = new WALog();

    public static WALog getLogger() {
        return instance;
    }

    private WALog() {
    }

    WALog setEnableLogging(boolean enableLogging) {
        this.enableLogging = enableLogging;
        return instance;
    }

    WALog setLogLevel(int logLevel) {
        this.logLevel = logLevel;
        return instance;
    }

    int d(String tag, String msg) {
        return this.enableLogging && this.logLevel <= 3? Log.d(tag, msg):0;
    }

    int d(String tag, String msg, Throwable tr) {
        return this.enableLogging && this.logLevel <= 3?Log.d(tag, msg, tr):0;
    }

    int e(String tag, String msg) {
        return this.enableLogging && this.logLevel <= 6?Log.e(tag, msg):0;
    }

    int e(String tag, String msg, Throwable tr) {
        return this.enableLogging && this.logLevel <= 6?Log.e(tag, msg, tr):0;
    }

    String getStackTraceString(Throwable tr) {
        return Log.getStackTraceString(tr);
    }

    int i(String tag, String msg) {
        return this.enableLogging && this.logLevel <= 4?Log.i(tag, msg):0;
    }

    int i(String tag, String msg, Throwable tr) {
        return this.enableLogging && this.logLevel <= 4?Log.i(tag, msg, tr):0;
    }

    boolean isLoggable(String tag, int level) {
        return Log.isLoggable(tag, level);
    }

    int println(int priority, String tag, String msg) {
        return Log.println(priority, tag, msg);
    }

    int v(String tag, String msg) {
        return this.enableLogging && this.logLevel <= 2?Log.v(tag, msg):0;
    }

    int v(String tag, String msg, Throwable tr) {
        return this.enableLogging && this.logLevel <= 2?Log.v(tag, msg, tr):0;
    }

    int w(String tag, String msg) {
        return this.enableLogging && this.logLevel <= 5?Log.w(tag, msg):0;
    }

    int w(String tag, Throwable tr) {
        return this.enableLogging && this.logLevel <= 5?Log.w(tag, tr):0;
    }

    int w(String tag, String msg, Throwable tr) {
        return this.enableLogging && this.logLevel <= 5?Log.w(tag, msg, tr):0;
    }

    int wtf(String tag, String msg) {
        return this.enableLogging && this.logLevel <= 7?Log.wtf(tag, msg):0;
    }

    int wtf(String tag, Throwable tr) {
        return this.enableLogging && this.logLevel <= 7?Log.wtf(tag, tr):0;
    }

    int wtf(String tag, String msg, Throwable tr) {
        return this.enableLogging && this.logLevel <= 7?Log.wtf(tag, msg, tr):0;
    }
}
