package com.medinin.medininapp.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
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
import com.medinin.medininapp.helpers.CountryCode;
import com.medinin.medininapp.utils.ClearFocusOnKBClose;
import com.medinin.medininapp.utils.SpinnerDropDown;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class welcome extends AppCompatActivity {
    private final int PERMISSION_ALL = 1;
    private final String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.CALL_PHONE
    };
    private static final String TAG = welcome.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private TextView getOtpBtn, resendOTP, countDownTxt, cancel_txt, country_code_txt, otpDisabledBtn;
    private EditText mobileNumberInput, otpNumberInput;
    private ConstraintLayout otpWrap;
    private CountDownTimer countDownTimer;
    private String med_user_id, med_user_token;
    private ImageView countryFlagImg, signUpCountryFlagImg;
    private long back_pressed = 0;
    BottomSheetDialog dialog;
    private ProgressDialog progress;
    int realWidth;
    int realHeight;
    private RadioButton docRadioBtn, patientRadioBtn, hospitalRadioBtn, clinicRadioBtn, labRadioBtn, pharmacyRadioBtn;
    private TextView nextBtntxt;
    private TextView loginTab, loginTabBr, signUpTab, signUpTabBr;
    private LinearLayout loginSec, sign_up_user_type_wrap, sign_up_mobile_wrap, sign_up_passcode_wrap, sign_up_basic_wrap;

    //Sing up variables --------------------------------------------------------->
    private FrameLayout nextBtnWrap;
    private int signUpPageNumber = 0;
    private EditText signUpMobileNumberInput, signUpUserNameInput, signUpDobInput;
    private Spinner signUpGenderSpinner, signUpdDepartmentSpinner;
    private TextView signUpCountDownTxt, signUpCancelOtpTxt, signUpGetOtpBtn,
            signUpResendOTP, signUpOtpDisabledBtn, signUp_country_code_txt, skipPasscodeTxtBtn, login_cancel_txt;
    private ConstraintLayout signUpOtpWrap, signUpBiometricLogWrap;
    private String signUpOtpStr_id = "";
    private Switch biometricLoginSwitchBtn;
    private String glDobStr, ageStr;
    private String _mobile_str = "", _otpID;
    private EditText otp_input_one, otp_input_two, otp_input_three, otp_input_four, otp_no_input_one, otp_no_input_two, otp_no_input_three, otp_no_input_four;
    Context mContext;
    CountryCode countryCode;
    private LinearLayout MobileNoErrorWrap;

    //PassCode
    private EditText passCode1, passCode2, passCode3, passCode4,
            confirmPassCode1, confirmPassCode2, confirmPassCode3, confirmPassCode4;

    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Checking for first time launch - before calling setContentView()
        setContentView(R.layout.activity_welcome);
        Display display = this.getWindowManager().getDefaultDisplay();
        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);
        mContext = this;
        progress = new ProgressDialog(this);
        mRequestQueue = Volley.newRequestQueue(welcome.this);
        prefManager = new PrefManager(this);

        if (Build.VERSION.SDK_INT >= 17) {
            //new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            realWidth = realMetrics.widthPixels;
            realHeight = realMetrics.heightPixels;

        } else if (Build.VERSION.SDK_INT >= 14) {
            //reflection for this weird in-between time
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                //this may not be 100% accurate, but it's all we've got
                realWidth = display.getWidth();
                realHeight = display.getHeight();
                Log.e("Display Info", "Couldn't use reflection to get the real display metrics.");
            }
        } else {
            //This should be close, as lower API devices should not have window navigation bars
            realWidth = display.getWidth();
            realHeight = display.getHeight();
        }

        ImageView hidePopup = findViewById(R.id.hide_popup);
        ImageView logoImg = findViewById(R.id.logo_img);
        TextView welcomeTxt = findViewById(R.id.welcome_txt);
        TextView tagline_txt = findViewById(R.id.tagline_txt);
        final TextView privacy_txt = findViewById(R.id.privacy_txt);
        final TextView terms_txt = findViewById(R.id.terms_txt);
        final TextView conditions_txt = findViewById(R.id.conditions_txt);
        final TextView agree_txt = findViewById(R.id.agree_txt);
        final TextView and_txt = findViewById(R.id.and_txt);
        final LinearLayout termsAndConditionsWrap = findViewById(R.id.termsAndConditionsWrap);

        logoImg.animate().alpha(1f).setDuration(1000).start();
        welcomeTxt.animate().setStartDelay(1300).alpha(1f).setDuration(1000).start();
        tagline_txt.animate().setStartDelay(1500).alpha(1f).setDuration(1500).start();
        conditions_txt.animate().setStartDelay(1700).alpha(1f).setDuration(1700).start();
        agree_txt.animate().setStartDelay(1900).alpha(1f).setDuration(1900).start();
        terms_txt.animate().setStartDelay(1900).alpha(1f).setDuration(1900).start();
        privacy_txt.animate().setStartDelay(1900).alpha(1f).setDuration(1900).start();

        agree_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agree_txt.setVisibility(View.VISIBLE);
                conditions_txt.setVisibility(View.VISIBLE);
                showBottomSheetDialog();
            }
        });

        if (!prefManager.isFirstTimeLaunch()) {
            termsAndConditionsWrap.setVisibility(View.GONE);
            new android.os.Handler().postDelayed(new Runnable() {
                public void run() {
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
                                    Intent dashboard = new Intent(getApplicationContext(), AllPatients.class);
                                    startActivity(dashboard);
                                } else {
                                    showBottomSheetDialog();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                showBottomSheetDialog();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                            showBottomSheetDialog();
                        }
                    });
                    // Adding request to volley request queue
                    mRequestQueue.add(jsonReq);
                }
            }, 4500);
        }


        countryCode = new CountryCode();


        privacy_txt.setTag("https://www.medinin.com/privacy-policy");
        privacy_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(privacy_txt.getTag().toString()));
                startActivity(viewIntent);
            }
        });


        terms_txt.setTag("https://www.medinin.com/terms-condition");
        terms_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(terms_txt.getTag().toString()));
                startActivity(viewIntent);
            }
        });


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
        View loginView = getLayoutInflater().inflate(R.layout.login_dialog, null);
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
        new ClearFocusOnKBClose(loginView);

        //Common variables
        nextBtntxt = loginView.findViewById(R.id.nextBtntxt);
        loginSec = loginView.findViewById(R.id.login_wrap);
        sign_up_mobile_wrap = loginView.findViewById(R.id.sign_up_mobile_wrap);
        sign_up_user_type_wrap = loginView.findViewById(R.id.sign_up_user_type_wrap);
        sign_up_passcode_wrap = loginView.findViewById(R.id.sign_up_passcode_wrap);
        sign_up_basic_wrap = loginView.findViewById(R.id.sign_up_basic_wrap);

        InitLoginEvents(loginView);
        initUserSignUpEvents(loginView);
        initPassCodeEvents(loginView);
        dialog.show();
    }

    private void InitLoginEvents(View view) {
        //current page events
        getOtpBtn = view.findViewById(R.id.getOtpBtn);
        otpDisabledBtn = view.findViewById(R.id.otpDisabledBtn);
        mobileNumberInput = view.findViewById(R.id.mobileNumberInput);
        countryFlagImg = view.findViewById(R.id.countryFlagImg);
        country_code_txt = view.findViewById(R.id.country_code_txt);
        cancel_txt = view.findViewById(R.id.cancel_txt);
        resendOTP = view.findViewById(R.id.resendOTP);

        otp_input_one = (EditText) view.findViewById(R.id.otpInputOne);
        otp_input_two = (EditText) view.findViewById(R.id.otpInputTwo);
        otp_input_three = (EditText) view.findViewById(R.id.otpInputThree);
        otp_input_four = (EditText) view.findViewById(R.id.otpInputFour);

        bindLoginOTPFormClickEvents(otp_input_one, otp_input_two, null);
        bindLoginOTPFormClickEvents(otp_input_two, otp_input_three, otp_input_one);
        bindLoginOTPFormClickEvents(otp_input_three, otp_input_four, otp_input_two);
        bindLoginOTPFormClickEvents(otp_input_four, null, otp_input_three);

        //CountryCode
        countryFlagImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryCode.set(mContext, countryFlagImg, country_code_txt, mobileNumberInput);
            }
        });

        countDownTxt = view.findViewById(R.id.loginCountDownTxt);
        otpWrap = view.findViewById(R.id.otpWrap);

        docRadioBtn = view.findViewById(R.id.docRadioBtn);
        patientRadioBtn = view.findViewById(R.id.patientRadioBtn);
        hospitalRadioBtn = view.findViewById(R.id.hospitalRadioBtn);
        clinicRadioBtn = view.findViewById(R.id.clinicRadioBtn);
        labRadioBtn = view.findViewById(R.id.labRadioBtn);
        pharmacyRadioBtn = view.findViewById(R.id.pharmacyRadioBtn);


        loginTab = view.findViewById(R.id.loginTabLink);
        loginTabBr = view.findViewById(R.id.loginTabLinkBr);

        signUpTab = view.findViewById(R.id.signUPTabLink);
        signUpTabBr = view.findViewById(R.id.signUPTabLinkBr);
        nextBtnWrap = view.findViewById(R.id.next_btn_wrap);

        //tab links
        loginTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_up_mobile_wrap.setVisibility(View.GONE);
                sign_up_passcode_wrap.setVisibility(View.GONE);
                sign_up_basic_wrap.setVisibility(View.GONE);

                signUpTab.setTextColor(Color.parseColor("#afbfc6"));
                signUpTabBr.setVisibility(View.GONE);

                loginSec.setVisibility(View.VISIBLE);
                loginTab.setTextColor(Color.parseColor("#8e63e6"));
                loginTabBr.setVisibility(View.VISIBLE);

                signUpPageNumber = 0;
                nextBtnWrap.setVisibility(View.GONE);
                sign_up_user_type_wrap.setVisibility(View.GONE);
            }
        });

        signUpTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSec.setVisibility(View.GONE);
                loginTab.setTextColor(Color.parseColor("#afbfc6"));
                loginTabBr.setVisibility(View.GONE);

                signUpTab.setTextColor(Color.parseColor("#8e63e6"));
                signUpTabBr.setVisibility(View.VISIBLE);

                signUpPageNumber = 0;
                nextBtnWrap.setVisibility(View.VISIBLE);
                sign_up_user_type_wrap.setVisibility(View.VISIBLE);
                sign_up_mobile_wrap.setVisibility(View.GONE);
                sign_up_basic_wrap.setVisibility(View.GONE);
                sign_up_passcode_wrap.setVisibility(View.GONE);
            }
        });


        getOtpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String _phone_num = mobileNumberInput.getText().toString();
                if (_phone_num.length() == 10) {
                    sendOTP();
                } else {
                    Toast.makeText(welcome.this, "Please enter valid mobile number.", Toast.LENGTH_LONG).show();
                }
            }
        });

        resendOTP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String _phone_num = mobileNumberInput.getText().toString();
                if (_phone_num.length() == 10) {
                    sendOTP();
                } else {
                    Toast.makeText(welcome.this, "Please enter valid mobile number.", Toast.LENGTH_LONG).show();
                }
            }
        });

        cancel_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });


        mobileNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mobileNumberInput.getText().toString().length() == 10) {
                    otpDisabledBtn.setVisibility(View.GONE);
                    getOtpBtn.setVisibility(View.VISIBLE);
                } else {
                    otpDisabledBtn.setVisibility(View.VISIBLE);
                    getOtpBtn.setVisibility(View.GONE);
                }
            }
        });


        patientRadioBtn.setEnabled(false);
        hospitalRadioBtn.setEnabled(false);
        clinicRadioBtn.setEnabled(false);
        labRadioBtn.setEnabled(false);
        pharmacyRadioBtn.setEnabled(false);
    }

    private void bindLoginOTPFormClickEvents(final EditText _input_listener, final EditText
            _next_focusable, final EditText _prev_focusable) {


        _input_listener.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (_input_listener.getText().length() > 0) {
                    if (_next_focusable != null) {
                        _next_focusable.requestFocus();
                    }
                }
                checkOTP();
            }
        });

        _input_listener.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (_prev_focusable != null) {
                        _prev_focusable.requestFocus();
                    }
                }
                return false;
            }
        });

    }


    private void bindsignupOTPFormClickEvents(final EditText _input_listener, final EditText
            _next_focusable, final EditText _prev_focusable) {
        _input_listener.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (_input_listener.getText().length() > 0) {
                    if (_next_focusable != null) {
                        _next_focusable.requestFocus();
                    }
                }
                signUpCheckOTP();
            }
        });
        _input_listener.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    _prev_focusable.setText(null);
                    if (_prev_focusable != null) {
                        _prev_focusable.requestFocus();
                    }
                }
                return false;
            }
        });
    }


    private void initUserSignUpEvents(View view) {
        //sign up mobile
        signUpMobileNumberInput = (EditText) view.findViewById(R.id.signUpMobileNumberInput);
        signUpCountryFlagImg = (ImageView) view.findViewById(R.id.signUpCountryFlagImg);

        MobileNoErrorWrap = (LinearLayout) view.findViewById(R.id.MobileNoErrorWrap);
        otp_no_input_one = (EditText) view.findViewById(R.id.otpNumberInputOne);
        otp_no_input_two = (EditText) view.findViewById(R.id.otpNumberInputTwo);
        otp_no_input_three = (EditText) view.findViewById(R.id.otpNumberInputThree);
        otp_no_input_four = (EditText) view.findViewById(R.id.otpNumberInputFour);

        bindsignupOTPFormClickEvents(otp_no_input_one, otp_no_input_two, null);
        bindsignupOTPFormClickEvents(otp_no_input_two, otp_no_input_three, otp_no_input_one);
        bindsignupOTPFormClickEvents(otp_no_input_three, otp_no_input_four, otp_no_input_two);
        bindsignupOTPFormClickEvents(otp_no_input_four, null, otp_no_input_three);


        login_cancel_txt = (TextView) view.findViewById(R.id.cancel_txt);
        signUpCountDownTxt = (TextView) view.findViewById(R.id.signUpCountDownTxt);
        signUpCancelOtpTxt = (TextView) view.findViewById(R.id.signUpCancelOtpTxt);
        signUpResendOTP = (TextView) view.findViewById(R.id.signUpResendOTP);
        signUpGetOtpBtn = (TextView) view.findViewById(R.id.signUpGetOtpBtn);
        signUpOtpDisabledBtn = (TextView) view.findViewById(R.id.signUpOtpDisabledBtn);
        signUp_country_code_txt = (TextView) view.findViewById(R.id.signUp_country_code_txt);
        signUpOtpWrap = (ConstraintLayout) view.findViewById(R.id.signUpOtpWrap);

        //sign up basic details
        signUpUserNameInput = (EditText) view.findViewById(R.id.signUpUserNameInput);
        signUpDobInput = (EditText) view.findViewById(R.id.signUpDobInput);
        signUpGenderSpinner = (Spinner) view.findViewById(R.id.signUpGenderSpinner);
        signUpdDepartmentSpinner = (Spinner) view.findViewById(R.id.signUpdDepartmentSpinner);
        signUpBiometricLogWrap = (ConstraintLayout) view.findViewById(R.id.signUpBiometricLogWrap);
        biometricLoginSwitchBtn = (Switch) view.findViewById(R.id.biometricLoginSwitchBtn);

        signUpDobInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePickerDialog();
            }
        });

        signUpCountryFlagImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryCode.set(mContext, signUpCountryFlagImg, signUp_country_code_txt, signUpMobileNumberInput);
            }
        });

        ArrayList<StringWithTag> genderList = new ArrayList<StringWithTag>();
        genderList.add(new StringWithTag("Select", ""));
        genderList.add(new StringWithTag("Male", "male"));
        genderList.add(new StringWithTag("Female", "female"));
        bindSpinnerDropDown(signUpGenderSpinner, genderList);

        fetchSpecialities();

        signUpUserNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                nextBtntxt.setTextColor(Color.parseColor("#ffffff"));
                nextBtntxt.setClickable(true);
            }
        });

        signUpDobInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                nextBtntxt.setTextColor(Color.parseColor("#ffffff"));
                nextBtntxt.setClickable(true);
            }
        });

        signUpGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                nextBtntxt.setTextColor(Color.parseColor("#ffffff"));
                nextBtntxt.setClickable(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        nextBtntxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextBtntxt.setTextColor(Color.parseColor("#80ffffff"));
                nextBtntxt.setClickable(false);
                signUpPageNumber = signUpPageNumber + 1;
                Log.i("signUpPageNumber", String.valueOf(signUpPageNumber));
                if (signUpPageNumber == 1) {
                    //sign up with mobile page
                    sign_up_user_type_wrap.setVisibility(View.GONE);
                    sign_up_mobile_wrap.setVisibility(View.VISIBLE);
                    sign_up_basic_wrap.setVisibility(View.GONE);
                    sign_up_passcode_wrap.setVisibility(View.GONE);
                } else if (signUpPageNumber == 2) {
                    //sign up basic details page
                    sign_up_user_type_wrap.setVisibility(View.GONE);
                    sign_up_mobile_wrap.setVisibility(View.GONE);
                    sign_up_basic_wrap.setVisibility(View.VISIBLE);
                    sign_up_passcode_wrap.setVisibility(View.GONE);
                } else if (signUpPageNumber == 3) {
                    //sign up basic details page
                    if (!validateSignUpDetails()) {
//                        if (!biometricLoginSwitchBtn.isChecked()) {
//                            sign_up_user_type_wrap.setVisibility(View.GONE);
//                            sign_up_mobile_wrap.setVisibility(View.GONE);
//                            sign_up_basic_wrap.setVisibility(View.GONE);
//                            sign_up_passcode_wrap.setVisibility(View.VISIBLE);
//                        } else {
                        postUserRegister();
//                        }
                    } else {
                        signUpPageNumber = 2;
                    }
                } else if (signUpPageNumber > 3) {
                    postUserRegister();
                }
            }
        });
        nextBtntxt.setClickable(false);

        signUpMobileNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (signUpMobileNumberInput.getText().toString().length() == 10) {
                    JSONObject _obj = new JSONObject();
                    try {
                        _obj.put("mobile", signUpMobileNumberInput.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                            API_URL.DocMobileUnique, _obj, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            VolleyLog.d(TAG, "Response: " + response.toString());
                            progress.dismiss();
                            Log.i("postRegisterPatient", response.toString());
                            try {
                                boolean _error = Boolean.parseBoolean(response.getString("error"));
                                if (!_error) {
                                    JSONObject _data = response.getJSONObject("data");
                                    String _id = _data.getString("id");
                                    if (_id != null) {
                                        MobileNoErrorWrap.setVisibility(View.VISIBLE);
                                        signUpOtpDisabledBtn.setVisibility(View.VISIBLE);
                                        signUpGetOtpBtn.setVisibility(View.GONE);
                                    }
                                } else {
                                    MobileNoErrorWrap.setVisibility(View.GONE);
                                    signUpOtpDisabledBtn.setVisibility(View.GONE);
                                    signUpGetOtpBtn.setVisibility(View.VISIBLE);
                                    Toast.makeText(welcome.this, response.getString("message"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                MobileNoErrorWrap.setVisibility(View.GONE);
                                signUpOtpDisabledBtn.setVisibility(View.GONE);
                                signUpGetOtpBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            MobileNoErrorWrap.setVisibility(View.GONE);
                            signUpOtpDisabledBtn.setVisibility(View.GONE);
                            signUpGetOtpBtn.setVisibility(View.VISIBLE);
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                            progress.dismiss();
                        }
                    });

                    // Adding request to volley request queue
                    mRequestQueue.add(jsonReq);
                } else {
                    MobileNoErrorWrap.setVisibility(View.GONE);
                    signUpOtpDisabledBtn.setVisibility(View.VISIBLE);
                    signUpGetOtpBtn.setVisibility(View.GONE);
                    nextBtntxt.setClickable(false);
                    nextBtntxt.setTextColor(Color.parseColor("#80ffffff"));
                }
            }
        });
        login_cancel_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        signUpCancelOtpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        signUpGetOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (signUpMobileNumberInput.getText().toString().length() == 10) {
                    signUpSendOTP();
                } else {
                    Toast.makeText(welcome.this, "Please enter valid mobile number.", Toast.LENGTH_LONG).show();
                }
            }
        });

        signUpResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (signUpMobileNumberInput.getText().toString().length() == 10) {
                    signUpSendOTP();
                } else {
                    Toast.makeText(welcome.this, "Please enter valid mobile number.", Toast.LENGTH_LONG).show();
                }
            }
        });


        // Initializing both Android Keyguard Manager and Fingerprint Manager
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        // Check whether the device has a Fingerprint sensor.
        if (fingerprintManager == null || !fingerprintManager.isHardwareDetected()) {
            Toast.makeText(this, "Finger Print not supported", Toast.LENGTH_LONG).show();
            signUpBiometricLogWrap.setVisibility(View.GONE);
        } else {
            // Checks whether fingerprint permission is set on manifest
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Fingerprint authentication permission not enabled", Toast.LENGTH_LONG).show();
            } else {
                // Check whether at least one fingerprint is registered
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    Toast.makeText(this, "Register at least one fingerprint in Settings", Toast.LENGTH_LONG).show();
                } else {
                    // Checks whether lock screen security is enabled or not
                    if (!keyguardManager.isKeyguardSecure()) {
                        Toast.makeText(this, "Lock screen security not enabled in Settings", Toast.LENGTH_LONG).show();
                    } else {
                        // signUpBiometricLogWrap.setVisibility(View.VISIBLE);
                        signUpBiometricLogWrap.setVisibility(View.GONE);
                    }
                }
            }
        }
    }


    private void handleUserExit() {
        finish();
        System.exit(0);
    }

    private void sendOTP() {
        //Loader
        progress.setMessage("Sending OTP...");
        progress.show();

        JSONObject _body = new JSONObject();
        try {
            _body.put("mobile", mobileNumberInput.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("sendOTP _body", _body.toString());
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.SendOTP, _body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                try {
                    Log.i("sendOTP", response.toString());
                    Boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        otpWrap.setVisibility(View.VISIBLE);
                        resendOTP.setVisibility(View.VISIBLE);
                        getOtpBtn.setVisibility(View.GONE);
                        cancel_txt.setVisibility(View.GONE);
//                        otpNumberInput.setText("");
                        mobileNumberInput.setEnabled(false);
                        country_code_txt.setAlpha(0.3f);
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }
                        startTimer();

                        Toast.makeText(welcome.this, "OTP has been sent to your mobile.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(welcome.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(welcome.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    progress.dismiss();
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

    private void checkOTP() {

        progress.setMessage("Verifying OTP...");

        Boolean _error = false;

        String _otp_str = otp_input_one.getText().toString() + otp_input_two.getText().toString() + otp_input_three.getText().toString() + otp_input_four.getText().toString();

        if (validateEditTextInput(otp_input_one)) {
            _error = true;
        }
        if (validateEditTextInput(otp_input_two)) {
            _error = true;
        }
        if (validateEditTextInput(otp_input_three)) {
            _error = true;
        }
        if (validateEditTextInput(otp_input_four)) {
            _error = true;
        }
        Log.i("_otp_str", _otp_str.toString());
        Log.i("checkOTP _error", _error.toString());
        if (!_error) {
            JSONObject _body = new JSONObject();
            try {
                _body.put("mobile", mobileNumberInput.getText());
                _body.put("otp", _otp_str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("checkOTP obj", _body.toString());
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                    API_URL.CheckOTP, _body, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    try {

                        Boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            JSONObject _data = response.getJSONObject("data");
                            SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                            SharedPreferences.Editor Ed = sp.edit();
                            Ed.putString("med_user_id", _data.getString("id"));
                            Ed.putString("med_user_token", _data.getString("token"));
                            Ed.apply();

                            Intent dashboard = new Intent(getApplicationContext(), Welcome2.class);
                            startActivity(dashboard);

                        } else {
                            Toast.makeText(welcome.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(welcome.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
    }


    private void startTimer() {
        countDownTimer = new CountDownTimer(180000, 1000) { // adjust the milli seconds here
            public void onTick(long millisUntilFinished) {
                countDownTxt.setText("" + String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                signUpCountDownTxt.setText("" + String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {

            }
        }.start();
    }


    private void uncheckAllRadioButton() {
        docRadioBtn.setChecked(false);
        patientRadioBtn.setChecked(false);
        hospitalRadioBtn.setChecked(false);
        clinicRadioBtn.setChecked(false);
        labRadioBtn.setChecked(false);
        pharmacyRadioBtn.setChecked(false);
    }

    public void onSignUpOptionSelect(View view) {
        RadioButton radioBtn = view.findViewWithTag("RadioButton");
        uncheckAllRadioButton();
        radioBtn.setChecked(true);
        nextBtntxt.setTextColor(Color.parseColor("#ffffff"));
        nextBtntxt.setClickable(true);
    }

    public void onClinicOptionSelect(View view)
    {
        Intent intent = new Intent(welcome.this, ActivityClinicLogin.class);
        startActivity(intent);
    }


    public void onLabOptionSelect(View view)
    {
        Intent intent = new Intent(welcome.this, ActivityLabLogin.class);
        startActivity(intent);
    }

    //SingUp section ---------------------->
    private void signUpSendOTP() {
        //Loader
        progress.setMessage("Sending OTP...");
        progress.show();

        JSONObject _body = new JSONObject();
        try {
            _body.put("mobile", signUpMobileNumberInput.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.DocMobileGetOTP, _body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        JSONObject _data = (JSONObject) response.getJSONObject("data");
                        signUpOtpStr_id = _data.getString("id");


                        signUpResendOTP.setVisibility(View.VISIBLE);
                        signUpOtpWrap.setVisibility(View.VISIBLE);
                        signUpGetOtpBtn.setVisibility(View.GONE);
                        signUpCancelOtpTxt.setVisibility(View.GONE);

//                        signUpOtpNumberInput.setText("");
                        signUpMobileNumberInput.setEnabled(false);
                        signUpCountryFlagImg.setEnabled(false);
                        signUp_country_code_txt.setAlpha(0.3f);

                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }
                        startTimer();

                        Toast.makeText(welcome.this, "OTP has been sent to your mobile.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(welcome.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(welcome.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    progress.dismiss();
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

    private void signUpCheckOTP() {
        progress.setMessage("Verifying OTP...");

        Boolean _error = false;

        String _otp_str = otp_no_input_one.getText().toString() + otp_no_input_two.getText().toString() + otp_no_input_three.getText().toString() + otp_no_input_four.getText().toString();

        if (validateEditTextInput(otp_no_input_one)) {
            _error = true;
        }
        if (validateEditTextInput(otp_no_input_two)) {
            _error = true;
        }
        if (validateEditTextInput(otp_no_input_three)) {
            _error = true;
        }
        if (validateEditTextInput(otp_no_input_four)) {
            _error = true;
        }

        if (!_error) {
            JSONObject _body = new JSONObject();
            try {
                _body.put("otp", _otp_str);
                _body.put("id", signUpOtpStr_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                    API_URL.DocMobileCheckOTP, _body, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            JSONObject _data = (JSONObject) response.getJSONObject("data");
                            String otp_code = _data.getString("code");
                            if (otp_code.equals("200")) {
                                nextBtntxt.setTextColor(Color.parseColor("#ffffff"));
                                nextBtntxt.setClickable(true);
                                nextBtntxt.performClick();

                                Toast.makeText(welcome.this, "Mobile number verified successfully!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(welcome.this, "Please enter valid OTP number!", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(welcome.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(welcome.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
    }

    private Boolean validateEditTextInput(EditText _view) {
        String _value = _view.getText().toString();
        if (_value != null && !_value.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean validateSignUpDetails() {
        Boolean error = false;

        if (validateEditTextInput(signUpMobileNumberInput)) {
            error = true;
        } else if (validateEditTextInput(signUpUserNameInput)) {
            error = true;
            Toast.makeText(welcome.this, "Please enter your name!", Toast.LENGTH_LONG).show();
        } else if (validateEditTextInput(signUpDobInput)) {
            error = true;
            Toast.makeText(welcome.this, "Please select  date of birth!", Toast.LENGTH_LONG).show();
        } else if (SpinnerDropDown.getSpinnerItem(signUpGenderSpinner).toString().isEmpty()) {
            error = true;
            Toast.makeText(welcome.this, "Please select gender!", Toast.LENGTH_LONG).show();
        } else if (SpinnerDropDown.getSpinnerItem(signUpdDepartmentSpinner).toString().isEmpty()) {
            error = true;
            Toast.makeText(welcome.this, "Please select your speciality!", Toast.LENGTH_LONG).show();
        }

        return error;
    }

    private void postUserRegister() {
        if (!validateSignUpDetails()) {
            JSONObject _body = new JSONObject();
            Boolean error = false;
            try {
                String name_str = signUpUserNameInput.getText().toString();

                _body.put("name", name_str);
                _body.put("gender", SpinnerDropDown.getSpinnerItem(signUpGenderSpinner).toString());
                _body.put("dob", glDobStr);
                _body.put("age", ageStr);
                _body.put("mobile", signUpMobileNumberInput.getText());
                _body.put("mobile_code", signUp_country_code_txt.getText());
                _body.put("specialty_id", SpinnerDropDown.getSpinnerItem(signUpdDepartmentSpinner));

//                if (!biometricLoginSwitchBtn.isChecked() && skipPasscodeTxtBtn.getTag().toString().equals("0")) {
//                    String passCodeStr = passCode1.getText().toString() + passCode2.getText().toString() + passCode3.getText().toString() + passCode4.getText().toString();
//                    String confirmPassCodeStr = confirmPassCode1.getText().toString() + confirmPassCode2.getText().toString() + confirmPassCode3.getText().toString() + confirmPassCode4.getText().toString();
//
//                    if (passCodeStr.length() != 4 || confirmPassCodeStr.length() != 4) {
//                        error = true;
//                    }
//
//                    if (passCodeStr.equals(confirmPassCodeStr)) {
//                        _body.put("passcode", passCodeStr);
//                    } else {
//                        error = true;
//                    }
//                }

                //_body.put("email", "");
                //_body.put("password", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i("_body", _body.toString());
            if (!error) {
                JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                        API_URL.DocRegister, _body, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.d(TAG, "Response: " + response.toString());
                        try {
                            boolean _error = Boolean.parseBoolean(response.getString("error"));
                            if (!_error) {
                                JSONObject _data = (JSONObject) response.getJSONObject("data");

                                SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                                SharedPreferences.Editor Ed = sp.edit();
                                Ed.putString("med_user_id", _data.getString("id"));
                                Ed.putString("med_user_token", _data.getString("token"));
                                Ed.apply();

                                Intent dashboard = new Intent(getApplicationContext(), Welcome2.class);
                                startActivity(dashboard);

                                Toast.makeText(welcome.this, "Registration done successfully!", Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(welcome.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(welcome.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                });

                mRequestQueue.add(jsonReq);
            } else {
                Toast.makeText(welcome.this, "Passcode didnot match with confirm passcode! ", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openDatePickerDialog() {
        final Dialog dialog = new Dialog(welcome.this);
        dialog.setContentView(R.layout.dob_select_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
        long now = System.currentTimeMillis() - 1000;
        datePicker.setMaxDate((long) (now - (6.6485e+11)));
        TextView doneBtn = (TextView) dialog.findViewById(R.id.doneBtn);


        doneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                glDobStr = datePicker.getDayOfMonth() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getYear();
                String _dateStr = dateFormatFun(datePicker.getDayOfMonth() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getYear());
                signUpDobInput.setText(_dateStr);
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

    private String dateFormatFun(String _date) {
        if (!_date.equals("")) {
            //Convert date into local format
            DateFormat localFormat = new SimpleDateFormat("DD-MM-yyyy");
            DateFormat dateFormat = new SimpleDateFormat("DD-MMM-YY");
            try {
                Date date = localFormat.parse(_date);
                String result = dateFormat.format(date);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

    private void bindSpinnerDropDown(Spinner spinner, ArrayList<StringWithTag> list) {
        ArrayAdapter<StringWithTag> spinnerArrayAdapter = new ArrayAdapter<StringWithTag>(welcome.this, android.R.layout.simple_spinner_item, list);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    //PassCode section
    private void initPassCodeEvents(View view) {
        //current page events
        passCode1 = view.findViewById(R.id.passCode1);
        passCode2 = view.findViewById(R.id.passCode2);
        passCode3 = view.findViewById(R.id.passCode3);
        passCode4 = view.findViewById(R.id.passCode4);
        confirmPassCode1 = view.findViewById(R.id.confirmPassCode1);
        confirmPassCode2 = view.findViewById(R.id.confirmPassCode2);
        confirmPassCode3 = view.findViewById(R.id.confirmPassCode3);
        confirmPassCode4 = view.findViewById(R.id.confirmPassCode4);

        skipPasscodeTxtBtn = (TextView) view.findViewById(R.id.skipPasscodeTxtBtn);


        bindOTPFormClickEvents(passCode1, passCode2, null);
        bindOTPFormClickEvents(passCode2, passCode3, passCode1);
        bindOTPFormClickEvents(passCode3, passCode4, passCode2);
        bindOTPFormClickEvents(passCode4, null, passCode3);

        bindOTPFormClickEvents(confirmPassCode1, confirmPassCode2, null);
        bindOTPFormClickEvents(confirmPassCode2, confirmPassCode3, confirmPassCode1);
        bindOTPFormClickEvents(confirmPassCode3, confirmPassCode4, confirmPassCode2);
        bindOTPFormClickEvents(confirmPassCode4, null, confirmPassCode3);


        skipPasscodeTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipPasscodeTxtBtn.setTag(1);
                postUserRegister();
            }
        });
        confirmPassCode4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    postUserRegister();
                }
                return false;
            }
        });

    }


    private void bindOTPFormClickEvents(final EditText _input_listener, final EditText _next_focusable, final EditText _prev_focusable) {
        _input_listener.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (_input_listener.getText().length() > 0) {
                    if (_next_focusable != null) {
                        _next_focusable.requestFocus();
                    }
                }
                nextBtntxt.setTextColor(Color.parseColor("#ffffff"));
                nextBtntxt.setClickable(true);
            }
        });
        _input_listener.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    _prev_focusable.setText(null);
                    if (_prev_focusable != null) {
                        _prev_focusable.requestFocus();
                    }
                }
                return false;
            }
        });

    }

    private void fetchSpecialities() {
        progress.setMessage("Loading...");
        progress.show();
        JsonObjectRequest jsonEduReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.DocSpecialties, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                try {

                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data") && response.getJSONArray("data").length() != 0) {
                        JSONArray _data = (JSONArray) response.getJSONArray("data");

                        ArrayList<StringWithTag> specialitiesList = new ArrayList<StringWithTag>();
                        specialitiesList.add(new StringWithTag("Select", ""));
                        for (int i = 0; i < _data.length(); i++) {
                            JSONObject _feedObj = (JSONObject) _data.get(i);
                            specialitiesList.add(new StringWithTag(_feedObj.getString("name"), _feedObj.getString("id")));
                        }
                        bindSpinnerDropDown(signUpdDepartmentSpinner, specialitiesList);
                    } else {
                        Toast.makeText(welcome.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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
        jsonEduReq.setShouldCache(false);
        mRequestQueue.add(jsonEduReq);
    }

}
