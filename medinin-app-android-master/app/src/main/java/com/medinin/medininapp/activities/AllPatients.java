package com.medinin.medininapp.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.PermissionRequest;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.medinin.medininapp.camera.CameraSourcePreview;
import com.medinin.medininapp.camera.GraphicOverlay;
import com.medinin.medininapp.config.API_URL;
import com.medinin.medininapp.data.StringWithTag;
import com.medinin.medininapp.helpers.CountryCode;
import com.medinin.medininapp.helpers.CreatePatient;
import com.medinin.medininapp.helpers.DeletePatient;
import com.medinin.medininapp.helpers.DownLoadCircleImageTask;
import com.medinin.medininapp.helpers.EditPatient;
import com.medinin.medininapp.utils.CreatePatientHelpers;
import com.medinin.medininapp.utils.CustomDateToString;
import com.medinin.medininapp.volley.AppHelper;
import com.medinin.medininapp.volley.VolleyMultipartRequest;
import com.medinin.medininapp.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.medinin.medininapp.utils.CommonMethods.hasPermissions;

public class AllPatients extends AppCompatActivity {
    public static final String TAG = AllPatients.class.getSimpleName();
    private final int SPLASH_DISPLAY_LENGHT = 3000;
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
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.CAPTURE_VIDEO_OUTPUT,
            android.Manifest.permission.MODIFY_AUDIO_SETTINGS
    };
    private LinearLayout linearLayoutList;
    public ProgressDialog progress;
    private CoordinatorLayout activity_all_patient;
    BottomSheetDialog editProfileDialog;
    int realHeight;
    int realWidth;
    ArrayList<StringWithTag> bloodGroupList;
    JSONArray _blood_arr;
    public int selectedPatRowIndex = 0;
    private EditText otp_input_one, otp_input_two, otp_input_three, otp_input_four,
            otp_no_input_one, otp_no_input_two, otp_no_input_three, otp_no_input_four;
    private LinearLayout editWrap, faceScanWrap;
    private TextView editTabLinkBr, faceScanTabLinkBr;
    private ImageView editTabLink, faceScanTabLink;
    public RequestQueue mRequestQueue;
    private long back_pressed = 0;
    private Boolean isEditPatientDialog = false, _isCameraLoaded = false, _isFaceApiLoaded = false;
    //edit-profile
    private Spinner genderSpinner;
    EditText name, name_edit, dob_edit, num_edit, otp_edit;
    TextView updateBtn, doneBtn, getOtpBtn, resendOtpBtn, countDownTxt, otpDisabledBtn, getOtpCancelBtnWrap, country_code_txt, otp_cancel_txt;
    TextWatcher _textWatcher, _num_textWatcher, _otp_textWatcher;
    LinearLayout otpInputWrap, mobileErrorSec;
    ConstraintLayout otpVerifyTxtWrap, loaderWrap;
    private FrameLayout next_btn_wrap, update_next_btn_wrap;
    private LinearLayout getOtpBtnWrap;
    private CountryCode countryCode;
    private ImageView countryFlagImg;
    boolean doubleBackToExitPressedOnce = false;
    String ageStr, tempDateStr;
    CountDownTimer countDownTimer;
    private String patient_id, med_user_id, med_user_token;
    LayoutInflater layoutInflater;
    Boolean _patientsLoadStatus = true;
    ScrollView scrollView;
    private EditText mainSearchBox;
    private ImageView closeSearchImg;
    private boolean isSearchPat = false;
    private Dialog getOTPDialog, otpInputDialog;
    private ConstraintLayout no_data_sec;
    int _page_number = 0;
    private String _mobile_str = "", _otpID;
    //Register new patient variables
    private BottomSheetDialog registerNewPatDialog;
    private ImageView regTabLink, addPhotoTabLink, faceTmpImageHolder;
    private TextView regTabLinkBr, addPhotoTabLinkBar;
    private TextView nextBtntxt;
    private LinearLayout patGetOtpBtnWrap, patientBasicDetailWrap, addPhotoWrap;
    WebView myWebView;
    private PermissionRequest mPermissionRequest;
    ImageView camPreview1, camPreview2, camPreview3, reloadSec, uploadSec, changeCam;
    private Boolean camPreview1_status = false, camPreview2_status = false,
            camPreview3_status = false, reloading = false;
    String camPreview1_status_str = "off";
    String profilePicData;
    Button uploadBtn;
    private Boolean isprogressBarStatus;
    LinearLayout previewImgSec;
    private Boolean isNewPatMobileVerified = false;
    private String new_patient_id = "";
    //Global patient face scan variables
    private BottomSheetDialog patGlobalFaceScanDialog;
    private BottomSheetBehavior mBehavior;
    private ImageView glPatFaceScanImg;
    private LinearLayout gl_pat_details_wrap, gl_scan_wrap, glFSPatAppointDetailWrap, glFSOtpInputWrap;
    private ImageView glFSDetailTabLink, glFSTabLink;
    private TextView glFSTabLinkBr, glFSDetailTabLinkBar;
    private Boolean glFaceScanStatus = false;
    //Global scanned patient details variables
    private ImageView glFSViewPatDetailsImgBtn, glFSEditProfileImgBtn, glFSDeleteImgBtn;
    private de.hdodenhof.circleimageview.CircleImageView glFSPatientImg;
    private TextView glFS_patient_name_txt, glFSPatDOBTxt, glFSPatAgeTxt, glFSPatGenderTxt,
            glFSPatMobileTxt, glFSLastVisitDateTxt, glFSVerifyOtpTxt, otpCancelTxtBtn,
            bookAppointBtn, glFSPatAgeSmallTxt, glFSPatRBCTxt;
    private FrameLayout bookAppointBtnWrap, gl_scan_controls_wrap;
    private LinearLayout ageWrap, rbcWrap;
    private Dialog scannedPatListDialog;
    private CircleImageView scannedPatImg;
    AlertDialog.Builder builder;
    Context mContext;
    private DeletePatient deletePatient;
    private View scanView;
    private boolean genderDropDownLoaded = false, isScannedFaceLoaded = false;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_patients);


        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        CreatePatientHelpers.initMenu(this);
        Intent intent = getIntent();
        isprogressBarStatus = intent.getBooleanExtra("ProgressBar", false);
        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);
        mRequestQueue = Volley.newRequestQueue(this);
        linearLayoutList = findViewById(R.id.linearLayoutList);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading...");
        mContext = this;
        activity_all_patient = findViewById(R.id.activity_all_patient);
        builder = new AlertDialog.Builder(this);
        mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Display display = this.getWindowManager().getDefaultDisplay();
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
        findViewById(R.id.addNewPatient).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                CreatePatient CreatePatient = new CreatePatient(getApplicationContext());
                CreatePatient.showRegisterPatientDialog(mContext, realHeight);
            }
        });
        scrollView = findViewById(R.id.feedScroll);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                //example
                View view = scrollView.getChildAt(0);

                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                if (diff <= 0 && _patientsLoadStatus) {
                    _patientsLoadStatus = false;
                    loadMorePatients();
                }
            }
        });

        mainSearchBox = findViewById(R.id.searchBox);
        closeSearchImg = findViewById(R.id.closeSearchImg);
        closeSearchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainSearchBox.setText("");
                closeSearchImg.setVisibility(View.GONE);
                mainSearchBox.clearFocus();
                hideKeyboard(AllPatients.this);
            }
        });


        if (isNetworkConnected()) {


        } else {

            layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            View no_internet = layoutInflater.inflate(R.layout.no_internet, null);
            linearLayoutList.addView(no_internet);
        }

        initGlobalFaceScanEvents();
        loadBloodGroups();


        //On close of soft keyboard clear edit text focus
        // it's works only if activity windowSoftInputMode=adjustResize
        deletePatient = new DeletePatient();
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            return cm.getActiveNetworkInfo() != null;
        } catch (Exception e) {
            return false;
        }

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void loadBloodGroups() {
        JsonObjectRequest jsonReqBloodGroups = new JsonObjectRequest(Request.Method.GET,
                API_URL.BloodGroup, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        _blood_arr = response.getJSONArray("data");

                        fetchAllPatients();
                        bindPatientSearchBox();

                    } else {
                        Toast.makeText(AllPatients.this, response.getString("message"), Toast.LENGTH_LONG).show();
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
        mRequestQueue.add(jsonReqBloodGroups);
    }


    private void fetchAllPatients() {
        linearLayoutList.removeAllViews();
        //api call
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("limit", 10);
            _obj.put("page", 0);
            _obj.put("doc_id", med_user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!isprogressBarStatus) {
            progress.show();
        }
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.DocPatientSearch, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                try {
                    parseJsonFeed(response, false);
                } catch (Exception e) {
                    Log.d("API callback error", e.getMessage());
                }
                TextView instruction_txt = findViewById(R.id.instruction_txt);
                FrameLayout no_data = findViewById(R.id.no_data);
                instruction_txt.setText("Click the + button on the lower right corner to create a new patient");
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {
                        JSONObject dataObj = response.getJSONObject("data");
                        JSONArray feedArray = dataObj.getJSONArray("result");
                        if (feedArray.length() != 0) {
                            _patientsLoadStatus = true;
                        }
                        for (int i = 0; i < feedArray.length(); i++) {
                            JSONObject feedObj = (JSONObject) feedArray.get(i);
                        }
                        if (feedArray.length() == 0) {
                            no_data.setVisibility(View.VISIBLE);
                        }
                        progress.dismiss();
                    } else {
                        progress.dismiss();
                        Toast.makeText(AllPatients.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    progress.dismiss();
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
        mRequestQueue.add(jsonReq);
    }

    public String removeNull(String _string) {
        return _string.equals("null") ? "" : _string;
    }

    private void parseJsonFeed(JSONObject response, Boolean search) {
        try {
            boolean _error = Boolean.parseBoolean(response.getString("error"));
            if (!_error && !response.isNull("data")) {
                JSONObject dataObj = response.getJSONObject("data");
                JSONArray feedArray = dataObj.getJSONArray("result");
                if (feedArray.length() != 0) {
                    _patientsLoadStatus = true;
                }
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);
                    JSONObject patientObj = feedObj.getJSONObject("patient");
                    createAllPatientRow(patientObj, search, -1);
                }
                progress.dismiss();
            } else {
                progress.dismiss();
                Toast.makeText(AllPatients.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            progress.dismiss();
            e.printStackTrace();
        }
    }

    public void createAllPatientRow(JSONObject patientObj, Boolean search, int index) {
        try {
            layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            final View preview = layoutInflater.inflate(R.layout.all_patient_row, null);

            CircleImageView patientImg = preview.findViewById(R.id.patientImg);
            TextView patient_name_txt = preview.findViewById(R.id.patient_name_txt);
            final LinearLayout dropDownBtn = preview.findViewById(R.id.dropDownBtn);
            TextView patDOBTxt = preview.findViewById(R.id.patDOBTxt);
            TextView patAgeTxt = preview.findViewById(R.id.patAgeTxt);
            TextView patAgeSmallTxt = preview.findViewById(R.id.patAgeSmallTxt);
            TextView patRBCTxt = preview.findViewById(R.id.patRBCTxt);
            TextView patMobileTxt = preview.findViewById(R.id.patMobileTxt);
            TextView countryCodeTxt = preview.findViewById(R.id.countryCode);
            TextView lastVisitDateTxt = preview.findViewById(R.id.lastVisitDateTxt);
            TextView patGenderTxt = preview.findViewById(R.id.patGenderTxt);
            ImageView viewPatDetailsImgBtn = preview.findViewById(R.id.viewPatDetailsImgBtn);
            ImageView editProfileImgBtn = preview.findViewById(R.id.editProfileImgBtn);
            ImageView deleteUserImgBtn = preview.findViewById(R.id.deleteUserImgBtn);
            LinearLayout bottomBarBtn = preview.findViewById(R.id.bottomBarBtn);
            LinearLayout ageWrap = preview.findViewById(R.id.ageWrap);
            LinearLayout rbcWrap = preview.findViewById(R.id.rbcWrap);

            patient_name_txt.setText(patientObj.getString("name"));
            patDOBTxt.setText(CustomDateToString.month(patientObj.getString("dob")));
            patAgeTxt.setText(removeNull(patientObj.getString("age")));
            patAgeSmallTxt.setText("(" + removeNull(patientObj.getString("age")) + "Y)");
            patMobileTxt.setText(removeNull(patientObj.getString("mobile")));
            patGenderTxt.setText(patientObj.getString("gender"));
            final String _tempPatID = patientObj.getString("id");
            final String _mobile = patientObj.getString("mobile");
            preview.setTag(_tempPatID);
            String mobile_verification = "";
            if (!patientObj.isNull("mobile_verification")) {
                mobile_verification = patientObj.getString("mobile_verification");
            }

            if (!patientObj.isNull("mobile_code") && !patientObj.getString("mobile_code").isEmpty()) {
                countryCodeTxt.setText(patientObj.getString("mobile_code"));
            }

            if (!patientObj.isNull("photo") && !patientObj.getString("photo").isEmpty()
                    && !patientObj.getString("photo").equals("null")) {
                String newurl = API_URL._domain + patientObj.getString("photo");
                new DownLoadCircleImageTask(patientImg).execute(newurl);
            } else {
                if (patientObj.getString("gender").equals("male")) {
                    patientImg.setImageResource(R.drawable.male_user);
                } else {
                    patientImg.setImageResource(R.drawable.female_user);
                }
            }

            if (!patientObj.isNull("last_visit")) {
                String last_visit_date_str = patientObj.getString("last_visit");
                if (!last_visit_date_str.isEmpty() && !last_visit_date_str.equals("null")) {
                    last_visit_date_str = "Last Visit " + new CustomDateToString().month(last_visit_date_str);
                    lastVisitDateTxt.setText(last_visit_date_str);
                } else if (!patientObj.isNull("updated_at")) {
//                    String[] splitDate = patientObj.getString("updated_at").split("T");
//                    String[] splitDate1 = splitDate[0].split("\\-");
//                    last_visit_date_str = "Last Visit " + new CustomDateToString().month(splitDate1[2] + splitDate1[1] + splitDate1[0]);
//                    lastVisitDateTxt.setText(last_visit_date_str);
                }
            }

//            if (!patientObj.isNull("blood_group_id")) {
//                String blood_group_id = patientObj.getString("blood_group_id");
//                if (blood_group_id != null && !blood_group_id.isEmpty() && !blood_group_id.equals("null")) {
//                    for (int blood_index = 0; blood_index < _blood_arr.length(); blood_index++) {
//                        JSONObject bloodObj = (JSONObject) _blood_arr.get(blood_index);
//                        if (blood_group_id.equals(bloodObj.getString("id"))) {
//                            patRBCTxt.setText(bloodObj.getString("name"));
//                            ageWrap.setVisibility(View.GONE);
//                            rbcWrap.setVisibility(View.VISIBLE);
//                            patAgeSmallTxt.setVisibility(View.VISIBLE);
//                        }
//                    }
//                }
//            }

            if (search) {
                String nameFullText = patientObj.getString("name");
                String mSearchText = mainSearchBox.getText().toString();

                if (!mSearchText.isEmpty()) {
                    int startPos = nameFullText.toLowerCase(Locale.US).indexOf(mSearchText.toLowerCase(Locale.US));
                    int endPos = startPos + mSearchText.length();

                    if (startPos != -1) {
                        Spannable spannable = new SpannableString(nameFullText);
                        ColorStateList _color = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.parseColor("#282f3f")});//Color.parseColor("#e6282f3f")
                        TextAppearanceSpan highlightSpan = new TextAppearanceSpan("@font/mulibold", Typeface.NORMAL, -1, _color, null);
                        spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        patient_name_txt.setText(spannable);
                    } else {
                        patient_name_txt.setText(nameFullText);
                    }
                } else {
                    patient_name_txt.setText(nameFullText);
                }
            }

            final ImageView arrow_right_img = preview.findViewById(R.id.arrow_right_img);
            final ConstraintLayout pat_details_wrap = preview.findViewById(R.id.pat_details_wrap);

            final AnimationSet animSet = new AnimationSet(true);
            animSet.setInterpolator(new DecelerateInterpolator());
            animSet.setFillAfter(true);
            animSet.setFillEnabled(true);

            //Load animation
            final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_down);

            final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up);


            //------------------------ check this out ------------------------ //

            dropDownBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    String _tag = dropDownBtn.getTag().toString();
                    if (_tag.equals("0")) {
                        dropDownBtn.setTag(1);
                        pat_details_wrap.startAnimation(slide_up);
                        pat_details_wrap.setVisibility(View.VISIBLE);
                    } else {
                        dropDownBtn.setTag(0);
                        pat_details_wrap.startAnimation(slide_down);
                        pat_details_wrap.setVisibility(View.GONE);
                    }

                    View view = AllPatients.this.getCurrentFocus();
                    if (view != null) {
                        getCurrentFocus().clearFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            });

            bottomBarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dropDownBtn.performClick();
                }
            });

        // -------------------- work out ------------------//

            viewPatDetailsImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(getApplicationContext(), PatientHistory.class);
//                    intent.putExtra("patient_id", _tempPatID);
//                    startActivity(intent);


                    Intent intent = new Intent(AllPatients.this, PatientHistory.class);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    intent.putExtra("patient_id", _tempPatID);
                    intent.putExtra("ProgressBar", true);
                    startActivity(intent);
                }
            });

            //mobile_verification
            final String final_mobile_verification = mobile_verification;
            editProfileImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    patient_id = _tempPatID;
                    selectedPatRowIndex = linearLayoutList.indexOfChild(preview);
                    if (final_mobile_verification.equals("YES")) {
                        openGetOtpDialog(_mobile);
                    } else {
                        EditPatient editPatient = new EditPatient(getApplicationContext());
                        editPatient.showEditPatientDialog(mContext, realHeight, patient_id);
                    }
                }
            });

            deleteUserImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog = new AlertDialog.Builder(AllPatients.this)
                            .setTitle("Delete")
                            .setMessage("Do you want to Delete")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    deletePatient.deletePatientAccount(mContext, preview, _tempPatID, linearLayoutList);
                                    dialog.dismiss();
                                }

                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();
                }
            });

            if (index < 0) {
                linearLayoutList.addView(preview);
            } else {
                if (isEditPatientDialog) {
                    linearLayoutList.removeViewAt(index);
                    linearLayoutList.addView(preview, index);
                } else {
                    linearLayoutList.addView(preview, 0);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadMorePatients() {
        progress.show();
        progress.setTitle("Loading...");

        _page_number = _page_number + 1;
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("page", _page_number);
            _obj.put("doc_id", med_user_id);
            _obj.put("limit", 10);

            if (!mainSearchBox.getText().toString().isEmpty()) {
                _obj.put("query", mainSearchBox.getText().toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.DocPatientSearch, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                try {
                    parseJsonFeed(response, isSearchPat);
                } catch (Exception e) {
                    Log.d("API callback error", e.getMessage());
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

    public void bindPatientSearchBox() {
        final TextWatcher _df = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mainSearchBox.getText().toString().length() >= 1) {
                    //Make the API request
                    _page_number = 0;
                    isSearchPat = true;
                    JSONObject _obj = new JSONObject();
                    try {
                        _obj.put("query", mainSearchBox.getText().toString());
                        _obj.put("limit", 10);
                        _obj.put("page", _page_number);
                        _obj.put("doc_id", med_user_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                            API_URL.DocPatientSearch, _obj, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                linearLayoutList.removeAllViews();
                                parseJsonFeed(response, true);
                            } catch (Exception e) {
                                Log.d("API callback error", e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                        }
                    });
                    // Adding request to volley request queue
                    mRequestQueue.add(jsonReq);
                } else {
                    isSearchPat = false;
                    fetchAllPatients();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mainSearchBox.getText().toString().length() > 0) {
                    closeSearchImg.setVisibility(View.VISIBLE);
                } else {
                    closeSearchImg.setVisibility(View.GONE);
                }
            }
        };

        mainSearchBox.addTextChangedListener(_df);
    }

    private void openGetOtpDialog(String _mobile) {
        getOTPDialog = new Dialog(AllPatients.this);
        getOTPDialog.setContentView(R.layout.get_otp_popup);
        TextView getOtpBtn = getOTPDialog.findViewById(R.id.getOtp);
        TextView number_txt = getOTPDialog.findViewById(R.id.number_txt);

        number_txt.setText(_mobile);
        _mobile_str = _mobile;

        getOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOTP();
            }
        });

        getOTPDialog.show();
    }

    private void getOTP() {
        //Loader
        Boolean _error = false;

        if (_mobile_str.length() < 10) {
            _error = true;
            Toast.makeText(AllPatients.this, "Please enter valid contact number!", Toast.LENGTH_LONG).show();
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

                            Toast.makeText(AllPatients.this, "OTP has been sent to your mobile.", Toast.LENGTH_LONG).show();

                            JSONObject _data = response.getJSONObject("data");
                            _otpID = _data.getString("id");
                            openOTPInputDialog();
                        } else {
                            Toast.makeText(AllPatients.this, response.getString("message"), Toast.LENGTH_LONG).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AllPatients.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
                                editPatient.showEditPatientDialog(mContext, realHeight, patient_id);
                            } else {
                                Toast.makeText(AllPatients.this, "Please enter valid OTP number!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AllPatients.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AllPatients.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private void updateVerifiedPatient() {
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("patient_mobile_id", _otpID);
            _obj.put("mobile_verification", "YES");
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

                    } else {
                        Toast.makeText(AllPatients.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AllPatients.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private void postDocPatient() {
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("doc_id", med_user_id);
            _obj.put("patient_id", patient_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.DocPatient, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {

                    } else {
                        Toast.makeText(AllPatients.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AllPatients.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);
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

    private void openOTPInputDialog() {
        otpInputDialog = new Dialog(AllPatients.this);
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

    private Boolean validateEditTextInput(EditText _view) {
        String _value = _view.getText().toString();
        return _value == null || _value.isEmpty();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        activity_all_patient.requestFocus();
        hideKeyboard(AllPatients.this);
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
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

                                    //updateBtn.setVisibility(View.VISIBLE);
                                    //doneBtn.setVisibility(View.GONE);
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

    private void patCheckOTP() {
        //Loader
        progress = new ProgressDialog(AllPatients.this);

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

                                Toast.makeText(AllPatients.this, "Mobile number verified successfully!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AllPatients.this, "Please enter valid OTP number!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AllPatients.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AllPatients.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    //patient edit profile end <----------------------------------------------------------------------


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
//                signUpCheckOTP();
//                checkOTP();
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

    //register new patient section  end<----------------------------------------------------------------

    //Global patient face scan section start --------------------------------------------------------->
    private void initGlobalFaceScanEvents() {
        glPatFaceScanImg = findViewById(R.id.glPatFaceScanImg);
        glPatFaceScanImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPatGlobalFaceScanDialog();
                glFaceScanStatus = true;
            }
        });
    }

    private void initPatGlobalFaceScanDialog() {
        _scanPatientTabVisible = false;
        camCount = 0;
        faceScanApiCount = 0;
        _isCameraLoaded = true;
        safeToTakePicture = true;
        _mobile_str = "";
        _otpID = null;
        if (mCameraSource != null) {
            closeCameraAndPreview();
        }
        scanView = getLayoutInflater().inflate(R.layout.pat_global_face_scan_dialog, null);
        patGlobalFaceScanDialog = new BottomSheetDialog(this, R.style.BaseBottomSheetDialog);
        patGlobalFaceScanDialog.setContentView(scanView);
        mBehavior = BottomSheetBehavior.from((View) scanView.getParent());
        mBehavior.setPeekHeight(realHeight);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int state) {
                if (state == BottomSheetBehavior.STATE_HIDDEN) {
                    patGlobalFaceScanDialog.dismiss();
                    glFaceScanStatus = false;
                    if (scannedPatListDialog != null) {
                        scannedPatListDialog.dismiss();
                    }
                    mCameraSource = null;
                    closeCameraAndPreview();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        patGlobalFaceScanDialog.show();
        mPreview = scanView.findViewById(R.id.preview);
        mGraphicOverlay = scanView.findViewById(R.id.faceOverlay);
        createCameraSource(CameraSource.CAMERA_FACING_BACK);
        startCameraSource();
        glFSTabLink = scanView.findViewById(R.id.glFSTabLink);
        glFSDetailTabLink = scanView.findViewById(R.id.glFSDetailTabLink);
        glFSTabLinkBr = scanView.findViewById(R.id.glFSTabLinkBr);
        glFSDetailTabLinkBar = scanView.findViewById(R.id.glFSDetailTabLinkBar);
        gl_scan_wrap = scanView.findViewById(R.id.gl_scan_wrap);
        gl_scan_controls_wrap = scanView.findViewById(R.id.faceScanCameraSecWrap);
        gl_pat_details_wrap = scanView.findViewById(R.id.gl_pat_details_wrap);
        bookAppointBtn = scanView.findViewById(R.id.bookAppointBtn);
        bookAppointBtnWrap = scanView.findViewById(R.id.bookAppointBtnWrap);
        faceTmpImageHolder = scanView.findViewById(R.id.faceTmpImageHolder);
        bookAppointBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), AppointmentBooking.class);
                intent.putExtra("patient_id", patient_id);
                intent.putExtra("face_scan", true);
                startActivity(intent);
            }
        });
        //tab links
        glFSTabLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                glFSTabLinkBr.setVisibility(View.VISIBLE);
                glFSTabLink.setImageDrawable(getDrawable(R.drawable.facial_recognition_purple_tab_link));
                glFSDetailTabLink.setImageDrawable(getDrawable(R.drawable.ic_notes_icon));
                glFSDetailTabLinkBar.setVisibility(View.GONE);
                gl_scan_wrap.setVisibility(View.VISIBLE);
                gl_scan_controls_wrap.setVisibility(View.VISIBLE);
                gl_pat_details_wrap.setVisibility(View.GONE);
                bookAppointBtn.setVisibility(View.GONE);
                glFSDetailTabLink.setClickable(false);
            }
        });

        glFSDetailTabLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                glFSTabLinkBr.setVisibility(View.GONE);
                glFSTabLink.setImageDrawable(getDrawable(R.drawable.facial_recognition_tab_link));
                glFSDetailTabLink.setImageDrawable(getDrawable(R.drawable.ic_notes_purple_icon));
                glFSDetailTabLinkBar.setVisibility(View.VISIBLE);
                gl_scan_wrap.setVisibility(View.GONE);
                gl_scan_controls_wrap.setVisibility(View.GONE);
                gl_pat_details_wrap.setVisibility(View.VISIBLE);
                bookAppointBtn.setVisibility(View.VISIBLE);
//                loaderWrap.setVisibility(View.GONE);
                glFSTabLink.setClickable(false);
            }
        });
        glFSTabLink.performClick();

        scanView.findViewById(R.id.changeCamBtn).setOnClickListener(new View.OnClickListener() {
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
                _isCameraLoaded = true;
                safeToTakePicture = true;
            }
        });

        scanView.findViewById(R.id.cameraIcon).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //Restart the camera scan
                startCameraAgain();
            }
        });

        scanView.findViewById(R.id.closeCamIcon).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                patGlobalFaceScanDialog.dismiss();
                glFaceScanStatus = false;
                if (scannedPatListDialog != null) {
                    scannedPatListDialog.dismiss();
                }
                mCameraSource = null;
                closeCameraAndPreview();
            }
        });

        glFSPatAppointDetailWrap = scanView.findViewById(R.id.glFSPatAppointDetailWrap);
        glFSVerifyOtpTxt = scanView.findViewById(R.id.glFSVerifyOtpTxt);

        getOtpBtnWrap = scanView.findViewById(R.id.getOtpBtnWrap);
        otpInputWrap = scanView.findViewById(R.id.otpInputWrap);

        otp_input_one = scanView.findViewById(R.id.otpInputOne);
        otp_input_two = scanView.findViewById(R.id.otpInputTwo);
        otp_input_three = scanView.findViewById(R.id.otpInputThree);
        otp_input_four = scanView.findViewById(R.id.otpInputFour);

        otpCancelTxtBtn = scanView.findViewById(R.id.otpCancelTxtBtn);
        getOtpBtn = scanView.findViewById(R.id.getOtpBtn);
        resendOtpBtn = scanView.findViewById(R.id.resendOtpBtn);

        glFSPatientImg = scanView.findViewById(R.id.glFSPatientImg);
        glFS_patient_name_txt = scanView.findViewById(R.id.glFS_patient_name_txt);
        glFSPatDOBTxt = scanView.findViewById(R.id.glFSPatDOBTxt);
        glFSPatAgeTxt = scanView.findViewById(R.id.glFSPatAgeTxt);
        glFSPatGenderTxt = scanView.findViewById(R.id.glFSPatGenderTxt);
        glFSPatMobileTxt = scanView.findViewById(R.id.glFSPatMobileTxt);
        glFSLastVisitDateTxt = scanView.findViewById(R.id.glFSLastVisitDateTxt);
        glFSVerifyOtpTxt = scanView.findViewById(R.id.glFSVerifyOtpTxt);

        glFSLastVisitDateTxt = scanView.findViewById(R.id.glFSLastVisitDateTxt);
        glFSEditProfileImgBtn = scanView.findViewById(R.id.glFSEditProfileImgBtn);
        glFSViewPatDetailsImgBtn = scanView.findViewById(R.id.glFSViewPatDetailsImgBtn);
        glFSDeleteImgBtn = scanView.findViewById(R.id.glFSDeleteImgBtn);

        countDownTxt = scanView.findViewById(R.id.countDownTxt);

        ageWrap = scanView.findViewById(R.id.ageWrap);
        rbcWrap = scanView.findViewById(R.id.rbcWrap);
        glFSPatAgeSmallTxt = scanView.findViewById(R.id.glFSPatAgeSmallTxt);
        glFSPatRBCTxt = scanView.findViewById(R.id.glFSPatRBCTxt);

        getOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                glFSPatGetOTP();
            }
        });

        resendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                glFSPatGetOTP();
            }
        });

        bindglFSOTPFormClickEvents(otp_input_one, otp_input_two, null);
        bindglFSOTPFormClickEvents(otp_input_two, otp_input_three, otp_input_one);
        bindglFSOTPFormClickEvents(otp_input_three, otp_input_four, otp_input_two);
        bindglFSOTPFormClickEvents(otp_input_four, null, otp_input_three);
    }

    private void bindglFSOTPFormClickEvents(EditText _input_listener, final EditText
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
                glFSPatCheckOTP();

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


    private void showScannedPatListInPopUp(JSONObject _data) {
        if (scannedPatListDialog != null) {
            scannedPatListDialog.hide();
        }
        if (!_scanPatientTabVisible) {
            scannedPatListDialog = new Dialog(AllPatients.this);
            scannedPatListDialog.setContentView(R.layout.face_multiple_profiles_popup);
            scannedPatListDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            final LinearLayout face_list_wrap = scannedPatListDialog.findViewById(R.id.face_list_wrap);
            //face_list_wrap

            try {
                Boolean _error = Boolean.parseBoolean(_data.getString("error"));
                if (!_error && !_data.isNull("data")) {
                    JSONArray feedArray = _data.getJSONArray("data");

                    int arr_len = feedArray.length();

                    //show only top 3 profiles
                    if (arr_len > 3) {
                        arr_len = 3;
                    }
                    for (int i = 0; i < arr_len; i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);

                        layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
                        final View preview = layoutInflater.inflate(R.layout.scanned_pat_face_row, null);

                        final CircleImageView patient_img = preview.findViewById(R.id.patient_img);
                        TextView patient_name_txt = preview.findViewById(R.id.patient_name_txt);
                        TextView patient_mobile = preview.findViewById(R.id.patient_mobile);
                        TextView selectPatBtnTxt = preview.findViewById(R.id.selectPatBtnTxt);

                        if (!feedObj.isNull("photo")) {
                            String newurl = null;
                            newurl = API_URL._domain + feedObj.getString("photo");

                            try {
                                new DownLoadCircleImageTask(patient_img).execute(newurl);
                            } catch (Exception e) { // Catch the download exception
                                e.printStackTrace();
                            }
                        } else {
                            if (feedObj.getString("gender").equals("male")) {
                                patient_img.setImageResource(R.drawable.male_user);
                            } else {
                                patient_img.setImageResource(R.drawable.female_user);
                            }
                        }

                        patient_name_txt.setText(feedObj.getString("name"));
                        String _mobile_str = feedObj.getString("mobile");
                        if (_mobile_str != null && !_mobile_str.isEmpty() && !_mobile_str.equals("null")) {
                            _mobile_str = "+91" + _mobile_str;
                            patient_mobile.setText(_mobile_str);
                        }

                        final String pat_id = feedObj.getString("id");
                        selectPatBtnTxt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                scannedPatListDialog.dismiss();
                                gl_scan_wrap.setVisibility(View.GONE);
                                // getScannedPatientDetails(pat_id);
                                checkDocPatLinkAndShowDetails(pat_id);
                            }
                        });

                        face_list_wrap.addView(preview);
                    }
                    progress.dismiss();
                } else {
                    progress.dismiss();
                    Toast.makeText(AllPatients.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            scannedPatListDialog.show();
        }
    }

    private void checkDocPatLinkAndShowDetails(final String _id) {
        //DocPatientLinkStatus
        patient_id = _id;
        progress.setTitle("Checking patient details...");

        JSONObject _obj = new JSONObject();
        try {
            _obj.put("patient_id", patient_id);
            _obj.put("doc_id", med_user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.DocPatientLinkStatus, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (scannedPatListDialog != null) {
                        scannedPatListDialog.dismiss();
                    }
                    gl_scan_wrap.setVisibility(View.GONE);
                    progress.dismiss();
                    _scanPatientTabVisible = true;
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        if (!response.isNull("data") && response.getJSONObject("data") != null) {
                            getScannedPatientDetails(_id, true);
                        } else {
                            getScannedPatientDetails(_id, false);
                        }
                    } else {
                        Toast.makeText(AllPatients.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                scannedPatListDialog.dismiss();
                progress.dismiss();
            }
        });

        // Adding request to volley request queue
        jsonReq.setShouldCache(false);
        mRequestQueue.add(jsonReq);
    }

    private void getScannedPatientDetails(String _id, final boolean docLink) {
        //DocPatientLinkStatus
        progress.setTitle("Loading patient details...");
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.PatientFetchFull + _id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (scannedPatListDialog != null) {
                        scannedPatListDialog.dismiss();
                    }
                    progress.dismiss();
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {
                        JSONObject _data = response.getJSONObject("data");

                        if (!_data.isNull("photo") && !_data.getString("photo").isEmpty()
                                && !_data.getString("photo").equals("null")) {
                            String _pat_photo_url = API_URL._domain + _data.getString("photo");
                            new DownLoadCircleImageTask(glFSPatientImg).execute(_pat_photo_url);
                        } else {
                            if (_data.getString("gender").equals("male")) {
                                glFSPatientImg.setImageResource(R.drawable.male_user);
                            } else {
                                glFSPatientImg.setImageResource(R.drawable.female_user);
                            }
                        }
                        isScannedFaceLoaded = false;
                        _mobile_str = _data.getString("mobile");
                        glFS_patient_name_txt.setText(_data.getString("name"));
                        glFSPatDOBTxt.setText(CustomDateToString.month(_data.getString("dob")));
                        glFSPatAgeTxt.setText(removeNull(_data.getString("age")));
                        glFSPatGenderTxt.setText(removeNull(_data.getString("gender")));
                        glFSPatMobileTxt.setText(removeNull(_data.getString("mobile")));
                        // glFSLastVisitDateTxt.setText(_data.getString("updated_at"));
                        glFSPatAgeSmallTxt.setText("(" + removeNull(_data.getString("age")) + "Y)");

                        glFSDetailTabLink.performClick();

                        final String _tempPatId = _data.getString("id");
                        glFSEditProfileImgBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                patient_id = _tempPatId;
                                View preview = linearLayoutList.findViewWithTag(patient_id);
                                if (preview != null) {
                                    selectedPatRowIndex = linearLayoutList.indexOfChild(preview);
                                }
                                EditPatient editPatient = new EditPatient(getApplicationContext());
                                editPatient.showEditPatientDialog(mContext, realHeight, patient_id);
                            }
                        });

                        if (docLink) {
                            verifiedUserContShow();
                        }

                        glFSViewPatDetailsImgBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (glFSEditProfileImgBtn.getVisibility() == View.VISIBLE) {
                                    if (scannedPatListDialog != null) {
                                        scannedPatListDialog.hide();
                                    }
                                    Intent intent = new Intent(getApplicationContext(), PatientHistory.class);
                                    intent.putExtra("patient_id", _tempPatId);
                                    startActivity(intent);
                                } else {
                                    glFSVerifyOtpTxt.setTextColor(Color.parseColor("#ff4040"));
                                    ObjectAnimator
                                            .ofFloat(glFSVerifyOtpTxt, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0)
                                            .setDuration(1000)
                                            .start();
                                }
                            }
                        });

                        String blood_group_id = _data.getString("blood_group_id");
                        if (blood_group_id != null && !blood_group_id.isEmpty() && !blood_group_id.equals("null")) {
                            for (int i = 0; i < _blood_arr.length(); i++) {
                                JSONObject feedObj = (JSONObject) _blood_arr.get(i);
                                if (blood_group_id.equals(feedObj.getString("id"))) {
                                    glFSPatRBCTxt.setText(feedObj.getString("name"));
                                    ageWrap.setVisibility(View.GONE);
                                    rbcWrap.setVisibility(View.VISIBLE);
                                    glFSPatAgeSmallTxt.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else {
                        Toast.makeText(AllPatients.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                scannedPatListDialog.dismiss();
                progress.dismiss();
            }
        });

        // Adding request to volley request queue
        jsonReq.setShouldCache(false);
        mRequestQueue.add(jsonReq);
    }

    private void verifiedUserContShow() {
        glFSDeleteImgBtn.setVisibility(View.VISIBLE);
        glFSEditProfileImgBtn.setVisibility(View.VISIBLE);
        glFSLastVisitDateTxt.setVisibility(View.VISIBLE);
        bookAppointBtnWrap.setVisibility(View.VISIBLE);
        //glFSPatAppointDetailWrap.setVisibility(View.VISIBLE);

        getOtpBtnWrap.setVisibility(View.GONE);
        otpInputWrap.setVisibility(View.GONE);
        glFSVerifyOtpTxt.setVisibility(View.GONE);
    }

    private void glFSPatGetOTP() {
        boolean _error = false;
        if (_mobile_str == null || _mobile_str.isEmpty()) {
            _error = true;
            Toast.makeText(AllPatients.this, "Please enter valid contact number!", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(AllPatients.this, "OTP has been sent to your mobile.", Toast.LENGTH_LONG).show();
                            ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                                    .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                            JSONObject _data = response.getJSONObject("data");
                            _otpID = _data.getString("id");

                            otpInputWrap.setVisibility(View.VISIBLE);
                            getOtpBtn.setVisibility(View.GONE);
                            otpCancelTxtBtn.setVisibility(View.GONE);
                            resendOtpBtn.setVisibility(View.VISIBLE);
                            otp_input_one.requestFocus();

                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }
                            startTimer();
                        } else {
                            Toast.makeText(AllPatients.this, response.getString("message"), Toast.LENGTH_LONG).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AllPatients.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    public void openCallDialog(final View v) {
        BottomSheetDialog dialog = new BottomSheetDialog(AllPatients.this);
        dialog.setContentView(R.layout.sms_call_popup);
        View _parent = (View) v.getParent();
        final TextView _textView = _parent.findViewById(v.getId());

        ImageView phone_img = dialog.findViewById(R.id.phone_img);
        phone_img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + _textView.getText()));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);
            }
        });

        ImageView whatsapp_img = dialog.findViewById(R.id.whatsapp_img);
        whatsapp_img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + "+91" + _textView.getText() + "&text=");
                Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(sendIntent);
            }
        });

        ImageView comment_img = dialog.findViewById(R.id.comment_img);
        comment_img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("smsto:" + "+91" + _textView.getText())); // This ensures only SMS apps respond
                intent.putExtra("sms_body", "");
                startActivity(intent);
            }
        });

        dialog.show();
    }

    private void glFSPatCheckOTP() {
        //Loader
        progress = new ProgressDialog(AllPatients.this);
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
        Log.i("_otp_str", _otp_str);
        Log.i("checkOTP _error", _error.toString());
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
                                updateVerifiedPatient();
                                postDocPatient();
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(otp_input_four.getWindowToken(), 0);
                                verifiedUserContShow();
                                Toast.makeText(AllPatients.this, "Mobile number verified successfully!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AllPatients.this, "Please enter valid OTP number!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AllPatients.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AllPatients.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
    //Global patient face scan section end <-------------------------------------------------------------

    //update patient row view start -------------------------------------------------------------->
    public void fetchPatAndUpdateRow(String pat_id) {
        progress.show();
        progress.setMessage("Loading...");
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.PatientFetchFull + pat_id, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                boolean _error = false;
                progress.dismiss();
                try {
                    _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {
                        createAllPatientRow(response.getJSONObject("data"), false, selectedPatRowIndex);
                    } else {
                        Toast.makeText(AllPatients.this, response.getString("message"), Toast.LENGTH_LONG).show();
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
        jsonReq.setShouldCache(false);
        mRequestQueue.add(jsonReq);
    }
    //update patient row view end <----------------------------------------------------------------

    //on click no open call dialog
    public void updateRow(String pat_id) {
        fetchPatAndUpdateRow(pat_id);
    }

    private void createCameraSource(int cameraFacing) {
        detector = new FaceDetector.Builder(mContext)
                .setClassificationType(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(true)
                .setTrackingEnabled(false)
                .build();

        detector.setProcessor(new MultiProcessor.Builder<>(new AllPatients.GraphicFaceTrackerFactory()).build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }
        mCameraSource = null;
        mCameraSource = new CameraSource.Builder(mContext, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(cameraFacing)
                .setAutoFocusEnabled(true)
                .setRequestedFps(30.0f)
                .build();
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new AllPatients.GraphicFaceTracker(mGraphicOverlay);
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
            mFaceGraphic.setId(faceId);
            if (safeToTakePicture && camCount < 10) {
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
            if (safeToTakePicture && camCount < 10) {
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
     * Restarts the camera
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera
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
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
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
        mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
            private File imageFile;

            @Override
            public void onPictureTaken(byte[] bytes) {
                try {
                    // convert byte array into bitmap
                    loadedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
                    String[] cameraIds = manager.getCameraIdList();
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIds[cameraId]);
                    Log.d("dfk", String.valueOf(characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)));
                    Matrix rotateMatrix = new Matrix();
                    rotateMatrix.postRotate(characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION));
                    rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), rotateMatrix, false);
                    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                    rotatedBitmap = resize(rotatedBitmap, 480, 720);
                    rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);

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
                    faceTmpImageHolder.setImageDrawable(new BitmapDrawable(mContext.getResources(), tempBitmap));
//                    faceTmpImageHolder.setImageBitmap(rotatedBitmap);
                    if (rotatedBitmap != null && faceTmpImageHolder.getDrawable() != null) {
                        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, API_URL.FaceScanImage, new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
                                camCount++;
                                try {
                                    String resultResponse = new String(response.data);
                                    Log.d("image", resultResponse);
                                    JSONObject _res = new JSONObject(resultResponse);
                                    boolean _error = Boolean.parseBoolean(_res.getString("error"));
                                    if (!_error) {
                                        JSONArray _data = _res.getJSONArray("data");
                                        if (_data.length() == 1) {
                                            stopCamera();
                                            //Single
                                            JSONObject _singlePatObj = (JSONObject) _data.get(0);
                                            checkDocPatLinkAndShowDetails(_singlePatObj.getString("id"));
                                        } else if (_data.length() > 0) {
                                            stopCamera();
                                            //Multi
                                            //Send full response
                                            showScannedPatListInPopUp(_res);
                                        } else if (_data.length() == 0) {
                                            safeToTakePicture = true;
//                                            Toast.makeText(getApplicationContext(), "No Patient found! Try to take camera closer to patient.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (JSONException e) {
                                    safeToTakePicture = true;
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
                                return params;
                            }

                            @Override
                            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                                params.put("photo", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), faceTmpImageHolder.getDrawable()), "image/jpeg"));
                                return params;
                            }
                        };

                        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
                    }
                } catch (Exception e) {
                    safeToTakePicture = true;
                    e.printStackTrace();
                }
            }
        });
    }

    public void MuteAudio() {
        AudioManager mAlramMAnager = (AudioManager) AllPatients.this.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
        } else {
            mAlramMAnager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_ALARM, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_RING, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        }
    }

    public void UnMuteAudio() {
        AudioManager mAlramMAnager = (AudioManager) AllPatients.this.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
        } else {
            if (mAlramMAnager != null) {
                mAlramMAnager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
                mAlramMAnager.setStreamMute(AudioManager.STREAM_ALARM, false);
                mAlramMAnager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                mAlramMAnager.setStreamMute(AudioManager.STREAM_RING, false);
                mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            }
        }
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

    private String trimDateYear(String _date) {
        if (_date != null && !_date.equals("null") && !_date.isEmpty()) {
            String[] splitDate = _date.split("\\-");
            String subStringYr = splitDate[2].substring(Math.max(splitDate[2].length() - 2, 0));

            return CustomDateToString.month(splitDate[0] + "-" + splitDate[1] + "-" + subStringYr);
        } else {
            return "";
        }
    }

    protected void closeCameraAndPreview() {
        try {
            if (mPreview != null) {
                mPreview.release();
                mPreview = null;
            }
            if (mCameraSource != null) {
                mCameraSource.release();
                mCameraSource = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
