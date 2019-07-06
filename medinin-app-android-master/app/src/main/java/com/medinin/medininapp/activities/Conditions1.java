package com.medinin.medininapp.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.medinin.medininapp.R;
import com.medinin.medininapp.config.API_URL;
import com.medinin.medininapp.data.StringWithTag;
import com.medinin.medininapp.utils.ClearFocusOnKBClose;
import com.medinin.medininapp.utils.CreatePatientHelpers;
import com.medinin.medininapp.utils.CustomDateToString;
import com.medinin.medininapp.utils.SpinnerDropDown;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.text.TextWatcher;

import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.medinin.medininapp.utils.CommonMethods.hasPermissions;

import com.medinin.medininapp.R;

public class Conditions1 extends AppCompatActivity {
    private static final String TAG = Conditions1.class.getSimpleName();
    private final int PERMISSION_ALL = 1;
    private LinearLayout linearLayoutList;
    private ProgressDialog progress;
    private CoordinatorLayout activity_all_conditions;

    TextWatcher _textWatcher1;

    int realHeight;
    int realWidth;
    private RequestQueue mRequestQueue;
    LayoutInflater layoutInflater;
    ScrollView scrollView;

    int _page_number = 0;
    private Boolean isprogressBarStatus;

    private String condition_id, med_user_id, med_user_token;
    private EditText mainSearchBox;
    Boolean _conditionLoadStatus = true;
    private ImageView closeSearchImg;

    List<String> tempSearchArray;
    JSONArray searchArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditions1);


        Intent intent = getIntent();
        isprogressBarStatus = intent.getBooleanExtra("ProgressBar", false);

        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);

        mRequestQueue = Volley.newRequestQueue(this);
        linearLayoutList = findViewById(R.id.linearLayoutList);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading...");

        activity_all_conditions = findViewById(R.id.activity_all_conditions);
        tempSearchArray = new ArrayList<String>();
        searchArray = new JSONArray();

        scrollView = findViewById(R.id.feedScroll);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                //example
                View view = scrollView.getChildAt(0);

                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                if (diff < 0 && _conditionLoadStatus) {
                    _conditionLoadStatus = false;
                    loadMoreConditions();
                }
            }
        });

        mainSearchBox = findViewById(R.id.searchBox);
        closeSearchImg = findViewById(R.id.closeSearchImg);

        closeSearchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainSearchBox.setText("");
                closeSearchImg.setVisibility(View.GONE);
                mainSearchBox.clearFocus();
                hideKeyboard(Conditions1.this);
            }
        });

        initMenu();

        fetchAllConditions();
        bindPatientSearchBox();

        LinearLayout backBtn = (LinearLayout) findViewById(R.id.back_sec);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                launchAccountScreen();
            }
        });

        //On close of soft keyboard clear edittext focus
        // it's works only if activity windowSoftInputMode=adjustResize
        new ClearFocusOnKBClose(activity_all_conditions);
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

    private void launchAccountScreen() {
        Intent intent = new Intent(getApplicationContext(), AccountPremium.class);
        finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_left);
        intent.putExtra("ProgressBar", true);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            launchAccountScreen();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void fetchAllConditions() {
        linearLayoutList.removeAllViews();
        if (!isprogressBarStatus) {
            progress.show();
        }

        JSONObject _obj = new JSONObject();
        try {
            _obj.put("limit", 10);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.ConditionsSearchAll, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                parseJsonFeed(response, false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progress.dismiss();
            }
        });

        jsonReq.setRetryPolicy(new
                DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);
    }

    private void parseJsonFeed(JSONObject response, Boolean search) {

        layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        //View no_data_row = layoutInflater.inflate(R.layout.no_data_row, null);

        if (search) {
            linearLayoutList.removeAllViews();
        }
        try {
            boolean _error = Boolean.parseBoolean(response.getString("error"));
            if (!_error && !response.isNull("data")) {
                JSONObject dataObj = response.getJSONObject("data");
                JSONArray feedArray = dataObj.getJSONArray("result");
                if (feedArray.length() != 0) {
                    _conditionLoadStatus = true;
                }
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);
                    createAllConditionRow(feedObj, search);
                }
                progress.dismiss();
            } else {
                progress.dismiss();
                Toast.makeText(Conditions1.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            progress.dismiss();
            e.printStackTrace();
        }
    }

    public String removeNull(String _string) {
        return _string.equals("null") ? "" : _string;
    }


    private void createAllConditionRow(JSONObject feedObj, Boolean search) {
        try {

            layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            final View preview = layoutInflater.inflate(R.layout.all_conditions_row, null);

            final TextView conditionText1 = preview.findViewById(R.id.conditionText1);
            final LinearLayout dropDownBtn = preview.findViewById(R.id.dropDownBtn);
            ImageView viewPatDetailsImgBtn = preview.findViewById(R.id.viewPatDetailsImgBtn);

            conditionText1.setText(feedObj.getString("name"));
            final String _tempPatID = feedObj.getString("id");
            preview.setTag(_tempPatID);

            conditionText1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Conditions1.this, Conditions2.class);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    intent.putExtra("condition_id", _tempPatID);
                    startActivity(intent);


                    if (conditionText1.getTag().toString().equals("0")) {
                        conditionText1.setTag(1);
                    } else {

                    }
                }
            });

            if (search) {
                String nameFullText = feedObj.getString("name");
                String mSearchText = mainSearchBox.getText().toString();

                if (!mSearchText.isEmpty()) {
                    int startPos = nameFullText.toLowerCase(Locale.US).indexOf(mSearchText.toLowerCase(Locale.US));
                    int endPos = startPos + mSearchText.length();

                    if (startPos != -1) {
                        Spannable spannable = new SpannableString(nameFullText);
                        ColorStateList _color = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.parseColor("#282f3f")});//Color.parseColor("#e6282f3f")
                        TextAppearanceSpan highlightSpan = new TextAppearanceSpan("@font/muliblod", Typeface.NORMAL, -1, _color, null);
                        spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        conditionText1.setText(spannable);
                    } else {
                        conditionText1.setText(nameFullText);
                    }
                } else {
                    conditionText1.setText(nameFullText);
                }
            }

            linearLayoutList.addView(preview);

        } catch (JSONException e) {
            e.printStackTrace();
            progress.dismiss();
        }
    }

    private void loadMoreConditions() {
        progress.show();
        progress.setTitle("Loading...");

        _page_number = _page_number + 1;
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("page", _page_number);
            _obj.put("limit", 10);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.ConditionsSearchAll, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                parseJsonFeed(response, false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progress.dismiss();
            }
        });

        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);
    }

    public void bindPatientSearchBox() {
        final TextWatcher _df = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mainSearchBox.getText().toString().length() >= 1) {
                    //Make the API request
                    _page_number = 0;
                    JSONObject _obj = new JSONObject();
                    try {
                        _obj.put("query", mainSearchBox.getText().toString());
                        _obj.put("limit", 10);
                        _obj.put("page", _page_number);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                            API_URL.ConditionsSearchAll, _obj, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            parseJsonFeed(response, true);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                        }
                    });
                    // Adding request to volley request queue
                    mRequestQueue.add(jsonReq);
                } else {
                    fetchAllConditions();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mainSearchBox.getText().toString().length() > 0) {
                    closeSearchImg.setVisibility(View.VISIBLE);
                } else {
                    closeSearchImg.setVisibility(View.GONE);
                }
            }
        };

        mainSearchBox.addTextChangedListener(_df);
    }

    private void initMenu() {
        RelativeLayout llyt_home = (RelativeLayout) findViewById(R.id.llyt_home);
        RelativeLayout llyt_appointment = (RelativeLayout) findViewById(R.id.llyt_appointment);
        RelativeLayout llyt_setting = (RelativeLayout) findViewById(R.id.llyt_setting);

        View home_view = (View) findViewById(R.id.home_view);
        View home_appoitment = (View) findViewById(R.id.home_appoitment);
        View home_setting = (View) findViewById(R.id.home_setting);

        ImageView img_home = (ImageView) findViewById(R.id.img_home);
        ImageView img_appoint = (ImageView) findViewById(R.id.img_appoint);
        ImageView img_setting = (ImageView) findViewById(R.id.img_setting);

        home_view.setBackgroundColor(getResources().getColor(R.color.gcolor));
        home_appoitment.setBackgroundColor(getResources().getColor(R.color.gcolor));
        home_setting.setBackgroundColor(getResources().getColor(R.color.color_blue));

        img_home.setBackgroundResource(R.drawable.ic_patient);
        img_appoint.setBackgroundResource(R.drawable.ic_cal);
        img_setting.setBackgroundResource(R.drawable.ic_user_circle_blue);

        llyt_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AppointmentList.class);
                startActivity(intent);
            }
        });

        llyt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllPatients.class);
                startActivity(intent);
            }
        });
    }
}
