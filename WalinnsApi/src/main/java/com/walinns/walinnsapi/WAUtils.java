package com.walinns.walinnsapi;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class WAUtils {
    private static WALog logger = WALog.getLogger();

    public WAUtils() {
    }
    public static boolean isEmptyString(String s) {
        return s == null || s.length() == 0;
    }

    static String normalizeInstanceName(String instance) {
        if(isEmptyString(instance)) {
            instance = "$default_instance";
        }

        return instance.toLowerCase();
    }
    public static String getDate(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(new Date(time));
        String date = ""+dateString;
        //localToGMT(date);
        System.out.println("WAClient" + date);
        logger.e("WAClient date_time",String.valueOf(time));
        return date;
    }
    public static String getCurrentUTC(){
        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        outputFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        // getDate("2017-12-04 09:52:59");
        return outputFmt.format(time);
    }

    public static String convertUtctoCurrent(String start ,String end ){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        try {
            Date startDate = simpleDateFormat.parse(start);
            Date endDate = simpleDateFormat.parse(end);
            logger.e("WAClient  Date time start:", startDate + "end : "+ endDate  );
            long time_stamp = endDate.getTime() - startDate.getTime();

            return getDateUTC(time_stamp);

        } catch (ParseException e) {
            e.printStackTrace();
            return "xx";
        }


    }

    private static String getDateUTC(long timeStamp){
        long timeDiffSecs = timeStamp/1000;
        String timeDiffString =String.format("%02d", timeDiffSecs/3600)  +":"+
                String.format("%02d", (timeDiffSecs%3600)/60)+":"+
                String.format("%02d", (timeDiffSecs%3600)%60);
        System.out.println("Time Diff = "+ timeDiffString);
        return timeDiffString;
    }
    public static boolean applicationInForeground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
    public static String getCurrentUTC_12(){
        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat outputFmt = new SimpleDateFormat("HH:mm:ss a");
        outputFmt.setTimeZone(TimeZone.getDefault());
        System.out.println("12 am time:" + outputFmt.format(time));
        return outputFmt.format(time);
    }
    private static String getDate(String OurDate)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(OurDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            OurDate = dateFormatter.format(value);
            System.out.println("Request_Data  date time Local time :" + OurDate);

            localToUTC(OurDate);

            //Log.d("OurDate", OurDate);
        }
        catch (Exception e)
        {
            OurDate = "00-00-0000 00:00";
        }
        return OurDate;
    }
    private static String localToUTC(String OurDate)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getDefault());
            Date value = formatter.parse(OurDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            OurDate = dateFormatter.format(value);

            System.out.println("Request_Data  date time UTC :" + OurDate);

            //Log.d("OurDate", OurDate);
        }
        catch (Exception e)
        {
            OurDate = "00-00-0000 00:00";
        }
        return OurDate;
    }
}
