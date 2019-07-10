package com.medinin.medininapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medinin.medininapp.R;

/**
 * Created by Bharath.shet on 06/23/2019.
 */

public class MedininProgressDialog {
    private static final String TAG = "MedininProgressDialog";
    private static Dialog mDialog;

    public static void show(Context context) {
        show(context, true);
    }

    public static void show(Context context, boolean cancelable) {
        try {
           // DLog.d(TAG, "show()");
            if (mDialog == null) {
                mDialog = new Dialog(context);
            } else if (mDialog.isShowing()) {
                mDialog.dismiss();
                mDialog = new Dialog(context);
            }

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.custom_progres, null);

            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mDialog.addContentView(v, params);
            mDialog.setCancelable(cancelable);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissDialog() {

        //DLog.d(TAG, "dismissDialog");
        try {
            if (mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
