package com.medinin.medininapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.medinin.medininapp.utils.NetworkChangeReceiver;
import com.medinin.medininapp.utils.PrefManager;
import com.medinin.medininapp.utils.Prefs;

import java.util.List;

import io.fabric.sdk.android.Fabric;


/**
 * Created by Kalyan on 4/19/2017.
 */

public class Myapp extends MultiDexApplication {
    private RequestQueue mRequestQueue;
    private static Myapp mInstance;
    private static Myapp mAppConfig;

    private Tracker mTracker;


    private static Context mApplicationContext;

    public static Handler sHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();

        mApplicationContext = getApplicationContext();

        Prefs.initPrefs(getApplicationContext());
        mInstance = this;
    }


    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }


    public static Context getContext() {
        return mApplicationContext;
    }


    public static final String TAG = Myapp.class
            .getSimpleName();


    public static synchronized Myapp getInstance() {
        return mInstance;
    }





    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }



    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }











    /* some of the working code havs to be deployed out here */


    public static boolean isReleaseBuild() {
        return !BuildConfig.DEBUG;
    }

    public static boolean isTablet() {
        return false;
//        return (mAppConfig.getResources().getConfiguration().screenLayout
//                & Configuration.SCREENLAYOUT_SIZE_MASK)
//                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    //Api to check whether the app is in background
    public static boolean isAppIsInBackground() {
        boolean isInBackground = true;
        try {
            ActivityManager am = (ActivityManager) mAppConfig.getSystemService(ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(mAppConfig.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(mAppConfig.getPackageName())) {
                    isInBackground = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
          //  DLog.e("App in background :", "Something went wrong");
        }

        return isInBackground;
    }

    public static boolean isNetworkAvailable() {
        return new NetworkChangeReceiver.ServiceManager(mAppConfig).isNetworkAvailable();
    }

    public void enableCrashlytics() {
        Fabric.with(this, new Crashlytics());
//        if (isReleaseBuild()) {
//            Fabric.with(this, new Crashlytics());
//        }
    }

    synchronized public Tracker getDefaultTracker() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        if (mTracker == null) {
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }




    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isFingerprintHardwarepresent() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;

        // Initializing both Android Keyguard Manager and Fingerprint Manager
        KeyguardManager keyguardManager = (KeyguardManager) mAppConfig.getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) mAppConfig.getSystemService(FINGERPRINT_SERVICE);
        if (fingerprintManager == null)
            return false;

        // Check whether the device has a Fingerprint sensor.
        if (fingerprintManager != null && !fingerprintManager.isHardwareDetected()) {
            /**
             * An error message will be displayed if the device does not contain the fingerprint hardware.
             * However if you plan to implement a default authentication method,
             * you can redirect the user to a default authentication activity from here.
             * Example:
             * Intent intent = new Intent(this, DefaultAuthenticationActivity.class);
             * startActivity(intent);
             */
            // Toast.makeText(mContext, "Your Device does not have a Fingerprint Sensor", Toast.LENGTH_LONG).show();


            return false;
        } else {
            // Checks whether fingerprint permission is set on manifest
            if (ActivityCompat.checkSelfPermission(mAppConfig, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // Toast.makeText(mContext, "Fingerprint authentication permission not enabled", Toast.LENGTH_LONG).show();
            } else {
                // Check whether at least one fingerprint is registered
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    //Toast.makeText(mContext, "Register at least one fingerprint in Settings", Toast.LENGTH_LONG).show();
                } else {
                    // Checks whether lock screen security is enabled or not
                    if (!keyguardManager.isKeyguardSecure()) {
                        //Toast.makeText(mContext, "Lock screen security not enabled in Settings", Toast.LENGTH_LONG).show();

                    } else {

                    }
                }
            }
            return true;
        }


    }

    public static float getPhoneStorage() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = 0;
        bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        return bytesAvailable / (1024.f * 1024.f);
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static Float getAppVersion() {
        String versionCode = BuildConfig.VERSION_NAME;
        return Float.valueOf(versionCode);
    }

    public static boolean isCompatibleWithCurrentCodeVersion() {
        Float serverVersion = new PrefManager().getServerVersions();
        if (getAppVersion() >= serverVersion) {
            return true;
        } else {
            return false;
        }
    }



    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }



    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }


}
