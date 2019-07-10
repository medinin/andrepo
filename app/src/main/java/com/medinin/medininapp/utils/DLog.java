package com.medinin.medininapp.utils;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.medinin.medininapp.Myapp;

import static com.medinin.medininapp.Myapp.isReleaseBuild;


/**
 * Created by Bharath on 06/22/2019.
 */

public class DLog {
    private static boolean RELEASE_BUILD = isReleaseBuild();

    public static void i(String TAG, String log) {
        Log.i("PFT::" + TAG, log);
    }

    public static void d(String TAG, String log) {
        if (!RELEASE_BUILD)
            Log.d("PFT::" + TAG, log);
    }

    public static void w(String TAG, String log) {
        Log.w("PFT::" + TAG, log);
    }

    public static void e(String TAG, String log) {
        Log.e("PFT::" + TAG, log);
        if (isReleaseBuild()) {
            Crashlytics.logException(new Exception(log));
        }
    }

    public static void trackScreenView(String screenName) {
        if (isReleaseBuild()) {
        try {
            Tracker t = Myapp.getInstance().getDefaultTracker();
            t.setAppVersion(Myapp.getInstance().getPackageManager().getPackageInfo(Myapp.getInstance().getPackageName(), 0).versionName);
            t.setScreenName(screenName);
            t.send(new HitBuilders.ScreenViewBuilder().build());
            GoogleAnalytics.getInstance(Myapp.getInstance()).dispatchLocalHits();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }

    public static void trackEvent(String category, String action, String label) {
        if (isReleaseBuild()) {
        try {
            Tracker t = Myapp.getInstance().getDefaultTracker();
            t.setAppVersion(Myapp.getInstance().getPackageManager().getPackageInfo(Myapp.getInstance().getPackageName(), 0).versionName);
            t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
            GoogleAnalytics.getInstance(Myapp.getInstance()).dispatchLocalHits();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }
}
