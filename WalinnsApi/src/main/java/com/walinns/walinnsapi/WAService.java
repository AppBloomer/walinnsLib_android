package com.walinns.walinnsapi;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class WAService extends Service{
    public static final int notify = 60000;  //interval between two services(Here Service run every 5 Minute)
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling
    private String start_time = "na" ;
    private String max_duration = "00:30:00";

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        start_time = WAUtils.getCurrentUTC();
        if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   //recreate new
        mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();    //For Cancel Timer
    }

    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // display toast
                    // Toast.makeText(MyService.this, "Service is running", Toast.LENGTH_SHORT).show(); //300000 * 6
                    System.out.println("Service max duration : " + WAUtils.applicationInForeground(getApplicationContext()) + "...." + WAUtils.getCurrentUTC() + "duarion....."+ WAUtils.convertUtctoCurrent(start_time,WAUtils.getCurrentUTC()));

                    if((!WAUtils.applicationInForeground(getApplicationContext())&&max_duration.equals(WAUtils.convertUtctoCurrent(start_time,WAUtils.getCurrentUTC())))||WAUtils.getCurrentUTC_12().equals("12:00:00 AM")){
                        JSONObject sessionProperties = new JSONObject();
                        try {
                            sessionProperties.put("$ae_session_length", WAUtils.convertUtctoCurrent(start_time,WAUtils.getCurrentUTC()));
                            sessionProperties.put("$start_time",start_time);
                            sessionProperties.put("$end_time",WAUtils.getCurrentUTC());
                            System.out.println("Service max duration hash map :" + sessionProperties.toString());
                            WALifeCycle.starttime=WAUtils.getCurrentUTC();
                            WalinnsAPI.getInstance().track_("$ae_session", sessionProperties, true );

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
        }
    }
}
