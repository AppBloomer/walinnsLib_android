package com.walinns.walinnsapi;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class WAWorkerThread extends HandlerThread {
    private Handler handler;

    public WAWorkerThread(String name) {
        super(name);
    }

    Handler getHandler() {
        return this.handler;
    }

    void post(Runnable r) {
        this.waitForInitialization();
        this.handler.post(r);
    }

    void postDelayed(Runnable r, long delayMillis) {
        this.waitForInitialization();
        this.handler.postDelayed(r, delayMillis);
    }

    void removeCallbacks(Runnable r) {
        this.waitForInitialization();
        this.handler.removeCallbacks(r);
    }

    private synchronized void waitForInitialization() {
        if(this.handler == null) {
            this.handler = new Handler(this.getLooper());
        }

    }
}
