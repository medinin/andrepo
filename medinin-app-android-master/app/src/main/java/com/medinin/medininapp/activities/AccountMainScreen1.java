package com.medinin.medininapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.medinin.medininapp.R;

public class AccountMainScreen1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_main_screen1);

        initMenu();

        LinearLayout profile_card = (LinearLayout) findViewById(R.id.profile_card);
        profile_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), DocBasicProfile.class);
                startActivity(intent);
            }
        });

        LinearLayout education_card = (LinearLayout) findViewById(R.id.education_card);
        education_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), DocEducation.class);
                startActivity(intent);
            }
        });


        LinearLayout clinic_card = (LinearLayout) findViewById(R.id.clinic_card);
        clinic_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), MyClinic.class);
                startActivity(intent);
            }
        });


        LinearLayout conditions_card = (LinearLayout) findViewById(R.id.conditions_card);
        conditions_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), Conditions1.class);
                startActivity(intent);
            }
        });

        LinearLayout scan_card = (LinearLayout) findViewById(R.id.scan_card);
        scan_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), AiScan1.class);
                startActivity(intent);
            }
        });


        LinearLayout campaign_card = (LinearLayout) findViewById(R.id.campaign_card);
        campaign_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), Campaign.class);
                startActivity(intent);
            }
        });

        LinearLayout chat_card = (LinearLayout) findViewById(R.id.chat_card);
        chat_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), Chat.class);
                startActivity(intent);
            }
        });

        LinearLayout models_card = (LinearLayout) findViewById(R.id.models_card);
        models_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), Anatomy.class);
                startActivity(intent);
            }
        });



        LinearLayout settings_card = (LinearLayout) findViewById(R.id.settings_card);
        settings_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });

        LinearLayout forms_card = (LinearLayout) findViewById(R.id.forms_card);
        forms_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), Forms.class);
                startActivity(intent);
            }
        });

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

}
