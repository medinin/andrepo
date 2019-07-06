package com.medinin.medininapp.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import static com.medinin.medininapp.utils.CommonMethods.hasPermissions;

public class UserMobileLogin extends AppCompatActivity {

    private final int PERMISSION_ALL = 1;
    private final String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_SMS, android.Manifest.permission.INTERNET, android.Manifest.permission.RECEIVE_SMS, Manifest.permission.CALL_PHONE};

    private static final String TAG = UserMobileLogin.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private TextView getOtpBtn, resendOTP, countDownTxt, cancle_txt, country_code_txt;
    private EditText mobileNumberInput, otpNumberInput;

    private ConstraintLayout otpWrap;
    private CountDownTimer countDownTimer;
    private String med_user_id, med_user_token;
    private ImageView countryflagImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_mobile_login);
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);
        if (med_user_id != null && med_user_token != null) {
            Intent dashboard = new Intent(getApplicationContext(), AllPatients.class);
            startActivity(dashboard);
        }
        //tab links
        TextView signUPTabLink = (TextView) findViewById(R.id.signUPTabLink);
        signUPTabLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserSignup.class);
                startActivity(intent);
            }
        });


        //current page events
        mRequestQueue = Volley.newRequestQueue(this);

        getOtpBtn = (TextView) findViewById(R.id.getOtpBtn);

        mobileNumberInput = (EditText) findViewById(R.id.mobileNumberInput);

        countryflagImg = (ImageView) findViewById(R.id.countryflagImg);
        country_code_txt = (TextView) findViewById(R.id.country_code_txt);
        cancle_txt = (TextView) findViewById(R.id.cancle_txt);
        resendOTP = (TextView) findViewById(R.id.resendOTP);
        otpNumberInput = (EditText) findViewById(R.id.otpNumberInputOne);
        countDownTxt = (TextView) findViewById(R.id.countDownTxt);
        otpWrap = (ConstraintLayout) findViewById(R.id.otpWrap);

        getOtpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String _phone_num = mobileNumberInput.getText().toString();
                if (_phone_num.length() == 10) {
                    sendOTP();
                } else {
                    Toast.makeText(UserMobileLogin.this, "Please enter valid mobile number.", Toast.LENGTH_LONG).show();
                }
            }
        });

        resendOTP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String _phone_num = mobileNumberInput.getText().toString();
                if (_phone_num.length() == 10) {
                    sendOTP();
                } else {
                    Toast.makeText(UserMobileLogin.this, "Please enter valid mobile number.", Toast.LENGTH_LONG).show();
                }
            }
        });

        cancle_txt.setOnClickListener(new View.OnClickListener() {
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

    public void openSelectFlagDialog(View v) {
        final Dialog dialog = new Dialog(UserMobileLogin.this);
        dialog.setContentView(R.layout.country_flag_and_code_dialog);

        LinearLayout country_list = (LinearLayout) dialog.findViewById(R.id.country_list);

        Log.i("openSelectFlagDialog", String.valueOf(country_list.getChildCount()));
        for (int i = 0; i < country_list.getChildCount(); i++) {
            final View _row = (View) country_list.getChildAt(i);
            _row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView flagImage = (ImageView) _row.findViewById(R.id.flagImage);
                    TextView country_code = (TextView) _row.findViewById(R.id.country_code);

                    countryflagImg.setImageDrawable(flagImage.getDrawable());
                    country_code_txt.setText(country_code.getTag().toString());
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
    }


    private void sendOTP() {
        //Loader
        final ProgressDialog pd = new ProgressDialog(UserMobileLogin.this);
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

                        Toast.makeText(UserMobileLogin.this, "OTP has been sent to your mobile.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(UserMobileLogin.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(UserMobileLogin.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(UserMobileLogin.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(UserMobileLogin.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
}
