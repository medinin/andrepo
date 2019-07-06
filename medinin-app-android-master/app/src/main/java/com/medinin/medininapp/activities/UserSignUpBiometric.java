package com.medinin.medininapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.medinin.medininapp.R;

public class UserSignUpBiometric extends AppCompatActivity {

    TextView nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up_biometric);

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

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserSignUpPasscode.class);
                startActivity(intent);
            }
        });

    }
}
