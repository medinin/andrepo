package com.medinin.medininapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.medinin.medininapp.Myapp;


public class NetworkChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkChangeReceiver";
    private static boolean currentNetworkState = true;

    //TODO: IM instance for chat module


    @Override
    public void onReceive(final Context context, final Intent intent) {
        //DLog.d(TAG, "No Network Connection !" + currentNetworkState + " " + checkInternet(context));
        if (currentNetworkState != checkInternet(context)) {
            currentNetworkState = checkInternet(context);
            if (!currentNetworkState) {
//                if (IMInstance.getInstance() != null)
//                    IMInstance.getInstance().updateInternetConnection(false);
                if (!Myapp.isAppIsInBackground()) {
                    Toast.makeText(context, "No Network Connection !", Toast.LENGTH_LONG).show();
                }
                final Intent intent1 = new Intent(NetworkConfig.NETWORK_STATE);
                intent1.putExtra(NetworkConfig.NETWORK_STATE, false);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
            } else {
//                if (IMInstance.getInstance() != null)
//                    IMInstance.getInstance().updateInternetConnection(true);
                if (!Myapp.isAppIsInBackground()) {
//                    if (MainActivity.OFFLINE_RELOGIN_CHECK) {
//                        Toast.makeText(context, "Re login to continue in online mode", Toast.LENGTH_LONG).show();
//                    }
                }
                final Intent intent1 = new Intent(NetworkConfig.NETWORK_STATE);
                intent1.putExtra(NetworkConfig.NETWORK_STATE, true);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
            }
        }
    }

    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        return serviceManager.isNetworkAvailable();
    }

    public static class ServiceManager extends ContextWrapper {

        public ServiceManager(Context base) {
            super(base);
        }

        public boolean isNetworkAvailable() {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }

    }
}