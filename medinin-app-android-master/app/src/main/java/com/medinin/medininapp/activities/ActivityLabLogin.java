package com.medinin.medininapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.medinin.medininapp.R;
import com.medinin.medininapp.helpers.CountryCode;
import com.medinin.medininapp.utils.ClearFocusOnKBClose;

import java.lang.reflect.Method;

public class ActivityLabLogin extends AppCompatActivity {

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
//        setContentView(R.layout.activity_lab_login);

        Display display = this.getWindowManager().getDefaultDisplay();
        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);
        mContext = this;
        progress = new ProgressDialog(this);
        mRequestQueue = Volley.newRequestQueue(ActivityLabLogin.this);
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
        showBottomSheetDialog();
    }
    public void onSignUpOptionSelect(View view) {
        RadioButton radioBtn = view.findViewWithTag("RadioButton");
        uncheckAllRadioButton();
        radioBtn.setChecked(true);
        nextBtntxt.setTextColor(Color.parseColor("#ffffff"));
        nextBtntxt.setClickable(true);
    }

    private void uncheckAllRadioButton() {
        docRadioBtn.setChecked(false);
        patientRadioBtn.setChecked(false);
        hospitalRadioBtn.setChecked(false);
        clinicRadioBtn.setChecked(false);
        labRadioBtn.setChecked(false);
        pharmacyRadioBtn.setChecked(false);
    }

    public void showBottomSheetDialog() {
        View loginView = getLayoutInflater().inflate(R.layout.lab_signup_dialog, null);
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
        dialog.show();
    }


    private void handleUserExit() {
        finish();
        System.exit(0);
    }

}
