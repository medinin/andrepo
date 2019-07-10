package com.medinin.medininapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;


import com.malinskiy.superrecyclerview.OnEmptyClickListener;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.medinin.medininapp.interfaces.TaskCompleteListener;
import com.medinin.medininapp.listeners.PermissionCallback;
import com.medinin.medininapp.network.ResponseHandler;
import com.medinin.medininapp.utils.DLog;
import com.medinin.medininapp.utils.NetworkConfig;
import com.medinin.medininapp.utils.PrefManager;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kalyan on 4/19/2017.
 */

public class BaseFragment extends Fragment implements OnMoreListener, OnEmptyClickListener,  SwipeRefreshLayout.OnRefreshListener, ResponseHandler {

    private PermissionCallback callback;
    private int mRequestcode;


    protected Context mContext;
    protected PrefManager mLocalSession = new PrefManager();
    protected static final String LISTENER = "Listener";
    protected Serializable mFragmentListener;

    private static final String TAG = "BaseFragment";
    private TaskCompleteListener mTaskCompleteListener;
    private BroadcastReceiver mBroadcastReceiver;


    public Serializable getFragmentListener() {
        return mFragmentListener;
    }

    public void setFragmentListener(Serializable mFragmentListener) {
        this.mFragmentListener = mFragmentListener;
    }

    @Override
    public void onRefresh() {



    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mContext != null && mBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
            DLog.d(TAG, "Unregistered receiver for : TASK_COMPLETE and DOWNLOAD_COMPLETE");
        }
    }


    protected void setTaskCompleteListener(Context context, TaskCompleteListener listener) {
        mContext = getActivity();
        if (null != listener) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    DLog.d(TAG, "onReceive ");
                    if (null != mTaskCompleteListener) {
                        mTaskCompleteListener.onTaskCompleted(context, intent);
                    }
                }
            };

            mTaskCompleteListener = listener;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(NetworkConfig.TASK_COMPLETE);
            intentFilter.addAction(NetworkConfig.DOWNLOAD_COMPLETE);
            LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver, intentFilter);
            DLog.d(TAG, "Registered receiver for : TASK_COMPLETE and DOWNLOAD_COMPLETE");
        }
    }



    @Override
    public void onSuccess(String responce, Object data, int urlId, int position) {

    }

    @Override
    public void onFailure(Exception e, int urlId) {

    }

    public boolean isPemissionAllowed(String permission) {
        return ContextCompat.checkSelfPermission(getActivity(),
                permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(String permission, int requestcode, PermissionCallback callback) {
        mRequestcode = requestcode;
        if (isPemissionAllowed(permission)) {
            callback.onPermissionStatus(true);
        } else {
            this.callback = callback;
            requestPermissions(new String[]{permission}, requestcode);
        }
    }


    public void requestPermission(String[] permission, int requestcode, PermissionCallback callback) {
        mRequestcode = requestcode;
        try {
            ArrayList<String> list = isPemissionAllowed(permission);
            if (list.size() == 0) {
                callback.onPermissionStatus(true);
            } else {
                String[] permissionList = new String[list.size()];
                for (int i = 0; i < permissionList.length; i++) {
                    permissionList[i] = list.get(i);
                }
                this.callback = callback;
                requestPermissions(permissionList, requestcode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> isPemissionAllowed(String[] permission) {
        ArrayList<String> list = new ArrayList<>();
        try {
            for (String permssion : permission) {
                boolean isGranted = ContextCompat.checkSelfPermission(getActivity(),
                        permssion) == PackageManager.PERMISSION_GRANTED;
                if (!isGranted) {
                    list.add(permssion);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("BaseFragment", "onRequestPermissionsResult (line 97): ");
        if (callback != null) {
            if (requestCode == this.mRequestcode) {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                            callback.onPermissionStatus(false);
                            break;
                        }
                    }
                    callback.onPermissionStatus(true);
                } else {
                    callback.onPermissionStatus(false);
                }
            } else {
                callback.onPermissionStatus(true);
            }
        }

    }

    @Override
    public void onEmptyItemClick(int emptyId) {

    }

    @Override
    public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {

    }
}
