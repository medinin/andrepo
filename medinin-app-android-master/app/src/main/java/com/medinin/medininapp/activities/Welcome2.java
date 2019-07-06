package com.medinin.medininapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medinin.medininapp.R;
import com.medinin.medininapp.helpers.CustomSlider;

import java.util.ArrayList;


public class Welcome2 extends AppCompatActivity {

    private PrefManager prefManager;
    private Button btnSkip, btnNext;
    private LayoutInflater layoutInflater;
    private ArrayList<ViewGroup> viewGroup;
    private TextView skipTxtBtn, nextTxtBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
        }
        setContentView(R.layout.activity_welcome2);

        layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View welcome_slide1 = layoutInflater.inflate(R.layout.welcome_slide1, null);
        View welcome_slide2 = layoutInflater.inflate(R.layout.welcome_slide2, null);
        View welcome_slide3 = layoutInflater.inflate(R.layout.welcome_slide3, null);

        final ViewPager view_pager = (ViewPager) findViewById(R.id.view_pager);
        LinearLayout layoutDots = (LinearLayout) findViewById(R.id.layoutDots);
        skipTxtBtn = (TextView) findViewById(R.id.skipTxtBtn);
        nextTxtBtn = (TextView) findViewById(R.id.nextTxtBtn);

        CustomSlider customSlider = new CustomSlider();

        viewGroup = new ArrayList<ViewGroup>();
        viewGroup.add((ViewGroup) welcome_slide1);
        viewGroup.add((ViewGroup) welcome_slide2);
        viewGroup.add((ViewGroup) welcome_slide3);

        customSlider.set(this, view_pager, layoutDots, viewGroup);
        view_pager.addOnPageChangeListener(viewPagerPageChangeListener);

        skipTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        nextTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = view_pager.getCurrentItem() + 1;
                if (current < viewGroup.size()) {
                    view_pager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            // changing the next button text 'NEXT' / 'DONE'
            if (position == viewGroup.size() - 1) {
                nextTxtBtn.setText("Done");
                skipTxtBtn.setVisibility(View.GONE);
            } else {
                skipTxtBtn.setVisibility(View.VISIBLE);
                nextTxtBtn.setText("Next");
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

    };

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        Intent dashboard = new Intent(getApplicationContext(), AllPatients.class);
        finish();
        startActivity(dashboard);
    }
}
