package com.medinin.medininapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.medinin.medininapp.R;
import com.medinin.medininapp.config.API_URL;
import com.medinin.medininapp.data.StringWithTag;
import com.medinin.medininapp.helpers.CountryCode;
import com.medinin.medininapp.helpers.ImageEvents;
import com.medinin.medininapp.utils.ClearFocusOnKBClose;
import com.medinin.medininapp.utils.CustomDateToString;
import com.medinin.medininapp.utils.SpinnerDropDown;
import com.medinin.medininapp.volley.AppHelper;
import com.medinin.medininapp.volley.VolleyMultipartRequest;
import com.medinin.medininapp.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class DocBasicProfile extends AppCompatActivity {


    private static final String TAG = EditProfile.class.getSimpleName();
    private String med_user_id, med_user_token;
    private RequestQueue mRequestQueue;
    private RelativeLayout activity_doc_basic_profile;
    Calendar calDate = Calendar.getInstance();
    int curentYear = calDate.get(Calendar.YEAR);

    private Spinner genderSpinner;
    EditText name, name_edit, dob_edit, num_edit, otp_edit;

    private EditText otp_input_one, otp_input_two, otp_input_three, otp_input_four;


    TextView getOtpBtn, resendOtpBtn, countDownTxt, otpDisabledBtn, cancleTxtBtn, editBtnTxt, saveBtnTxt, country_code_txt;

    TextWatcher _textWatcher, _num_textWatcher, _otp_textWatcher;

    LinearLayout otpInputWrap, MobileNoErrorWrap;
    ConstraintLayout getOtpBtnWrap;

    ProgressDialog progress;
    String ageStr;
    private Bitmap bitmapProfPic;

    CountDownTimer countDownTimer;
    private Uri _file_path;
    private String glDobStr;
    private String oldNumberStr;
    private CircleImageView docProfileImg;

    private ConstraintLayout name_wrap, dob_wrap, gender_wrap, mobile_wrap, profileImgWrap;
    private FrameLayout blockInputViews, errorMsgWrap;

    private CountryCode countryCode;
    private ImageView countryFlagImg;
    private AlertDialog alertPageExitDialog;
    private ByteArrayOutputStream ostream;
    private Matrix rotateMatrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_basic_profile);

        Intent intent = getIntent();
        initMenu();

        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);

        mRequestQueue = Volley.newRequestQueue(this);
        progress = new ProgressDialog(DocBasicProfile.this);
        progress.setMessage("Loading...");
        ostream = new ByteArrayOutputStream();
        rotateMatrix = new Matrix();

        activity_doc_basic_profile = (RelativeLayout) findViewById(R.id.activity_doc_basic_profile);

        blockInputViews = (FrameLayout) findViewById(R.id.blockInputViews);
        errorMsgWrap = (FrameLayout) findViewById(R.id.errorMsgWrap);
        genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
        name_edit = (EditText) findViewById(R.id.name_edit);
        dob_edit = (EditText) findViewById(R.id.dob_edit);
        num_edit = (EditText) findViewById(R.id.num_edit);


        otp_edit = (EditText) findViewById(R.id.otp_edit);

        otp_input_one = (EditText) findViewById(R.id.otpInputOne);
        otp_input_two = (EditText) findViewById(R.id.otpInputTwo);
        otp_input_three = (EditText) findViewById(R.id.otpInputThree);
        otp_input_four = (EditText) findViewById(R.id.otpInputFour);

        bindOTPFormClickEvents(otp_input_one, otp_input_two, null);
        bindOTPFormClickEvents(otp_input_two, otp_input_three, otp_input_one);
        bindOTPFormClickEvents(otp_input_three, otp_input_four, otp_input_two);
        bindOTPFormClickEvents(otp_input_four, null, otp_input_three);

        getOtpBtn = (TextView) findViewById(R.id.getOtpBtn);
        otpDisabledBtn = (TextView) findViewById(R.id.otpDisabledBtn);
        resendOtpBtn = (TextView) findViewById(R.id.resendOtpBtn);
        cancleTxtBtn = (TextView) findViewById(R.id.cancleTxtBtn);

        editBtnTxt = (TextView) findViewById(R.id.editBtnTxt);
        saveBtnTxt = (TextView) findViewById(R.id.saveBtnTxt);

        countDownTxt = (TextView) findViewById(R.id.countDownTxt);
        docProfileImg = (CircleImageView) findViewById(R.id.docProfileImg);

        otpInputWrap = (LinearLayout) findViewById(R.id.otpInputWrap);
        MobileNoErrorWrap = (LinearLayout) findViewById(R.id.MobileNoErrorWrap);
        getOtpBtnWrap = (ConstraintLayout) findViewById(R.id.getOtpBtnWrap);

        name_wrap = (ConstraintLayout) findViewById(R.id.name_wrap);
        dob_wrap = (ConstraintLayout) findViewById(R.id.dob_wrap);
        gender_wrap = (ConstraintLayout) findViewById(R.id.gender_wrap);
        mobile_wrap = (ConstraintLayout) findViewById(R.id.mobile_wrap);
        profileImgWrap = (ConstraintLayout) findViewById(R.id.profileImgWrap);

        countryFlagImg = findViewById(R.id.countryFlagImg);
        country_code_txt = findViewById(R.id.country_code_txt);

        countryCode = new CountryCode();
        countryFlagImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryCode.set(DocBasicProfile.this, countryFlagImg, country_code_txt, num_edit);
            }
        });

        mobile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num_edit.requestFocus();
            }
        });

        bindGenderDropDown(genderSpinner);
        fetchDocDetails();

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

        cancleTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num_edit.setText(oldNumberStr);
            }
        });

        dob_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePickerDialog();
            }
        });


        editBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableDisableInputs(true);
            }
        });

        saveBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(DocBasicProfile.this);
                updateDocDetails();
            }
        });

        blockInputViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DocBasicProfile.this, "Please click on the edit button to update details!", Toast.LENGTH_LONG).show();
            }
        });

        enableDisableInputs(false);


        alertPageExitDialog = new AlertDialog.Builder(DocBasicProfile.this)
                .setTitle("Close page")
                .setMessage("Do you want to exit without saving your details?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        launchAccountScreen();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        LinearLayout backBtn = (LinearLayout) findViewById(R.id.back_sec);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (saveBtnTxt.getVisibility() == View.VISIBLE) {
                    alertPageExitDialog.show();
                } else {
                    launchAccountScreen();
                }
            }
        });

        //On close of soft keyboard clear edittext focus
        // it's works only if activity windowSoftInputMode=adjustResize
        new ClearFocusOnKBClose(activity_doc_basic_profile);

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
            if (saveBtnTxt.getVisibility() == View.VISIBLE) {
                alertPageExitDialog.show();
            } else {
                launchAccountScreen();
            }
        }
        return super.onKeyDown(keyCode, event);
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
//                signUpCheckOTP();
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

    private void enableDisableInputs(Boolean _enable) {
//        num_edit.setEnabled(_enable);
//        name_edit.setEnabled(_enable);
//
//        num_edit.setFocusable(_enable);
//        num_edit.setFocusableInTouchMode(_enable);
//        name_edit.setFocusable(_enable);
//        name_edit.setFocusableInTouchMode(_enable);
//
//        dob_edit.setEnabled(_enable);
//        genderSpinner.setEnabled(_enable);
//
//        profileImgWrap.setClickable(_enable);

        if (_enable) {
            name_wrap.setBackgroundResource(R.drawable.input_border_bottom_focus);
            dob_wrap.setBackgroundResource(R.drawable.input_border_bottom_focus);
            gender_wrap.setBackgroundResource(R.drawable.input_border_bottom_focus);
            mobile_wrap.setBackgroundResource(R.drawable.input_border_bottom_focus);
            getOtpBtnWrap.setVisibility(View.VISIBLE);
            blockInputViews.setVisibility(View.GONE);
            editBtnTxt.setVisibility(View.GONE);
            saveBtnTxt.setVisibility(View.VISIBLE);
        } else {
            name_wrap.setBackgroundResource(R.drawable.input_border_bottom);
            dob_wrap.setBackgroundResource(R.drawable.input_border_bottom);
            gender_wrap.setBackgroundResource(R.drawable.input_border_bottom);
            mobile_wrap.setBackgroundResource(R.drawable.input_border_bottom);
            getOtpBtnWrap.setVisibility(View.GONE);
            blockInputViews.setVisibility(View.VISIBLE);
            editBtnTxt.setVisibility(View.VISIBLE);
            saveBtnTxt.setVisibility(View.GONE);
        }
    }

    public void openDobSelctDialog(View v) {
        Dialog dialog = new Dialog(DocBasicProfile.this);
        dialog.setContentView(R.layout.dob_select_popup);
        DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);

        bindGenderDropDown(genderSpinner);

        dialog.show();
    }

    public void openFileUploadOptionsDialog(View v) {
        String _tag = v.getTag().toString();
        final Dialog dialog = new Dialog(DocBasicProfile.this);
        dialog.setContentView(R.layout.file_upload_options);

        TextView cameraOpenBtn = (TextView) dialog.findViewById(R.id.cameraOpenBtn);
        TextView galleryOpenBtn = (TextView) dialog.findViewById(R.id.galleryOpenBtn);

        cameraOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraLayout(0);
                dialog.dismiss();
            }
        });
        galleryOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser(1);
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void showFileChooser(int reqCode) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(pickPhoto, reqCode);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    public void openCameraLayout(int reqCode) {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                //android.Manifest.permission.READ_CONTACTS,
                //android.Manifest.permission.WRITE_CONTACTS,
                //android.Manifest.permission.READ_SMS,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            _file_path = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, _file_path);
            startActivityForResult(intent, 0);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch ( requestCode ) {
            case 0:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), _file_path);
                        //bitmapImage = ImageEvents.orientation(DocBasicProfile.this, bitmapImage, Uri.parse(_file_path.toString()));
                        bitmapImage = rotateImage(bitmapImage);
                        docProfileImg.setImageBitmap(bitmapImage);
                        bitmapProfPic = bitmapImage;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri mImageUri = imageReturnedIntent.getData();
                        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                        //bitmapImage = ImageEvents.orientation(DocBasicProfile.this, bitmapImage, mImageUri);
                        bitmapImage = rotateImage(bitmapImage);
                        docProfileImg.setImageBitmap(bitmapImage);
                        bitmapProfPic = bitmapImage;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private Bitmap rotateImage(Bitmap _imageBitmap) {
        rotateMatrix.postRotate(90);
        _imageBitmap = Bitmap.createBitmap(_imageBitmap, 0, 0,
                _imageBitmap.getWidth(), _imageBitmap.getHeight(),
                rotateMatrix, false);
        _imageBitmap.compress(Bitmap.CompressFormat.PNG, 60, ostream);

        return _imageBitmap;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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

    private void bindDocEvents() {
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
                if (num_edit.getText().toString().length() == 10 && !num_edit.getText().toString().equals(oldNumberStr)) {
                    JSONObject _obj = new JSONObject();
                    try {
                        _obj.put("mobile", num_edit.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                            API_URL.DocMobileUnique, _obj, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            VolleyLog.d(TAG, "Response: " + response.toString());
                            progress.dismiss();
                            try {
                                boolean _error = Boolean.parseBoolean(response.getString("error"));
                                if (!_error) {
                                    JSONObject _data = response.getJSONObject("data");
                                    String _id = _data.getString("id");
                                    if (_id != null) {
                                        MobileNoErrorWrap.setVisibility(View.VISIBLE);
                                        errorMsgWrap.setVisibility(View.GONE);
                                        otpDisabledBtn.setVisibility(View.VISIBLE);
                                        getOtpBtn.setVisibility(View.GONE);
                                    }
                                } else {
                                    MobileNoErrorWrap.setVisibility(View.GONE);
                                    otpDisabledBtn.setVisibility(View.GONE);
                                    getOtpBtn.setVisibility(View.VISIBLE);
                                    Toast.makeText(DocBasicProfile.this, response.getString("message"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                MobileNoErrorWrap.setVisibility(View.GONE);
                                otpDisabledBtn.setVisibility(View.GONE);
                                getOtpBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            MobileNoErrorWrap.setVisibility(View.GONE);
                            otpDisabledBtn.setVisibility(View.GONE);
                            getOtpBtn.setVisibility(View.VISIBLE);
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                            progress.dismiss();
                        }
                    });

                    // Adding request to volley request queue
                    mRequestQueue.add(jsonReq);
                } else {
                    otpDisabledBtn.setVisibility(View.VISIBLE);
                    getOtpBtn.setVisibility(View.GONE);
                    MobileNoErrorWrap.setVisibility(View.GONE);
                }
            }
        };
        num_edit.addTextChangedListener(_num_textWatcher);
    }

    private void bindGenderDropDown(Spinner spinner) {
        ArrayList<StringWithTag> monthList = new ArrayList<StringWithTag>();
        monthList.add(new StringWithTag("Select", ""));
        monthList.add(new StringWithTag("Male", "male"));
        monthList.add(new StringWithTag("Female", "female"));

        ArrayAdapter<StringWithTag> spinnerArrayAdapter = new ArrayAdapter<StringWithTag>(DocBasicProfile.this, android.R.layout.simple_spinner_item, monthList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    private void fetchDocDetails() {
        progress.setMessage("Loading...");
        progress.show();

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.Doc + "/" + med_user_id, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                parseJsonDocDetails(response);
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
    }

    private void parseJsonDocDetails(JSONObject response) {
        try {
            progress.dismiss();
            boolean _error = Boolean.parseBoolean(response.getString("error"));
            if (!_error) {
                if (!response.isNull("data")) {
                    JSONObject _data = (JSONObject) response.getJSONObject("data");
                    name_edit.setText(removeNull(_data.getString("name")));
                    dob_edit.setText(trimDateYear(_data.getString("dob")));
                    num_edit.setText(removeNull(_data.getString("mobile")));
                    countryCode.get(DocBasicProfile.this, countryFlagImg, country_code_txt, _data.getString("mobile_code"));
                    SpinnerDropDown.setSpinnerItem(genderSpinner, _data.getString("gender"));
                    glDobStr = _data.getString("dob");
                    oldNumberStr = _data.getString("mobile");

                    new CountDownTimer(3000, 1000) { // adjust the milli seconds here
                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            bindDocEvents();
                        }
                    }.start();
                }
            } else {
                Toast.makeText(DocBasicProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

    private void getOTP() {
        Boolean _error = false;
        String _mobile_str = num_edit.getText().toString();
        if (validateEditTextInput(num_edit)) {
            _error = true;
            Toast.makeText(DocBasicProfile.this, "Please enter contact number!", Toast.LENGTH_LONG).show();
        } else if (num_edit.getText().length() < 10) {
            _error = true;
            Toast.makeText(DocBasicProfile.this, "Please enter valid contact number!", Toast.LENGTH_LONG).show();
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
                    API_URL.SendOTP, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    progress.dismiss();
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            Toast.makeText(DocBasicProfile.this, "OTP has been sent to your mobile.", Toast.LENGTH_LONG).show();

                            otpInputWrap.setVisibility(View.VISIBLE);
                            getOtpBtn.setVisibility(View.GONE);
                            cancleTxtBtn.setVisibility(View.GONE);
                            resendOtpBtn.setVisibility(View.VISIBLE);
                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }
                            startTimer();
                        } else {
                            Toast.makeText(DocBasicProfile.this, response.getString("message"), Toast.LENGTH_LONG).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DocBasicProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

        if (validateEditTextInput(num_edit)) {
            _error = true;
        }

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
                _obj.put("mobile", num_edit.getText());
                _obj.put("otp", _otp_str.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                    API_URL.CheckOTP, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    progress.dismiss();
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            JSONObject _data = (JSONObject) response.getJSONObject("data");

                            String otp_status = _data.getString("otp_status");
                            if (otp_status.equals("success")) {
                                updateDocDetails();
                                otpInputWrap.setVisibility(View.GONE);
                                getOtpBtn.setVisibility(View.GONE);
                                resendOtpBtn.setVisibility(View.GONE);
                                otpDisabledBtn.setVisibility(View.VISIBLE);
                                Toast.makeText(DocBasicProfile.this, "Mobile number verified successfully!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(DocBasicProfile.this, "Please enter valid OTP number!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(DocBasicProfile.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DocBasicProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private void updateDocDetails() {
        Boolean _error = false;

        if (validateEditTextInput(name_edit)) {
            _error = true;
            //Toast.makeText(DocBasicProfile.this, "Please enter name!", Toast.LENGTH_LONG).show();
        } else if (validateEditTextInput(dob_edit)) {
            _error = true;
            //Toast.makeText(DocBasicProfile.this, "Please enter your date of birth!", Toast.LENGTH_LONG).show();
        } else if (SpinnerDropDown.getSpinnerItem(genderSpinner).isEmpty()) {
            _error = true;
            //Toast.makeText(DocBasicProfile.this, "Please select gender!", Toast.LENGTH_LONG).show();
        } else if (num_edit.getText().length() < 10) {
            _error = true;
            //Toast.makeText(DocBasicProfile.this, "Please enter valid contact number!", Toast.LENGTH_LONG).show();
        }

        if (!_error) {
            progress.setMessage("Updating...");
            progress.show();
            errorMsgWrap.setVisibility(View.GONE);
            enableDisableInputs(false);
            JSONObject _obj = new JSONObject();
            try {
                String name_str = name_edit.getText().toString();
                _obj.put("name", name_str);
                _obj.put("age", ageStr);
                _obj.put("gender", SpinnerDropDown.getSpinnerItem(genderSpinner));
                _obj.put("mobile", num_edit.getText());
                _obj.put("mobile_code", country_code_txt.getText());
                if (glDobStr != null) {
                    _obj.put("dob", glDobStr);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                    API_URL.Doc + "/" + med_user_id, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    progress.dismiss();
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            hideKeyboard(DocBasicProfile.this);
                            uploadProfilePhoto();
                            Toast.makeText(DocBasicProfile.this, "Data updated successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(DocBasicProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DocBasicProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
            errorMsgWrap.setVisibility(View.VISIBLE);
            MobileNoErrorWrap.setVisibility(View.GONE);
        }
    }

    private void openDatePickerDialog() {
        final Dialog dialog = new Dialog(DocBasicProfile.this);
        dialog.setContentView(R.layout.dob_select_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
        long now = System.currentTimeMillis() - 1000;
        datePicker.setMaxDate((long) (now - (6.6485e+11)));
        TextView doneBtn = (TextView) dialog.findViewById(R.id.doneBtn);


        doneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                glDobStr = datePicker.getDayOfMonth() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getYear();
                String _dateStr = trimDateYear(datePicker.getDayOfMonth() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getYear());
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

    private void uploadProfilePhoto() {
        if (bitmapProfPic != null) {
            progress.setTitle("uploading...");
            progress.show();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapProfPic.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            JSONObject _obj = new JSONObject();
            try {
                _obj.put("src", encoded);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                    API_URL.DocAvatarUpload + med_user_id, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("uploadProfilePhoto", response.toString());
                    progress.dismiss();
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error && !response.isNull("data")) {
                            bitmapProfPic = null;
                            postPhoto(response.getJSONObject("data"));
                        } else {
                            Toast.makeText(DocBasicProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
        }
    }

    private void postPhoto(JSONObject _data) {
        progress.show();
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("photo", _data.getString("path"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                API_URL.Doc + "/" + med_user_id, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("postPhoto", response.toString());
                progress.dismiss();
                try {
                    Boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {
                        Toast.makeText(DocBasicProfile.this, "Profile photo uploaded successfully", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(DocBasicProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
    }
}