package com.medinin.medininapp.helpers;

import android.annotation.SuppressLint;
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
import com.medinin.medininapp.utils.SpinnerDropDown;
import com.medinin.medininapp.volley.AppHelper;
import com.medinin.medininapp.volley.VolleyMultipartRequest;
import com.medinin.medininapp.volley.VolleySingleton;

import org.json.JSONArray;
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


public class CreatePatient extends AllPatients {
    Context context;
    String new_patient_id, mobile_verified_id, tempDateStr, ageStr, TAG, med_user_id, profilePicData;
    RequestQueue mRequestQueue;
    int realHeight;
    EditText name_edit, dob_edit, mobile_input, otp_edit;
    TextView getOtpBtn, resendOtpBtn, countDownTxt, otpDisabledBtn, bio_txt, otp_cancel_txt;
    LinearLayout previewImgSec, otpInputWrap, getOtpBtnWrap, patientBasicDetailWrap, addPhotoWrap, mobileErrorSec;
    Boolean camPreview1_status = false, camPreview2_status = false,
            camPreview3_status = false, reloading = false;
    ImageView camPreview1, camPreview2, camPreview3, reloadSec, uploadSec, changeCam;
    EditText otp_no_input_one, otp_no_input_two, otp_no_input_three, otp_no_input_four;
    ProgressDialog progress;
    PermissionRequest mPermissionRequest;
    FrameLayout next_btn_wrap;
    Spinner genderSpinner;
    boolean isEditPatientDialog = false;
    boolean isNewPatMobileVerified, genderDropDownLoaded = false;
    CountDownTimer countDownTimer;
    BottomSheetDialog registerNewPatDialog;
    ConstraintLayout otpVerifyTxtWrap;
    ImageView regTabLink, faceTabLink, countryFlagImg;
    TextView regTabLinkBr, addPhotoTabLinkBar, country_code_txt;
    TextView nextBtnTxt;
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
    private ImageView addPhotoTabLink, faceTmpImageHolder;

    public CreatePatient(Context context) {
        this.context = context;
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled", "InflateParams"})
    public void showRegisterPatientDialog(final Context cont, int real_height) {
        TAG = CreatePatient.class.getSimpleName();
        context = cont;
        TextWatcher _textWatcher, _num_textWatcher;
        isNewPatMobileVerified = false;
        progress = new ProgressDialog(context);
        sp = context.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        mRequestQueue = Volley.newRequestQueue(context);
        realHeight = real_height;

        isEditPatientDialog = false;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        regView = Objects.requireNonNull(inflater).inflate(R.layout.patient_register_dialog, null);
        registerNewPatDialog = new BottomSheetDialog(context, R.style.BaseBottomSheetDialog);
        registerNewPatDialog.setContentView(regView);
        mBehavior = BottomSheetBehavior.from((View) regView.getParent());
        mBehavior.setPeekHeight(realHeight);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    if (isNewPatMobileVerified) {
                        builder = new AlertDialog.Builder(context);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                registerNewPatDialog.hide();
                            }
                        });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                mBehavior.setPeekHeight(realHeight);
                                registerNewPatDialog.show();
                            }
                        });
                        builder.setMessage("Are you sure you want to cancel registration?");
                        dialog = builder.create();
                        dialog.show();
                    } else {
                        registerNewPatDialog.hide();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        new ClearFocusOnKBClose(regView);
        mPreview = regView.findViewById(R.id.preview);
        mGraphicOverlay = regView.findViewById(R.id.faceOverlay);
        createCameraSource(CameraSource.CAMERA_FACING_BACK);
        startCameraSource();
        regTabLink = regView.findViewById(R.id.regTabLink);
        regTabLinkBr = regView.findViewById(R.id.regTabLinkBr);

        faceTabLink = regView.findViewById(R.id.addPhotoTabLink);
        addPhotoTabLinkBar = regView.findViewById(R.id.addPhotoTabLinkBar);

        next_btn_wrap = regView.findViewById(R.id.next_btn_wrap);
        nextBtnTxt = regView.findViewById(R.id.nextBtntxt);
        patientBasicDetailWrap = regView.findViewById(R.id.patientBasicDetailWrap);
        addPhotoWrap = regView.findViewById(R.id.addPhotoWrap);
        final EditText tempFocusableView = regView.findViewById(R.id.tempFocusableView);
        genderSpinner = regView.findViewById(R.id.genderSpinner);
        name_edit = regView.findViewById(R.id.name_edit);
        dob_edit = regView.findViewById(R.id.dob_edit);
        mobile_input = regView.findViewById(R.id.num_edit);
        mobileErrorSec = regView.findViewById(R.id.MobileNoErrorWrap);

        countryFlagImg = regView.findViewById(R.id.countryFlagImg);
        country_code_txt = regView.findViewById(R.id.country_code_txt);

        countryCode = new CountryCode();
        countryFlagImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryCode.set(context, countryFlagImg, country_code_txt, mobile_input);
            }
        });

        //tab links
        regTabLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regTabLinkBr.setVisibility(View.VISIBLE);
                regTabLink.setImageDrawable(context.getDrawable(R.drawable.ic_edit_purple_icon));

                faceTabLink.setImageDrawable(context.getDrawable(R.drawable.facial_recognition_tab_link));
                addPhotoTabLinkBar.setVisibility(View.GONE);

                patientBasicDetailWrap.setVisibility(View.VISIBLE);
                addPhotoWrap.setVisibility(View.GONE);
            }
        });

        faceTabLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regTabLinkBr.setVisibility(View.GONE);
                regTabLink.setImageDrawable(context.getDrawable(R.drawable.ic_edit_gray_icon));

                faceTabLink.setImageDrawable(context.getDrawable(R.drawable.facial_recognition_purple_tab_link));
                addPhotoTabLinkBar.setVisibility(View.VISIBLE);

                patientBasicDetailWrap.setVisibility(View.GONE);
                addPhotoWrap.setVisibility(View.VISIBLE);
            }
        });

        faceTabLink.setClickable(false);

        nextBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validatePatientDetails(true)) {
                    nextBtnTxt.setTextColor(Color.parseColor("#80ffffff"));
                    nextBtnTxt.setClickable(false);
                    if (!isNewPatMobileVerified) {
                        progress.show();
                        progress.setMessage("Verifying...");

                        JSONObject _obj = new JSONObject();
                        try {
                            _obj.put("name", name_edit.getText());
                            _obj.put("dob", tempDateStr);
                            _obj.put("age", ageStr);
                            _obj.put("gender", SpinnerDropDown.getSpinnerItem(genderSpinner));
                            _obj.put("mobile", mobile_input.getText());
                            _obj.put("mobile_code", country_code_txt.getText());
                            _obj.put("doc_id", med_user_id);

                            if (isNewPatMobileVerified) {
                                _obj.put("mobile_verified_id", mobile_verified_id);
                                _obj.put("mobile_verification", "YES");
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
                                        mobile_verified_id = null;
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
                        regTabLink.setClickable(false);
                        faceTabLink.setClickable(true);
                        faceTabLink.performClick();
                        next_btn_wrap.setVisibility(View.GONE);
                    } else {
                        next_btn_wrap.setVisibility(View.GONE);
                        faceTabLink.setClickable(true);
                        faceTabLink.performClick();
                    }
                }
            }
        });

        nextBtnTxt.setClickable(false);
        registerNewPatDialog.show();

        otp_edit = regView.findViewById(R.id.otp_edit);

        otp_no_input_one = regView.findViewById(R.id.otpInputOne);
        otp_no_input_two = regView.findViewById(R.id.otpInputTwo);
        otp_no_input_three = regView.findViewById(R.id.otpInputThree);
        otp_no_input_four = regView.findViewById(R.id.otpInputFour);
        bindRegisterOTPFormClickEvents(otp_no_input_one, otp_no_input_two, null);
        bindRegisterOTPFormClickEvents(otp_no_input_two, otp_no_input_three, otp_no_input_one);
        bindRegisterOTPFormClickEvents(otp_no_input_three, otp_no_input_four, otp_no_input_two);
        bindRegisterOTPFormClickEvents(otp_no_input_four, null, otp_no_input_three);

        getOtpBtn = regView.findViewById(R.id.getOtpBtn);
        otp_cancel_txt = regView.findViewById(R.id.otp_cancel_txt);
        otpDisabledBtn = regView.findViewById(R.id.otpDisabledBtn);
        resendOtpBtn = regView.findViewById(R.id.resendOtpBtn);
        countDownTxt = regView.findViewById(R.id.countDownTxt);
        otpInputWrap = regView.findViewById(R.id.otpInputWrap);
        getOtpBtnWrap = regView.findViewById(R.id.getOtpBtnWrap);

        otp_cancel_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        registerNewPatDialog.hide();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builder.setMessage("Are you sure you want to close this activity?");
                dialog = builder.create();
                dialog.show();
            }
        });
        otpVerifyTxtWrap = regView.findViewById(R.id.otpVerifyTxtWrap);
        bio_txt = regView.findViewById(R.id.bio_txt);

        bindGenderDropDown(genderSpinner);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                if (genderDropDownLoaded) {
                    if (!validatePatientDetails(false)) {
                        next_btn_wrap.setVisibility(View.VISIBLE);
                        nextBtnTxt.setTextColor(Color.parseColor("#ffffff"));
                        nextBtnTxt.setClickable(true);
                    }
                } else {
                    genderDropDownLoaded = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

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

        dob_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePickerDialog();
            }
        });

        _textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!validatePatientDetails(false)) {
                    next_btn_wrap.setVisibility(View.VISIBLE);
                    nextBtnTxt.setTextColor(Color.parseColor("#ffffff"));
                    nextBtnTxt.setClickable(true);
                }
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
                if (mobile_input.getText().toString().length() == 10) {
                    //check validation and make next btn clickable
                    if (!validatePatientDetails(false)) {
                        next_btn_wrap.setVisibility(View.VISIBLE);
                        nextBtnTxt.setTextColor(Color.parseColor("#ffffff"));
                        nextBtnTxt.setClickable(true);
                    }

                    JSONObject _obj = new JSONObject();
                    try {
                        _obj.put("mobile", mobile_input.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                            API_URL.PatientMobileUnique, _obj, new Response.Listener<JSONObject>() {
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
                                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                        Objects.requireNonNull(imm).hideSoftInputFromWindow(mobile_input.getWindowToken(), 0);
                                        mobileErrorSec.setVisibility(View.VISIBLE);
                                        otpVerifyTxtWrap.setVisibility(View.VISIBLE);
                                        bio_txt.setText(R.string.useFaceScanTxt);
                                    }
                                } else {
                                    mobileErrorSec.setVisibility(View.GONE);
                                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();
                                    //show get otp button
                                    bio_txt.setText(R.string.sendSMSWarning);
                                    otpVerifyTxtWrap.setVisibility(View.VISIBLE);
                                    otpDisabledBtn.setVisibility(View.GONE);
                                    getOtpBtn.setVisibility(View.VISIBLE);
                                    otpInputWrap.setVisibility(View.GONE);
                                    resendOtpBtn.setVisibility(View.GONE);
                                }
                            } catch (JSONException e) {
                                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                Objects.requireNonNull(imm).hideSoftInputFromWindow(mobile_input.getWindowToken(), 0);
                                mobileErrorSec.setVisibility(View.GONE);
                                e.printStackTrace();
                                //show get otp button
                                bio_txt.setText(R.string.sendSMSWarning);
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
        mobile_input.addTextChangedListener(_num_textWatcher);
        previewImgSec = regView.findViewById(R.id.previewImgSec);
        camPreview1 = regView.findViewById(R.id.patientImg1);
        camPreview2 = regView.findViewById(R.id.patientImg2);
        camPreview3 = regView.findViewById(R.id.patientImg3);
        reloadSec = regView.findViewById(R.id.cameraIcon);
        uploadSec = regView.findViewById(R.id.uploadSec);
        changeCam = regView.findViewById(R.id.changeCam);

        //Menu links sec end
//        reloadSec.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View arg0) {
//                if (!reloading) {
//                    reloading = true;
//                    reloadSec.setAlpha(0.5f);
//                    uploadSec.setAlpha(0.5f);
//                    camPreview1_status = false;
//                    camPreview2_status = false;
//                    camPreview3_status = false;
//                }
//            }
//        });
        regView.findViewById(R.id.cameraIcon).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //Restart the camera scan
                if (!reloading) {
                    reloading = true;
                    reloadSec.setAlpha(0.5f);
                    uploadSec.setAlpha(0.5f);
                    camPreview1_status = false;
                    camPreview2_status = false;
                    camPreview3_status = false;
                    startCameraAgain();
                }
            }
        });
        regView.findViewById(R.id.changeCam).setOnClickListener(new View.OnClickListener() {
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

        tempFocusableView.requestFocus();
    }

    private Boolean validatePatientDetails(boolean msg) {
        boolean _error = false;
        if (validateEditTextInput(name_edit)) {
            _error = true;
            if (msg) {
                Toast.makeText(context, "Please enter name!", Toast.LENGTH_LONG).show();
            }
        } else if (validateEditTextInput(dob_edit)) {
            _error = true;
            if (msg) {
                Toast.makeText(context, "Please enter your date of birth!", Toast.LENGTH_LONG).show();
            }
        } else if (SpinnerDropDown.getSpinnerItem(genderSpinner).isEmpty()) {
            _error = true;
            if (msg) {
                Toast.makeText(context, "Please select gender!", Toast.LENGTH_LONG).show();
            }
        } else if (mobile_input.getText().length() < 10) {
            _error = true;
            if (msg) {
                Toast.makeText(context, "Please enter valid contact number!", Toast.LENGTH_LONG).show();
            }
        }
        return _error;
    }

    private static Boolean validateEditTextInput(EditText _view) {
        String _value = _view.getText().toString();
        return _value.isEmpty();
    }

    private void openDatePickerDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dob_select_popup);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final DatePicker datePicker = dialog.findViewById(R.id.datePicker);
        datePicker.setMaxDate(System.currentTimeMillis() - 1000);
        TextView doneBtn = dialog.findViewById(R.id.doneBtn);


        doneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String _dateStr = datePicker.getDayOfMonth() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getYear();
                dob_edit.setText(_dateStr);
                tempDateStr = _dateStr;
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

        ageStr = String.valueOf(age);
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

        boolean _error = false;


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

        if (validateEditTextInput(mobile_input)) {
            _error = true;
        }

        if (!_error) {
            progress.show();
            JSONObject _obj = new JSONObject();
            try {
                _obj.put("id", mobile_verified_id);
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
                                isNewPatMobileVerified = true;
                                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                Objects.requireNonNull(imm).hideSoftInputFromWindow(otp_no_input_four.getWindowToken(), 0);
                                Toast.makeText(context, "Mobile number verified successfully!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "Please enter valid OTP number!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        Objects.requireNonNull(imm).hideSoftInputFromWindow(otp_no_input_four.getWindowToken(), 0);
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
        }
    }

    private void bindGenderDropDown(Spinner spinner) {
        ArrayList<StringWithTag> monthList = new ArrayList<>();
        monthList.add(new StringWithTag("Select", ""));
        monthList.add(new StringWithTag("Male", "male"));
        monthList.add(new StringWithTag("Female", "female"));

        ArrayAdapter<StringWithTag> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, monthList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    private void patGetOTP() {
        boolean _error = false;
        String _mobile_str = mobile_input.getText().toString();

        if (validateEditTextInput(mobile_input)) {
            _error = true;
            Toast.makeText(context, "Please enter contact number!", Toast.LENGTH_LONG).show();
        } else if (mobile_input.getText().length() < 10) {
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
                            JSONObject _data = response.getJSONObject("data");
                            mobile_verified_id = _data.getString("id");
                            otpInputWrap.setVisibility(View.VISIBLE);
                            otp_cancel_txt.setVisibility(View.GONE);
                            getOtpBtn.setVisibility(View.GONE);
                            resendOtpBtn.setVisibility(View.VISIBLE);
                            otp_no_input_one.requestFocus();

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

    private void postRegisterPatient() {
        if (!validatePatientDetails(true)) {
            progress.show();
            progress.setMessage("Verifying...");

            JSONObject _obj = new JSONObject();
            try {
                _obj.put("name", name_edit.getText());
                _obj.put("dob", tempDateStr);
                _obj.put("age", ageStr);
                _obj.put("gender", SpinnerDropDown.getSpinnerItem(genderSpinner));
                _obj.put("mobile", mobile_input.getText());
                _obj.put("doc_id", med_user_id);

                if (isNewPatMobileVerified) {
                    _obj.put("mobile_verified_id", mobile_verified_id);
                    _obj.put("mobile_verification", "YES");
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
                            JSONObject _data = response.getJSONObject("data");
                            new_patient_id = _data.getString("id");
                            next_btn_wrap.setVisibility(View.GONE);
                            mobile_verified_id = null;
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


    private void startTimer() {
        countDownTimer = new CountDownTimer(180000, 1000) { // adjust the milli seconds here
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
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
                        registerNewPatDialog.hide();
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

    private void createCameraSource(int cameraFacing) {
        detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .setTrackingEnabled(false)
                .build();


        detector.setProcessor(new MultiProcessor.Builder<>(new CreatePatient.GraphicFaceTrackerFactory()).build());

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
            return new CreatePatient.GraphicFaceTracker(mGraphicOverlay);
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
        MuteAudio();
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
                    UnMuteAudio();
                } catch (Exception e) {
                    UnMuteAudio();
                    safeToTakePicture = true;
                    e.printStackTrace();
                }
            }
        });
    }

    public void MuteAudio() {
        AudioManager mAlramMAnager = (AudioManager) CreatePatient.this.getSystemService(Context.AUDIO_SERVICE);
        Objects.requireNonNull(mAlramMAnager).adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
    }

    public void UnMuteAudio() {
        AudioManager mAlramMAnager = (AudioManager) CreatePatient.this.getSystemService(Context.AUDIO_SERVICE);
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
