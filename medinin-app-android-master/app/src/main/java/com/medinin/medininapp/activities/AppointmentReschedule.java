package com.medinin.medininapp.activities;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AppointmentReschedule extends AppCompatActivity {

    private static final String TAG = AllPatients.class.getSimpleName();

    private android.support.v7.widget.GridLayout morningSlotGrid, afternoonSlotGrid, eveningSlotGrid;
    private String pat_name, pat_appoint_date, pat_appoint_time, count, time_type, slot_status;
    private String patient_id, med_user_id, med_user_token, selectedPateint_id, appointmentDate_str = "", appointmentTime_str = "", _tempPatID, _tempAppointID;
    private ProgressDialog progress;


    private String appointment_id;
    private TextView dateInput;
    private long back_pressed = 0;
    private RequestQueue mRequestQueue;

    private TextView appointDateInput;
    private String appointDateStr, appointTimeStr;
    private TextView selectedSlot;
    private ImageView dateArrow;

    Calendar calDate = Calendar.getInstance();
    int year = calDate.get(Calendar.YEAR);
    int month = calDate.get(Calendar.MONTH);
    int day = calDate.get(Calendar.DAY_OF_MONTH);
    String currentDate = String.format("%d-%d-%d", year, (month + 1), day);

    LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_reschedule);

        initMenu();


        morningSlotGrid = (android.support.v7.widget.GridLayout) findViewById(R.id.morningSlotGrid);
        afternoonSlotGrid = (android.support.v7.widget.GridLayout) findViewById(R.id.afternoonSlotGrid);
        eveningSlotGrid = (android.support.v7.widget.GridLayout) findViewById(R.id.eveningSlotGrid);
        dateArrow = (ImageView) findViewById(R.id.date_arrow);

        Intent intent = getIntent();
        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);
        appointment_id = intent.getStringExtra("appointment_id");
        patient_id = intent.getStringExtra("patient_id");

        mRequestQueue = Volley.newRequestQueue(this);


        appointDateInput = (TextView) findViewById(R.id.appointDateInput);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading...");


        ConstraintLayout backBtn = (ConstraintLayout) findViewById(R.id.back_sec);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });


        TextView cancle_txt2 = (TextView) findViewById(R.id.cancle_txt2);


        fetchAppointmentDetails();
        fetchPatientDetails();

        cancle_txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(AppointmentReschedule.this)
                        .setTitle("Cancel")
                        .setMessage("Do you want to Cancel the appointment?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent(getApplicationContext(), AppointmentList.class);
                                intent.putExtra("patient_id", _tempPatID);
                                intent.putExtra("appointment_id", _tempAppointID);
                                startActivity(intent);
                                dialog.dismiss();
                            }

                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        });


    }

    private void fetchAppointmentDetails() {
        progress.show();
        progress.setMessage("Loading...");
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.PatientAppointment + "/" + appointment_id, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                parseJsonAppointment(response);
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

    private void fetchPatientDetails() {
        progress.show();
        progress.setMessage("Loading...");
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.PatientFetchFull + patient_id, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
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

                pat_name = _data.getString("name").toString();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJsonAppointment(JSONObject response) {
        try {
            progress.dismiss();
            Boolean _error = Boolean.parseBoolean(response.getString("error"));
            if (!_error && !response.isNull("data")) {
                layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
                View preview = layoutInflater.inflate(R.layout.time_slot, null);


                JSONObject _data = (JSONObject) response.getJSONObject("data");
                appointmentDate_str = _data.getString("date");
                appointTimeStr = _data.getString("time");

                TextView appointDateInput = (TextView) findViewById(R.id.appointDateInput);
                appointDateInput.setText(appointmentDate_str);

                initTimeSlot();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initTimeSlot() {

        //api call
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("date", appointmentDate_str);
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


//        int hour = 8, minute = 0;
//
//        for (int i = 0; i < 27; i++) {
//
//
//            layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
//            View preview = layoutInflater.inflate(R.layout.time_slot, null);
//
//
//            TextView timeSlot = (TextView) preview.findViewById(R.id.timeSlot);
//
//            Date d = new Date();
//            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
//            String currentDateTimeString = sdf.format(d);
//            if (i != 0) {
//                minute = minute + 30;
//            }
//
//            if (minute >= 60) {
//                hour = hour + 1;
//                minute = 0;
//            }
//            String endTimeStr = String.format("%02d:%02d", hour, minute); //hour + ":" + minute;
//
//            timeSlot.setText(endTimeStr);
//
//            if (endTimeStr.equals(appointTimeStr)) {
//                selectedSlot = timeSlot;
//                timeSlot.setBackground(ContextCompat.getDrawable(AppointmentReschedule.this, R.drawable.time_selected_puple));
//                timeSlot.setTextColor(Color.parseColor("#FFFFFF"));
//            }
//
//
//            if (hour <= 11) {
//                morningSlotGrid.addView(preview);
//                count= morningSlotGrid.toString();
////                Log.i("count all slots",morningSlotGrid.getChildCount());
//            } else if (hour > 11 && hour <= 16) {
//                afternoonSlotGrid.addView(preview);
//            } else if (hour > 16) {
//                eveningSlotGrid.addView(preview);
//            }
//        }

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
                    Log.i("res", feedArray.toString());
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
                    int _total_slots1 = AppointmentReschedule.this.morningSlotGrid.getChildCount();
                    int _total_slots2 = AppointmentReschedule.this.afternoonSlotGrid.getChildCount();
                    int _total_slots3 = AppointmentReschedule.this.eveningSlotGrid.getChildCount();
                    total_slot_count1.setText("(" + String.valueOf(_total_slots1) + " Slots" + ")");
                    total_slot_count2.setText("(" + String.valueOf(_total_slots2) + " Slots" + ")");
                    total_slot_count3.setText("(" + String.valueOf(_total_slots3) + " Slots" + ")");

                    if (slot_status.equals("active")) {
                        timeSlot.setBackground(ContextCompat.getDrawable(AppointmentReschedule.this, R.drawable.time_slot_white));
                        timeSlot.setTextColor(Color.parseColor("#8e63e6"));

                    } else if (slot_status.equals("inactive")) {
                        timeSlot.setBackground(ContextCompat.getDrawable(AppointmentReschedule.this, R.drawable.time_selected_puple));
                        timeSlot.setTextColor(Color.parseColor("#FFFFFF"));

                        Log.i("appointTimeStr", appointTimeStr);
                        if (!(timeSlot.getText()).equals(appointTimeStr)) {
                            timeSlot.setPaintFlags(timeSlot.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            timeSlot.setClickable(false);
                        }


                    }

                }
                progress.dismiss();
            } else {
                progress.dismiss();
                Toast.makeText(AppointmentReschedule.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            progress.dismiss();
            e.printStackTrace();
        }
    }

    public void openDateSelectDialog(View v) {
        // Get Current Date

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AppointmentReschedule.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(
                            DatePicker view,
                            int year,
                            int monthOfYear,
                            int dayOfMonth
                    ) {
                        appointDateInput.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        appointmentDate_str = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        rescheduleAppointment();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }


    public void timeSelect(View view) {
        View _parent = (View) view.getParent();
        TextView _view = (TextView) _parent.findViewById(R.id.timeSlot);


        if (appointmentDate_str != null && !appointmentDate_str.isEmpty()) {
            _view.setBackground(ContextCompat.getDrawable(AppointmentReschedule.this, R.drawable.time_selected_puple));
            _view.setTextColor(Color.parseColor("#FFFFFF"));
            appointmentTime_str = _view.getText().toString();


        } else {
            Toast.makeText(AppointmentReschedule.this, "Please select Date!", Toast.LENGTH_LONG).show();
        }

        if (selectedSlot != null && appointmentDate_str == null) {
            selectedSlot.setBackground(ContextCompat.getDrawable(AppointmentReschedule.this, R.drawable.time_slot_white));
            selectedSlot.setTextColor(Color.parseColor("#8e63e6"));
        }

        selectedSlot = _view;

        // Get Current Date

        rescheduleAppointment();

    }

    private Boolean validateAppointmentDetails() {
        Boolean _error = false;
        if (appointmentDate_str == null || appointmentDate_str.isEmpty()) {
            _error = true;
            appointDateInput.setTextColor(Color.parseColor("#cf7381"));
            dateArrow.setImageDrawable(getDrawable(R.drawable.ic_arrow_down_red));
            ObjectAnimator
                    .ofFloat(appointDateInput, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0)
                    .setDuration(1000)
                    .start();

            ObjectAnimator
                    .ofFloat(dateArrow, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0)
                    .setDuration(1000)
                    .start();
            Toast.makeText(AppointmentReschedule.this, "Please select date!", Toast.LENGTH_LONG).show();
        } else if (appointmentTime_str.isEmpty()) {
            _error = true;
            appointDateInput.setTextColor(Color.parseColor("#667480"));
            dateArrow.setImageDrawable(getDrawable(R.drawable.ic_arrow_down_gray));
            Toast.makeText(AppointmentReschedule.this, "Please select time!", Toast.LENGTH_LONG).show();
        }
        return _error;
    }


    private void rescheduleAppointment() {

        progress.setMessage("Booking appointment...");

        //post appoinement

        if (!validateAppointmentDetails()) {

            JSONObject _jsonObj = new JSONObject();
            try {
                _jsonObj.put("status", "pending");
                _jsonObj.put("date", appointmentDate_str);
                _jsonObj.put("time", appointmentTime_str);
                _jsonObj.put("doc_id", med_user_id);

                Boolean _error = false;


            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                    API_URL.PatientAppointment + "/" + appointment_id, _jsonObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    Log.i("response.toString()", response.toString());

                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            Toast.makeText(AppointmentReschedule.this, "Appointment rescheduled successfully.", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getApplicationContext(), AppointmentBookingLoader.class);
                            intent.putExtra("pat_name", pat_name);
                            intent.putExtra("appointmentDate_str", appointmentDate_str);
                            intent.putExtra("appointmentTime_str", appointmentTime_str);
                            new android.os.Handler().postDelayed(new Runnable() {
                                public void run() {
                                    Log.i("tag", "This'll run 300 milliseconds later");
                                    Intent intent = new Intent(getApplicationContext(), AppointmentList.class);

                                    startActivity(intent);
                                }
                            }, 4000);
                            startActivity(intent);

                        } else {
                            Toast.makeText(AppointmentReschedule.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
