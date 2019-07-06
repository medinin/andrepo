package com.medinin.medininapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.medinin.medininapp.R;
import com.medinin.medininapp.config.API_URL;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONException;
import org.json.JSONObject;

public class AppointmentBookingLoader extends AppCompatActivity {

    private static final String TAG = PatientHistory.class.getSimpleName();

    //Common variables
    private String patient_id, med_user_id, med_user_token;
    private String appointment_id, patient_name, appointment_status, appointmentDate = "", appointmentTime = "";
    private ProgressDialog progress;
    private RequestQueue mRequestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_booking_loader);
        final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);
        rippleBackground.startRippleAnimation();


        progress = new ProgressDialog(this);
        progress.setTitle("Loading...");

        mRequestQueue = Volley.newRequestQueue(this);


        Intent intent = getIntent();
        patient_name = intent.getStringExtra("pat_name");
        appointmentDate = intent.getStringExtra("appointmentDate_str");
        appointmentTime = intent.getStringExtra("appointmentTime_str");


        TextView patient_name_txt = (TextView) findViewById(R.id.patient_name_txt);
        TextView appointmentDate_str = (TextView) findViewById(R.id.appointmentDate_str);
        TextView appointmentTime_str = (TextView) findViewById(R.id.appointmentTime_str);

        patient_name_txt.setText(patient_name);
        appointmentDate_str.setText(appointmentDate);
        appointmentTime_str.setText(appointmentTime);


    }


}
