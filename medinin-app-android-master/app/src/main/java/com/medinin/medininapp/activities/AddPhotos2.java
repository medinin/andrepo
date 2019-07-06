package com.medinin.medininapp.activities;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medinin.medininapp.R;

public class AddPhotos2 extends AppCompatActivity {

    LinearLayout _galleryLink, _cameraLink;
    android.support.v7.widget.GridLayout _gridLayoutGalleryPhotos, _gridLayoutCameraPhotos;

    private View.OnClickListener mCorkyListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.galleryLink:
                    _gridLayoutCameraPhotos.setVisibility(View.GONE);
                    _gridLayoutGalleryPhotos.setVisibility(View.VISIBLE);

                    break;

                case R.id.cameraLink:
                    _gridLayoutGalleryPhotos.setVisibility(View.GONE);
                    _gridLayoutCameraPhotos.setVisibility(View.VISIBLE);

                    break;

                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos2);


        _galleryLink = (LinearLayout) findViewById(R.id.galleryLink);
        _cameraLink = (LinearLayout) findViewById(R.id.cameraLink);

        _gridLayoutGalleryPhotos = (android.support.v7.widget.GridLayout) findViewById(R.id.gridLayoutGalleryPhotos);
        _gridLayoutCameraPhotos = (android.support.v7.widget.GridLayout) findViewById(R.id.gridLayoutCameraPhotos);


        _galleryLink.setOnClickListener(mCorkyListener);
        _cameraLink.setOnClickListener(mCorkyListener);
    }


}
