package com.medinin.medininapp.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.medinin.medininapp.R;
import com.medinin.medininapp.config.API_URL;
import com.medinin.medininapp.utils.ClearFocusOnKBClose;
import com.medinin.medininapp.utils.CustomDateToString;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentBooking extends AppCompatActivity {

    private static final String TAG = AllPatients.class.getSimpleName();

    private final int PERMISSION_ALL = 1;
    private final String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_SMS, android.Manifest.permission.INTERNET, android.Manifest.permission.RECEIVE_SMS, Manifest.permission.CALL_PHONE};

    private android.support.v7.widget.GridLayout morningSlotGrid, afternoonSlotGrid, eveningSlotGrid;

    private LinearLayout linearLayoutList;
    private ConstraintLayout patients_wrap;
    private LinearLayout select_time_wrap, select_pat_hd_sec, time_slot_hd_sec;
    private ProgressDialog progress;
    private CoordinatorLayout activity_book_appointment;


    private RequestQueue mRequestQueue;
    private long back_pressed = 0;


    private String patient_id, med_user_id, med_user_token, selectedPateint_id, pat_name, time_type, slot_status;
    LayoutInflater layoutInflater;

    Boolean _patientsLoadStatus = true;
    ScrollView scrollView;

    private EditText mainSearchBox;
    private Boolean face_scan = false;

    int _page_number = 0;

    private TextView appointDateInput;
    private String appointDateStr = "";
    private String appointTimeStr = "";
    private TextView selectedSlot;
    private ImageView dateArrow;


    Calendar calDate = Calendar.getInstance();
    int year = calDate.get(Calendar.YEAR);
    int month = calDate.get(Calendar.MONTH);
    int day = calDate.get(Calendar.DAY_OF_MONTH);
    String currentDate = String.format("%d-%d-%d", year, (month + 1), day);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_booking);

        initMenu();

        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);


        Intent intent = getIntent();
        patient_id = intent.getStringExtra("patient_id");
        face_scan = intent.getBooleanExtra("face_scan", false);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading...");


        patients_wrap = (ConstraintLayout) findViewById(R.id.patients_wrap);
        select_time_wrap = (LinearLayout) findViewById(R.id.select_time_wrap);
        select_pat_hd_sec = (LinearLayout) findViewById(R.id.select_pat_hd_sec);
        time_slot_hd_sec = (LinearLayout) findViewById(R.id.time_slot_hd_sec);


        mRequestQueue = Volley.newRequestQueue(this);

        activity_book_appointment = (CoordinatorLayout) findViewById(R.id.activity_book_appointment);
        linearLayoutList = (LinearLayout) findViewById(R.id.linearLayoutList);
        morningSlotGrid = (android.support.v7.widget.GridLayout) findViewById(R.id.morningSlotGrid);
        afternoonSlotGrid = (android.support.v7.widget.GridLayout) findViewById(R.id.afternoonSlotGrid);
        eveningSlotGrid = (android.support.v7.widget.GridLayout) findViewById(R.id.eveningSlotGrid);
        dateArrow = (ImageView) findViewById(R.id.date_arrow);




        scrollView = (ScrollView) findViewById(R.id.feedScroll);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                //example
                LinearLayout view = (LinearLayout) scrollView.getChildAt(0);
                if (view.getChildCount() > 1) {
                    int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                    if (diff <= 0 && _patientsLoadStatus) {
                        _patientsLoadStatus = false;
                        loadMorePatients();
                    }
                }

            }
        });

        mainSearchBox = findViewById(R.id.searchBox);
        mainSearchBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        appointDateInput = (TextView) findViewById(R.id.appointDateInput);

        ConstraintLayout backBtn = (ConstraintLayout) findViewById(R.id.back_sec);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

        ConstraintLayout backBtn2 = (ConstraintLayout) findViewById(R.id.back_sec2);
        backBtn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

        fetchAllPatients();
        bindPatientSearchBox();


        initTimeSlot();


        if (face_scan) {
            Log.i("patient_id",patient_id);
            patients_wrap.setVisibility(View.GONE);
            select_pat_hd_sec.setVisibility(View.GONE);
            time_slot_hd_sec.setVisibility(View.VISIBLE);
            select_time_wrap.setVisibility(View.VISIBLE);


            selectedPateint_id = patient_id;

            fetchPatientDetails();

            Log.i("selectedPateint_id",selectedPateint_id);


        }


        //On close of soft keyboard clear edittext focus
        // it's works only if activity windowSoftInputMode=adjustResize
        new ClearFocusOnKBClose(activity_book_appointment);
    }

    private void initTimeSlot() {

        //api call
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("date", currentDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progress.show();
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.DocFetchTimeSlot + "/" + med_user_id, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                parseJsonTimeSlot(response, false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progress.dismiss();
            }
        });

        jsonReq.setRetryPolicy(new
                DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);

    }

    private void parseJsonTimeSlot(JSONObject response, Boolean search) {
        morningSlotGrid.removeAllViews();
        afternoonSlotGrid.removeAllViews();
        eveningSlotGrid.removeAllViews();
        try {
            boolean _error = Boolean.parseBoolean(response.getString("error"));
            if (!_error && !response.isNull("data")) {

                JSONArray feedArray = response.getJSONArray("data");
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);
                    layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
                    View preview = layoutInflater.inflate(R.layout.time_slot, null);

                    TextView timeSlot = preview.findViewById(R.id.timeSlot);

                    timeSlot.setText(feedObj.getString("time"));
                    time_type = feedObj.getString("type");
                    slot_status = feedObj.getString("status");

                    if (time_type.equals("Morning")) {
                        morningSlotGrid.addView(preview);
                    } else if (time_type.equals("Afternoon")) {
                        afternoonSlotGrid.addView(preview);
                    } else if (time_type.equals("Evening")) {
                        eveningSlotGrid.addView(preview);
                    }

                    TextView total_slot_count1 = (TextView) findViewById(R.id.total_slot_count1);
                    TextView total_slot_count2 = (TextView) findViewById(R.id.total_slot_count2);
                    TextView total_slot_count3 = (TextView) findViewById(R.id.total_slot_count3);
                    int _total_slots1 = AppointmentBooking.this.morningSlotGrid.getChildCount();
                    int _total_slots2 = AppointmentBooking.this.afternoonSlotGrid.getChildCount();
                    int _total_slots3 = AppointmentBooking.this.eveningSlotGrid.getChildCount();
                    total_slot_count1.setText("(" + String.valueOf(_total_slots1) + " Slots" + ")");
                    total_slot_count2.setText("(" + String.valueOf(_total_slots2) + " Slots" + ")");
                    total_slot_count3.setText("(" + String.valueOf(_total_slots3) + " Slots" + ")");

                    if (slot_status.equals("active")) {
                        timeSlot.setBackground(ContextCompat.getDrawable(AppointmentBooking.this, R.drawable.time_slot_white));
                        timeSlot.setTextColor(Color.parseColor("#8e63e6"));

                    } else if (slot_status.equals("inactive")) {
                        timeSlot.setBackground(ContextCompat.getDrawable(AppointmentBooking.this, R.drawable.time_selected_puple));
                        timeSlot.setTextColor(Color.parseColor("#FFFFFF"));
                        timeSlot.setPaintFlags(timeSlot.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        timeSlot.setClickable(false);
                    }

                }
                progress.dismiss();
            } else {
                progress.dismiss();
                Toast.makeText(AppointmentBooking.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            progress.dismiss();
            e.printStackTrace();
        }
    }

    private String TimeFormatFun(String _date) {
        if (_date != null && !_date.equals("")) {
            //Convert date into local format
            DateFormat localFormat = new SimpleDateFormat("DD-MM-YYYY");
            DateFormat dateFormat = new SimpleDateFormat("DD-MMM-YYYY");
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

    private void fetchPatientDetails() {
        progress.show();
        progress.setMessage("Loading...");
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.PatientFetchFull + selectedPateint_id, null, new Response.Listener<JSONObject>() {



            @Override
            public void onResponse(JSONObject response) {
                Log.i("response",response.toString());
                VolleyLog.d(TAG, "Response: " + response.toString());
                parseJsonPatient(response);

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

    private void parseJsonPatient(JSONObject response) {
        try {
            progress.dismiss();
            Boolean _error = Boolean.parseBoolean(response.getString("error"));
            if (!_error && !response.isNull("data")) {

                TextView patient_name_txt = (TextView) findViewById(R.id.patient_name_txt);
                TextView pat_age_txt = (TextView) findViewById(R.id.pat_age_txt);
                TextView pat_gender_txt = (TextView) findViewById(R.id.pat_gender_txt);
                TextView pat_mobile_txt = (TextView) findViewById(R.id.pat_mobile_txt);

                JSONObject _data = (JSONObject) response.getJSONObject("data");
                patient_name_txt.setText(_data.getString("name"));
                pat_age_txt.setText(_data.getString("age") + " Y");
                pat_gender_txt.setText(_data.getString("gender"));
                pat_mobile_txt.setText(_data.getString("mobile"));

                pat_name = _data.getString("name");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void fetchAllPatients() {
        linearLayoutList.removeAllViews();
        //api call
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("limit", "10");
            _obj.put("page", 0);
            _obj.put("doc_id", med_user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("hello", _obj.toString());
        progress.show();
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.DocPatientSearch, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("hello", response.toString());
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                parseJsonFeed(response, false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progress.dismiss();
            }
        });

//        RequestQueue requestQueue = Volley.newRequestQueue(this);
        jsonReq.setRetryPolicy(new
                DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        requestQueue.add(jsonReq);
        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);
    }

    public String removeNull(String _string) {
        return _string.equals("null") ? "" : _string;
    }

    private void parseJsonFeed(JSONObject response, Boolean search) {
        if (search) {
            linearLayoutList.removeAllViews();
        }
        try {
            JSONObject dataObj = (JSONObject) response.getJSONObject("data");
            boolean _error = Boolean.parseBoolean(response.getString("error"));


            if (!_error && !response.isNull("data")) {
                JSONArray feedArray = dataObj.getJSONArray("result");
                if (feedArray.length() != 0) {
                    _patientsLoadStatus = true;
                }
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);
                    JSONObject patientObj = (JSONObject) feedObj.getJSONObject("patient");

                    layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
                    View preview = layoutInflater.inflate(R.layout.all_patient_row, null);

                    CircleImageView patientImg = (CircleImageView) preview.findViewById(R.id.patientImg);
                    TextView patient_name_txt = (TextView) preview.findViewById(R.id.patient_name_txt);
                    final LinearLayout dropDownBtn = (LinearLayout) preview.findViewById(R.id.dropDownBtn);

                    TextView patDOBTxt = (TextView) preview.findViewById(R.id.patDOBTxt);
                    TextView patAgeTxt = (TextView) preview.findViewById(R.id.patAgeTxt);
                    TextView patMobileTxt = (TextView) preview.findViewById(R.id.patMobileTxt);
                    TextView lastVisitDateTxt = (TextView) preview.findViewById(R.id.lastVisitDateTxt);
                    TextView patGenderTxt = (TextView) preview.findViewById(R.id.patGenderTxt);
                    ImageView viewPatDetailsImgBtn = (ImageView) preview.findViewById(R.id.viewPatDetailsImgBtn);
                    ImageView editProfileImgBtn = (ImageView) preview.findViewById(R.id.editProfileImgBtn);
                    LinearLayout bottomBarBtn = (LinearLayout) preview.findViewById(R.id.bottomBarBtn);


                    patient_name_txt.setText(patientObj.getString("name"));
                    if (!patientObj.isNull("dob") && !patientObj.getString("dob").isEmpty() && !patientObj.getString("dob").equals("null")) {
                        patDOBTxt.setText(dateFormatFun(patientObj.getString("dob")));
                    }
                    patAgeTxt.setText(removeNull(patientObj.getString("age")));
                    patMobileTxt.setText(removeNull(patientObj.getString("mobile")));
                    patGenderTxt.setText(patientObj.getString("gender"));
                    final String _tempPatID = patientObj.getString("id");

                    final String _mobile = patientObj.getString("mobile");

                    String _otp_status = "";
                    if (!patientObj.isNull("otp_status")) {
                        _otp_status = patientObj.getString("otp_status");
                    }

                    if (!patientObj.isNull("photo")) {
                        String newurl = API_URL._domain + patientObj.getString("photo");
                        new AppointmentBooking.DownLoadCircleImageTask(patientImg).execute(newurl);
                    } else {
                        if (patientObj.getString("gender").equals("male")) {
                            patientImg.setImageResource(R.drawable.male_user);
                        } else {
                            patientImg.setImageResource(R.drawable.female_user);
                        }
                    }

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


                    preview.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View arg0) {

                            patients_wrap.setVisibility(View.GONE);
                            select_pat_hd_sec.setVisibility(View.GONE);
                            select_time_wrap.setVisibility(View.VISIBLE);
                            time_slot_hd_sec.setVisibility(View.VISIBLE);

                            selectedPateint_id = _tempPatID;
                            fetchPatientDetails();

                        }
                    });

//                    bottomBarBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dropDownBtn.performClick();
//                        }
//                    });
//
//                    viewPatDetailsImgBtn.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View arg0) {
//                            patients_wrap.setVisibility(View.GONE);
//                            select_pat_hd_sec.setVisibility(View.GONE);
//                            select_time_wrap.setVisibility(View.VISIBLE);
//                            time_slot_hd_sec.setVisibility(View.VISIBLE);
//                            selectedPateint_id = _tempPatID;
//
//
//                        }
//                    });

//                    final String final_otp_status = _otp_status;
//                    editProfileImgBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (final_otp_status.equals("success")) {
//                                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
//                                intent.putExtra("patient_id", _tempPatID);
//                                startActivity(intent);
//                            } else {
//                                patient_id = _tempPatID;
//                            }
//                        }
//                    });

                    linearLayoutList.addView(preview);
                }
                progress.dismiss();
            } else {
                progress.dismiss();
                Toast.makeText(AppointmentBooking.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            progress.dismiss();
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("_obj", _obj.toString());
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.DocPatientSearch, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                parseJsonFeed(response, false);
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

    private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    private class DownLoadCircleImageTask extends AsyncTask<String, Void, Bitmap> {
        CircleImageView imageView;

        public DownLoadCircleImageTask(CircleImageView imageView) {
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
            // imageView.setScaleType(CircleImageView.ScaleType.FIT_XY);
        }
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
                    JSONObject _obj = new JSONObject();
                    try {
                        _obj.put("query", mainSearchBox.getText().toString());
                        _obj.put("limit", "30");
                        _obj.put("page", _page_number);
                        _obj.put("doc_id", med_user_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                            API_URL.DocPatientSearch, _obj, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            parseJsonFeed(response, true);
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
                    fetchAllPatients();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mainSearchBox.addTextChangedListener(_df);
    }


    private Boolean validateEditTextInput(EditText _view) {
        String _value = _view.getText().toString();
        if (_value != null && !_value.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean validateTextViewInput(TextView _view) {
        String _value = _view.getText().toString();
        if (_value != null && !_value.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private String dateFormatFun(String _date) {
        if (_date != null && !_date.equals("")) {
            //Convert date into local format
            DateFormat localFormat = new SimpleDateFormat("YYYY-MM-DD");
            DateFormat dateFormat = new SimpleDateFormat("YYYY-MMM-DD");
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

    public void openDateSelectDialog(View v) {
        // Get Current Date
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AppointmentBooking.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(
                            DatePicker view,
                            int year,
                            int monthOfYear,
                            int dayOfMonth
                    ) {
                        appointDateInput.setText(String.format("%d-%d-%d", year, (monthOfYear + 1), dayOfMonth));
                        appointDateStr = String.format("%d-%d-%d", year, (monthOfYear + 1), dayOfMonth);
                        Log.i("appointDateStr", appointDateStr);
                        currentDate = appointDateStr;
                        Log.i("appointDateStr", appointDateStr);
                        initTimeSlot();
                        postAppointment();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void timeSelect(View view) {
        View _parent = (View) view.getParent();
        TextView _view = (TextView) _parent.findViewById(R.id.timeSlot);


        if (appointDateStr != null && !appointDateStr.isEmpty()) {
            appointDateInput.setTextColor(Color.parseColor("#A9A9A9"));
            dateArrow.setImageDrawable(getDrawable(R.drawable.ic_arrow_drop_down_gray));

            _view.setBackground(ContextCompat.getDrawable(AppointmentBooking.this, R.drawable.time_selected_puple));
            _view.setTextColor(Color.parseColor("#FFFFFF"));


            appointTimeStr = _view.getText().toString();


        } else {
            Toast.makeText(AppointmentBooking.this, "Please select Date!", Toast.LENGTH_LONG).show();
        }
        if (selectedSlot != null && appointDateStr == null) {
            selectedSlot.setBackground(ContextCompat.getDrawable(AppointmentBooking.this, R.drawable.time_slot_white));
            selectedSlot.setTextColor(Color.parseColor("#8e63e6"));
        }

        selectedSlot = _view;

        // Get Current Date

        postAppointment();

    }

    private Boolean validateAppointmentDetails() {
        Boolean _error = false;
        if (appointDateStr == null || appointDateStr.isEmpty()) {
            _error = true;
            appointDateInput.setTextColor(Color.parseColor("#cf7381"));
            dateArrow.setImageDrawable(getDrawable(R.drawable.ic_arrow_drop_down_red));
            ObjectAnimator
                    .ofFloat(appointDateInput, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0)
                    .setDuration(1000)
                    .start();

            ObjectAnimator
                    .ofFloat(dateArrow, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0)
                    .setDuration(1000)
                    .start();
            Toast.makeText(AppointmentBooking.this, "Please select date!", Toast.LENGTH_LONG).show();
        } else if (appointTimeStr.isEmpty()) {
            _error = true;
            appointDateInput.setTextColor(Color.parseColor("#667480"));
            dateArrow.setImageDrawable(getDrawable(R.drawable.ic_arrow_drop_down_gray));
            Toast.makeText(AppointmentBooking.this, "Please select time!", Toast.LENGTH_LONG).show();

        }

        return _error;
    }

    private void postAppointment() {

        progress.setMessage("Booking appointment...");

        //post appoinement

        if (!validateAppointmentDetails()) {

            JSONObject _jsonObj = new JSONObject();
            try {
                _jsonObj.put("date", appointDateStr);
                _jsonObj.put("time", appointTimeStr);
                _jsonObj.put("patient_id", selectedPateint_id);
                _jsonObj.put("doc_id", med_user_id);

                Boolean _error = false;


            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("_jsonObj", _jsonObj.toString());
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                    API_URL.PatientAppointment, _jsonObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    Log.i("response.toString()", response.toString());

                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            Toast.makeText(AppointmentBooking.this, "Appointment booked successfully.", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getApplicationContext(), AppointmentBookingLoader.class);
                            intent.putExtra("pat_name", pat_name);
                            intent.putExtra("appointmentDate_str", appointDateStr);
                            intent.putExtra("appointmentTime_str", appointTimeStr);
                            new android.os.Handler().postDelayed(new Runnable() {
                                public void run() {
                                    Log.i("tag", "This'll run 300 milliseconds later");
                                    Intent intent = new Intent(getApplicationContext(), AppointmentList.class);
                                    startActivity(intent);
                                }
                            }, 4000);
                            startActivity(intent);

                        } else {
                            Toast.makeText(AppointmentBooking.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
            mRequestQueue.add(jsonReq);
        }
    }

    private int getHoursValue(int hours) {
        return hours - 12;
    }

    private void displayTimeSlots() {
        String timeValue = appointDateStr;
        StringTokenizer stringTokenizer = new StringTokenizer(timeValue, "T");
        String dateValue = stringTokenizer.nextElement().toString();
        String endDateValue = appointDateStr;
        String restString = stringTokenizer.nextElement().toString();
        StringTokenizer secondTokeniser = new StringTokenizer(restString, ":");
        String hours = secondTokeniser.nextElement().toString();
        String minutes = secondTokeniser.nextElement().toString();
        hours = String.valueOf(Integer.parseInt(hours) + 2);
        if (Integer.parseInt(minutes) > 30) {
            minutes = "00";
        } else {
            minutes = "30";
        }

        String amOrPm;
        if (Integer.parseInt(hours) < 12) {
            amOrPm = "AM";
        } else {
            amOrPm = "PM";
            hours = String.valueOf(getHoursValue(Integer.parseInt(hours)));
        }
        String time1 = hours + ":" + minutes + " " + amOrPm;
        String time2 = "12" + ":" + "00" + " AM ";
        String format = "yyyy-MM-dd hh:mm a";

        SimpleDateFormat sdf = new SimpleDateFormat(format);

        try {
            Date dateObj1 = sdf.parse(dateValue + " " + time1);
            Date dateObj2 = sdf.parse(endDateValue + " " + time2);
            Log.d("TAG", "Date Start: " + dateObj1);
            Log.d("TAG", "Date End: " + dateObj2);
            long dif = dateObj1.getTime();
            while (dif < dateObj2.getTime()) {
                Date slot1 = new Date(dif);
                dif += 3600000;
                Date slot2 = new Date(dif);
                dif += 3600000;
                SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
                SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a, dd/MM/yy");
                Log.d("TAG", "Hour slot = " + sdf1.format(slot1) + " - " + sdf2.format(slot2));
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
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
        home_appoitment.setBackgroundColor(getResources().getColor(R.color.color_blue));
        home_setting.setBackgroundColor(getResources().getColor(R.color.gcolor));


        img_home.setBackgroundResource(R.drawable.ic_patient);
        img_appoint.setBackgroundResource(R.drawable.ic_calender_blue);
        img_setting.setBackgroundResource(R.drawable.ic_user_circle);

        llyt_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AccountPremium.class);
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
}
