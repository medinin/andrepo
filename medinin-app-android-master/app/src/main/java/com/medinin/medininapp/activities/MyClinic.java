package com.medinin.medininapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.medinin.medininapp.app.AppController;
import com.medinin.medininapp.config.API_URL;
import com.medinin.medininapp.data.StringWithTag;
import com.medinin.medininapp.helpers.DownLoadCircleImageTask;
import com.medinin.medininapp.helpers.ImageEvents;
import com.medinin.medininapp.models.ImageSlide;
import com.medinin.medininapp.utils.ClearFocusOnKBClose;
import com.medinin.medininapp.utils.SlideshowDialogFragment;
import com.medinin.medininapp.utils.SpinnerDropDown;
import com.medinin.medininapp.volley.AppHelper;
import com.medinin.medininapp.volley.VolleyMultipartRequest;
import com.medinin.medininapp.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyClinic extends AppCompatActivity {
    private static final String TAG = MyClinic.class.getSimpleName();

    private String med_user_id, med_user_token;
    private RequestQueue mRequestQueue;
    private ProgressDialog progress;

    EditText startTimeInput, endTimeInput, clinicNameInput, feeInput;
    String startTime = "00:00:00", endTime = "00:00:00", clinicID;
    LinearLayout weekDaysWrap, galleryPhotoListWrap, add_photo_sec;
    private ConstraintLayout clinicNameInputWrap, startTimeInputWrap, endTimeInputWrap;
    private TextView timeDiffTxt, consultationTxtView, editBtnTxt, saveBtnTxt, sunBtn,
            monBtn, tueBtn, wedBtn, thuBtn, friBtn, satBtn;
    private RelativeLayout activity_my_clinic, add_more_photo_sec;
    private Spinner currancySpinner;
    ArrayList<StringWithTag> currencyList;
    private Boolean clinicUpdateStatus = false;
    LayoutInflater layoutInflater;
    private FrameLayout blockInputViews, consultationSecWrap, errorMsgWrap;
    private int PICK_IMAGE_MULTIPLE = 1;
    private ArrayList<Drawable> newPhotosArrayList;
    private ImageView addMorePhotoImgBtn;
    private ArrayList<ImageSlide> galleryImages;
    private AlertDialog alertPageExitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_clinic);

        initMenu();

        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);

        mRequestQueue = Volley.newRequestQueue(this);
        progress = new ProgressDialog(MyClinic.this);
        progress.setMessage("Loading...");
        layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        galleryImages = new ArrayList<>();
        newPhotosArrayList = new ArrayList<Drawable>();


        add_photo_sec = (LinearLayout) findViewById(R.id.add_photo_sec);
        add_more_photo_sec = (RelativeLayout) findViewById(R.id.add_more_photo_sec);
        addMorePhotoImgBtn = (ImageView) findViewById(R.id.addMorePhotoImgBtn);
        galleryPhotoListWrap = (LinearLayout) findViewById(R.id.galleryPhotoListWrap);


        LinearLayout location_wrap = (LinearLayout) findViewById(R.id.location_wrap);
        location_wrap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), SelectLocation.class);
                startActivity(intent);
            }
        });

        activity_my_clinic = (RelativeLayout) findViewById(R.id.activity_my_clinic);

        blockInputViews = (FrameLayout) findViewById(R.id.blockInputViews);
        errorMsgWrap = (FrameLayout) findViewById(R.id.errorMsgWrap);
        startTimeInput = (EditText) findViewById(R.id.startTimeInput);
        endTimeInput = (EditText) findViewById(R.id.endTimeInput);
        clinicNameInput = (EditText) findViewById(R.id.clinicNameInput);
        feeInput = (EditText) findViewById(R.id.feeInput);
        timeDiffTxt = (TextView) findViewById(R.id.timeDiffTxt);
        weekDaysWrap = (LinearLayout) findViewById(R.id.weekDaysWrap);
        consultationTxtView = (TextView) findViewById(R.id.consultationTxtView);
        consultationSecWrap = (FrameLayout) findViewById(R.id.consultationSecWrap);

        clinicNameInputWrap = (ConstraintLayout) findViewById(R.id.clinicNameInputWrap);
        startTimeInputWrap = (ConstraintLayout) findViewById(R.id.startTimeInputWrap);
        endTimeInputWrap = (ConstraintLayout) findViewById(R.id.endTimeInputWrap);


        editBtnTxt = (TextView) findViewById(R.id.editBtnTxt);
        saveBtnTxt = (TextView) findViewById(R.id.saveBtnTxt);

        sunBtn = (TextView) findViewById(R.id.sunBtn);
        monBtn = (TextView) findViewById(R.id.monBtn);
        tueBtn = (TextView) findViewById(R.id.tueBtn);
        wedBtn = (TextView) findViewById(R.id.wedBtn);
        thuBtn = (TextView) findViewById(R.id.thuBtn);
        friBtn = (TextView) findViewById(R.id.friBtn);
        satBtn = (TextView) findViewById(R.id.satBtn);

        bindWeekDaysClickEvent(sunBtn);
        bindWeekDaysClickEvent(monBtn);
        bindWeekDaysClickEvent(tueBtn);
        bindWeekDaysClickEvent(wedBtn);
        bindWeekDaysClickEvent(thuBtn);
        bindWeekDaysClickEvent(friBtn);
        bindWeekDaysClickEvent(satBtn);

        startTimeInput.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(MyClinic.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                startTimeInput.setText(String.format("%02d:%02d", hourOfDay, minute));
                                startTime = hourOfDay + ":" + minute;
                                calTimeDiff();

                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });

        endTimeInput.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(MyClinic.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                endTimeInput.setText(String.format("%02d:%02d", hourOfDay, minute));
                                endTime = hourOfDay + ":" + minute;
                                calTimeDiff();
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });


        currencyList = new ArrayList<StringWithTag>();
        currencyList.add(new StringWithTag("Select", ""));
        currencyList.add(new StringWithTag("( $ )", "$"));
        currencyList.add(new StringWithTag("( € )", "€"));
        currencyList.add(new StringWithTag("( £ )", "£"));
        currencyList.add(new StringWithTag("( ₹ )", "₹"));
        currencyList.add(new StringWithTag("( AU$)", "AU$"));
        currencyList.add(new StringWithTag("( CA$ )", "CA$"));
        currencyList.add(new StringWithTag("( S$ )", "S$"));
        currencyList.add(new StringWithTag("( ₣ )", "₣"));
        currencyList.add(new StringWithTag("( RM )", "RM"));
        currencyList.add(new StringWithTag("( ¥ )", "¥"));
        currencyList.add(new StringWithTag("( ¥ )", "¥"));

        loadClinicDetails();


        editBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableDisableInputs(true);
            }
        });

        saveBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(MyClinic.this);
                if (clinicUpdateStatus) {
                    updateClinicDetails();
                } else {
                    saveClinicDetails();
                }
            }
        });

        blockInputViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MyClinic.this, "Please click on the edit button to update details!", Toast.LENGTH_LONG).show();
            }
        });

        enableDisableInputs(false);

        add_photo_sec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (blockInputViews.getVisibility() == View.GONE) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
                } else {
                    blockInputViews.performClick();
                }
            }
        });

        addMorePhotoImgBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (blockInputViews.getVisibility() == View.GONE) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
                } else {
                    blockInputViews.performClick();
                }
            }
        });

        consultationSecWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blockInputViews.getVisibility() == View.GONE) {
                    openConsultationFeeDialog();
                } else {
                    blockInputViews.performClick();
                }
            }
        });

        alertPageExitDialog = new AlertDialog.Builder(MyClinic.this)
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

        //On close of soft keyboard clear EditText focus
        //it's works only if activity windowSoftInputMode=adjustResize
        new ClearFocusOnKBClose(activity_my_clinic);
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

    private static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void openConsultationFeeDialog() {
        final Dialog dialog = new Dialog(MyClinic.this);
        dialog.setContentView(R.layout.consultation_fee);

        final EditText consultationFeeInput = (EditText) dialog.findViewById(R.id.consultationFeeInput);
        TextView doneBtn = (TextView) dialog.findViewById(R.id.doneBtn);
        currancySpinner = (Spinner) dialog.findViewById(R.id.currancySpinner);
        bindCurrancyDropDown(currancySpinner, currencyList);
        SpinnerDropDown.setSpinnerItem(currancySpinner, "₹");


        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEditTextInput(consultationFeeInput)) {
                    String feeStr = "Consultation Fee - " + SpinnerDropDown.getSpinnerItem(currancySpinner) + consultationFeeInput.getText();
                    consultationTxtView.setText(feeStr);
                    feeInput.setText(consultationFeeInput.getText());
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void calTimeDiff() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(startTimeInput.getText().toString());
            d2 = format.parse(endTimeInput.getText().toString());

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();
            long diffHours = diff / (60 * 60 * 1000) % 24;

            if (diffHours < 0) {
                diffHours = 0;
            }

            timeDiffTxt.setText(diffHours + "h");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void bindCurrancyDropDown(Spinner spinner, ArrayList<StringWithTag> list) {
        ArrayAdapter<StringWithTag> spinnerArrayAdapter = new ArrayAdapter<StringWithTag>(MyClinic.this, android.R.layout.simple_spinner_item, list);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    private void bindWeekDaysClickEvent(final TextView _textView) {
        _textView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String _tag = _textView.getTag().toString();
                if (_tag.equals("0")) {
                    _textView.setTag(1);
                    _textView.setBackground(ContextCompat.getDrawable(MyClinic.this, R.drawable.week_day_active_circle));
                    _textView.setTextColor(Color.parseColor("#282f3f"));
                } else {
                    _textView.setTag(0);
                    _textView.setBackground(ContextCompat.getDrawable(MyClinic.this, R.drawable.week_day_circle));
                    _textView.setTextColor(Color.parseColor("#88a1ac"));
                }
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

    private JSONObject createClinicObjectAndValidate() {
        Boolean _error = false, daySelectedStatus = false;
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("doc_id", med_user_id);
            _obj.put("name", clinicNameInput.getText());
            _obj.put("fee", feeInput.getText());

            for (int i = 0; i < weekDaysWrap.getChildCount(); i++) {
                View _row = weekDaysWrap.getChildAt(i);

                if (i == 0 && sunBtn.getTag().toString().equals("1")) {
                    _obj.put("sun", true);
                    _obj.put("sun_start", startTimeInput.getText());
                    _obj.put("sun_end", endTimeInput.getText());
                    daySelectedStatus = true;
                } else if (i == 0 && sunBtn.getTag().toString().equals("0")) {
                    _obj.put("sun", false);
                }

                if (i == 1 && monBtn.getTag().toString().equals("1")) {
                    _obj.put("mon", true);
                    _obj.put("mon_start", startTimeInput.getText());
                    _obj.put("mon_end", endTimeInput.getText());
                    daySelectedStatus = true;
                } else if (i == 1 && monBtn.getTag().toString().equals("0")) {
                    _obj.put("mon", false);
                }

                if (i == 2 && tueBtn.getTag().toString().equals("1")) {
                    _obj.put("tue", true);
                    _obj.put("tue_start", startTimeInput.getText());
                    _obj.put("tue_end", endTimeInput.getText());
                    daySelectedStatus = true;
                } else if (i == 2 && tueBtn.getTag().toString().equals("0")) {
                    _obj.put("tue", false);
                }

                if (i == 3 && wedBtn.getTag().toString().equals("1")) {
                    _obj.put("wed", true);
                    _obj.put("wed_start", startTimeInput.getText());
                    _obj.put("wed_end", endTimeInput.getText());
                    daySelectedStatus = true;
                } else if (i == 3 && wedBtn.getTag().toString().equals("0")) {
                    _obj.put("wed", false);
                }

                if (i == 4 && thuBtn.getTag().toString().equals("1")) {
                    _obj.put("thu", true);
                    _obj.put("thu_start", startTimeInput.getText());
                    _obj.put("thu_end", endTimeInput.getText());
                    daySelectedStatus = true;
                } else if (i == 4 && thuBtn.getTag().toString().equals("0")) {
                    _obj.put("thu", false);
                }

                if (i == 5 && friBtn.getTag().toString().equals("1")) {
                    _obj.put("fri", true);
                    _obj.put("fri_start", startTimeInput.getText());
                    _obj.put("fri_end", endTimeInput.getText());
                    daySelectedStatus = true;
                } else if (i == 5 && friBtn.getTag().toString().equals("0")) {
                    _obj.put("fri", false);
                }

                if (i == 6 && satBtn.getTag().toString().equals("1")) {
                    _obj.put("sat", true);
                    _obj.put("sat_start", startTimeInput.getText());
                    _obj.put("sat_end", endTimeInput.getText());
                    daySelectedStatus = true;
                } else if (i == 6 && satBtn.getTag().toString().equals("0")) {
                    _obj.put("sat", false);
                }
            }

            if (validateEditTextInput(clinicNameInput)) {
                _error = true;
                //Toast.makeText(MyClinic.this, "Please enter clinic name", Toast.LENGTH_LONG).show();
            } else if (validateEditTextInput(feeInput)) {
                _error = true;
                //Toast.makeText(MyClinic.this, "Please enter consultation fee!", Toast.LENGTH_LONG).show();
            } else if (!daySelectedStatus) {
                _error = true;
                //Toast.makeText(MyClinic.this, "Please select working days", Toast.LENGTH_LONG).show();
            }

            _obj.put("error", _error);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return _obj;
    }

    private void saveClinicDetails() {
        progress.setMessage("Saving...");

        Boolean _error = false;
        JSONObject _obj = createClinicObjectAndValidate();
        try {
            _error = Boolean.parseBoolean(_obj.getString("error"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!_error) {
            progress.show();
            errorMsgWrap.setVisibility(View.GONE);
            enableDisableInputs(false);
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                    API_URL.DocClinic, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    progress.dismiss();
                    Log.i("saveClinicDetails", response.toString());
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            JSONObject _data = (JSONObject) response.getJSONObject("data");
                            clinicUpdateStatus = true;
                            clinicID = _data.getString("id");
                            uploadClinicPhotos();
                            Toast.makeText(MyClinic.this, "Data saved successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MyClinic.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MyClinic.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
        } else {
            errorMsgWrap.setVisibility(View.VISIBLE);
        }
    }

    private void updateClinicDetails() {
        progress.setMessage("Updating...");
        Boolean _error = false;
        JSONObject _obj = createClinicObjectAndValidate();
        try {
            _error = Boolean.parseBoolean(_obj.getString("error"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!_error && clinicID != null) {
            progress.show();
            errorMsgWrap.setVisibility(View.GONE);
            enableDisableInputs(false);
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                    API_URL.DocClinic + "/" + clinicID, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    progress.dismiss();
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            uploadClinicPhotos();
                            Toast.makeText(MyClinic.this, "Data updated successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MyClinic.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MyClinic.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
        } else {
            errorMsgWrap.setVisibility(View.VISIBLE);
        }
    }

    private void loadClinicDetails() {
        progress.setTitle("Loading...");
        progress.show();
        String api_url = API_URL.DocClinicMine + med_user_id;
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET, api_url,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        if (!response.isNull("data")) {
                            JSONObject _data = (JSONObject) response.getJSONObject("data");
                            feedClinic(_data);
                        }
                        progress.dismiss();
                    } else {
                        Toast.makeText(MyClinic.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        progress.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progress.dismiss();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        //Adding request to volley request queue
        AppController.getInstance().getRequestQueue().getCache().remove(api_url);
        jsonReq.setShouldCache(false);
        mRequestQueue.add(jsonReq);

    }

    private void feedClinic(JSONObject _data) {
        try {
            if (!_data.isNull("name")) {
                clinicUpdateStatus = true;
                clinicID = _data.getString("id");

                clinicNameInput.setText(_data.getString("name"));
                feeInput.setText(_data.getString("fee"));

                if (!_data.getString("fee").isEmpty() && !_data.getString("fee").equals("null")) {
                    String feeStr = "Consultation Fee - ₹" + _data.getString("fee");
                    consultationTxtView.setText(feeStr);
                }

                if (_data.getBoolean("sun")) {
                    sunBtn.performClick();
                    startTimeInput.setText(_data.getString("sun_start"));
                    endTimeInput.setText(_data.getString("sun_end"));
                }

                if (_data.getBoolean("sat")) {
                    satBtn.performClick();
                    startTimeInput.setText(_data.getString("sat_start"));
                    endTimeInput.setText(_data.getString("sat_end"));
                }

                if (_data.getBoolean("mon")) {
                    monBtn.performClick();
                    startTimeInput.setText(_data.getString("mon_start"));
                    endTimeInput.setText(_data.getString("mon_end"));
                }
                if (_data.getBoolean("tue")) {
                    tueBtn.performClick();
                    startTimeInput.setText(_data.getString("tue_start"));
                    endTimeInput.setText(_data.getString("tue_end"));
                }
                if (_data.getBoolean("wed")) {
                    wedBtn.performClick();
                    startTimeInput.setText(_data.getString("wed_start"));
                    endTimeInput.setText(_data.getString("wed_end"));
                }
                if (_data.getBoolean("thu")) {
                    thuBtn.performClick();
                    startTimeInput.setText(_data.getString("thu_start"));
                    endTimeInput.setText(_data.getString("thu_end"));
                }
                if (_data.getBoolean("fri")) {
                    friBtn.performClick();
                    startTimeInput.setText(_data.getString("fri_start"));
                    endTimeInput.setText(_data.getString("fri_end"));
                }

                calTimeDiff();

                if (!_data.isNull("doc_clinic_files")) {
                    JSONArray _filesArray = (JSONArray) _data.getJSONArray("doc_clinic_files");
                    int _totalFiles = _filesArray.length();

                    if (_totalFiles > 0) {
                        add_more_photo_sec.setVisibility(View.VISIBLE);
                        add_photo_sec.setVisibility(View.GONE);
                    }

                    for (int f = 0; f < _totalFiles; f++) {
                        JSONObject _jObj = (JSONObject) _filesArray.get(f);
                        String img_url = API_URL._domain + "/doc-clinic-file/photo/" + _jObj.getString("file");

                        ImageSlide image = new ImageSlide();
                        //image.setName("New image");
                        image.setSmall(img_url);
                        image.setMedium(img_url);
                        image.setLarge(img_url);
                        galleryImages.add(image);

                        if (f < 5) {
                            final View preview = layoutInflater.inflate(R.layout.clinic_preview_round_img, null);
                            final CircleImageView img_view_sec = (CircleImageView) preview.findViewById(R.id.img_view_sec);
                            new DownLoadCircleImageTask(img_view_sec).execute(img_url);
                            galleryPhotoListWrap.addView(preview);

                            img_view_sec.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showBigImgPopup(img_view_sec);
                                }
                            });
                        }


                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showBigImgPopup(View v) {
        View view = (View) findViewById(v.getId());
//         int _position = parseInt(view.getTag().toString());

        Bundle bundle = new Bundle();
        bundle.putSerializable("images", galleryImages);
        //bundle.putInt("position", _position);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(ft, "slideshow");
    }

    private void enableDisableInputs(Boolean _enable) {
//        startTimeInput.setEnabled(_enable);
//        startTimeInput.setFocusable(_enable);
//        startTimeInput.setFocusableInTouchMode(_enable);
//
//        endTimeInput.setEnabled(_enable);
//        endTimeInput.setFocusable(_enable);
//        endTimeInput.setFocusableInTouchMode(_enable);

        startTimeInput.setClickable(_enable);
        endTimeInput.setClickable(_enable);

        clinicNameInput.setEnabled(_enable);
        clinicNameInput.setFocusable(_enable);
        clinicNameInput.setFocusableInTouchMode(_enable);

        sunBtn.setClickable(_enable);
        monBtn.setClickable(_enable);
        tueBtn.setClickable(_enable);
        wedBtn.setClickable(_enable);
        thuBtn.setClickable(_enable);
        friBtn.setClickable(_enable);
        satBtn.setClickable(_enable);
        consultationSecWrap.setClickable(_enable);

        if (_enable) {
            clinicNameInputWrap.setBackgroundResource(R.drawable.input_border_bottom_focus);
            startTimeInputWrap.setBackgroundResource(R.drawable.input_border_bottom_focus);
            endTimeInputWrap.setBackgroundResource(R.drawable.input_border_bottom_focus);
            blockInputViews.setVisibility(View.GONE);
            editBtnTxt.setVisibility(View.GONE);
            saveBtnTxt.setVisibility(View.VISIBLE);
        } else {
            clinicNameInputWrap.setBackgroundResource(R.drawable.input_border_bottom);
            startTimeInputWrap.setBackgroundResource(R.drawable.input_border_bottom);
            endTimeInputWrap.setBackgroundResource(R.drawable.input_border_bottom);
            blockInputViews.setVisibility(View.VISIBLE);
            editBtnTxt.setVisibility(View.VISIBLE);
            saveBtnTxt.setVisibility(View.GONE);
        }
    }

    //Add photos from gallery start ---------------------------------------------------------------->

    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        progress.setMessage("Uploading photos...");
        progress.show();
        try {
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
                add_photo_sec.setVisibility(View.GONE);
                add_more_photo_sec.setVisibility(View.VISIBLE);

                if (data.getData() != null) {
                    Uri mImageUri = data.getData();
                    newPhotosArrayList = new ArrayList<Drawable>();

                    Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                    bitmapImage = ImageEvents.orientation(MyClinic.this, bitmapImage, mImageUri);

                    BitmapDrawable bitmapDrawable = new BitmapDrawable(MyClinic.this.getResources(), bitmapImage);
                    newPhotosArrayList.add(bitmapDrawable);

                    final View preview = layoutInflater.inflate(R.layout.clinic_preview_round_img, null);
                    final CircleImageView img_view_sec = (CircleImageView) preview.findViewById(R.id.img_view_sec);
                    img_view_sec.setImageDrawable(bitmapDrawable);
                    galleryPhotoListWrap.addView(preview);

                    String img_url = ImageEvents.getAbsolutePath(MyClinic.this, mImageUri);
                    ImageSlide image = new ImageSlide();
                    //image.setName("New image");
                    image.setSmall(img_url);
                    image.setMedium(img_url);
                    image.setLarge(img_url);
                    galleryImages.add(image);

                    img_view_sec.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showBigImgPopup(img_view_sec);
                        }
                    });
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        newPhotosArrayList = new ArrayList<Drawable>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();

                            Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            bitmapImage = ImageEvents.orientation(MyClinic.this, bitmapImage, uri);

                            BitmapDrawable bitmapDrawable = new BitmapDrawable(MyClinic.this.getResources(), bitmapImage);
                            newPhotosArrayList.add(bitmapDrawable);

                            final View preview = layoutInflater.inflate(R.layout.clinic_preview_round_img, null);
                            final CircleImageView img_view_sec = (CircleImageView) preview.findViewById(R.id.img_view_sec);
                            img_view_sec.setImageDrawable(bitmapDrawable);
                            galleryPhotoListWrap.addView(preview);

                            String img_url = ImageEvents.getAbsolutePath(MyClinic.this, uri);
                            ImageSlide image = new ImageSlide();
                            //image.setName("New image");
                            image.setSmall(img_url);
                            image.setMedium(img_url);
                            image.setLarge(img_url);
                            galleryImages.add(image);

                            img_view_sec.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showBigImgPopup(img_view_sec);
                                }
                            });
                        }
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.i("Exception", e.getMessage());
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

        progress.dismiss();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadClinicPhotos() {
        for (int i = 0; i < newPhotosArrayList.size(); i++) {
            final Drawable drawableImg = newPhotosArrayList.get(i);

            if (drawableImg != null) {
                progress.setTitle("uploading...");
                progress.show();
                VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, API_URL.DocClinicFileUpload, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String resultResponse = new String(response.data);
                        progress.dismiss();
                        try {
                            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            JSONObject result = new JSONObject(json);
                            boolean _error = Boolean.parseBoolean(result.getString("error"));
                            if (!_error && !result.isNull("data")) {
                                postClinicFiles(result.getJSONObject("data"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
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
                            Log.i("upload file error", result.toString());
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
                        Map<String, DataPart> params = new HashMap<>();
                        params.put("photo", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), drawableImg), "image/jpeg"));

                        return params;
                    }
                };

                VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
            }
        }
        newPhotosArrayList.clear();
    }

    private void postClinicFiles(JSONObject _data) {
        final JSONObject _obj = new JSONObject();
        progress.setTitle("updating photo list...");
        progress.show();
        try {
            _obj.put("doc_clinic_id", clinicID);
            _obj.put("file", _data.getString("file"));
            _obj.put("file_type", _data.getString("file_type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("_obj", _obj.toString());
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.DocClinicFile, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progress.dismiss();
                try {
                    Boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {
                        Log.i("postClinicFiles", response.toString());
                        JSONObject _fileData = (JSONObject) response.getJSONObject("data");
                        Toast.makeText(MyClinic.this, "Photo uploaded successfully", Toast.LENGTH_LONG).show();
                    } else {
                        Log.i("Error ", "Something went wrong!" + response.toString());
                        Toast.makeText(MyClinic.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                        progress.dismiss();
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
    //Add photos from gallery end <----------------------------------------------------------------


}
