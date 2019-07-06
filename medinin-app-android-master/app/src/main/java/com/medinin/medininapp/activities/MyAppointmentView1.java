package com.medinin.medininapp.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.medinin.medininapp.R;
import com.medinin.medininapp.app.AppController;
import com.medinin.medininapp.config.API_URL;

import org.json.JSONException;
import org.json.JSONObject;

public class MyAppointmentView1 extends AppCompatActivity {

    private static final String TAG = AllPatients.class.getSimpleName();

    private FrameLayout actionPopup;
    private LinearLayout viewCard, hd_sec;

    private ConstraintLayout patients_wrap;
    private LinearLayout select_time_wrap, select_pat_hd_sec, time_slot_hd_sec;

    private String patient_id, appointment_id, med_user_id, med_user_token;
    private ProgressDialog progress;

    private long back_pressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointment_view1);

        initMenu();

        Intent intent = getIntent();
        appointment_id = intent.getStringExtra("appointment_id");

        actionPopup = (FrameLayout) findViewById(R.id.actionPopup);
        viewCard = (LinearLayout) findViewById(R.id.viewCard);
        hd_sec = (LinearLayout) findViewById(R.id.hd_sec);

        LinearLayout back_sec = (LinearLayout) findViewById(R.id.back_sec);
        back_sec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

        viewCard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                actionPopup.setVisibility(View.GONE);
                return false;
            }
        });

        hd_sec.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                actionPopup.setVisibility(View.GONE);
                return false;
            }
        });

        FrameLayout actionPopup = (FrameLayout) findViewById(R.id.actionPopup);
        actionPopup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(intent);
            }
        });

    }

    public void hideOrShowButton(View v) {
        // Hide layouts if VISIBLE
        if (actionPopup.getVisibility() == View.GONE) {
            actionPopup.setVisibility(View.VISIBLE);
        }
        // Show layouts if they're not VISIBLE
        else {
            actionPopup.setVisibility(View.GONE);
        }
    }

    public void openEditProfileDialog(View v) {
        Dialog dialog = new Dialog(MyAppointmentView1.this);
        dialog.setContentView(R.layout.edit_view_popup);


        TextView edit_view_txt = (TextView) dialog.findViewById(R.id.edit_view_txt);
        edit_view_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    public void openAppointmentDetailDialog(View v) {
        Dialog dialog = new Dialog(MyAppointmentView1.this);
        dialog.setContentView(R.layout.appointment_view_popup);

        TextView reschedule_txt = (TextView) dialog.findViewById(R.id.reschedule_txt);
        reschedule_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                //reschedule appoinement
                JSONObject _jsonObj = new JSONObject();
                try {
                    _jsonObj.put("status", "pending");
                    _jsonObj.put("date_txt", "date");
                    _jsonObj.put("month_txt", "date");
                    _jsonObj.put("appointment_time_txt", "time");

                    Boolean _error = false;


                    if (!_error) {

                        Log.i("_jsonObj", _jsonObj.toString());
                        editAppointment(_jsonObj, appointment_id);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        TextView cancle_appoint_txt = (TextView) dialog.findViewById(R.id.cancle_txt);
        cancle_appoint_txt.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                //calcle appoinement
                JSONObject _jsonObj = new JSONObject();
                try {
                    _jsonObj.put("status", "cancelled");

                    Boolean _error = false;


                    if (!_error) {

                        Log.i("_jsonObj", _jsonObj.toString());
                        updateAppointment(_jsonObj, appointment_id);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        TextView invoice_txt = (TextView) dialog.findViewById(R.id.invoice_txt);
        invoice_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), Invoice_two.class);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    public void openFollowUpDialog(View v) {
        BottomSheetDialog dialog = new BottomSheetDialog(MyAppointmentView1.this);
        dialog.setContentView(R.layout.followup_popup);

        dialog.show();
    }

    private void updateAppointment(JSONObject _jsonObj, final String appointment_id) {
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                API_URL.PatientAppointment + "/" + appointment_id, _jsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                Log.i("response.toString()", response.toString());

                Intent intent = new Intent(getApplicationContext(), AppointmentList.class);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }


    private void editAppointment(JSONObject _jsonObj, final String appointment_id) {
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                API_URL.PatientAppointment + "/" + appointment_id, _jsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                Log.i("response.toString()", response.toString());

                Intent intent = new Intent(getApplicationContext(), AppointmentReschedule.class);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
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
                Intent intent = new Intent(getApplicationContext(), Settings.class);
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
