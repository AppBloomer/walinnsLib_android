package com.walinns.walinnsapi;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class WALifeCycle  implements Application.ActivityLifecycleCallbacks {
    private static final WALog logger = WALog.getLogger();
    protected WalinnsAPIClient mInstance;
    Context mContext;
    private static Double sStartSessionTime;
    public static String starttime;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable check;
    private boolean mIsForeground = true;
    private boolean mPaused = true;
    private final WAConfig mConfig;


    public WALifeCycle(WalinnsAPIClient instance, WAConfig config, Context context) {
        this.mInstance = instance;
        this.mConfig = config;
        mContext = context;
        if (sStartSessionTime == null) {
            sStartSessionTime = Double.valueOf((double) System.currentTimeMillis());
            starttime = WAUtils.getCurrentUTC();

            WALifeCycle.this.mInstance.track_(WALifeCycle.starttime, true);
            logger.e("WAClient session_start_time", String.valueOf(sStartSessionTime));
            logger.e("WAClient", "life_cycle_method" + "inside_lifecycle");

        }

    }


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        logger.e("WAClient Lifecycle", "onActivityCreated");

    }

    @Override
    public void onActivityStarted(Activity activity) {
        logger.e("WAClient Lifecycle", "onActivityStarted");
        new WAGesture(this.mInstance, activity);


    }

    @Override
    public void onActivityResumed(Activity activity) {
        logger.e("WAClient Lifecycle", "onActivityResumed");
        this.mPaused = false;
        boolean wasBackground = !this.mIsForeground;
        this.mIsForeground = true;
        if (this.check != null) {
            this.mHandler.removeCallbacks(this.check);
        }
        logger.e("WAClient session length background", String.valueOf(wasBackground));
        sStartSessionTime = Double.valueOf((double) System.currentTimeMillis());
        starttime = WAUtils.getCurrentUTC();
        logger.e("WAClient session length session start time", String.valueOf(sStartSessionTime));
        mContext.startService(new Intent(mContext, WAService.class)); //start service which is MyService.java
        WALifeCycle.this.mInstance.track_(WAUtils.getCurrentUTC(), true);


    }

    @Override
    public void onActivityPaused(Activity activity) {
        logger.e("WAClient Lifecycle", "onActivityPaused");
        this.mPaused = true;
        if (this.check != null) {
            this.mHandler.removeCallbacks(this.check);
        }
        mContext.stopService(new Intent(mContext, WAService.class)); //start service which is MyService.java

        this.mHandler.postDelayed(this.check = new Runnable() {
            public void run() {
                logger.e("WAClient session length_if", String.valueOf(WALifeCycle.this.mIsForeground) + WALifeCycle.this.mPaused);
                WALifeCycle.this.mInstance.sendpush();
                if (WALifeCycle.this.mIsForeground && WALifeCycle.this.mPaused) {
                    WALifeCycle.this.mIsForeground = false;

                    try {
                        double e = (double) System.currentTimeMillis() - WALifeCycle.sStartSessionTime.doubleValue();
                        if (e >= (double) WALifeCycle.this.mConfig.getMinimumSessionDuration() && e < (double) WALifeCycle.this.mConfig.getSessionTimeoutDuration()) {
                            NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
                            nf.setMaximumFractionDigits(1);
                            logger.e("WAClient session differ", String.valueOf(((double) System.currentTimeMillis())) + "....." + (WALifeCycle.sStartSessionTime.doubleValue()) / 1000.0D);

                            String sessionLengthString = nf.format(((double) System.currentTimeMillis() - WALifeCycle.sStartSessionTime.doubleValue()) / 1000.0D);
                            logger.e("WAClient session length", sessionLengthString);
                            JSONObject sessionProperties = new JSONObject();
                            sessionProperties.put("$ae_session_length", sessionLengthString);
                            sessionProperties.put("$start_time", WALifeCycle.starttime);
                            sessionProperties.put("$end_time", WAUtils.getCurrentUTC());
                            WALifeCycle.this.mInstance.track_("$ae_session", sessionProperties, true);
                            WALifeCycle.this.mInstance.track_(WAUtils.getCurrentUTC(), false);


                        }
                    } catch (JSONException var6) {
                        var6.printStackTrace();
                    }

                    // WALifeCycle.this.mInstance.onBackground();
                }

            }
        }, 500L);

    }

    @Override
    public void onActivityStopped(Activity activity) {
        logger.e("WAClient Lifecycle", "onActivityStopped");
        mContext.stopService(new Intent(mContext, WAService.class)); //start service which is MyService.java

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        logger.e("WAClient Lifecycle", "onActivitySaveInstanceState");

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        logger.e("WAClient Lifecycle", "onActivityDestroyed");
        mContext.stopService(new Intent(mContext, WAService.class)); //start service which is MyService.java


    }
}
