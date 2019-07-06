package com.medinin.medininapp.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.medinin.medininapp.FaceGraphic;
import com.medinin.medininapp.R;
import com.medinin.medininapp.activities.AllPatients;
import com.medinin.medininapp.camera.CameraSourcePreview;
import com.medinin.medininapp.camera.GraphicOverlay;
import com.medinin.medininapp.config.API_URL;
import com.medinin.medininapp.data.StringWithTag;
import com.medinin.medininapp.utils.ClearFocusOnKBClose;
import com.medinin.medininapp.utils.CustomDateToString;
import com.medinin.medininapp.utils.SpinnerDropDown;
import com.medinin.medininapp.volley.AppHelper;
import com.medinin.medininapp.volley.VolleyMultipartRequest;
import com.medinin.medininapp.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class EditPatient extends AllPatients {
    Context context;
    String new_patient_id, patient_id, mobile_verified_id, tempDateStr, ageStr, TAG, med_user_id, profilePicData;
    RequestQueue mRequestQueue;
    int realHeight;
    EditText name_edit, dob_edit, mobile_input, otp_edit;
    TextView getOtpBtn, resendOtpBtn, countDownTxt, otpDisabledBtn, bio_txt, otp_cancel_txt;
    LinearLayout previewImgSec, otpInputWrap, getOtpBtnWrap, patientBasicDetailWrap, addPhotoWrap, mobileErrorSec;
    Boolean camPreview1_status = false, camPreview2_status = false,
            camPreview3_status = false, reloading = false;
    ImageView camPreview1, camPreview2, camPreview3, reloadSec, uploadSec, changeCam;
    EditText otp_no_input_one, otp_no_input_two, otp_no_input_three, otp_no_input_four, name, num_edit;
    ProgressDialog progress;
    PermissionRequest mPermissionRequest;
    FrameLayout next_btn_wrap;
    Spinner genderSpinner;
    boolean isEditPatientDialog = false, isNewPatMobileVerified, genderDropDownLoaded = false;
    CountDownTimer countDownTimer;
    BottomSheetDialog registerNewPatDialog;
    ConstraintLayout otpVerifyTxtWrap;
    ImageView regTabLink, faceTabLink, countryFlagImg;
    TextView regTabLinkBr, addPhotoTabLinkBar, country_code_txt, nextBtnTxt;
    AlertDialog dialog;
    AlertDialog.Builder builder;
    BottomSheetBehavior mBehavior;
    View regView;
    CountryCode countryCode;
    SharedPreferences sp;
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private FrameLayout no_data;
    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private int rotation;
    private int cameraId;
    boolean startCam = true;
    boolean faceScanUploading = false, safeToTakePicture = true, _scanPatientTabVisible = false;
    int camCount = 0;
    int faceScanApiCount = 0;
    AudioManager mgr;
    Bitmap loadedImage = null;
    Bitmap rotatedBitmap = null;
    FaceDetector detector;
    private Dialog getOTPDialog, otpInputDialog;
    private EditText otp_input_one, otp_input_two, otp_input_three, otp_input_four;
    private ImageView addPhotoTabLink, faceTmpImageHolder;
    private String _mobile_str = "", _otpID;
    BottomSheetDialog editProfileDialog;
    private FrameLayout update_next_btn_wrap;
    TextView updateBtn, doneBtn, getOtpCancelBtnWrap;
    WebView myWebView;
    TextWatcher _textWatcher, _num_textWatcher, _otp_textWatcher;
    private Boolean glFaceScanStatus = false;

    public EditPatient(Context context) {
        this.context = context;
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled", "InflateParams"})
    public void showEditPatientDialog(final Context cont, int real_height, String patient_id) {
        TAG = EditPatient.class.getSimpleName();
        context = cont;
        isNewPatMobileVerified = false;
        progress = new ProgressDialog(context);
        sp = context.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        mRequestQueue = Volley.newRequestQueue(context);
        realHeight = real_height;
        isEditPatientDialog = false;

        if (otpInputDialog != null) {
            otpInputDialog.dismiss();
        }
        isEditPatientDialog = true;
        new_patient_id = patient_id;
        _otpID = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View preview = Objects.requireNonNull(inflater).inflate(R.layout.patient_register_dialog, null);
        editProfileDialog = new BottomSheetDialog(context, R.style.BaseBottomSheetDialog);
        editProfileDialog.setContentView(preview);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) preview.getParent());
        mBehavior.setPeekHeight(realHeight);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    editProfileDialog.hide();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        new ClearFocusOnKBClose(preview);
        mPreview = preview.findViewById(R.id.preview);
        mGraphicOverlay = preview.findViewById(R.id.faceOverlay);
        createCameraSource(CameraSource.CAMERA_FACING_BACK);
        startCameraSource();
        editProfileDialog.show();
        countryFlagImg = preview.findViewById(R.id.countryFlagImg);
        country_code_txt = preview.findViewById(R.id.country_code_txt);

        regTabLink = preview.findViewById(R.id.regTabLink);
        regTabLinkBr = preview.findViewById(R.id.regTabLinkBr);

        addPhotoTabLink = preview.findViewById(R.id.addPhotoTabLink);
        addPhotoTabLinkBar = preview.findViewById(R.id.addPhotoTabLinkBar);

        next_btn_wrap = preview.findViewById(R.id.next_btn_wrap);
        update_next_btn_wrap = preview.findViewById(R.id.update_next_btn_wrap);
        patientBasicDetailWrap = preview.findViewById(R.id.patientBasicDetailWrap);
        addPhotoWrap = preview.findViewById(R.id.addPhotoWrap);
        final EditText tempFocusableView = preview.findViewById(R.id.tempFocusableView);


        next_btn_wrap.setVisibility(View.GONE);
        update_next_btn_wrap.setVisibility(View.VISIBLE);

        tempFocusableView.requestFocus();

        countryCode = new CountryCode();
        countryFlagImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryCode.set(EditPatient.this, countryFlagImg, country_code_txt, num_edit);
            }
        });

        //tab links
        regTabLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regTabLink.setImageDrawable(context.getDrawable(R.drawable.ic_edit_purple_icon));
                addPhotoTabLink.setImageDrawable(context.getDrawable(R.drawable.facial_recognition_tab_link));
                addPhotoTabLinkBar.setVisibility(View.GONE);
                addPhotoWrap.setVisibility(View.GONE);
                regTabLinkBr.setVisibility(View.VISIBLE);
                patientBasicDetailWrap.setVisibility(View.VISIBLE);
                update_next_btn_wrap.setVisibility(View.VISIBLE);
            }
        });

        addPhotoTabLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regTabLinkBr.setVisibility(View.GONE);
                regTabLink.setImageDrawable(context.getDrawable(R.drawable.ic_edit_gray_icon));
                addPhotoTabLink.setImageDrawable(context.getDrawable(R.drawable.facial_recognition_purple_tab_link));
                patientBasicDetailWrap.setVisibility(View.GONE);
                addPhotoTabLinkBar.setVisibility(View.VISIBLE);
                addPhotoWrap.setVisibility(View.VISIBLE);
                update_next_btn_wrap.setVisibility(View.GONE);
            }
        });


        //edit-profile --------------------------------------------------------------------------------
        genderSpinner = preview.findViewById(R.id.genderSpinner);
        name_edit = preview.findViewById(R.id.name_edit);
        dob_edit = preview.findViewById(R.id.dob_edit);
        num_edit = preview.findViewById(R.id.num_edit);
        mobileErrorSec = preview.findViewById(R.id.MobileNoErrorWrap);
        otp_no_input_one = preview.findViewById(R.id.otpInputOne);
        otp_no_input_two = preview.findViewById(R.id.otpInputTwo);
        otp_no_input_three = preview.findViewById(R.id.otpInputThree);
        otp_no_input_four = preview.findViewById(R.id.otpInputFour);

        updateBtn = preview.findViewById(R.id.updateBtn);
        doneBtn = preview.findViewById(R.id.doneBtn);
        getOtpBtn = preview.findViewById(R.id.getOtpBtn);
        otp_cancel_txt = preview.findViewById(R.id.otp_cancel_txt);
        otpDisabledBtn = preview.findViewById(R.id.otpDisabledBtn);
        resendOtpBtn = preview.findViewById(R.id.resendOtpBtn);

        countDownTxt = preview.findViewById(R.id.countDownTxt);

        otpInputWrap = preview.findViewById(R.id.otpInputWrap);
        getOtpBtnWrap = preview.findViewById(R.id.getOtpBtnWrap);
        otpVerifyTxtWrap = preview.findViewById(R.id.otpVerifyTxtWrap);

        doneBtn = preview.findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileDialog.hide();
            }
        });

        bindGenderDropDown(genderSpinner);

        progress.show();
        progress.setMessage("Loading...");

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.PatientFetchFull + new_patient_id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                try {
                    progress.dismiss();
                    if (!response.isNull("data")) {
                        JSONObject _data = response.getJSONObject("data");
                        name_edit.setText(removeNull(_data.getString("name")));
                        // dob_edit.setText(CustomDateToString.month(_data.getString("dob")));
                        dob_edit.setText(trimDateYear(_data.getString("dob")));
                        dob_edit.setTag(_data.getString("dob"));
                        tempDateStr = removeNull(_data.getString("dob"));
                        num_edit.setText(removeNull(_data.getString("mobile")));
                        countryCode.get(context, countryFlagImg, country_code_txt, _data.getString("mobile_code"));
                        if (_data.getString("mobile_verification").equals("NO")) {
                            getOtpBtnWrap.setVisibility(View.VISIBLE);
                            getOtpBtn.setVisibility(View.VISIBLE);
                            otpDisabledBtn.setVisibility(View.GONE);
                        } else {
                            getOtpBtnWrap.setVisibility(View.GONE);
                        }
                        SpinnerDropDown.setSpinnerItem(genderSpinner, _data.getString("gender"));
                        bindTextChangeEvents();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        jsonReq.setShouldCache(false);
        mRequestQueue.add(jsonReq);

        otp_cancel_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(EditPatient.this);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        editProfileDialog.hide();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builder.setMessage("Are you sure you want to close this activity?");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        getOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patGetOTP();
            }
        });

        resendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patGetOTP();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validatePatientDetails()) {
                    progress.show();
                    progress.setMessage("Verifying...");

                    JSONObject _obj = new JSONObject();
                    try {
                        String name_str = name_edit.getText().toString();
                        _obj.put("name", name_str);
                        _obj.put("dob", tempDateStr);
                        _obj.put("age", ageStr);
                        _obj.put("gender", SpinnerDropDown.getSpinnerItem(genderSpinner));
                        _obj.put("mobile", num_edit.getText());
                        _obj.put("mobile_code", country_code_txt.getText());
                        _obj.put("doc_id", med_user_id);

                        if (_otpID != null) {
                            _obj.put("patient_mobile_id", _otpID);
                            _obj.put("mobile_verification", "YES");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                            API_URL.Patient + "/" + new_patient_id, _obj, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            VolleyLog.d(TAG, "Response: " + response.toString());
                            progress.dismiss();

                            try {
                                boolean _error = Boolean.parseBoolean(response.getString("error"));
                                if (!_error) {
                                    editProfileDialog.hide();
                                    _otpID = null;
                                    if (!glFaceScanStatus) {
//                                fetchPatAndUpdateRow(new_patient_id);
                                    }
                                } else {
                                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
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
        });

        dob_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePickerDialog(dob_edit.getTag().toString());
            }
        });

        previewImgSec = preview.findViewById(R.id.previewImgSec);
        camPreview1 = preview.findViewById(R.id.patientImg1);
        camPreview2 = preview.findViewById(R.id.patientImg2);
        camPreview3 = preview.findViewById(R.id.patientImg3);
        reloadSec = preview.findViewById(R.id.cameraIcon);
        uploadSec = preview.findViewById(R.id.uploadSec);
        changeCam = preview.findViewById(R.id.changeCam);

        //Menu links sec end

        preview.findViewById(R.id.cameraIcon).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //Restart the camera scan
                reloadSec.setAlpha(0.5f);
                uploadSec.setAlpha(0.5f);
                safeToTakePicture = true;
                camCount = 0;
                camPreview1_status = false;
                camPreview2_status = false;
                camPreview3_status = false;
                startCameraAgain();
            }
        });
        preview.findViewById(R.id.changeCam).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (mCameraSource == null) {
                    createCameraSource(CameraSource.CAMERA_FACING_BACK);
                    startCameraSource();
                } else {
                    if (mCameraSource.getCameraFacing() == CameraSource.CAMERA_FACING_FRONT) {
                        if (mCameraSource != null) {
                            mCameraSource.release();
                        }
                        cameraId = CameraSource.CAMERA_FACING_BACK;
                        createCameraSource(CameraSource.CAMERA_FACING_BACK);
                    } else if (mCameraSource.getCameraFacing() == CameraSource.CAMERA_FACING_BACK) {
                        if (mCameraSource != null) {
                            mCameraSource.release();
                        }
                        cameraId = CameraSource.CAMERA_FACING_FRONT;
                        createCameraSource(CameraSource.CAMERA_FACING_FRONT);
                    }
                    startCameraSource();
                }

                camCount = 0;
                faceScanApiCount = 0;
                safeToTakePicture = true;
            }
        });

        uploadSec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (camPreview1_status && camPreview2_status && camPreview3_status) {
                    if (!isEditPatientDialog && isNewPatMobileVerified) {
                        postRegisterPatient();
                    } else if (!new_patient_id.isEmpty()) {
                        uploadFace();
                    }
                }
            }
        });
    }

    private void bindGenderDropDown(Spinner spinner) {
        ArrayList<StringWithTag> monthList = new ArrayList<StringWithTag>();
        monthList.add(new StringWithTag("Select", ""));
        monthList.add(new StringWithTag("Male", "male"));
        monthList.add(new StringWithTag("Female", "female"));

        ArrayAdapter<StringWithTag> spinnerArrayAdapter = new ArrayAdapter<StringWithTag>(context, android.R.layout.simple_spinner_item, monthList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    private void bindTextChangeEvents() {
        _textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateBtn.setVisibility(View.VISIBLE);
                doneBtn.setVisibility(View.GONE);
            }
        };


        name_edit.addTextChangedListener(_textWatcher);
        dob_edit.addTextChangedListener(_textWatcher);


        _num_textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getOtpBtnWrap.setVisibility(View.VISIBLE);
                if (num_edit.getText().toString().length() == 10) {
                    JSONObject _obj = new JSONObject();
                    try {
                        _obj.put("mobile", num_edit.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                            API_URL.PatientMobileUnique, _obj, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            VolleyLog.d(TAG, "Response: " + response.toString());
                            progress.dismiss();
                            Log.i("Verify number", response.toString());
                            try {
                                boolean _error = Boolean.parseBoolean(response.getString("error"));
                                if (!_error && !response.isNull("data")) {
                                    JSONObject _data = response.getJSONObject("data");
                                    String _id = _data.getString("id");
                                    if (_id != null) {
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(num_edit.getWindowToken(), 0);
                                        mobileErrorSec.setVisibility(View.VISIBLE);
                                        otpVerifyTxtWrap.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    mobileErrorSec.setVisibility(View.GONE);
                                    updateBtn.setVisibility(View.VISIBLE);
                                    doneBtn.setVisibility(View.GONE);
                                    otpVerifyTxtWrap.setVisibility(View.VISIBLE);
                                    otpDisabledBtn.setVisibility(View.GONE);
                                    getOtpBtn.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(num_edit.getWindowToken(), 0);
                                mobileErrorSec.setVisibility(View.GONE);
                                e.printStackTrace();
                                //show get otp button
                                otpVerifyTxtWrap.setVisibility(View.VISIBLE);
                                otpDisabledBtn.setVisibility(View.GONE);
                                getOtpBtn.setVisibility(View.VISIBLE);
                                otpInputWrap.setVisibility(View.GONE);
                                resendOtpBtn.setVisibility(View.GONE);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mobileErrorSec.setVisibility(View.GONE);
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                            progress.dismiss();
                        }
                    });

                    // Adding request to volley request queue
                    mRequestQueue.add(jsonReq);
                } else {
                    otpDisabledBtn.setVisibility(View.VISIBLE);
                    getOtpBtn.setVisibility(View.GONE);
                    mobileErrorSec.setVisibility(View.GONE);
                }
            }
        };
        num_edit.addTextChangedListener(_num_textWatcher);

        genderDropDownLoaded = false;
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                if (genderDropDownLoaded) {
                    updateBtn.setVisibility(View.VISIBLE);
                    doneBtn.setVisibility(View.GONE);
                } else {
                    genderDropDownLoaded = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        bindRegisterOTPFormClickEvents(otp_no_input_one, otp_no_input_two, null);
        bindRegisterOTPFormClickEvents(otp_no_input_two, otp_no_input_three, otp_no_input_one);
        bindRegisterOTPFormClickEvents(otp_no_input_three, otp_no_input_four, otp_no_input_two);
        bindRegisterOTPFormClickEvents(otp_no_input_four, null, otp_no_input_three);
    }

    private void bindRegisterOTPFormClickEvents(EditText _input_listener, final EditText
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
                patCheckOTP();
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

    private void patCheckOTP() {
        //Loader
        progress = new ProgressDialog(context);
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

        if (validateEditTextInput(num_edit)) {
            _error = true;
        }

        if (!_error) {
            progress.show();
            JSONObject _obj = new JSONObject();
            try {
                _obj.put("id", _otpID);
                _obj.put("otp", _otp_str);
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
                            JSONObject _data = response.getJSONObject("data");

                            String otp_code = _data.getString("code");
                            if (otp_code.equals("200")) {
                                getOtpBtnWrap.setVisibility(View.GONE);
                                otpInputWrap.setVisibility(View.GONE);
                                getOtpBtn.setVisibility(View.VISIBLE);
                                resendOtpBtn.setVisibility(View.GONE);

                                Toast.makeText(context, "Mobile number verified successfully!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "Please enter valid OTP number!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private Boolean validateEditTextInput(EditText _view) {
        String _value = _view.getText().toString();
        return _value == null || _value.isEmpty();
    }

    private void openOTPInputDialog() {
        otpInputDialog = new Dialog(context);
        otpInputDialog.setContentView(R.layout.otp_input_popup);
        otpInputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        otpInputDialog.setCancelable(false);
        otpInputDialog.show();

        final TextView countDownTxt = otpInputDialog.findViewById(R.id.countDownTxt);
        final TextView dialogSendOtp = otpInputDialog.findViewById(R.id.dialogSendOtp);
        final TextView dialogClose = otpInputDialog.findViewById(R.id.dialogClose);
        TextView dialogMobileNo = otpInputDialog.findViewById(R.id.dialogMobileNo);

        otp_input_one = otpInputDialog.findViewById(R.id.otp_input_one);
        otp_input_two = otpInputDialog.findViewById(R.id.otp_input_two);
        otp_input_three = otpInputDialog.findViewById(R.id.otp_input_three);
        otp_input_four = otpInputDialog.findViewById(R.id.otp_input_four);

        bindOTPFormClickEvents(otp_input_one, otp_input_two, null);
        bindOTPFormClickEvents(otp_input_two, otp_input_three, otp_input_one);
        bindOTPFormClickEvents(otp_input_three, otp_input_four, otp_input_two);
        bindOTPFormClickEvents(otp_input_four, null, otp_input_three);

        dialogMobileNo.setText(_mobile_str);


        new CountDownTimer(180000, 1000) { // adjust the milli seconds here
            public void onTick(long millisUntilFinished) {
                countDownTxt.setText("" + String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                countDownTxt.setVisibility(View.GONE);
                dialogSendOtp.setVisibility(View.VISIBLE);
                otpInputDialog.dismiss();
            }
        }.start();


        dialogSendOtp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                countDownTxt.setVisibility(View.VISIBLE);
                dialogSendOtp.setVisibility(View.GONE);
                otpInputDialog.dismiss();
                getOTP();

            }
        });

        dialogClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                otpInputDialog.dismiss();
            }
        });
    }

    private void patGetOTP() {
        //Loader
        Boolean _error = false;

        String _mobile_str = num_edit.getText().toString();

        if (validateEditTextInput(num_edit)) {
            _error = true;
            Toast.makeText(context, "Please enter contact number!", Toast.LENGTH_LONG).show();
        } else if (num_edit.getText().length() < 10) {
            _error = true;
            Toast.makeText(context, "Please enter valid contact number!", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(context, "OTP has been sent to your mobile.", Toast.LENGTH_LONG).show();
                            ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                                    .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                            JSONObject _data = response.getJSONObject("data");
                            _otpID = _data.getString("id");

                            otpInputWrap.setVisibility(View.VISIBLE);
                            getOtpBtn.setVisibility(View.GONE);
                            otp_cancel_txt.setVisibility(View.GONE);
                            resendOtpBtn.setVisibility(View.VISIBLE);

                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }
                            startTimer();
                        } else {
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private Boolean validatePatientDetails() {
        boolean _error = false;
        if (validateEditTextInput(name_edit)) {
            _error = true;
            Toast.makeText(context, "Please enter name!", Toast.LENGTH_LONG).show();
        } else if (validateEditTextInput(dob_edit)) {
            _error = true;
            Toast.makeText(context, "Please enter your date of birth!", Toast.LENGTH_LONG).show();
        } else if (SpinnerDropDown.getSpinnerItem(genderSpinner).isEmpty()) {
            _error = true;
            Toast.makeText(context, "Please select gender!", Toast.LENGTH_LONG).show();
        } else if (num_edit.getText().length() < 10) {
            _error = true;
            Toast.makeText(context, "Please enter valid contact number!", Toast.LENGTH_LONG).show();
        }
        return _error;
    }

    private void openDatePickerDialog(String date) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dob_select_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final DatePicker datePicker = dialog.findViewById(R.id.datePicker);
        datePicker.setMaxDate(System.currentTimeMillis() - 1000);
//        datePicker.updateDate();
        TextView doneBtn = dialog.findViewById(R.id.doneBtn);

        //datePicker.date
        doneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String _dateStr = datePicker.getDayOfMonth() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getYear();
                dob_edit.setText(trimDateYear(_dateStr));
                tempDateStr = _dateStr;
                getAge(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private String trimDateYear(String _date) {
        if (_date != null && !_date.equals("null") && !_date.isEmpty()) {
            String[] splitDate = _date.split("\\-");
            String subStringYr = splitDate[2].substring(Math.max(splitDate[2].length() - 2, 0));
            return CustomDateToString.month(splitDate[0] + "-" + splitDate[1] + "-" + subStringYr);
        } else {
            return "";
        }
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

    private void postRegisterPatient() {
        if (!validatePatientDetails()) {
            progress.show();
            progress.setMessage("Verifying...");

            JSONObject _obj = new JSONObject();
            try {
                _obj.put("name", name_edit.getText());
                _obj.put("dob", tempDateStr);
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
                    Log.i("postRegisterPatient", response.toString());
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            JSONObject _data = response.getJSONObject("data");
                            new_patient_id = _data.getString("id");
                            next_btn_wrap.setVisibility(View.GONE);
                            _otpID = null;
                            if (camPreview1_status && camPreview2_status && camPreview3_status) {
                                uploadFace();
                            }
                        } else {
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private void uploadFace() {
        progress = new ProgressDialog(context);
        progress.setTitle("Uploading");
        progress.setMessage("Wait while we upload your photo");
        progress.show();
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, API_URL.PatientChangePhoto + '/' + new_patient_id, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    String resultResponse = new String(response.data);
                    Log.d("image", resultResponse);
                    JSONObject _res = new JSONObject(resultResponse);
                    boolean _error = Boolean.parseBoolean(_res.getString("error"));
                    if (!_error) {
                        TrainFace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    Log.i("upload file error", result);
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                params.put("photo", new DataPart(new_patient_id + ".png", AppHelper.getFileDataFromDrawable(context, camPreview1.getDrawable()), "image/png"));
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);
    }

    private void TrainFace() {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, API_URL.FaceTrainImage, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    String resultResponse = new String(response.data);
                    Log.d("image", resultResponse);
                    JSONObject _res = new JSONObject(resultResponse);
                    boolean _error = Boolean.parseBoolean(_res.getString("error"));
                    if (!_error) {
                        editProfileDialog.hide();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                safeToTakePicture = true;
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    Log.i("upload file error", result);
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", new_patient_id);
                return params;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                params.put("photo", new DataPart(new_patient_id + ".png", AppHelper.getFileDataFromDrawable(context, camPreview1.getDrawable()), "image/png"));
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);
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

    private void checkOTP() {
        //Loader
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

        if (!_error) {
            progress.show();
            JSONObject _obj = new JSONObject();
            try {
                _obj.put("id", _otpID);
                _obj.put("otp", _otp_str);
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
                            JSONObject _data = response.getJSONObject("data");

                            String otp_code = _data.getString("code");
                            if (otp_code.equals("200")) {
                                getOTPDialog.dismiss();
                                EditPatient editPatient = new EditPatient(getApplicationContext());
                                editPatient.showEditPatientDialog(context, realHeight, patient_id);
                            } else {
                                Toast.makeText(context, "Please enter valid OTP number!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private void getOTP() {
        //Loader
        boolean _error = false;
        if (_mobile_str.length() < 10) {
            _error = true;
            Toast.makeText(context, "Please enter valid contact number!", Toast.LENGTH_LONG).show();
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
                    getOTPDialog.dismiss();
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {

                            Toast.makeText(context, "OTP has been sent to your mobile.", Toast.LENGTH_LONG).show();

                            JSONObject _data = response.getJSONObject("data");
                            _otpID = _data.getString("id");
                            openOTPInputDialog();
                        } else {
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private void createCameraSource(int cameraFacing) {
        detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .setTrackingEnabled(false)
                .build();


        detector.setProcessor(new MultiProcessor.Builder<>(new EditPatient.GraphicFaceTrackerFactory()).build());

        if (!detector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }
        mCameraSource = null;
        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(cameraFacing)
                .setAutoFocusEnabled(true)
                .setRequestedFps(30.0f)
                .build();
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new EditPatient.GraphicFaceTracker(mGraphicOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            Log.d("face found", "2");
            camCount++;
            mFaceGraphic.setId(faceId);
            if (safeToTakePicture && camCount > 30) {
                safeToTakePicture = false;
                takePicture();
                Log.d("face update", "3");
            }
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            camCount++;
            if (safeToTakePicture && camCount > 30) {
                safeToTakePicture = false;
                takePicture();
                Log.d("face update", "3");
            }
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    private void startCameraSource() {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private void takePicture() {
//        MuteAudio();
        safeToTakePicture = false;
        mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
            private File imageFile;

            @Override
            public void onPictureTaken(byte[] bytes) {
                try {
                    // convert byte array into bitmap
                    loadedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                    String[] cameraIds = manager.getCameraIdList();
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIds[cameraId]);
                    Log.d("dfk", String.valueOf(characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)));
                    Matrix rotateMatrix = new Matrix();
                    rotateMatrix.postRotate(characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION));
                    rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), rotateMatrix, false);
                    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                    rotatedBitmap = resize(rotatedBitmap, 480, 600);
                    rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                    previewImgSec.setVisibility(View.VISIBLE);

                    Frame frame = new Frame.Builder().setBitmap(rotatedBitmap).build();
                    SparseArray<Face> mFaces = detector.detect(frame);
                    int size = 600;
                    float left = 0;
                    float top = 0;
                    float right = 0;
                    float bottom = 0;
                    for (int i = 0; i < mFaces.size(); i++) {
                        Face face = mFaces.valueAt(i);
                        left = (float) (face.getPosition().x);
                        top = (float) (face.getPosition().y);
                        right = (float) (face.getPosition().x + face.getWidth());
                        bottom = (float) (face.getPosition().y + face.getHeight());
                    }
                    Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
                    Rect dst = new Rect(0, 0, size, size);
                    Bitmap tempBitmap = Bitmap.createBitmap(rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), Bitmap.Config.RGB_565);
                    Canvas canvas = new Canvas(tempBitmap);
                    canvas.drawBitmap(rotatedBitmap, src, dst, null);
                    camPreview1.setImageDrawable(new BitmapDrawable(context.getResources(), tempBitmap));
                    camPreview2.setImageDrawable(new BitmapDrawable(context.getResources(), tempBitmap));
                    camPreview3.setImageDrawable(new BitmapDrawable(context.getResources(), tempBitmap));
                    camPreview1_status = true;
                    camPreview2_status = true;
                    camPreview3_status = true;
                    safeToTakePicture = false;
                } catch (Exception e) {
                    safeToTakePicture = true;
                    e.printStackTrace();
                }
            }
        });
    }

    public void MuteAudio() {
        AudioManager mAlramMAnager = (AudioManager) EditPatient.this.getSystemService(Context.AUDIO_SERVICE);
        Objects.requireNonNull(mAlramMAnager).adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
    }

    public void UnMuteAudio() {
        AudioManager mAlramMAnager = (AudioManager) EditPatient.this.getSystemService(Context.AUDIO_SERVICE);
        Objects.requireNonNull(mAlramMAnager).adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
    }

    private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    private void stopCamera() {
        camCount = 20;
        faceScanApiCount = 5;
        safeToTakePicture = false;
        if (mPreview != null) {
            mPreview.stop();
        }
        UnMuteAudio();
    }

    private void killCamera() {
        if (mCameraSource != null) {
            try {
                mCameraSource.release();
            } catch (NullPointerException ignored) {

            }
        }
    }

    private void startCameraAgain() {
        camCount = 0;
        faceScanApiCount = 0;
        safeToTakePicture = true;
        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    protected void closeCameraAndPreview() {
        Log.d("mcam", String.valueOf(mCameraSource));
        if (mPreview != null) {
            mPreview.release();
            mPreview = null;
        }
        if (mCameraSource != null) {
            mCameraSource.release();
            mCameraSource = null;
        }
    }
}
