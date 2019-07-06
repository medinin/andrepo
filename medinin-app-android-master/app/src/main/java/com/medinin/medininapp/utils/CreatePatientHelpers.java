package com.medinin.medininapp.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.medinin.medininapp.R;
import com.medinin.medininapp.activities.AccountPremium;
import com.medinin.medininapp.activities.AppointmentList;

public class CreatePatientHelpers {

    public static void initMenu(final Activity activity) {
        RelativeLayout llyt_appointment = activity.findViewById(R.id.llyt_appointment);
        RelativeLayout llyt_setting = activity.findViewById(R.id.llyt_setting);

        llyt_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AppointmentList.class);
                activity.finish();
                activity.overridePendingTransition(0,0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
            }
        });

        llyt_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AccountPremium.class);
                activity.finish();
                activity.overridePendingTransition(0,0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
            }
        });
    }
}
