package com.walinns.walinnsapi;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class WalinnsAPI {

    static final Map<String, WalinnsAPIClient> instances = new HashMap();


    public static WalinnsAPIClient getInstance() {
        return getInstance((String)null);
    }

    private static synchronized WalinnsAPIClient getInstance(String instance) {
        instance = WAUtils.normalizeInstanceName(instance);
        WalinnsAPIClient client = (WalinnsAPIClient)instances.get(instance);
        if(client == null) {
            client = new WalinnsAPIClient(instance);
            instances.put(instance, client);
        }

        return client;
    }

    private void initialize(Context context, String apiKey) {
        getInstance().initialize(context, apiKey);
    }
}
