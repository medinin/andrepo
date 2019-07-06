package com.medinin.medininapp.activities;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import com.medinin.medininapp.R;

public class NativeShare extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_share);


        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText("I'm sharing!")
                .startChooser();
    }


}
