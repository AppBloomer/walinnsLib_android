package com.walinns.walinnsapi;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class WAPref {
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    static final String device_id = "device_id";
    static final String crash_report = "crash_report";
    static final String project_token="project_token";
    static final String push_token="push_token";

    public WAPref(Context context) {
        sharedPreferences = context.getSharedPreferences("my_pref", 0);
        editor = sharedPreferences.edit();
    }

    public void save(String key, String value) {
        editor.putString(key, value).apply();
        editor.commit();
    }

    public String getValue(String key) {
        String result = sharedPreferences.getString(key, "");
        return result;
    }
}
