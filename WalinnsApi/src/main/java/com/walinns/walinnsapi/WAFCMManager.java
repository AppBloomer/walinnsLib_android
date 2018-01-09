package com.walinns.walinnsapi;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class WAFCMManager {
    static Context context = null;
    static final WALog logger = WALog.getLogger();
    static final ExecutorService es = Executors.newFixedThreadPool(1);
    static long EXECUTOR_THREAD_ID = 0;
    static Boolean areGoogleServicesAvailable = null;
    static WAPref sharedPref;

    static void onTokenRefresh(Context context) {
        WAFCMManager.context = context;
        sharedPref=new WAPref(context);
        doFCMRefresh();
    }
    static void doFCMRefresh() {
        postAsyncSafely("FcmManager#doFCMRefresh", new Runnable() {
            @Override
            public void run() {
                if (!isGooglePlayServicesAvailable(context)) {
                    logger.e("WalinnsTrackerClient"+"FcmManager: Play Services unavailable, unable to request FCM token","available");
                    return;
                }
                String freshToken = FCMGetFreshToken();
                if (freshToken == null) return;
                cacheFCMToken(freshToken);
            }
        });

    }

    private static void postAsyncSafely(final String name, final Runnable runnable) {
        try {
            final boolean executeSync = Thread.currentThread().getId() == EXECUTOR_THREAD_ID;

            if (executeSync) {
                runnable.run();
            } else {
                es.submit(new Runnable() {
                    @Override
                    public void run() {
                        EXECUTOR_THREAD_ID = Thread.currentThread().getId();
                        try {
                            //Logger.logFine("Executor service: Starting task - " + name);
                            //final long start = System.currentTimeMillis();
                            runnable.run();
                            //final long time = System.currentTimeMillis() - start;
                            //Logger.logFine("Executor service: Task completed successfully in " + time + "ms (" + name + ")");
                        } catch (Throwable t) {
                            logger.e("WalinnsTrackerClient"+"Executor service: Failed to complete the scheduled task", String.valueOf(t));
                        }
                    }
                });
            }
        } catch (Throwable t) {
            logger.e("WalinnsTrackerClient"+"Failed to submit task to the executor service", String.valueOf(t));
        }
    }
    static boolean isGooglePlayServicesAvailable(Context context){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }
    private static boolean isGooglePlayServicesAvailable() {
        if (areGoogleServicesAvailable == null) {
            try {
                GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
                int result = googleAPI.isGooglePlayServicesAvailable(context);
                //int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
                areGoogleServicesAvailable = (result == ConnectionResult.SUCCESS);

                if (areGoogleServicesAvailable) {
                    logger.e("WalinnsTrackerClient"+"Google Play services availabile","");
                } else {
                    logger.e("WalinnsTrackerClient"+"Google Play services not available","");
                }

            } catch (Throwable t) {
                logger.e("WalinnsTrackerClient"+"Error checking Google Play services availability", t.toString());
                areGoogleServicesAvailable = false;
            }
        }
        return areGoogleServicesAvailable;
    }
    private static String FCMGetFreshToken() {
        logger.e("WalinnsTrackerClient","FcmManager: Requesting a FCM token");
        String token = null;
        try {
            token = FirebaseInstanceId.getInstance().getToken();
            logger.e("WalinnsTrackerClient"+"FCM token : ",token);
        } catch (Throwable t) {
            logger.e("WalinnsTrackerClient"+"FcmManager: Error requesting FCM token", t.toString());
        }
        return token;
    }
    private static void cacheFCMToken(String token) {
        try {
            if (token == null || alreadyHaveFCMToken(token)) return;


            if (sharedPref.getValue(WAPref.push_token) == null) return;

            sharedPref.save(WAPref.push_token,token);
        } catch (Throwable t) {
            logger.e("WalinnsTrackerClient","FcmManager: Unable to cache FCM Token", t);
        }
    }
    private static boolean alreadyHaveFCMToken(final String newToken) {
        if (newToken == null) return false;
        String cachedToken = getCachedFCMToken();
        logger.e("WalinnsTrackerClient","Older token:" + cachedToken+" new token: "+ newToken );
        return (cachedToken != null && cachedToken.equals(newToken));
    }
    private static String getCachedFCMToken() {
        if(sharedPref.getValue(WAPref.push_token).isEmpty()){
            return null;
        }else {
            return sharedPref.getValue(WAPref.push_token);
        }

    }
}
