package com.medinin.medininapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class CommonMethods {


    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static final String myPref = "mypref";
    public static final String LATITUDE_FIELD = "latId";
    public static final String LONGITUDE_FIELD = "longId";
    public static final String ADDRESS_FIELD = "addressId";
    public static final String DEPARTMENT_ID_FIELD = "deprtId";
    public static final String DEPARTMENT_NAME_FIELD = "deprtName";
    public static final String HOSPITAL_ID_FIELD = "hospitalId";
    public static final String HOSPITAL_NAME_FIELD = "hosptName";
    public static final String HOSPITAL_ADDRESS_FIELD = "hosptAddress";
    public static final String TIME_SLOT_FIELD = "timeslot";

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
