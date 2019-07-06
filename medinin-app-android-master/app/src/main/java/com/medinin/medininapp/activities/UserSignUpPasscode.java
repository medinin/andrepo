package com.medinin.medininapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.medinin.medininapp.R;

public class UserSignUpPasscode extends AppCompatActivity {

    private EditText passCode1, passCode2, passCode3, passCode4,
            confirmPassCode1, confirmPassCode2, confirmPassCode3, confirmPassCode4;

    private TextView nextBtn, skipBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up_passcode);

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
        nextBtn = (TextView) findViewById(R.id.nextBtn);
        skipBtn = (TextView) findViewById(R.id.skipBtn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllPatients.class);
                startActivity(intent);
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllPatients.class);
                startActivity(intent);
            }
        });
    }
}
