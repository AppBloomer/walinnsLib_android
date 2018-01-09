package com.walinns.walinnsapi;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class WADeviceInfo {

    private boolean locationListening = true;
    private Context context;
    private CachedInfo cachedInfo;
    private static final WALog logger = WALog.getLogger();

    public WADeviceInfo(Context context) {
        this.context = context;
    }

    private CachedInfo getCachedInfo() {
        if(this.cachedInfo == null) {
            this.cachedInfo = new CachedInfo();
        }
        logger.e("WAClient",cachedInfo.country);
        return this.cachedInfo;
    }

    public CachedInfo prefetch() {
        cachedInfo=this.getCachedInfo();
        return cachedInfo;
    }
    public class CachedInfo {
        String advertisingId;
        String country;
        String versionName;
        String osName;
        String osVersion;
        String app_version;
        String brand;
        String manufacturer;
        String model;
        String carrier;
        String language;
        String connectivty;
        String screen_dpi;
        String screen_height;
        String screen_width;
        String age;
        String gender;
        String mail;
        boolean bluetooth;
        boolean limitAdTrackingEnabled;
        boolean gpsEnabled;
        boolean playservice;


        private CachedInfo() {
            //  this.advertisingId = this.getAdvertisingId();
            this.versionName = this.getVersionName();
            this.osName = this.getOsName();
            this.osVersion = this.getOsVersion();
            this.brand = this.getBrand();
            this.manufacturer = this.getManufacturer();
            this.model = this.getModel();
            if(!this.getCarrier().isEmpty()) {
                this.carrier = this.getCarrier();
            }else {
                this.carrier = "no sim";
            }
            this.country = this.getCountry();
            this.language = this.getLanguage();
            this.gpsEnabled = this.checkGPSEnabled();
            this.connectivty= checkNetworkStatus(context);
            this.playservice=this.isGooglePlayServicesAvailable();
            this.bluetooth=this.isBluetoothCheck();
            this.screen_dpi=this.getScreen_dpi();
            this.screen_height=this.getScreen_height();
            this.screen_width=this.getScreen_width();
            this.age="min 21";
            this.gender="female";
            this.mail=getMail();
            this.app_version=getApp_version();
        }

        private String getVersionName() {
            try {
                PackageInfo packageInfo = WADeviceInfo.this.context.getPackageManager().getPackageInfo(WADeviceInfo.this.context.getPackageName(), 0);
                return packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException var3) {
                return null;
            }
        }

        private String getMail() {
            String possibleEmail = null;
            Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
            Account[] accounts = AccountManager.get(context).getAccounts();
            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    possibleEmail = account.name;
                    logger.e("Synced email is", possibleEmail);

                }
            }
            return possibleEmail;
        }

        private String getOsName() {
            return "android";
        }

        private String getOsVersion() {
            return Build.VERSION.RELEASE;
        }

        private String getBrand() {
            return Build.BRAND;
        }

        private String getManufacturer() {
            return Build.MANUFACTURER;
        }

        private String getModel() {
            return Build.MODEL;
        }

        private String getCarrier() {
            try {
                TelephonyManager manager = (TelephonyManager)WADeviceInfo.this.context.getSystemService("phone");
                return manager.getNetworkOperatorName();
            } catch (Exception var2) {
                return null;
            }

        }
        private String getApp_version(){
            PackageInfo pInfo = null;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                return pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return "1.0";
        }
        private String getConnectivty(){
            TelephonyManager mTelephonyManager = (TelephonyManager)
                    WADeviceInfo.this.context.getSystemService(Context.TELEPHONY_SERVICE);
            int networkType = mTelephonyManager.getNetworkType();
            System.out.println("Network_TYPE"+networkType);
            if(networkType != 0) {

                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return "2G";
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return "3G";
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return "4G";
                    default:
                        return "3G";
                }
            }

            return "2G";
        }

        private boolean isBluetoothCheck(){
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
                return false;

            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    return false;
                }
            }
            return true;
        }
        private String getScreen_dpi(){
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int densityDpi = (int)(metrics.density * 160f);
            return String.valueOf(densityDpi);
        }

        private String getScreen_height() {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int height = metrics.heightPixels;
            return String.valueOf(height);
        }

        private String getScreen_width() {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            ((Activity) WADeviceInfo.this.context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;
            return String.valueOf(width);

        }

        private  boolean isGooglePlayServicesAvailable() {
            GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
            int status = googleApiAvailability.isGooglePlayServicesAvailable(WADeviceInfo.this.context);
            if(status != ConnectionResult.SUCCESS) {
                if(googleApiAvailability.isUserResolvableError(status)) {
                    googleApiAvailability.getErrorDialog((Activity) WADeviceInfo.this.context, status, 2404).show();
                }
                return false;
            }
            return true;
        }

        private String getCountry() {
            String country = this.getCountryFromLocation();
            if(!WAUtils.isEmptyString(country)) {
                return country;
            } else {
                country = this.getCountryFromNetwork();
                return !WAUtils.isEmptyString(country)?country:this.getCountryFromLocale();
            }
        }

        private String getCountryFromLocation() {
            if(!WADeviceInfo.this.isLocationListening()) {
                return null;
            } else {
                Location recent = WADeviceInfo.this.getMostRecentLocation();
                if(recent != null) {
                    try {
                        if(Geocoder.isPresent()) {
                            Geocoder geocoder = WADeviceInfo.this.getGeocoder();
                            List addresses = geocoder.getFromLocation(recent.getLatitude(), recent.getLongitude(), 1);
                            if(addresses != null) {
                                Iterator var4 = addresses.iterator();

                                while(var4.hasNext()) {
                                    Address address = (Address)var4.next();
                                    if(address != null) {
                                        return address.getCountryCode();
                                    }
                                }
                            }
                        }
                    } catch (IOException var6) {
                        ;
                    } catch (NullPointerException var7) {
                        ;
                    } catch (NoSuchMethodError var8) {
                        ;
                    } catch (IllegalArgumentException var9) {
                        ;
                    }
                }

                return null;
            }
        }

        private String getCountryFromNetwork() {
            try {
                TelephonyManager manager = (TelephonyManager)WADeviceInfo.this.context.getSystemService("phone");
                if(manager.getPhoneType() != 2) {
                    String country = manager.getNetworkCountryIso();
                    if(country != null) {
                        return country.toUpperCase(Locale.US);
                    }
                }
            } catch (Exception var3) {
                ;
            }

            return null;
        }

        private String getCountryFromLocale() {
            return Locale.getDefault().getCountry();
        }

        private String getLanguage() {
            return Locale.getDefault().getLanguage();
        }

        private String getAdvertisingId() {
            return "Amazon".equals(this.getManufacturer())?this.getAndCacheAmazonAdvertisingId():this.getAndCacheGoogleAdvertisingId();
        }

        private String getAndCacheAmazonAdvertisingId() {
            ContentResolver cr = WADeviceInfo.this.context.getContentResolver();
            this.limitAdTrackingEnabled = Settings.Secure.getInt(cr, "limit_ad_tracking", 0) == 1;
            this.advertisingId = Settings.Secure.getString(cr, "advertising_id");
            return this.advertisingId;
        }

        private String getAndCacheGoogleAdvertisingId() {
            try {
                Class e = Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient");
                Method getAdvertisingInfo = e.getMethod("getAdvertisingIdInfo", new Class[]{Context.class});
                Object advertisingInfo = getAdvertisingInfo.invoke((Object)null, new Object[]{WADeviceInfo.this.context});
                Method isLimitAdTrackingEnabled = advertisingInfo.getClass().getMethod("isLimitAdTrackingEnabled", new Class[0]);
                Boolean limitAdTrackingEnabled = (Boolean)isLimitAdTrackingEnabled.invoke(advertisingInfo, new Object[0]);
                this.limitAdTrackingEnabled = limitAdTrackingEnabled != null && limitAdTrackingEnabled.booleanValue();
                Method getId = advertisingInfo.getClass().getMethod("getId", new Class[0]);
                this.advertisingId = (String)getId.invoke(advertisingInfo, new Object[0]);
            } catch (ClassNotFoundException var7) {
                WALog.getLogger().w("WAClient Device info", "Google Play Services SDK not found!");
            } catch (InvocationTargetException var8) {
                WALog.getLogger().w("WAClient Device info", "Google Play Services not available");
            } catch (Exception var9) {
                WALog.getLogger().e("WAClient Device info", "Encountered an error connecting to Google Play Services", var9);
            }

            return this.advertisingId;
        }

        private boolean checkGPSEnabled() {
            try {
                Class e = Class.forName("com.google.android.gms.common.GooglePlayServicesUtil");
                Method getGPSAvailable = e.getMethod("isGooglePlayServicesAvailable", new Class[]{Context.class});
                Integer status = (Integer)getGPSAvailable.invoke((Object)null, new Object[]{WADeviceInfo.this.context});
                return status != null && status.intValue() == 0;
            } catch (NoClassDefFoundError var4) {
                WALog.getLogger().w("WAClient Device info", "Google Play Services Util not found!");
            } catch (ClassNotFoundException var5) {
                WALog.getLogger().w("WAClient Device info", "Google Play Services Util not found!");
            } catch (NoSuchMethodException var6) {
                WALog.getLogger().w("WAClient Device info", "Google Play Services not available");
            } catch (InvocationTargetException var7) {
                WALog.getLogger().w("WAClient Device info", "Google Play Services not available");
            } catch (IllegalAccessException var8) {
                WALog.getLogger().w("WAClient Device info", "Google Play Services not available");
            } catch (Exception var9) {
                WALog.getLogger().w("WAClient Device info", "Error when checking for Google Play Services: " + var9);
            }

            return false;
        }
    }
    private Location getMostRecentLocation() {
        if(!this.isLocationListening()) {
            return null;
        } else {
            LocationManager locationManager = (LocationManager)this.context.getSystemService("location");
            if(locationManager == null) {
                return null;
            } else {
                List providers = null;

                try {
                    providers = locationManager.getProviders(true);
                } catch (SecurityException var11) {
                    ;
                }

                if(providers == null) {
                    return null;
                } else {
                    ArrayList locations = new ArrayList();
                    Iterator maximumTimestamp = providers.iterator();

                    Location bestLocation;
                    while(maximumTimestamp.hasNext()) {
                        String provider = (String)maximumTimestamp.next();
                        bestLocation = null;

                        try {
                            bestLocation = locationManager.getLastKnownLocation(provider);
                        } catch (IllegalArgumentException var9) {
                            ;
                        } catch (SecurityException var10) {
                            ;
                        }

                        if(bestLocation != null) {
                            locations.add(bestLocation);
                        }
                    }

                    long maximumTimestamp1 = -1L;
                    bestLocation = null;
                    Iterator var7 = locations.iterator();

                    while(var7.hasNext()) {
                        Location location = (Location)var7.next();
                        if(location.getTime() > maximumTimestamp1) {
                            maximumTimestamp1 = location.getTime();
                            bestLocation = location;
                        }
                    }

                    return bestLocation;
                }
            }
        }
    }

    private boolean isLocationListening() {
        return this.locationListening;
    }

    public void setLocationListening(boolean locationListening) {
        this.locationListening = locationListening;
    }

    protected Geocoder getGeocoder() {
        return new Geocoder(this.context, Locale.ENGLISH);
    }
    private static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private String getVersionName() {
        return this.getCachedInfo().versionName;
    }

    public String getOsName() {
        return this.getCachedInfo().osName;
    }

    private String getOsVersion() {
        return this.getCachedInfo().osVersion;
    }

    private String getBrand() {
        return this.getCachedInfo().brand;
    }

    private String getManufacturer() {
        return this.getCachedInfo().manufacturer;
    }

    private String getModel() {
        return this.getCachedInfo().model;
    }

    private String getCarrier() {
        return this.getCachedInfo().carrier;
    }

    public String getCountry() {
        return this.getCachedInfo().country;
    }

    private String getLanguage() {
        return this.getCachedInfo().language;
    }

    private String getAdvertisingId() {
        return this.getCachedInfo().advertisingId;
    }

    private boolean isLimitAdTrackingEnabled() {
        return this.getCachedInfo().limitAdTrackingEnabled;
    }

    private boolean isGooglePlayServicesEnabled() {
        return this.getCachedInfo().gpsEnabled;
    }

    private String checkNetworkStatus(Context context) {

        String networkStatus ="";
        final ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //Check Wifi
        final android.net.NetworkInfo wifi = manager.getActiveNetworkInfo();
        //Check for mobile data
        final android.net.NetworkInfo mobile = manager.getActiveNetworkInfo();

        if( wifi.getType() == ConnectivityManager.TYPE_WIFI) {
            networkStatus = "wifi";
        }else if(mobile.getType() == ConnectivityManager.TYPE_MOBILE){
            networkStatus = "mobileData";
        }else{
            networkStatus="noNetwork";
        }
        WALog.getLogger().d("WAClient Network type :" , networkStatus);
        return networkStatus;
    }
}
