package com.medinin.medininapp.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medinin.medininapp.R;
import com.medinin.medininapp.activities.Conditions2;
import com.medinin.medininapp.config.API_URL;
import com.medinin.medininapp.models.ImageSlide;
import com.medinin.medininapp.utils.SlideshowDialogFragment;

import org.json.JSONObject;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class CustomSlider extends AppCompatActivity {
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private ArrayList<ViewGroup> viewGroup;
    private String presc_id;
    private Context context;

    public void set(Context _ctx, ViewPager _viewPager, LinearLayout _dotsLayout, ArrayList<ViewGroup> _viewGroup) {
        context = _ctx;
        viewPager = _viewPager;
        dotsLayout = _dotsLayout;
        viewGroup = _viewGroup;
        //wrapperView = _wrapperView;

        initSlider();
    }

    private void initSlider() {
        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[viewGroup.size()];

//        int[] colorsActive = context.getResources().getIntArray(R.array.array_dot_active);
//        int[] colorsInactive = context.getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(context);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(28);
            dots[i].setTextColor(ContextCompat.getColor(context, R.color.dot_dark_screen1));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(ContextCompat.getColor(context, R.color.dot_light_screen1));
        //dots[currentPage].setTextSize(32);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }


    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == viewGroup.size() - 1) {
                //last page. make button text to GOT IT
                //btnNext.setText(getString(R.string.start));
                //btnSkip.setVisibility(View.GONE);
            } else {
                //still pages are left
                //btnNext.setText(getString(R.string.next));
                //btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = context.getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void addItem(ViewGroup v) {
        viewGroup.add(viewGroup.size() - 1, v);
    }

    private class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View slideView = viewGroup.get(position);
            if (slideView.getParent() != null) {
                ((ViewGroup) slideView.getParent()).removeView(slideView);
            }
            container.addView(slideView);
            return slideView;
        }

        @Override
        public int getCount() {
            return viewGroup.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

    }
}
