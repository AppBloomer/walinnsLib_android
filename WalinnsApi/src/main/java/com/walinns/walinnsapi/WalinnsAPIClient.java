package com.walinns.walinnsapi;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Patterns;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class WalinnsAPIClient extends Activity implements GoogleApiClient.ConnectionCallbacks ,GoogleApiClient.OnConnectionFailedListener{
    private static final WALog logger = WALog.getLogger();
    private WADeviceInfo deviceInfo;
    protected Context context,mContext;
    protected String deviceId;
    protected String project_token;
    WAWorkerThread logThread;
    WAWorkerThread httpThread;
    protected String instanceName;
    protected WAPref shared_pref;
    protected WALifeCycle mWalinnsactivitylifecycle;
    GoogleApiClient client;
    public WalinnsAPIClient(Context context) {
        this((String)null);
        mContext=context;
        this.shared_pref=new WAPref(mContext);
        Thread.setDefaultUncaughtExceptionHandler(handleAppCrash);
    }

    public WalinnsAPIClient(String instance) {
        this.logThread = new WAWorkerThread("logThread");
        this.httpThread = new WAWorkerThread("httpThread");
        Thread.setDefaultUncaughtExceptionHandler(handleAppCrash);

        this.instanceName = WAUtils.normalizeInstanceName(instance);
        this.logThread.start();
        this.httpThread.start();

        //this.apiService= APIClient.getClient().create(ApiService.class);

    }

    public WalinnsAPIClient initialize(Context context, String project_token) {
        this.mContext=context;
        new APIClient(mContext);
        this.shared_pref=new WAPref(context);
        shared_pref.save(WAPref.project_token,project_token);

        logger.d("WalinnsTrackerClient Token:" , project_token );
        return this.initialize(context, project_token, (String)null);
    }

    private synchronized WalinnsAPIClient initialize(final Context context, String apiKey, final String userId) {
        // connect to app and dashboard
        if(context == null) {
            logger.e("WalinnsTrackerClient", "Argument context cannot be null in initialize()");
            return this;
        } else if(WAUtils.isEmptyString(apiKey)) {
            logger.e("WalinnsTrackerClient", "Argument apiKey cannot be null or blank in initialize()");
            return this;
        }else {
            this.context = context.getApplicationContext();
            this.project_token = apiKey;

            this.runOnLogThread(new Runnable() {
                @Override
                public void run() {
                    WADeviceInfo.CachedInfo cachedInfo=initializeDeviceInfo();
                    logger.e("Device_data)))",cachedInfo.country+ cachedInfo.model);
                    JSONObject hashMap =new JSONObject();

                    try {
                        hashMap.put("device_id",deviceId);
                        hashMap.put("device_model",cachedInfo.brand);
                        hashMap.put("os_name",cachedInfo.osName);
                        hashMap.put("os_version",cachedInfo.osVersion);
                        hashMap.put("app_version",cachedInfo.app_version);
                        hashMap.put("connectivity",cachedInfo.connectivty);
                        hashMap.put("carrier", cachedInfo.carrier);
                        hashMap.put("play_service",String.valueOf(cachedInfo.playservice));
                        hashMap.put("bluetooth",String.valueOf(cachedInfo.bluetooth));
                        hashMap.put("screen_dpi",cachedInfo.screen_dpi);
                        hashMap.put("screen_height",cachedInfo.screen_height);
                        hashMap.put("screen_width",cachedInfo.screen_width);
                        hashMap.put("age",cachedInfo.age);
                        hashMap.put("gender",cachedInfo.gender);
                        hashMap.put("language",cachedInfo.language);
                        hashMap.put("country", cachedInfo.country);
                        hashMap.put("email",cachedInfo.mail);
                        hashMap.put("date_time",WAUtils.getCurrentUTC());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    getGender(cachedInfo.mail);
                    logger.e("Request_Data",hashMap.toString() );

                    // Call<ResponseBody> call = apiService.fetch_device(hashMap);
                    // call.enqueue(device_response);

                    new APIClient(mContext,"devices",hashMap);


                }
            });
        }
        return this;
    }

    private  WADeviceInfo.CachedInfo initializeDeviceInfo() {
        
        this.deviceInfo = new WADeviceInfo(mContext);
        this.deviceId =  Settings.Secure.getString(this.context.getContentResolver(), "android_id");
        shared_pref.save(WAPref.device_id,deviceId);
        logger.e("WalinnsTrackerClient",deviceId +"..."+ deviceInfo.toString());
        this.deviceInfo.prefetch();
        System.out.println("Device_data"+deviceInfo.getCountry()+"...."+deviceInfo.getOsName());
        return this.deviceInfo.prefetch();
    }
    protected void runOnLogThread(Runnable r) {
        if(Thread.currentThread() != this.logThread) {
            this.logThread.post(r);
        } else {
            r.run();

        }

    }

//    private Callback<ResponseBody> callResponse = new Callback<ResponseBody>() {
//        @Override
//        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//            logger.e("WalinnsTrackerClient Device Response", String.valueOf(response.isSuccessful()));
//        }
//
//        @Override
//        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
//            logger.e("WalinnsTrackerClient Device Response", call.toString()+throwable.toString());
//
//        }
//    };
//    public Callback<ResponseBody> device_response = new Callback<ResponseBody>() {
//        @Override
//        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//            logger.e("WalinnsTrackerClient Device Response", String.valueOf(response.isSuccessful()));
//            if(response.isSuccessful()){
//                registerWalinnsActivityLifecycleCallbacks();
//            }
//        }
//
//        @Override
//        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
//            logger.e("WalinnsTrackerClient Device Response", call.toString()+throwable.toString());
//
//        }
//    };


    protected long getCurrentTimeMillis() {
        logger.e("Current session",String.valueOf(System.currentTimeMillis()));
        return System.currentTimeMillis();
    }

    public void track(String eventType /*view name like Button*/, String event_name/*Button name like submit*/) {
        this.logEvent(eventType, event_name);
    }

    private void logEvent(String eventType, String event_name) {
        if(this.validateLogEvent(eventType)) {
            this.logEventAsync(eventType,event_name, this.getCurrentTimeMillis());
        }
    }
    protected boolean validateLogEvent(String eventType) {
        if(TextUtils.isEmpty(eventType)) {
            logger.e("WalinnsTrackerClient", "Argument eventType cannot be null or blank in eventTrack()");
            return false;
        } else {
            return this.contextAndApiKeySet("logEvent()");
        }
    }
    protected synchronized boolean contextAndApiKeySet(String methodName) {
        if(this.context == null) {
            logger.e("WalinnsTrackerClient", "context cannot be null, set context with initialize() before calling " + methodName);
            return false;
        } else if(TextUtils.isEmpty(this.project_token)) {
            logger.e("WalinnsTrackerClient", "apiKey cannot be null or empty, set apiKey with initialize() before calling " + methodName);
            return false;
        } else {
            return true;
        }
    }
    protected void logEventAsync(final String eventType,final String event_name,final long timestamp) {
        if(event_name != null) {
            this.runOnLogThread(new Runnable() {
                public void run() {
                    WalinnsAPIClient.this.logEvent(eventType,event_name ,timestamp);
                }
            });
        }


    }

    private void logEvent(String eventType, String event_name, long timestamp) {
        deviceId=shared_pref.getValue(WAPref.device_id);
        logger.e("walinnstrackerclient device_id",deviceId);
        JSONObject hashMap= new JSONObject();
        try {
            hashMap.put("event_type",eventType);
            hashMap.put("event_name",event_name);
            hashMap.put("device_id",deviceId);
            hashMap.put("date_time",WAUtils.getCurrentUTC());
            //Call<ResponseBody> call = apiService.event_post(hashMap);
            //call.enqueue(callResponse);
            logger.e("WalinnTrackerClient date_time_event",hashMap.toString());
            new APIClient(mContext,"events",hashMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @TargetApi(14)
    private void registerWalinnsActivityLifecycleCallbacks() {
        if(Build.VERSION.SDK_INT >= 14) {
            if(this.mContext !=null) {
                logger.e("WalinnsTrackerClient","life_cycle_method"+"inside_if");
                Application app = (Application)this.mContext.getApplicationContext();
                this.mWalinnsactivitylifecycle = new WALifeCycle(this,WAConfig.getInstance(mContext),mContext);
                app.registerActivityLifecycleCallbacks(this.mWalinnsactivitylifecycle);
                mContext.startService(new Intent(mContext, WAService.class)); //start service which is MyService.java

            } else {
                logger.i("WalinnsTrackerClient", "Context is not an Application, Walinns will not automatically show in-app notifications or A/B test experiments. We won\'t be able to automatically flush on an app background.");
            }
        }

    }
    protected void track_(String eventName) {//
        logger.e("WalinnsTrackerClient gesture tracker", eventName);
    }
    protected  void track_(String eventName, JSONObject properties, boolean isAutomaticEvent) {
        try {
            // logger.e("WalinnsTrackerClient  tracker_session", eventName + Utils.convertUtctoCurrent(properties.getString("$start_time"),properties.getString("$end_time")));
            if(isAutomaticEvent){
                final JSONObject hashMapp=new JSONObject();
                hashMapp.put("device_id",shared_pref.getValue(WAPref.device_id));
                if(!WAUtils.convertUtctoCurrent(properties.getString("$start_time"),properties.getString("$end_time")).isEmpty()){
                    hashMapp.put("session_length", WAUtils.convertUtctoCurrent(properties.getString("$start_time"), properties.getString("$end_time")));

                }else {
                    hashMapp.put("session_length", properties.getString("$ae_session_length"));
                }
                hashMapp.put("start_time", properties.getString("$start_time"));
                hashMapp.put("end_time", properties.getString("$end_time"));


                this.runOnLogThread(new Runnable() {
                    public void run() {
                        // Call<ResponseBody> call = apiService.session(hashMapp);
                        //  call.enqueue(callResponse);
                        new APIClient(mContext,"session",hashMapp);
                    }
                });

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    protected void track_(String value,boolean status){
        logger.e("WalinnsTrackerClient active_status",String.valueOf(status)+shared_pref.getValue(WAPref.device_id));
        JSONObject hash = null;
        try {
            if(status){
                hash=new JSONObject();
                hash.put("active_status","yes");
                hash.put("date_time",value);
                hash.put("device_id",shared_pref.getValue(WAPref.device_id));

            }else {

                hash=new JSONObject();
                hash.put("active_status","no");
                hash.put("date_time",value);
                hash.put("device_id",shared_pref.getValue(WAPref.device_id));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JSONObject finalHash = hash;
        this.runOnLogThread(new Runnable() {
            public void run() {
                //  Call<ResponseBody> call = apiService.fetchAppUserDetailPost(hash);
                // call.enqueue(callResponse);
                new APIClient(mContext,"fetchAppUserDetail", finalHash);
            }
        });
    }
    private Thread.UncaughtExceptionHandler handleAppCrash =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, final Throwable ex) {
                    logger.e("WalinnsTrackerClient crash report", ex.getMessage()+thread.getName());
                    shared_pref.save(WAPref.crash_report,ex.toString());
                    runOnLogThread(new Runnable() {
                        @Override
                        public void run() {

                            sendCreash();

                        }
                    });

                }
            };

    private void sendCreash(){
        runOnLogThread(new Runnable() {
            public void run() {
                final JSONObject hash;
                hash=new JSONObject();
                if(!WAUtils.isEmptyString(shared_pref.getValue(WAPref.crash_report))) {
                    try {
                        hash.put("reason", shared_pref.getValue(WAPref.crash_report));
                        hash.put("device_id", shared_pref.getValue(WAPref.device_id));
                        hash.put("date_time",WAUtils.getCurrentUTC());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // hash.put("date_time",Utils.getDate(Long.parseLong(String.valueOf(System.currentTimeMillis()))));


                    //Call<ResponseBody> call = apiService.send_crash(hash);
                    //call.enqueue(callResponse);
                    new APIClient(mContext,"crashReport", hash);
                }
            }
        });
    }
    public void track(final String screen_name){
        runOnLogThread(new Runnable() {
            public void run() {
                final JSONObject hash;
                hash=new JSONObject();
                if(!WAUtils.isEmptyString(screen_name)) {
                    try {
                        hash.put("screen_name", screen_name );
                        hash.put("date_time", WAUtils.getCurrentUTC());
                        hash.put("device_id", shared_pref.getValue(WAPref.device_id));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    logger.e("WalinnsTrackerClient","ScreenView value is empty"+"Please enter valid name");
                }

                //Call<ResponseBody> call = apiService.screenView(hash);
                // call.enqueue(callResponse);
                new APIClient(mContext,"screenView", hash);

            }
        });
    }
    protected void sendpush(){
        String pushtoken=null;
        if(!shared_pref.getValue(WAPref.push_token).isEmpty()) {
            pushtoken = shared_pref.getValue(WAPref.push_token);
        }else if(!shared_pref.getValue(WAPref.push_token_dummy).isEmpty()){
            pushtoken=shared_pref.getValue(WAPref.push_token_dummy);
        }else {
            pushtoken = "Google play service is not available";
        }

        logger.d("WalinnsTracker push token", pushtoken);
        logger.d("WalinnsTracker package name", mContext.getPackageName());
        final String finalPushtoken = pushtoken;
        runOnLogThread(new Runnable() {
            public void run() {
                final JSONObject hash;
                hash=new JSONObject();
                if(!WAUtils.isEmptyString(finalPushtoken)) {
                    try {
                        hash.put("push_token", finalPushtoken);
                        hash.put("package_name",mContext.getPackageName());
                        hash.put("device_id", shared_pref.getValue(WAPref.device_id));
                        hash.put("date_time",WAUtils.getCurrentUTC());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Call<ResponseBody> call = apiService.uninstallcount(hash);
                    // call.enqueue(callResponse);
                    new APIClient(mContext,"uninstallcount", hash);
                }
            }
        });
    }

    protected void lifeCycle(){
        logger.e("WalinnsTrackerClient","life_cycle_method_detected");
        registerWalinnsActivityLifecycleCallbacks();
    }
    public void getGender(String email){
        System.out.println("Person gender email" + email);
        if(mContext!=null) {
            System.out.println("Person gender email if" + email);

        }else {
            System.out.println("Person gender email else" + email);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("Person gender email" + "onConnected");
        Person.Gender gender;
        Person person  = Plus.PeopleApi.getCurrentPerson(client);

        if (person.hasGender()) // it's not guaranteed
            System.out.println("Person gender" + person.getAgeRange() + "...."+person.getGender());
        person.getGender();


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("Person gender  failure" + connectionResult.getErrorMessage());
    }
}
