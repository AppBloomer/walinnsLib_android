package com.walinns.walinnsapi;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class WAInstanceIDService extends FirebaseInstanceIdService {

    private static final WALog logger = WALog.getLogger();
    WAPref sharedPref;
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {


        WAFCMManager.onTokenRefresh(this.getApplicationContext());
    }
}
