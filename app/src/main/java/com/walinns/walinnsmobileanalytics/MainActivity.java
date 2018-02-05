package com.walinns.walinnsmobileanalytics;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.walinns.walinnsapi.WalinnsAPI;

import java.util.List;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_READ_PHONE_STATE = 112;
    GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         WalinnsAPI.getInstance().initialize(MainActivity.this, "qwerty1234");



        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();
        System.out.println("Phone Number"+ "number " + accounts.toString());

        for (Account ac : accounts) {
            String acname = ac.name;
            String actype = ac.type;
            // Take your time to look at all available accounts
            System.out.println("Phone Number Accounts : " + acname + ", " + actype);
            if(actype.equals("com.whatsapp")){
                String phoneNumber = ac.name;
                System.out.println("Phone Number"+ "number " + phoneNumber);

            }
        }
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addApi(Plus.API)
                .build();

        client.connect();
         

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        List<SubscriptionInfo> subscription = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
                        for (int i = 0; i < subscription.size(); i++) {
                            SubscriptionInfo info = subscription.get(i);
                            Log.d("Phone Number", "number " + info.getNumber());
                            Log.d("Phone Number", "network name : " + info.getCarrierName());
                            Log.d("Phone Number", "country iso " + info.getCountryIso());
                        }
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
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

    }
}
