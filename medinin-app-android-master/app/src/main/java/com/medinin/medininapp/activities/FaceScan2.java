package com.medinin.medininapp.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.medinin.medininapp.config.API_URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import com.medinin.medininapp.R;

public class FaceScan2 extends AppCompatActivity {
    private final int PERMISSION_ALL = 1;
    private final String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_SMS, android.Manifest.permission.INTERNET, android.Manifest.permission.RECEIVE_SMS, Manifest.permission.CALL_PHONE};
    private static final String TAG = FaceScan2.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private TextView getOtpBtn, resendOTP, countDownTxt, cancel_txt, country_code_txt;
    private EditText mobileNumberInput, otpNumberInput;
    private ConstraintLayout otpWrap;
    private CountDownTimer countDownTimer;
    private String med_user_id, med_user_token;
    private ImageView countryflagImg;
    private long back_pressed = 0;
    BottomSheetDialog dialog;
    int realWidth;
    int realHeight;
    private LinearLayout loginSec, signUpSec, signUpSec2,patSec,getOtpSec,otpSec,testDetailsSec;
    private TextView loginTab, loginTabBr, signUpTab, signUpTabBr;
    private FrameLayout nextBtnWrap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_scan2);


    }


    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            finish();
            System.exit(0);
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }

    public void showBottomSheetDialog() {
        View loginView = getLayoutInflater().inflate(R.layout.global_face_scan_dialog, null);
        dialog = new BottomSheetDialog(this) {
            @Override
            public void onBackPressed() {
                if (back_pressed + 2000 > System.currentTimeMillis()) {
                    dialog.hide();
                    handleUserExit();
                } else {
                    Toast.makeText(getBaseContext(), "Press once again to exit", Toast.LENGTH_SHORT).show();
                    back_pressed = System.currentTimeMillis();
                }
            }
        };
        dialog.setContentView(loginView);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) loginView.getParent());
        mBehavior.setPeekHeight(realHeight);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dialog.hide();
                    handleUserExit();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        InitLoginEvents(loginView);
        dialog.show();
    }

    private void InitLoginEvents(View view) {
        //current page events
        mRequestQueue = Volley.newRequestQueue(FaceScan2.this);
        getOtpBtn = view.findViewById(R.id.getOtpBtn);
        mobileNumberInput = view.findViewById(R.id.mobileNumberInput);
        countryflagImg = view.findViewById(R.id.countryflagImg);
        country_code_txt = view.findViewById(R.id.country_code_txt);
        cancel_txt = view.findViewById(R.id.cancel_txt);
        resendOTP = view.findViewById(R.id.resendOTP);
        otpNumberInput = view.findViewById(R.id.otpNumberInputOne);
        countDownTxt = view.findViewById(R.id.countDownTxt);
        otpWrap = view.findViewById(R.id.otpWrap);



        loginSec = view.findViewById(R.id.login_wrap);
        signUpSec = view.findViewById(R.id.sign_up_wrap);
       // signUpSec2 = view.findViewById(R.id.sign_up_wrap_2);

        patSec = view.findViewById(R.id.pat_details_sec);
        getOtpSec = view.findViewById(R.id.get_otp_sec);
        otpSec = view.findViewById(R.id.otp_wrap_sec);
        testDetailsSec = view.findViewById(R.id.test_details_sec);


        loginTab = view.findViewById(R.id.loginTabLink);
        loginTabBr = view.findViewById(R.id.loginTabLinkBr);

        signUpTab = view.findViewById(R.id.signUPTabLink);
        signUpTabBr = view.findViewById(R.id.signUPTabLinkBr);
        nextBtnWrap = view.findViewById(R.id.next_btn_wrap);

        //tab links
        loginTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpSec.setVisibility(View.GONE);
                signUpTab.setTextColor(Color.parseColor("#afbfc6"));
                signUpTabBr.setVisibility(View.GONE);
                nextBtnWrap.setVisibility(View.GONE);
               // signUpSec2.setVisibility(View.GONE);
                nextBtnWrap.setVisibility(View.GONE);

                patSec.setVisibility(View.VISIBLE);
                getOtpSec.setVisibility(View.VISIBLE);
                otpSec.setVisibility(View.GONE);
                testDetailsSec.setVisibility(View.GONE);



                loginSec.setVisibility(View.VISIBLE);
                loginTab.setTextColor(Color.parseColor("#8e63e6"));
                loginTabBr.setVisibility(View.VISIBLE);
            }
        });

        signUpTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSec.setVisibility(View.GONE);
                loginTab.setTextColor(Color.parseColor("#afbfc6"));
                loginTabBr.setVisibility(View.GONE);
               // signUpSec2.setVisibility(View.GONE);
                nextBtnWrap.setVisibility(View.GONE);

                signUpSec.setVisibility(View.VISIBLE);
                signUpTab.setTextColor(Color.parseColor("#8e63e6"));
                signUpTabBr.setVisibility(View.VISIBLE);
                nextBtnWrap.setVisibility(View.VISIBLE);
            }
        });



        getOtpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String _phone_num = mobileNumberInput.getText().toString();
                if (_phone_num.length() == 10) {
                    sendOTP();
                } else {
                    Toast.makeText(FaceScan2.this, "Please enter valid mobile number.", Toast.LENGTH_LONG).show();
                }
            }
        });

        resendOTP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String _phone_num = mobileNumberInput.getText().toString();
                if (_phone_num.length() == 10) {
                    sendOTP();
                } else {
                    Toast.makeText(FaceScan2.this, "Please enter valid mobile number.", Toast.LENGTH_LONG).show();
                }
            }
        });

        cancel_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

        otpNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String _otp = otpNumberInput.getText().toString();
                if (_otp.length() == 4) {
                    checkOTP(_otp);
                }
            }
        });

    }

    private void handleUserExit() {
        finish();
        System.exit(0);
    }

    private void sendOTP() {
        //Loader
        final ProgressDialog pd = new ProgressDialog(FaceScan2.this);
        pd.setMessage("Sending OTP...");
        pd.show();

        JSONObject _body = new JSONObject();
        try {
            _body.put("mobile", mobileNumberInput.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.SendOTP, _body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                pd.dismiss();
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        resendOTP.setVisibility(View.VISIBLE);
                        getOtpBtn.setVisibility(View.GONE);
                        otpWrap.setVisibility(View.VISIBLE);
                        otpNumberInput.setText("");
                        mobileNumberInput.setEnabled(false);
                        country_code_txt.setAlpha(0.3f);
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }
                        startTimer();

                        Toast.makeText(FaceScan2.this, "OTP has been sent to your mobile.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(FaceScan2.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(FaceScan2.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                pd.dismiss();
            }
        });

        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);
    }

    private void checkOTP(String _otp) {
        JSONObject _body = new JSONObject();
        try {
            _body.put("mobile", mobileNumberInput.getText());
            _body.put("otp", _otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.CheckOTP, _body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    JSONObject _data = response.getJSONObject("data");
                    if (!_error) {
                        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                        SharedPreferences.Editor Ed = sp.edit();
                        Ed.putString("med_user_id", _data.getString("id"));
                        Ed.putString("med_user_token", _data.getString("token"));
                        Ed.apply();

                        Intent dashboard = new Intent(getApplicationContext(), AllPatients.class);
                        startActivity(dashboard);

                    } else {
                        Toast.makeText(FaceScan2.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(FaceScan2.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        mRequestQueue.add(jsonReq);
    }


    private void startTimer() {
        countDownTimer = new CountDownTimer(180000, 1000) { // adjust the milli seconds here
            public void onTick(long millisUntilFinished) {
                countDownTxt.setText("" + String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {

            }
        }.start();
    }


    private void uncheckAllRadioButton() {

    }

    public void onSignUpOptionSelect(View view) {
        RadioButton radioBtn = view.findViewWithTag("RadioButton");
        uncheckAllRadioButton();
        radioBtn.setChecked(true);
    }

}
