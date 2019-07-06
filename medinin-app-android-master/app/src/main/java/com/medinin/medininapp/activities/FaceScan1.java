package com.medinin.medininapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.medinin.medininapp.R;

public class FaceScan1 extends AppCompatActivity {

    private String patient_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_scan1);
        Intent intent = getIntent();
        patient_id = intent.getStringExtra("patient_id");

        ImageView editTabLink = (ImageView) findViewById(R.id.editTabLink);
        editTabLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                intent.putExtra("patient_id", patient_id);
                startActivity(intent);
            }
        });

        TextView doneBtn = (TextView) findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllPatients.class);
                startActivity(intent);
            }
        });
    }
}
