package com.medinin.medininapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.medinin.medininapp.R;

public class UserSignup extends AppCompatActivity {

    private RadioButton docRadioBtn, patientRadioBtn, hospitalRadioBtn, clinicRadioBtn, labRadioBtn, pharmacyRadioBtn;
    private TextView nextBtntxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        //tab links
        TextView loginTabLink = (TextView) findViewById(R.id.loginTabLink);
        loginTabLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserPassCodeLogin.class);
                startActivity(intent);
            }
        });

        //current page events
        docRadioBtn = (RadioButton) findViewById(R.id.docRadioBtn);
        patientRadioBtn = (RadioButton) findViewById(R.id.patientRadioBtn);
        hospitalRadioBtn = (RadioButton) findViewById(R.id.hospitalRadioBtn);
        clinicRadioBtn = (RadioButton) findViewById(R.id.clinicRadioBtn);
        labRadioBtn = (RadioButton) findViewById(R.id.labRadioBtn);
        pharmacyRadioBtn = (RadioButton) findViewById(R.id.pharmacyRadioBtn);
        nextBtntxt = (TextView) findViewById(R.id.nextBtntxt);


        nextBtntxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserSignUpMobile.class);
                startActivity(intent);
            }
        });

        patientRadioBtn.setEnabled(false);
        hospitalRadioBtn.setEnabled(false);
        clinicRadioBtn.setEnabled(false);
        labRadioBtn.setEnabled(false);
        pharmacyRadioBtn.setEnabled(false);
    }

    private void uncheckAllRadioButton() {
        docRadioBtn.setChecked(false);
        patientRadioBtn.setChecked(false);
        hospitalRadioBtn.setChecked(false);
        clinicRadioBtn.setChecked(false);
        labRadioBtn.setChecked(false);
        pharmacyRadioBtn.setChecked(false);
    }

    public void onSignUpOptionSelect(View view) {
        RadioButton radioBtn = (RadioButton) view.findViewWithTag("RadioButton");
        uncheckAllRadioButton();
        radioBtn.setChecked(true);
    }
}
