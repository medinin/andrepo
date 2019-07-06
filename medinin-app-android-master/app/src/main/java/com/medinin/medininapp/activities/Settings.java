package com.medinin.medininapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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

import java.lang.reflect.Method;

import static com.medinin.medininapp.utils.CommonMethods.hasPermissions;

public class Settings extends AppCompatActivity {
    private static final String TAG = Settings.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private String med_user_id, med_user_token, _subject = "Medinin App Bug";
    private long back_pressed = 0;
    BottomSheetDialog dialog;
    int realWidth;
    int realHeight;
    private FrameLayout btnWrap;
    private ProgressDialog progress;
    private RelativeLayout resetpasscodeSec;
    private String passcode, confirmPasscode, patient_id;
    private EditText passCode1, passCode2, passCode3, passCode4, confirmPassCode1, confirmPassCode2, confirmPassCode3, confirmPassCode4;
    private FrameLayout actionPopup;
    private LinearLayout settingsWrap, head_sec, mainWrap1;
    private RelativeLayout resetPasswordBtn;
    private RadioGroup selectRadioBtn;
    private ImageView sendBtn;
    private JSONObject docDetails;
    private android.support.v7.widget.SwitchCompat Switch;
    private static final int PICK_CONTACT = 111;
    BottomSheetDialog resetDialog;
    private RadioButton defaultRadioBtn, customRadioBtn;
    private ConstraintLayout default_sec, custom_sec;
    boolean isBugVisible = true, isSendingFeedback = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initMenu();
        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);
        mRequestQueue = Volley.newRequestQueue(this);
        progress = new ProgressDialog(this);
        progress.setTitle("Checking login details...");
        progress.show();
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
                progress.hide();
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
        actionPopup = (FrameLayout) findViewById(R.id.actionPopup);
        settingsWrap = (LinearLayout) findViewById(R.id.settingsWrap);
        head_sec = (LinearLayout) findViewById(R.id.head_sec);

        Display display = this.getWindowManager().getDefaultDisplay();
        mainWrap1 = (LinearLayout) findViewById(R.id.mainWrap1);

        LinearLayout backBtn = (LinearLayout) findViewById(R.id.back_sec);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                launchAccountScreen();
            }
        });

        settingsWrap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                actionPopup.setVisibility(View.GONE);
                return false;
            }
        });

        head_sec.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                actionPopup.setVisibility(View.GONE);
                return false;
            }
        });

        mainWrap1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                actionPopup.setVisibility(View.GONE);
                return false;
            }
        });


        //reset-passcode
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

//        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resetPasswdDialog();
//            }
//        });


        android.support.v7.widget.SwitchCompat sw = (android.support.v7.widget.SwitchCompat) findViewById(R.id.biomatricLoginSwitch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!hasPermissions(Settings.this, new String[]{Manifest.permission.USE_FINGERPRINT})) {
                    ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.USE_FINGERPRINT}, 1);
                }
//                if (isChecked) {
//                    startActivity(new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS));
//                } else {
//                    Log.e("Display Info", "Biometric login not using.");
//                }
            }
        });

        RelativeLayout invite_txt = (RelativeLayout) findViewById(R.id.invite_txt);
        invite_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Medinin");
                    String strShareMessage = "You have been invited to try “Medinin” healthcare management tool.\n";
                    String strDownloadMessage = "\n Download Link - \n\n";
                    strShareMessage = strShareMessage + strDownloadMessage + "https://play.google.com/store/apps/details?id=com.medinin.medininapp&hl=en" + getPackageName();
                    Uri screenshotUri = Uri.parse("android.resource://packagename/drawable/image_name");
                    i.setType("image/png");
                    i.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                    i.putExtra(Intent.EXTRA_TEXT, strShareMessage);
                    startActivity(Intent.createChooser(i, "Share via"));
                } catch (Exception e) {
                    //e.toString();
                }

            }
        });


        RelativeLayout rate_app_txt = (RelativeLayout) findViewById(R.id.rate_app_txt);
        rate_app_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.medinin.medininapp&hl=en" + getPackageName())));

                } catch (Exception e) {
                    //e.toString();
                }

            }
        });

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


//    public void resetPasswdDialog() {
//        View loginView = getLayoutInflater().inflate(R.layout.reset_password_dialog, null);
//        resetDialog = new BottomSheetDialog(this) {
//            @Override
//            public void onBackPressed() {
//                if (back_pressed + 2000 > System.currentTimeMillis()) {
//                    resetDialog.hide();
//                    handleUserExit();
//                } else {
//                    Toast.makeText(getBaseContext(), "Press once again to exit", Toast.LENGTH_SHORT).show();
//                    back_pressed = System.currentTimeMillis();
//                }
//            }
//        };
//
//        resetDialog.setContentView(loginView);
//        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) loginView.getParent());
//        mBehavior.setPeekHeight(realHeight);
//        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
//                    resetDialog.hide();
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//            }
//        });
//
//        //InitLoginEvents(loginView);
//        resetDialog.show();
//        //dialog.show();
//    }


    public void openFeedbackDialog(View v) {
        View dialogView = getLayoutInflater().inflate(R.layout.feedback_ticket_popup, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(Settings.this);
        dialog.setContentView(dialogView);
        final EditText feedback_txt = (EditText) dialogView.findViewById(R.id.feedback_txt);
        final EditText bug_txt = (EditText) dialogView.findViewById(R.id.bug_txt);
        selectRadioBtn = (RadioGroup) dialogView.findViewById(R.id.radioSelect);
        defaultRadioBtn = (RadioButton) dialogView.findViewById(R.id.defaultRadioBtn);
        customRadioBtn = (RadioButton) dialogView.findViewById(R.id.customRadioBtn);
        default_sec = (ConstraintLayout) dialogView.findViewById(R.id.default_sec);
        custom_sec = (ConstraintLayout) dialogView.findViewById(R.id.custom_sec);
        final ImageView sendBtnGray = (ImageView) dialogView.findViewById(R.id.sendGray1);
        sendBtn = (ImageView) dialogView.findViewById(R.id.sendBtn1);
        selectRadioBtn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (defaultRadioBtn.getId() == checkedId) {
                    isBugVisible = true;
                    _subject = "Medinin App Bug";
                    default_sec.setVisibility(View.VISIBLE);
                    custom_sec.setVisibility(View.GONE);
                } else if (customRadioBtn.getId() == checkedId) {
                    isBugVisible = false;
                    _subject = "Medinin App Feedback";
                    default_sec.setVisibility(View.GONE);
                    custom_sec.setVisibility(View.VISIBLE);
                }
            }
        });

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

        final TextWatcher _ddf = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (bug_txt.getText().toString().length() >= 1) {
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
        bug_txt.addTextChangedListener(_ddf);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //post SMS
                progress.setTitle("Sending...");
                progress.show();
                if (!isSendingFeedback) {
                    isSendingFeedback = true;
                    JSONObject _jsonObj = new JSONObject();
                    try {
                        _jsonObj.put("subject", _subject);
                        if (!isBugVisible) {
                            _jsonObj.put("message", feedback_txt.getText().toString());
                        } else {
                            _jsonObj.put("message", bug_txt.getText().toString());
                        }
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
                            isSendingFeedback = false;
                            dialog.hide();
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
                            isSendingFeedback = false;
                            progress.hide();
                        }
                    });
                    // Adding request to volley request queue
                    mRequestQueue.add(jsonReq);
                }
            }
        });
        default_sec.setVisibility(View.VISIBLE);
        custom_sec.setVisibility(View.GONE);
        dialog.show();
    }

    public void menuPopUp(View v) {
        // Hide layouts if VISIBLE
        if (actionPopup.getVisibility() == View.GONE) {
            actionPopup.setVisibility(View.VISIBLE);
            final TextView termsLink = findViewById(R.id.termsLink);
            final TextView policiesLink = findViewById(R.id.policiesLink);
            termsLink.setTag("https://www.medinin.com/terms-condition");
            termsLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openWebView(termsLink);
                }
            });
            policiesLink.setTag("https://www.medinin.com/privacy-policy");
            policiesLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openWebView(policiesLink);
                }
            });
        }
        // Show layouts if they're not VISIBLE
        else {
            actionPopup.setVisibility(View.GONE);
        }
    }

    private void openWebView(TextView view) {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(view.getTag().toString()));
        startActivity(viewIntent);
    }

//    private void handleUserExit() {
//        finish();
//        System.exit(0);
//    }

    public void openSignOutDialog(View v) {
        View view = getLayoutInflater().inflate(R.layout.signout_popup, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(Settings.this);
        dialog.setContentView(view);
        view.findViewById(R.id.signout_txt).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor Ed = sp.edit();
                Ed.putString("med_user_id", null);
                Ed.putString("med_user_token", null);
                Ed.apply();
                Intent intent = new Intent(getApplicationContext(), welcome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.cancle_txt).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void openDeleteAccountDialog(View v) {
        View view = getLayoutInflater().inflate(R.layout.delete_account_popup, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(Settings.this);
        dialog.setContentView(view);
        view.findViewById(R.id.cancel_txt).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.delete_txt).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                progress.setTitle("Deleting...");
                progress.show();
                JsonObjectRequest jsonReq = null;
                try {
                    jsonReq = new JsonObjectRequest(Request.Method.POST,
                            API_URL.DocDeleteAccount + "/" + docDetails.getString("id"), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            VolleyLog.d(TAG, "Response: " + response.toString());
                            Log.i("response", response.toString());
                            isSendingFeedback = false;
                            dialog.hide();
                            progress.hide();
                            try {
                                boolean _error = Boolean.parseBoolean(response.getString("error"));
                                if (!_error) {
                                    finish();
                                    Intent intent = new Intent(getApplicationContext(), welcome.class);
                                    startActivity(intent);
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
                            isSendingFeedback = false;
                            progress.hide();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Adding request to volley request queue
                assert jsonReq != null;
                mRequestQueue.add(jsonReq);
            }
        });
        dialog.show();
    }

    public void showBottomSheetDialog(View view) {
        View resetPassCodeView = getLayoutInflater().inflate(R.layout.reset_passcode_dialog, null);
        dialog = new BottomSheetDialog(this) {
            @Override
            public void onBackPressed() {
                if (back_pressed + 2000 > System.currentTimeMillis()) {
                    dialog.hide();
                } else {
                    Toast.makeText(getBaseContext(), "Press once again to exit", Toast.LENGTH_SHORT).show();
                    back_pressed = System.currentTimeMillis();
                }
            }
        };
        dialog.setContentView(resetPassCodeView);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) resetPassCodeView.getParent());
        mBehavior.setPeekHeight(realHeight);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dialog.hide();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        InitResetEvents(resetPassCodeView);
        dialog.show();
    }

    private void InitResetEvents(View view) {
        //current page events
        mRequestQueue = Volley.newRequestQueue(Settings.this);
        resetpasscodeSec = view.findViewById(R.id.resetpasscodeSec);
        passCode1 = view.findViewById(R.id.passCode1);
        passCode2 = view.findViewById(R.id.passCode2);
        passCode3 = view.findViewById(R.id.passCode3);
        passCode4 = view.findViewById(R.id.passCode4);
        confirmPassCode1 = view.findViewById(R.id.confirmPassCode1);
        confirmPassCode2 = view.findViewById(R.id.confirmPassCode2);
        confirmPassCode3 = view.findViewById(R.id.confirmPassCode3);
        confirmPassCode4 = view.findViewById(R.id.confirmPassCode4);


        bindOTPFormClickEvents(passCode1, passCode2, null);
        bindOTPFormClickEvents(passCode2, passCode3, passCode1);
        bindOTPFormClickEvents(passCode3, passCode4, passCode2);
        bindOTPFormClickEvents(passCode4, null, passCode3);

        bindOTPFormClickEvents(confirmPassCode1, confirmPassCode2, null);
        bindOTPFormClickEvents(confirmPassCode2, confirmPassCode3, confirmPassCode1);
        bindOTPFormClickEvents(confirmPassCode3, confirmPassCode4, confirmPassCode2);
        bindOTPFormClickEvents(confirmPassCode4, null, confirmPassCode3);

    }

    private void bindOTPFormClickEvents(EditText _input_listener, final EditText
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
                if (_next_focusable != null) {
                    _next_focusable.requestFocus();
                }

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

    public void postPassCode(View v) {
        progress.show();
        Intent intent = new Intent(getApplicationContext(), welcome.class);
        startActivity(intent);

        if (!validatePasscode()) {
            JSONObject _obj = new JSONObject();
            try {
                JSONObject _em_s = new JSONObject();
                _obj.put("passcode", passcode);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                    API_URL.DocResetPassCode + med_user_id, _obj, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    try {
                        if (!response.isNull("data")) {
                            JSONObject dataObj = response.getJSONObject("data");
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
            mRequestQueue.add(jsonReq);
        } else {
            progress.dismiss();
            Toast.makeText(Settings.this, "Passcode not matching!", Toast.LENGTH_LONG).show();
        }
    }

    public boolean validatePasscode() {
        Boolean error = false;
        passcode = passCode1.getText().toString() + passCode2.getText().toString() + passCode3.getText().toString() + passCode4.getText().toString();
        confirmPasscode = confirmPassCode1.getText().toString() + confirmPassCode2.getText().toString() + confirmPassCode3.getText().toString() + confirmPassCode4.getText().toString();
        if (passcode.length() == 4 && confirmPasscode.length() == 4) {
            if (passcode.equals(confirmPasscode)) {
                error = false;

            } else {
                error = true;
                Toast.makeText(Settings.this, "Passcode not matching!", Toast.LENGTH_LONG).show();
            }
        } else {
            error = true;
            Toast.makeText(Settings.this, "Passcode length not matching!", Toast.LENGTH_LONG).show();
        }

        return error;
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
