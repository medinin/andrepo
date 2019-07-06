package com.medinin.medininapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.medinin.medininapp.R;
import com.medinin.medininapp.config.API_URL;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;


public class FaceScan extends AppCompatActivity {
    WebView myWebView;
    private PermissionRequest mPermissionRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_scan);

        //Menu links sec end
        myWebView = (WebView) findViewById(R.id.cameraSec);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        myWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        myWebView.getSettings().setAppCacheEnabled(false);
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.setWebChromeClient(new WebChromeClient() {
            // Grant permissions for cam
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                mPermissionRequest = request;
                final String[] requestedResources = request.getResources();
                for (String r : requestedResources) {
                    if (r.equals(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                        mPermissionRequest.grant(new String[]{PermissionRequest.RESOURCE_VIDEO_CAPTURE, PermissionRequest.RESOURCE_AUDIO_CAPTURE});
                    }
                }
            }

            @Override
            public void onPermissionRequestCanceled(PermissionRequest request) {
                super.onPermissionRequestCanceled(request);
                Toast.makeText(FaceScan.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        });
        myWebView.loadUrl(API_URL.FacePredictMobile);

        ImageView camIcon = (ImageView) findViewById(R.id.cameraIcon);
        camIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                myWebView.loadUrl("javascript:(function(){changeCam();})()");
            }
        });

        ImageView startCam = (ImageView) findViewById(R.id.startScan);
        startCam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                myWebView.loadUrl("javascript:(function(){startScanAgain();})()");
            }
        });
    }


    public class WebAppInterface {
        Context mContext;
        String patient_id;
        Bitmap bitmap;
        InputStream stream;
        String imageDataBytes;

        WebAppInterface(Context ctx) {
            this.mContext = ctx;
        }

        @JavascriptInterface
        public void sendData(String _id) {
            this.patient_id = _id;
            Log.i("Patient Id", _id);
//            Intent intent = new Intent(getApplicationContext(), PatientProfile.class);
//            intent.putExtra("patient_id", _id);
//            intent.putExtra("page", "scanner");
//            startActivity(intent);
        }

        @JavascriptInterface
        public void patientNotFound(String data) {
        }

        @JavascriptInterface
        public void showCamera(String data) {
        }

        @JavascriptInterface
        public void showLoader(String data) {
        }

        @JavascriptInterface
        public void hideLoader(String data) {
            Log.i("output", data);
        }
    }
}
