package com.medinin.medininapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.medinin.medininapp.R;
import com.medinin.medininapp.config.API_URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class AddFace extends AppCompatActivity {
    private static final String TAG = AddFace.class.getSimpleName();
    private RequestQueue mRequestQueue;
    WebView myWebView;
    private PermissionRequest mPermissionRequest;
    ImageView camPreview1, camPreview2, camPreview3, reloadSec, uploadSec, changeCam;
    private Boolean camPreview1_status = false, camPreview2_status = false, camPreview3_status = false, reloading = false;
    String camPreview1_status_str = "off";
    String profilePicData;
    private String patient_id;
    Button uploadBtn;
    ProgressDialog progress;
    LinearLayout previewImgSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_face);
        mRequestQueue = Volley.newRequestQueue(this);
        View decorView = getWindow().getDecorView();

        previewImgSec = (LinearLayout) findViewById(R.id.previewImgSec);

        camPreview1 = (ImageView) findViewById(R.id.patientImg1);
        camPreview2 = (ImageView) findViewById(R.id.patientImg2);
        camPreview3 = (ImageView) findViewById(R.id.patientImg3);
        reloadSec = (ImageView) findViewById(R.id.cameraIcon);
        uploadSec = (ImageView) findViewById(R.id.uploadSec);
        changeCam = (ImageView) findViewById(R.id.changeCam);

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
                Toast.makeText(AddFace.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        });
        myWebView.loadUrl(API_URL.AddFaceScanMobile);

        changeCam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                myWebView.loadUrl("javascript:(function(){changeCam();})()");
            }
        });

        reloadSec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!reloading) {
                    reloading = true;
                    reloadSec.setAlpha(0.5f);
                    uploadSec.setAlpha(0.5f);
                    camPreview1_status = false;
                    camPreview2_status = false;
                    camPreview3_status = false;
                    myWebView.loadUrl("javascript:(function(){startScanAgain();})()");
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    public class WebAppInterface {
        Context mContext;
        String data;
        Bitmap bitmap;
        InputStream stream;
        String imageDataBytes;

        WebAppInterface(Context ctx) {
            this.mContext = ctx;
        }

        @JavascriptInterface
        public void sendData(String data) {
            //Get the string value to process
            this.data = data;
            if (!camPreview1_status || !camPreview2_status || !camPreview3_status) {
                profilePicData = data;
                imageDataBytes = data.substring(data.indexOf(",") + 1);
                stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
                bitmap = BitmapFactory.decodeStream(stream);
            }

            if (!camPreview1_status) {
                camPreview1_status = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        camPreview1.setImageBitmap(bitmap);
                        camPreview1.setVisibility(View.VISIBLE);
                    }
                });
            }

            if (!camPreview2_status && !camPreview3_status && camPreview1_status) {
                camPreview2_status = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        camPreview2.setImageBitmap(bitmap);
                        camPreview2.setVisibility(View.VISIBLE);
                    }
                });
            }

            if (!camPreview3_status && camPreview2_status && camPreview1_status) {
                camPreview3_status = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        camPreview3.setImageBitmap(bitmap);
                        camPreview3.setVisibility(View.VISIBLE);
                        previewImgSec.setVisibility(View.VISIBLE);
                        reloadSec.setAlpha(1f);
                        uploadSec.setAlpha(1f);
                        reloading = false;
                    }
                });
            }
        }
    }

    private void uploadFace(final String encodedImageString) {
        progress = new ProgressDialog(this);
        progress.setTitle("Uploading");
        progress.setMessage("Wait while upload your photo");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        JSONObject _em_s = new JSONObject();
        try {
            _em_s.put("src", encodedImageString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.PatientPhotoFileUpload + "/" + patient_id, _em_s, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {
                        JSONObject _data = (JSONObject) response.getJSONObject("data");
                        Log.i("Suresh", _data.toString());

                        //Update the patient profile photo using the path value got from server
                        updatePatientPhotoPath(_data.getString("path"), encodedImageString);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Log.i("Photo error : ", error.toString());
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);
    }

    private void updatePatientPhotoPath(String _path, final String encodedImageString) {
        progress = new ProgressDialog(this);
        progress.setTitle("Uploading");
        progress.setMessage("Wait while upload your photo");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        JSONObject _em_s = new JSONObject();
        try {
            _em_s.put("photo", _path);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                API_URL.Patient + "/" + patient_id, _em_s, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {
                        JSONObject _data = (JSONObject) response.getJSONObject("data");
                        Log.i("Suresh", _data.toString());

                        //Upload Image for training
                        TrainFace(encodedImageString);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Log.i("Photo error : ", error.toString());
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);
    }

    private void TrainFace(String encodedImageString) {
        JSONObject _em_s = new JSONObject();
        try {
            _em_s.put("image", encodedImageString);
            _em_s.put("name", patient_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.FaceTrain, _em_s, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progress.dismiss();
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {
                        JSONObject _data = (JSONObject) response.getJSONObject("data");
                        Log.i("Suresh", _data.toString());

                        //Write the code to redirect to profile page
//                        Intent intent = new Intent(getApplicationContext(), PatientProfile.class);
//                        intent.putExtra("patient_id", patient_id);
//                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Log.i("Photo error : ", error.toString());
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);
    }

}
