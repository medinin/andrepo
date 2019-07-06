package com.medinin.medininapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import com.medinin.medininapp.R;

import java.util.ArrayList;
import java.util.List;

import ir.apend.slider.model.Slide;
import ir.apend.slider.ui.Slider;

public class slideshow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);
        Slider slider = findViewById(R.id.slider);

        //create list of slides
        List<Slide> slideList = new ArrayList<>();
        slideList.add(new Slide(0, "http://cssslider.com/sliders/demo-20/data1/images/picjumbo.com_img_4635.jpg", getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
        slideList.add(new Slide(1, "http://cssslider.com/sliders/demo-12/data1/images/picjumbo.com_hnck1995.jpg", getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
        slideList.add(new Slide(2, "http://cssslider.com/sliders/demo-19/data1/images/picjumbo.com_hnck1588.jpg", getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
        slideList.add(new Slide(3, "http://wowslider.com/sliders/demo-18/data1/images/shanghai.jpg", 0));

        //handle slider click listener
        slider.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //do what you want
            }
        });

        //add slides to slider
        slider.addSlides(slideList);
    }
}
