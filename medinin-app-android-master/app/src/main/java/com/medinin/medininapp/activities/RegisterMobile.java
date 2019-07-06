package com.medinin.medininapp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.concurrent.TimeUnit;

public class RegisterMobile extends AppCompatActivity {
    private static final String TAG = UserMobileLogin.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private String patient_id, med_user_id, med_user_token, _otpID;

    private TextView resendOtpBtn, countDownTxt, otpBtn, otp_txt, cancel_txt;
    private EditText mobileNumberInput, otpInput;

    private ConstraintLayout otpWrap;
    private CountDownTimer countDownTimer;

    private ProgressDialog progress;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mobile);
        Intent intent = getIntent();
        patient_id = intent.getStringExtra("patient_id");

        mRequestQueue = Volley.newRequestQueue(this);
        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);

        progress = new ProgressDialog(RegisterMobile.this);
        progress.setMessage("Loading...");

        otpBtn = (TextView) findViewById(R.id.otpBtn);

        mobileNumberInput = (EditText) findViewById(R.id.mobileNumberInput);

        otpInput = (EditText) findViewById(R.id.otpInput);
        countDownTxt = (TextView) findViewById(R.id.countDownTxt);
        otpWrap = (ConstraintLayout) findViewById(R.id.otpWrap);


        resendOtpBtn = (TextView) findViewById(R.id.resendOtpBtn);
        otpBtn = (TextView) findViewById(R.id.otpBtn);
        otp_txt = (TextView) findViewById(R.id.otp_txt);
        cancel_txt = (TextView) findViewById(R.id.cancel_txt);
        countDownTxt = (TextView) findViewById(R.id.countDownTxt);


        ConstraintLayout backBtn = (ConstraintLayout) findViewById(R.id.back_sec);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });


        ConstraintLayout add_blue_sec = (ConstraintLayout) findViewById(R.id.add_blue_sec);
        add_blue_sec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), FaceScan1.class);
                startActivity(intent);
            }
        });


        cancel_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), FaceScan1.class);
                startActivity(intent);
            }
        });

        otpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String _phone_num = mobileNumberInput.getText().toString();
                if (_phone_num.length() == 10) {
                    updatePatientNumber();
                } else {
                    Toast.makeText(RegisterMobile.this, "Please enter valid mobile number.", Toast.LENGTH_LONG).show();
                }
            }
        });

        resendOtpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String _phone_num = mobileNumberInput.getText().toString();
                if (_phone_num.length() == 10) {
                    getOTP();
                } else {
                    Toast.makeText(RegisterMobile.this, "Please enter valid mobile number.", Toast.LENGTH_LONG).show();
                }
            }
        });

        otpInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String _otp = otpInput.getText().toString();
                if (_otp.length() == 4) {
                    checkOTP();
                }
            }
        });

        TextWatcher _num_textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                count = mobileNumberInput.getText().toString().length();
                if (count > 0 && count == 10) {
                    otp_txt.setVisibility(View.GONE);
                    otpBtn.setVisibility(View.VISIBLE);
                } else {
                    otp_txt.setVisibility(View.VISIBLE);
                    otpBtn.setVisibility(View.GONE);
                }

            }
        };
        mobileNumberInput.addTextChangedListener(_num_textWatcher);
    }

    private Boolean validateEditTextInput(EditText _view) {
        String _value = _view.getText().toString();
        if (_value != null && !_value.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private void getOTP() {
        //Loader
        Boolean _error = false;

        String _mobile_str = mobileNumberInput.getText().toString();

        if (validateEditTextInput(mobileNumberInput)) {
            _error = true;
            Toast.makeText(RegisterMobile.this, "Please enter contact number!", Toast.LENGTH_LONG).show();
        } else if (mobileNumberInput.getText().length() < 10) {
            _error = true;
            Toast.makeText(RegisterMobile.this, "Please enter valid contact number!", Toast.LENGTH_LONG).show();
        }

        if (!_error) {
            progress.show();
            progress.setMessage("Sending OTP...");
            JSONObject _obj = new JSONObject();
            try {
                _obj.put("doc_id", med_user_id);
                _obj.put("mobile", _mobile_str);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i("getOTP _obj", _obj.toString());
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                    API_URL.PatientGetOTP, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    progress.dismiss();

                    Log.i("getOTP", response.toString());
                    try {

                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            Toast.makeText(RegisterMobile.this, "OTP has been sent to your mobile.", Toast.LENGTH_LONG).show();
                            ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                                    .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                            JSONObject _data = (JSONObject) response.getJSONObject("data");
                            _otpID = _data.getString("id");

                            otpWrap.setVisibility(View.VISIBLE);
                            otpBtn.setVisibility(View.GONE);
                            resendOtpBtn.setVisibility(View.VISIBLE);

                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }
                            startTimer();
                        } else {
                            Toast.makeText(RegisterMobile.this, response.getString("message"), Toast.LENGTH_LONG).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterMobile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
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
    }


    private void checkOTP() {
        //Loader
        progress = new ProgressDialog(RegisterMobile.this);
        progress.setMessage("Verifying OTP...");

        Boolean _error = false;

        if (validateEditTextInput(otpInput)) {
            _error = true;
        }
        if (validateEditTextInput(mobileNumberInput)) {
            _error = true;
        }
        if (otpInput.getText().toString().length() != 4) {
            _error = true;
        }

        if (!_error) {
            progress.show();
            JSONObject _obj = new JSONObject();
            try {
                _obj.put("id", _otpID);
                _obj.put("otp", otpInput.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i("checkOTP _obj", _obj.toString());
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                    API_URL.PatientCheckOTP, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    progress.dismiss();
                    Log.i("checkOTP ", response.toString());
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            JSONObject _data = (JSONObject) response.getJSONObject("data");

                            String otp_code = _data.getString("code");
                            if (otp_code.equals("200")) {
                                Toast.makeText(RegisterMobile.this, "Mobile number verified successfully!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), FaceScan1.class);
                                intent.putExtra("patient_id", patient_id);
                                startActivity(intent);
                            } else {
                                Toast.makeText(RegisterMobile.this, "Please enter valid OTP number!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(RegisterMobile.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterMobile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
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

    private void updatePatientNumber() {
        Boolean _error = false;

        if (validateEditTextInput(mobileNumberInput)) {
            _error = true;
            Toast.makeText(RegisterMobile.this, "Please enter name!", Toast.LENGTH_LONG).show();
        }

        if (!_error) {
            progress.show();
            progress.setMessage("Verifying number...");

            JSONObject _obj = new JSONObject();
            try {

                _obj.put("mobile", mobileNumberInput.getText());
                if (_otpID != null) {
                    _obj.put("mobile_otp_id", _otpID);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                    API_URL.Patient + "/" + patient_id, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    progress.dismiss();
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            getOTP();
                        } else {
                            Toast.makeText(RegisterMobile.this, "This mobile number already registered", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterMobile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
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
    }
}
