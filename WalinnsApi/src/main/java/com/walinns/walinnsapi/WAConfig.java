package com.walinns.walinnsapi;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class WAConfig {
    private static WAConfig sInstance;
    private static final Object sInstanceLock = new Object();
    private static final WALog logger = WALog.getLogger();
    private SSLSocketFactory mSSLSocketFactory;
    public static boolean DEBUG = false;
    private final int mMinSessionDuration;
    private final int mSessionTimeoutDuration;
    public static WAConfig getInstance(Context context) {
        Object var1 = sInstanceLock;
        synchronized(sInstanceLock) {
            if(null == sInstance) {
                logger.e("WalinnsTrackerClient instance null or not", "null");

                Context appContext = context.getApplicationContext();
                sInstance = readConfig(appContext);
            }else {
                logger.e("WalinnsTrackerClient instance null or not", "not null");
            }
        }

        return sInstance;
    }
    static WAConfig readConfig(Context appContext) {
        String packageName = appContext.getPackageName();
        logger.e("WalinnsTrackerClient instance null or pck", packageName);


        try {
            ApplicationInfo e = appContext.getPackageManager().getApplicationInfo(packageName, 128);
            Bundle configBundle = e.metaData;
            if(null == configBundle) {
                configBundle = new Bundle();
            }

            return new WAConfig(configBundle, appContext);
        } catch (PackageManager.NameNotFoundException var4) {
            throw new RuntimeException("Can\'t configure Mixpanel with package name " + packageName, var4);
        }
    }
    WAConfig(Bundle metaData, Context context) {
        SSLSocketFactory foundSSLFactory;
        try {
            SSLContext notificationChannelId = SSLContext.getInstance("TLS");
            notificationChannelId.init((KeyManager[])null, (TrustManager[])null, (SecureRandom)null);
            foundSSLFactory = notificationChannelId.getSocketFactory();
        } catch (GeneralSecurityException var11) {
            logger.e("WalinnsTrackerClient", "System has no SSL support. Built-in events editor will not be available", var11);
            foundSSLFactory = null;
        }

        this.mSSLSocketFactory = foundSSLFactory;
        DEBUG = metaData.getBoolean("com.mixpanel.android.MPConfig.EnableDebugLogging", false);
        if(DEBUG) {
            WTLog.setLevel(2);
        }
        logger.e("WalinnaTrackerClient config", metaData.toString());
        this.mMinSessionDuration = metaData.getInt("com.example.walinnstracker.MinimumSessionDuration", 10000);
        this.mSessionTimeoutDuration = metaData.getInt("com.example.walinnstracker.SessionTimeoutDuration", 2147483647);
    }
    public synchronized void setSSLSocketFactory(SSLSocketFactory factory) {
        this.mSSLSocketFactory = factory;
    }
    public int getSessionTimeoutDuration() {
        return this.mSessionTimeoutDuration;
    }
    public int getMinimumSessionDuration() {
        return this.mMinSessionDuration;
    }
}
