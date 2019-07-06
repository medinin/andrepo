package com.medinin.medininapp.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.medinin.medininapp.utils.CommonMethods.hasPermissions;

public class AppointmentList extends AppCompatActivity {

    private static final String TAG = AllPatients.class.getSimpleName();
    private final int PERMISSION_ALL = 1;
    private final String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_SMS, android.Manifest.permission.INTERNET, android.Manifest.permission.RECEIVE_SMS, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_WIFI_STATE};

    private LinearLayout linearLayoutList;
    private ProgressDialog progress;

    private RequestQueue mRequestQueue;
    private long back_pressed = 0;


    private String patient_id, med_user_id, med_user_token, _tempPatID,dateInputStr;
    LayoutInflater layoutInflater;

    Boolean _appointmentLoadStatus = true;
    ScrollView scrollView;
    int _page_number = 0;
    private EditText mainSearchBox;

    private TextView dateInput;
    private FrameLayout no_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        initMenu();
        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);

        mRequestQueue = Volley.newRequestQueue(this);
        linearLayoutList = (LinearLayout) findViewById(R.id.linearLayoutList);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading...");

        dateInput = (TextView) findViewById(R.id.dateInput);



        ImageView addAppointmentSec = (ImageView) findViewById(R.id.addAppointmentSec);
        addAppointmentSec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), AppointmentBooking.class);
                startActivity(intent);
            }
        });

        scrollView = (ScrollView) findViewById(R.id.feedScroll);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                //example
                View view = (View) scrollView.getChildAt(0);

                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                if (diff <= 0 && _appointmentLoadStatus) {
                    _appointmentLoadStatus = false;
                    loadMoreAppointments();
                }

            }
        });

        fetchAllAppointments();

        if (isNetworkConnected()) {


        } else {

            layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            View no_internet = layoutInflater.inflate(R.layout.no_internet, null);
            linearLayoutList.addView(no_internet);
        }


    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), AllPatients.class);
        finish();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("ProgressBar", true);
        startActivity(intent);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            return cm.getActiveNetworkInfo() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public String removeNull(String _string) {
        return _string.equals("null") ? "" : _string;
    }

    private void fetchAllAppointments() {
        linearLayoutList.removeAllViews();
        //api call
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("limit", "10");
            _obj.put("page", 0);
            _obj.put("doc_id", med_user_id);
            _obj.put("patient_id", _tempPatID);
            _obj.put("status", "pending");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progress.show();
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.DocAppointmentSearch, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("hello", response.toString());
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                docAppointListFeed(response, false);
//                layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
//                View no_data_row = layoutInflater.inflate(R.layout.no_data, null);

                FrameLayout no_data = findViewById(R.id.no_data);
                TextView instruction_txt = findViewById(R.id.instruction_txt);
                instruction_txt.setText("Click the + button on the lower right corner to create a new appointment");

                try {
                    JSONObject dataObj = (JSONObject) response.getJSONObject("data");
                    boolean _error = Boolean.parseBoolean(response.getString("error"));


                    TextView total_appointment_count = (TextView) findViewById(R.id.total_appointment_count);
                    String _total_pats = dataObj.getString("total");
                    total_appointment_count.setText(_total_pats);


                    if (!_error && !response.isNull("data")) {
                        JSONArray feedArray = dataObj.getJSONArray("result");
                        if (feedArray.length() != 0) {
                            _appointmentLoadStatus = true;
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
                        Toast.makeText(AppointmentList.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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


    private void loadMoreAppointments() {
        progress.show();
        progress.setTitle("Loading...");

        _page_number = _page_number + 1;
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("page", _page_number);
            _obj.put("doc_id", med_user_id);
            _obj.put("patient_id", _tempPatID);
            _obj.put("status", "pending");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.DocAppointmentSearch, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                docAppointListFeed(response, false);


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

    private void docAppointListFeed(JSONObject response, Boolean search) {
        if (search) {
            linearLayoutList.removeAllViews();
        }
        try {
            JSONObject dataObj = (JSONObject) response.getJSONObject("data");
            boolean _error = Boolean.parseBoolean(response.getString("error"));

            TextView total_appointment_count = (TextView) findViewById(R.id.total_appointment_count);
            String _total_pats = dataObj.getString("total");
            total_appointment_count.setText(_total_pats);


            if (!_error && !response.isNull("data")) {
                JSONArray feedArray = dataObj.getJSONArray("result");
                if (feedArray.length() != 0) {
                    _appointmentLoadStatus = true;
                }
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);
                    JSONObject patientObj = (JSONObject) feedObj.getJSONObject("patient");
                    JSONObject appointObj = (JSONObject) feedObj.getJSONObject("appointment");

                    String curTime = 00 + ":" + 30 + ":" + 00;

                    layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
                    View preview = layoutInflater.inflate(R.layout.all_appointment_row, null);

                    TextView date_txt = (TextView) preview.findViewById(R.id.date_txt);
                    TextView month_txt = (TextView) preview.findViewById(R.id.month_txt);
                    TextView appointment_time_txt = (TextView) preview.findViewById(R.id.appointment_time_txt);
                    TextView patient_name_txt = (TextView) preview.findViewById(R.id.patient_name_txt);
                    TextView pat_age_txt = (TextView) preview.findViewById(R.id.pat_age_txt);
                    TextView pat_gender_txt = (TextView) preview.findViewById(R.id.pat_gender_txt);
                    TextView pat_mobile_txt = (TextView) preview.findViewById(R.id.pat_mobile_txt);
                    TextView appointment_time_txt2 = (TextView) preview.findViewById(R.id.appointment_time_txt2);

                    ConstraintLayout bookAppointViewLink = (ConstraintLayout) preview.findViewById(R.id.bookAppointViewLink);

                    date_txt.setText(dateFormatFun(appointObj.getString("date")));
                    month_txt.setText(dateFormatMonth(appointObj.getString("date")));
                    appointment_time_txt.setText(appointObj.getString("time"));


                    patient_name_txt.setText(patientObj.getString("name"));
                    pat_age_txt.setText(removeNull(patientObj.getString("age") + " Y"));
                    pat_mobile_txt.setText(removeNull(patientObj.getString("mobile")));
                    final String _tempPatID = patientObj.getString("id");
                    final String _tempAppointID = appointObj.getString("id");
                    final String _status = appointObj.getString("status");
                    String _gender = patientObj.getString("gender");

                    String name_str = appointObj.getString("time");
                    String[] splited = name_str.split("\\:");


                    if (!patientObj.isNull("mobile_code") && !patientObj.getString("mobile_code").isEmpty()) {
                        TextView mobileCode = preview.findViewById(R.id.mobile_code);

                    }


                    if (!name_str.equals("null") && !name_str.isEmpty()) {

                        Log.i("hour", splited[0].toString());
                        Log.i("min", splited[1].toString());
                        int hour = Integer.parseInt(splited[0]);
                        int min = Integer.parseInt(splited[1]);
                        min = min + 30;
                        if (min >= 60) {
                            hour = hour + 1;
                            min = 0;
                        }
                        String endTimeStr = String.format("%02d:%02d", hour, min);//hour + ":" + min;
                        appointment_time_txt2.setText(endTimeStr);
                    }


                    Log.i("_status", _status);

                    if (_status.equals("checkout") || _status.equals("cancelled")){
                        onStatusChange(preview,true,true);
                    }

                    if (_gender.equals("male")) {

                        pat_gender_txt.setText("M");


                    } else if (_gender.equals("female")) {
                        pat_gender_txt.setText("F");


                    } else {
                        pat_gender_txt.setText("Other");

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

                    bookAppointViewLink.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), PatientHistory.class);
                            finish();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                            intent.putExtra("patient_id", _tempPatID);
                            intent.putExtra("appointment_id", _tempAppointID);
                            startActivity(intent);
                        }
                    });


                    linearLayoutList.addView(preview);
                }
                progress.dismiss();
            } else {
                progress.dismiss();
                Toast.makeText(AppointmentList.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            progress.dismiss();
            e.printStackTrace();
        }
    }

    public void onStatusChange(View preview, boolean _cancel,boolean checkout) {
        final TextView nameSec = (TextView) preview.findViewById(R.id.patient_name_txt);
        if (_cancel || checkout) {
            nameSec.setPaintFlags(nameSec.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            nameSec.setPaintFlags(nameSec.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    private String dateFormatFun(String _date) {
        if (_date != null && !_date.equals("")) {
            //Convert date into local format
            DateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateFormat = new SimpleDateFormat("dd");
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

    private String dateFormatMonth(String _date) {
        if (_date != null && !_date.equals("")) {
            //Convert date into local format
            DateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateFormat = new SimpleDateFormat("MMM");
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

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AppointmentList.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(
                            DatePicker view,
                            int year,
                            int monthOfYear,
                            int dayOfMonth
                    ) {
                        dateInput.setText( dayOfMonth + "-" + (monthOfYear + 1) + "-" +  year);
                        dateInputStr = (year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        appointmentFilter();

                    }
                }, mYear, mMonth, mDay);


        datePickerDialog.show();
    }

    public void appointmentFilter() {
        linearLayoutList.removeAllViews();
        //api call
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("date", dateInputStr);
            _obj.put("doc_id", med_user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progress.show();
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.DocAppointmentSearch, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("hello", response.toString());
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                docAppointListFeed(response, false);
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

    public void openCallDialog(final View v) {
        BottomSheetDialog dialog = new BottomSheetDialog(AppointmentList.this);
        dialog.setContentView(R.layout.sms_call_popup);
        View _parent = (View) v.getParent();
        final TextView _textView = (TextView) _parent.findViewById(v.getId());

        ImageView phone_img = (ImageView) dialog.findViewById(R.id.phone_img);
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

        ImageView whatsapp_img = (ImageView) dialog.findViewById(R.id.whatsapp_img);
        whatsapp_img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + "+91" + _textView.getText() + "&text=");

                Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);

                startActivity(sendIntent);


            }
        });

        ImageView comment_img = (ImageView) dialog.findViewById(R.id.comment_img);
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
                finish();
                overridePendingTransition(0,0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

            }
        });

        llyt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllPatients.class);
                finish();
                overridePendingTransition(0,0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }
}
