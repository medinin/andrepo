package com.medinin.medininapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.medinin.medininapp.data.StringWithTag;
import com.medinin.medininapp.utils.SpinnerDropDown;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Registration extends AppCompatActivity {
    private static final String TAG = Registration.class.getSimpleName();
    private String patient_id, med_user_id, med_user_token;
    private RequestQueue mRequestQueue;

    private Spinner genderSpinner;
    EditText name, name_edit, dob_edit, num_edit, otp_edit;
    TextView nextBtn, getOtpBtn, resendOtpBtn, countDownTxt;

    TextWatcher _textWatcher, _num_textWatcher, _otp_textWatcher;

    LinearLayout otpInputWrap;
    ConstraintLayout getOtpBtnWrap, otpVerifyTxtWrap;

    ProgressDialog progress;
    String _otpID, ageStr;
    ImageView faceScanTabLink;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Intent intent = getIntent();

        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);

        mRequestQueue = Volley.newRequestQueue(this);
        progress = new ProgressDialog(Registration.this);
        progress.setMessage("Loading...");

        genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
        name_edit = (EditText) findViewById(R.id.name_edit);
        dob_edit = (EditText) findViewById(R.id.dob_edit);
        num_edit = (EditText) findViewById(R.id.num_edit);
        otp_edit = (EditText) findViewById(R.id.otp_edit);

        nextBtn = (TextView) findViewById(R.id.nextBtn);
        getOtpBtn = (TextView) findViewById(R.id.getOtpBtn);
        resendOtpBtn = (TextView) findViewById(R.id.resendOtpBtn);

        countDownTxt = (TextView) findViewById(R.id.countDownTxt);

        otpInputWrap = (LinearLayout) findViewById(R.id.otpInputWrap);
        getOtpBtnWrap = (ConstraintLayout) findViewById(R.id.getOtpBtnWrap);
        otpVerifyTxtWrap = (ConstraintLayout) findViewById(R.id.otpVerifyTxtWrap);

        bindGenderDropDown(genderSpinner);

        getOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOTP();
            }
        });

        resendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOTP();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postPatientDetails();
            }
        });


        dob_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePickerDialog();
            }
        });
        bindTextChangeEvents();
    }


    public String removeNull(String _string) {
        return _string.equals("null") ? "" : _string;
    }

    private Boolean validateEditTextInput(EditText _view) {
        String _value = _view.getText().toString();
        if (_value != null && !_value.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private void bindTextChangeEvents() {
        _num_textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (num_edit.getText().toString().length() == 10) {
                    getOtpBtnWrap.setVisibility(View.VISIBLE);
                    otpVerifyTxtWrap.setVisibility(View.VISIBLE);
                }
            }
        };
        num_edit.addTextChangedListener(_num_textWatcher);


        _otp_textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (otp_edit.getText().toString().length() == 4) {
                    checkOTP();
                }
            }
        };
        otp_edit.addTextChangedListener(_otp_textWatcher);
    }

    private void bindGenderDropDown(Spinner spinner) {
        ArrayList<StringWithTag> monthList = new ArrayList<StringWithTag>();
        monthList.add(new StringWithTag("Select", ""));
        monthList.add(new StringWithTag("Male", "male"));
        monthList.add(new StringWithTag("Female", "female"));

        ArrayAdapter<StringWithTag> spinnerArrayAdapter = new ArrayAdapter<StringWithTag>(Registration.this, android.R.layout.simple_spinner_item, monthList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }


    private void getOTP() {
        //Loader
        Boolean _error = false;

        String _mobile_str = num_edit.getText().toString();

        if (validateEditTextInput(num_edit)) {
            _error = true;
            Toast.makeText(Registration.this, "Please enter contact number!", Toast.LENGTH_LONG).show();
        } else if (num_edit.getText().length() < 10) {
            _error = true;
            Toast.makeText(Registration.this, "Please enter valid contact number!", Toast.LENGTH_LONG).show();
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
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                    API_URL.PatientGetOTP, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    progress.dismiss();
                    try {

                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            Toast.makeText(Registration.this, "OTP has been sent to your mobile.", Toast.LENGTH_LONG).show();
                            ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                                    .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                            JSONObject _data = (JSONObject) response.getJSONObject("data");
                            _otpID = _data.getString("id");

                            otpInputWrap.setVisibility(View.VISIBLE);
                            getOtpBtn.setVisibility(View.GONE);
                            resendOtpBtn.setVisibility(View.VISIBLE);
                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }
                            startTimer();
                        } else {
                            Toast.makeText(Registration.this, response.getString("message"), Toast.LENGTH_LONG).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Registration.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
        progress = new ProgressDialog(Registration.this);
        progress.setMessage("Verifying OTP...");

        Boolean _error = false;

        if (validateEditTextInput(otp_edit)) {
            _error = true;
        }
        if (validateEditTextInput(num_edit)) {
            _error = true;
        }
        if (otp_edit.getText().toString().length() != 4) {
            _error = true;
        }

        if (!_error) {
            progress.show();
            JSONObject _obj = new JSONObject();
            try {
                _obj.put("id", _otpID);
                _obj.put("otp", otp_edit.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                    API_URL.PatientCheckOTP, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    progress.dismiss();
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            JSONObject _data = (JSONObject) response.getJSONObject("data");

                            String otp_code = _data.getString("code");
                            if (otp_code.equals("200")) {
                                Toast.makeText(Registration.this, "Mobile number verified successfully!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(Registration.this, "Please enter valid OTP number!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(Registration.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Registration.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private void postPatientDetails() {
        Boolean _error = false;

        if (validateEditTextInput(name_edit)) {
            _error = true;
            Toast.makeText(Registration.this, "Please enter name!", Toast.LENGTH_LONG).show();
        } else if (validateEditTextInput(dob_edit)) {
            _error = true;
            Toast.makeText(Registration.this, "Please enter your date of birth!", Toast.LENGTH_LONG).show();
        } else if (SpinnerDropDown.getSpinnerItem(genderSpinner).isEmpty()) {
            _error = true;
            Toast.makeText(Registration.this, "Please select gender!", Toast.LENGTH_LONG).show();
        } else if (num_edit.getText().length() < 10) {
            _error = true;
            Toast.makeText(Registration.this, "Please enter valid contact number!", Toast.LENGTH_LONG).show();
        }

        if (!_error) {
            progress.show();
            progress.setMessage("Verifying...");

            JSONObject _obj = new JSONObject();
            try {
                String name_str = name_edit.getText().toString();
                _obj.put("name", name_str);
                _obj.put("dob", dob_edit.getText());
                _obj.put("age", ageStr);
                _obj.put("gender", SpinnerDropDown.getSpinnerItem(genderSpinner));
                _obj.put("mobile", num_edit.getText());
                _obj.put("doc_id", med_user_id);

                if (_otpID != null) {
                    _obj.put("mobile_otp_id", _otpID);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                    API_URL.Patient, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    progress.dismiss();

                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            JSONObject _data = (JSONObject) response.getJSONObject("data");

                            Intent intent = new Intent(getApplicationContext(), FaceScan1.class);
                            intent.putExtra("patient_id", _data.getString("id"));
                            startActivity(intent);
                        } else {
                            Toast.makeText(Registration.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Registration.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private void openDatePickerDialog() {
        final Dialog dialog = new Dialog(Registration.this);
        dialog.setContentView(R.layout.dob_select_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
        TextView doneBtn = (TextView) dialog.findViewById(R.id.doneBtn);


        doneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String _dateStr = datePicker.getDayOfMonth() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getYear();
                dob_edit.setText(_dateStr);
                getAge(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();


        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        if (age < 0) {
            age = 0;
        }

        String ageS = String.valueOf(age);
        ageStr = ageS;
    }

}

