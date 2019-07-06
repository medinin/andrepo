package com.medinin.medininapp.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
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

public class Anatomy extends AppCompatActivity {

    private static final String TAG = Settings.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private ProgressDialog progress;
    BottomSheetDialog dialog;
    LinearLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;
    int realWidth;
    int realHeight;
    String progressValue;
    boolean popOpen = true;
    int splashTime = 0;
    Thread splashTimer;
    private String med_user_id, med_user_token, _subject = "Anatomy Feedback";
    ProgressBar pgsBar;
    SeekBar seekbar;
    private JSONObject docDetails;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anatomy);

        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);
        mRequestQueue = Volley.newRequestQueue(this);

        JSONObject _obj = new JSONObject();
        try {
            _obj.put("id", med_user_id);
            _obj.put("token", med_user_token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.LoginCheck, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                Log.i("response", response.toString());
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        docDetails = response.getJSONObject("data");
                    } else {
                        Log.i("Login detail", "Not logged in");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("Login detail", "Not logged in");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.i("Login detail", "Not logged in");
            }
        });
        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);
        progress = new ProgressDialog(this);
        progress.setTitle("Saving...");

        Display display = this.getWindowManager().getDefaultDisplay();
        final WebView myWebView = (WebView) findViewById(R.id.modelSec);
        pgsBar = (ProgressBar) findViewById(R.id.pBar);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        myWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        myWebView.addJavascriptInterface(new Anatomy.WebAppInterface(this), "PhoneApp");
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        });
        myWebView.loadUrl(API_URL.headModel);

        final ImageView arrowDown = (ImageView) findViewById(R.id.hide_popup);
        layoutBottomSheet = (LinearLayout) findViewById(R.id.anatomy_bottom_sheet);

        final EditText feedback_txt = layoutBottomSheet.findViewById(R.id.feedback_txt);
        final ImageView sendBtnGray = layoutBottomSheet.findViewById(R.id.sendGray1);
        final ImageView sendBtn = layoutBottomSheet.findViewById(R.id.sendBtn1);

        final TextWatcher _df = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (feedback_txt.getText().toString().length() >= 1) {
                    sendBtn.setVisibility(View.VISIBLE);
                    sendBtnGray.setVisibility(View.GONE);
                } else {
                    sendBtn.setVisibility(View.GONE);
                    sendBtnGray.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        feedback_txt.addTextChangedListener(_df);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //post SMS
                progress.setTitle("Sending...");
                progress.show();

                JSONObject _jsonObj = new JSONObject();
                try {
                    _jsonObj.put("subject", _subject);
                    _jsonObj.put("mobile", docDetails.getString("mobile"));
                    _jsonObj.put("name", docDetails.getString("name"));
                    Log.i("_jsonObj", _jsonObj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                        API_URL.SendFeedback, _jsonObj, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.d(TAG, "Response: " + response.toString());
                        Log.i("response", response.toString());
                        progress.hide();
                        try {
                            boolean _error = Boolean.parseBoolean(response.getString("error"));
                            if (!_error) {
                                Toast.makeText(getBaseContext(), "Your details has been submitted.", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i("Login detail", "Not logged in");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("Login detail", "Not logged in");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        progress.hide();
                    }
                });
                // Adding request to volley request queue
                mRequestQueue.add(jsonReq);

            }
        });

        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setPeekHeight(250);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch ( newState ) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        arrowDown.setVisibility(View.GONE);
                        sheetBehavior.setPeekHeight(420);
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        arrowDown.setVisibility(View.VISIBLE);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        arrowDown.setVisibility(View.GONE);
                        sheetBehavior.setPeekHeight(420);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        seekbar = (SeekBar) findViewById(R.id.seek_bar);
        seekbar.setMax(100);
        seekbar.setProgress(10);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progressValue = String.valueOf(progress);
                myWebView.loadUrl("javascript:(function(){changeLayers(" + progressValue + ");})()");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        findViewById(R.id.snapButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popOpen) {
                    popOpen = false;
                    sheetBehavior.setPeekHeight(250);
                } else {
                    popOpen = true;
                    sheetBehavior.setPeekHeight(420);
                }
            }
        });

        findViewById(R.id.hide_popup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBehavior.setPeekHeight(420);
            }
        });

        findViewById(R.id.modelHead).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.headModel);
            }
        });

        findViewById(R.id.modelHand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.handModel);
            }
        });

        findViewById(R.id.modelLeg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.legModel);
            }
        });

        findViewById(R.id.modelNose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.noseModel);
            }
        });

        findViewById(R.id.modelHair).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.hairModel);
            }
        });

        findViewById(R.id.modelEye).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.eyeModel);
            }
        });

        findViewById(R.id.modelTeeth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.teethModel);
            }
        });

        findViewById(R.id.modelKidney).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.kidneyModel);
            }
        });

        findViewById(R.id.modelSpinal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.spinalModel);
            }
        });

        findViewById(R.id.modelPelvis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.pelvisModel);
            }
        });

        findViewById(R.id.modelLungs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.lungsModel);
            }
        });

        findViewById(R.id.modelUrinary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.urinaryModel);
            }
        });

        findViewById(R.id.modelFemaleUrinary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.femaleUrinaryModel);
            }
        });

        findViewById(R.id.modelSkull).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.skullModel);
            }
        });

        findViewById(R.id.modelMouth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.mouthModel);
            }
        });

        findViewById(R.id.modelBrain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.brainModel);
            }
        });

        findViewById(R.id.modelHeart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.heartModel);
            }
        });

        findViewById(R.id.modelDigestive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.digestiveModel);
            }
        });

        findViewById(R.id.modelBreast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.breastModel);
            }
        });

        findViewById(R.id.modelTorso).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.torsoModel);
            }
        });

        findViewById(R.id.modelTorsoUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewModel();
                myWebView.loadUrl(API_URL.torsoUpModel);
            }
        });
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), AccountPremium.class);
        finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_left);
        intent.putExtra("ProgressBar", true);
        startActivity(intent);
    }

    private void loadNewModel() {
        findViewById(R.id.loadingWrap).setVisibility(View.VISIBLE);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        findViewById(R.id.progressbar).setVisibility(View.GONE);
        sheetBehavior.setPeekHeight(250);
        seekbar.setProgress(10);
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context ctx) {
            this.mContext = ctx;
        }

        @JavascriptInterface
        public void showModel() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.loadingWrap).setVisibility(View.GONE);
                }
            });
        }

        @JavascriptInterface
        public void showSlider() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
                    sheetBehavior.setPeekHeight(420);
                }
            });
        }
    }


}
