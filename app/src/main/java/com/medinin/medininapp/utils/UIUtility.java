package com.medinin.medininapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.medinin.medininapp.Myapp;
import com.medinin.medininapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Bharath on 06/22/2019.
 */

public class UIUtility {
    public static final String TAG = UIUtility.class.getSimpleName();

    public static final int ANIMATING_TIME = 500;
    /**
     * @param activityContext
     * @return screen width
     */
    public static int getScreenWidth(Activity activityContext) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activityContext.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        return width;
    }

    public static int getScreenHeight(Activity activityContext) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activityContext.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //int height = displaymetrics.heightPixels;
        int height = displaymetrics.heightPixels;
        return height;
    }

    private static final int PADDING = 10;

    /**
     * @param activityContext
     * @return FolderView height 16:9 ratio
     */
    public static int getFolderViewHeight(Activity activityContext) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activityContext.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (displaymetrics.widthPixels) / (Myapp.isTablet() ? 4 : 2) - 40;
        return ((width / 16) * 9) + PADDING;
    }

    public static int getFolderViewWidth(Activity activityContext) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activityContext.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (displaymetrics.widthPixels) / (Myapp.isTablet() ? 3 : 2);
        return width;
    }

    /**
     * @param activity
     * @return ImageView height 16:9 ratio
     */
    public static int getImageViewHeight(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return ((displaymetrics.widthPixels / 16) * 9);
    }



    public static String convertDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat output = new SimpleDateFormat("MMM dd, yyyy");
            Date d = sdf.parse(date);
            return output.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertDateTime(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
            SimpleDateFormat output = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss aa");
            Date d = sdf.parse(date);
            date = output.format(d).toUpperCase();
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }



    public static String dateFormat(String date) {
        if (date.contains("T")) {//	2001-07-04T12:08:56.235-0700 or 2001-07-04T12:08:56.235-07:00
            return "yyyy-MM-dd'T'HH:mm:ss";
        } else if (date.contains(",") && date.contains(":")) {//Wed, 4 Jul 2001 12:08:56 -0700
            return "EEE, d MMM yyyy HH:mm:ss";
        } else if (date.contains(",")) {//Wed, Jul 4, '01
            return "EEE, MMM d, ''yy";
        } else if (date.contains("at")) {//2001.07.04 AD at 12:08:56 PDT
            return "yyyy.MM.dd G 'at' HH:mm:ss z";
        } else if (date.contains("AD")) {//2001.07.04 AD at 12:08:56 PDT
            return "yyyyy.MMMMM.dd GGG hh:mm aaa";
        }
        //default
        return "yyyy-MM-dd'T'HH:mm:ss";
    }

    public static String outputDateFormat(String dateFormat) {
        if (dateFormat.startsWith("DD") && dateFormat.contains("MMM") && dateFormat.endsWith("YY")) {
            return "dd MMM, yyyy";
        } else if (dateFormat.startsWith("DD") && dateFormat.contains("MM") && dateFormat.contains("YY")) {
            return "dd MM, yyyy";
        } else if (dateFormat.startsWith("MMM") && dateFormat.contains("dd") && dateFormat.endsWith("YY")) {
            return "MMM dd, yyyy";
        } else if (dateFormat.startsWith("MM") && dateFormat.contains("dd") && dateFormat.endsWith("YY")) {
            return "MM dd, yyyy";
        } else if (dateFormat.startsWith("yy") && dateFormat.contains("MMM") && dateFormat.endsWith("dd")) {
            return "yyyy, MMM dd";
        } else if (dateFormat.startsWith("yy") && dateFormat.contains("MMM") && dateFormat.endsWith("dd")) {
            return "yyyy, MM dd";
        } else if (dateFormat.startsWith("yy") && dateFormat.contains("MMM")) {
            return "yyyy, MMM dd";
        }
        return "dd MMM, yyyy";
    }


    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showSoftKeyboard(Context context, View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }




    public static void loadImage(Context context, String url, ImageView imgView) {
       // DLog.i(TAG,"mmmmmmmm before:"+url);
        if (url == null || url.length() == 0 ) {
            Glide.with(context)
                    .load(R.drawable.bg_no_preview)
                    .into(imgView);
        } else {
         //   DLog.i(TAG,"mmmmmmmm after:"+url);
            Glide.with(context)
                    .load(url)
                    .error(R.drawable.ic_user_circle_blue)
                    .into(imgView);
        }
    }








    // slide the view from below itself to the current position
    public static void slideLeft(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                view.getWidth(),    // fromXDelta
                0,  // toXDelta
                0,  // fromYDelta
                0); // toYDelta
        animate.setDuration(ANIMATING_TIME);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public static void slideRight(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,    // fromXDelta
                view.getWidth(),  // toXDelta
                0,  // fromYDelta
                0); // toYDelta
        animate.setDuration(ANIMATING_TIME);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public static int getScreenresolution(Activity activityContext) {
        Display display = activityContext.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        return (width / height);
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void showToastMsg_short(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToastMsg_long(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showToast_noInternet(Context context) {
        Toast.makeText(context, R.string.show_internet_msg, Toast.LENGTH_SHORT).show();
    }

    public static String dateFormatFun(String _date) {
        if (!_date.equals("")) {
            //Convert date into local format
            DateFormat localFormat = new SimpleDateFormat("DD-MM-yyyy");
            DateFormat dateFormat = new SimpleDateFormat("DD-MMM-YY");
            try {
                Date date = localFormat.parse(_date);
                String result = dateFormat.format(date);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

    public static String getAge(int year, int month, int day) {
        String ageforToday = "";
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();


        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        if (age < 0) {
            age = 0;
        }

        ageforToday = String.valueOf(age);
        return ageforToday;
    }
}
