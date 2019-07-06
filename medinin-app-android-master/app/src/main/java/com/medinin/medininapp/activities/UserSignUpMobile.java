package com.medinin.medininapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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

public class UserSignUpMobile extends AppCompatActivity {
    private static final String TAG = UserSignUpMobile.class.getSimpleName();
    private TextView otpBtn, otp_txt, cancel_txt, nextBtn, resendOtpBtn, countDownTxt;
    private EditText num_edit;

    TextWatcher _num_textWatcher;
    int count = 0;

    private RequestQueue mRequestQueue;
    ProgressDialog progress;

    private ConstraintLayout otp_wrap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up_mobile);

        //tab links
        TextView loginTabLink = (TextView) findViewById(R.id.loginTabLink);
        loginTabLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserPassCodeLogin.class);
                startActivity(intent);
            }
        });

        //current page events
        mRequestQueue = Volley.newRequestQueue(this);
        progress = new ProgressDialog(UserSignUpMobile.this);
        progress.setMessage("Loading...");


        num_edit = (EditText) findViewById(R.id.num_edit);
        otpBtn = (TextView) findViewById(R.id.otpBtn);
        otp_txt = (TextView) findViewById(R.id.otp_txt);
        cancel_txt = (TextView) findViewById(R.id.cancel_txt);
        nextBtn = (TextView) findViewById(R.id.nextBtn);
        countDownTxt = (TextView) findViewById(R.id.countDownTxt);
        resendOtpBtn = (TextView) findViewById(R.id.resendOtpBtn);

        otp_wrap = (ConstraintLayout) findViewById(R.id.otp_wrap);


        _num_textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                count = num_edit.getText().toString().length();
                if (count > 0 && count == 10) {
                    otp_txt.setVisibility(View.GONE);
                    otpBtn.setVisibility(View.VISIBLE);
                } else {
                    otp_txt.setVisibility(View.VISIBLE);
                    otpBtn.setVisibility(View.GONE);
                }

            }
        };
        num_edit.addTextChangedListener(_num_textWatcher);

        otpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp_wrap.setVisibility(View.VISIBLE);
                resendOtpBtn.setVisibility(View.VISIBLE);

                otp_txt.setVisibility(View.GONE);
                otpBtn.setVisibility(View.GONE);

                num_edit.setFocusable(false);
                startTimer();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserSignUpBiometric.class);
                intent.putExtra("mobile", num_edit.getText());
                startActivity(intent);
            }
        });

        cancel_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

        final String _mobile_str = num_edit.getText().toString();

        if (validateEditTextInput(num_edit)) {
            _error = true;
            Toast.makeText(UserSignUpMobile.this, "Please enter contact number!", Toast.LENGTH_LONG).show();
        } else if (num_edit.getText().length() < 10) {
            _error = true;
            Toast.makeText(UserSignUpMobile.this, "Please enter valid contact number!", Toast.LENGTH_LONG).show();
        }

        if (!_error) {
            progress.show();
            progress.setMessage("Sending OTP...");
            JSONObject _obj = new JSONObject();
            try {
                _obj.put("mobile", _mobile_str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                    API_URL.PatientGetOTP, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    progress.dismiss();
                    try {

                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            Toast.makeText(UserSignUpMobile.this, "OTP has been sent to your mobile.", Toast.LENGTH_LONG).show();
//                            Intent intent = new Intent(getApplicationContext(), UserSignUpMobileOtp.class);
//                            intent.putExtra("mobile", _mobile_str);
//                            startActivity(intent);
                        } else {
                            Toast.makeText(UserSignUpMobile.this, response.getString("message"), Toast.LENGTH_LONG).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(UserSignUpMobile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
        new CountDownTimer(180000, 1000) { // adjust the milli seconds here
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
}
