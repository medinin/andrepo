package com.medinin.medininapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;


import com.medinin.medininapp.Myapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String getRequestId() {
        return UUID.randomUUID().toString();
    }

    public static String getDeviceId(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }


    public static void disable(View view) {
        if (view.isEnabled()) {
            view.setEnabled(false);
        }
        if (view.isClickable()) {
            view.setClickable(false);
        }
    }

    public static void enable(View view) {
        if (!view.isEnabled()) {
            view.setEnabled(true);
        }
        if (!view.isClickable()) {
            view.setClickable(true);
        }
    }

    public static int dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * scale);
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static void logMessage(String className, String message) {
//        Crashlytics.log(Log.ERROR, className, message);
    }

    public static void logCrash(Exception e) {
//        if (e != null) {
//            Crashlytics.logException(e);
//            if (BuildConfig.DEBUG) {
//                e.printStackTrace();
//            }
//        }
    }

    public static String getVersionName(Context ctx) {
        try {
            PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0);
            String version = pInfo.versionName;
            return version;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static boolean isOnline(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null;
        }
        return false;
    }


    public static void hideKeyboard(Activity ctx) {
        // Check if no view has focus:
        View view = ctx.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            //inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public static boolean isValidemail(String text) {
        if (TextUtils.isEmpty(text) || text.length() > 255) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches();
        }
    }


    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


    public static String getMimeType(String mPath) {

        if (!mPath.endsWith(".flv")) {
            String mimeType = "";
            Uri uri = Uri.fromFile(new File(mPath));
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                ContentResolver cr = Myapp.getContext().getContentResolver();
                mimeType = cr.getType(uri);
            } else {
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                        .toString());
                if (TextUtils.isEmpty(fileExtension)) {
                    String filePath = uri.toString();
                    //mimeType = filePath.substring(filePath.lastIndexOf(".") + 1);
                    mimeType = getType(filePath);
                } else {
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                            fileExtension.toLowerCase());
                }
            }
            return mimeType;
        } else {
            return "audio/flv";
        }
    }

    /**
     * Determines the MIME type for a given filename.
     *
     * @param filename The file to determine the MIME type of.
     * @return The MIME type of the file, or a wildcard if none could be
     * determined.
     */
    public static String getType(final String filename) {
        // There does not seem to be a way to ask the OS or file itself for this
        // information, so unfortunately resorting to extension sniffing.
        int pos = filename.lastIndexOf('.');
        if (pos != -1) {
            String ext = filename.substring(filename.lastIndexOf('.') + 1,
                    filename.length());
            //Video mime types
            if (ext.equalsIgnoreCase("mp4"))
                return "video/mp4";
            if (ext.equalsIgnoreCase("avi"))
                return "video/x-msvideo";
            if (ext.equalsIgnoreCase("wmv"))
                return "video/x-ms-wmv";

            //image mime types
            if (ext.equalsIgnoreCase("png"))
                return "image/png";
            if (ext.equalsIgnoreCase("jpg"))
                return "image/jpeg";
            if (ext.equalsIgnoreCase("jpe"))
                return "image/jpeg";
            if (ext.equalsIgnoreCase("jpeg"))
                return "image/jpeg";
            if (ext.equalsIgnoreCase("gif"))
                return "image/gif";
        }
        return "*/*";
    }


    public static void showToast(Context ctx, String text) {
        Toast toast = Toast.makeText(ctx, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static void createFolder(Context context) {
        if (isExternalStorageAvailable()) {
            File file = new File(Environment.getExternalStorageDirectory() + "/ProfilePics");
            if (!file.exists()) {
                if (file.mkdir()) ; //directory is created;

            }
        } else {
            File mydir = context.getDir("ProfilePics", Context.MODE_PRIVATE); //Creating an internal dir;
            if (!mydir.exists()) {
                mydir.mkdirs();

            }
        }

    }


    public static boolean isEmpty(List list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        return false;
    }

    public static Uri getTempFile(boolean isImg, Context mContext) {
        File root = new File(Environment.getExternalStorageDirectory(),
                Environment.DIRECTORY_PICTURES);
        if (!root.exists()) {
            root.mkdirs();
        }

        String filename = "" + System.currentTimeMillis();
        File file;
        if (isImg) {
            file = new File(root, filename + ".jpeg");
        } else {
            file = new File(root, filename + ".mp4");
        }
        Uri muri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            muri = Uri.fromFile(file);
        } else {
            muri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", file);
        }
        return muri;
    }


    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static String getMiles(float meters) {
        double miles = meters * 0.000621371192;
        String format = new DecimalFormat("#0.0").format(miles);
        return format;
    }


    public static void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static boolean isAboveLollipop() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP;
    }

    public static void setEditTextMaxLength(EditText edt_text, int maxLength) {
        if (edt_text != null) {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(maxLength);
            edt_text.setFilters(FilterArray);
        }
    }


//    public static boolean isLoggedIn() {
//        LoginDetails loginDetails = LocalDb.getLoginDetails();
//        return loginDetails != null;
//    }

//
//    public static void makeDeviceReginfoApi(String refreshedToken, Context context) {
//        UserDeviceDetailReq deviceDetail = new UserDeviceDetailReq();
//        deviceDetail.deviceplatform = "Android";
//        deviceDetail.devicename = Build.MODEL;
//        deviceDetail.deviceid = Utils.getDeviceId(context);
//        deviceDetail.registrationid = refreshedToken;
//        LoginDetails loginDetails = LocalDb.getLoginDetails();
//        if (loginDetails != null) {
//            deviceDetail.apikey = loginDetails.apikey;
//        } else {
//            deviceDetail.apikey = "";
//        }
//        RestClient restClient = RestClient.getInstance();
//        restClient.post(context, deviceDetail, SuccessDetails.class, new ResponseHandler() {
//            @Override
//            public void onSuccess(String response, Object data, int urlId, int position) {
//                SuccessDetails details = (SuccessDetails) data;
//                if (details != null && details.status == 1) {
//                    Log.d("Utils", "onSuccess: ");
//                }
//            }
//
//            @Override
//            public void onFailure(Exception e, int urlId) {
//
//            }
//        }, URLData.URL_ADD_DEVICEID);
//    }



    public static Date stringToDate(String str) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = null;
        try {
            // Fri Feb 24 00:00:00 CST 2012
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }



    public static String  RailToNormal(String str)
    {
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        Date date = null;
        try {
             date = displayFormat.parse(str);

        }catch (ParseException e){
            e.printStackTrace();
        }

      String s =  parseFormat.format(date);

        return s;
    }


    private static final int MEGABYTE = 1024 * 1024;

    public static void downloadFile(String fileUrl, File directory) {
        try {

            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(true);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(directory);
            int totalSize = urlConnection.getContentLength();

            byte[] buffer = new byte[MEGABYTE];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bufferLength);
            }
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }



    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^[2-9]{2}[0-9]{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }


    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        java.util.regex.Matcher match = pattern.matcher(str);
        if (match.matches() == false) {
            return false;
        } else {
            return true;
        }
    }


    public static boolean isNetworkAvailable(Context context) {
        if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivity == null) {
                Log.w("Utility", "couldn't get connectivity manager");
            } else {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].isAvailable()) {
                            Log.d("Utility", "network is available");
                            return true;
                        }
                    }
                }
            }
        }
        Log.d("Utility", "network is not available");
        return false;
    }
}


