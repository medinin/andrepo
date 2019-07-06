package com.medinin.medininapp.utils;

import android.app.Activity;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;

import com.medinin.medininapp.R;

public class ScanFaceHelpers {
    private void initGlobalFaceScanEvents(final Activity activity) {

        activity.findViewById(R.id.glPatFaceScanImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!_isCameraLoaded) {
//                    _isCameraLoaded = true;
//                    initPatGlobalFaceScanDialog();
//                    patGlobalFaceScanDialog.show();
//                } else {
//                    mBehavior.setPeekHeight(realHeight);
//                    mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                    patGlobalFaceScanDialog.show();
//                    myWebView.loadUrl("javascript:(function(){startScanAgain();})()");
//                }
            }
        });
    }
}
