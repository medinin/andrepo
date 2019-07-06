package com.medinin.medininapp.activities;

import android.media.Image;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.medinin.medininapp.R;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.medinin.medininapp.config.API_URL;
import com.medinin.medininapp.helpers.DownLoadImageTask;
import com.medinin.medininapp.models.ImageSlide;
import com.medinin.medininapp.utils.SlideshowDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.text.Html;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class Conditions2 extends AppCompatActivity {
    private static final String TAG = Conditions1.class.getSimpleName();
    //common variable
    private String condition_id, med_user_id, med_user_token;
    private ProgressDialog progress;
    private RequestQueue mRequestQueue;

    //View pager
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private JSONObject condObj;
    private LayoutInflater layoutInflater;
    private ViewGroup[] viewGroup;
    private ArrayList<ImageSlide> galleryImages;
    private Boolean isprogressBarStatus;

    private TextView conditionName, moreInfoLink;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditions2);

        initMenu();

        Intent intent = getIntent();
        condition_id = intent.getStringExtra("condition_id");


        progress = new ProgressDialog(this);
        progress.setTitle("Loading...");
        mRequestQueue = Volley.newRequestQueue(this);
        layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));

        LinearLayout backBtn = (LinearLayout) findViewById(R.id.back_sec1);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                launchConditionScreen();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        conditionName = (TextView) findViewById(R.id.conditionName);
        moreInfoLink = (TextView) findViewById(R.id.moreInfoLink);

        moreInfoLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!moreInfoLink.getTag().toString().isEmpty()) {
                    Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(moreInfoLink.getTag().toString()));
                    startActivity(viewIntent);
                }
            }
        });

        layouts = new int[]{
                R.layout.condition_common_slide,
                R.layout.condition_common_slide,
                R.layout.condition_common_slide,
                R.layout.condition_common_slide,
                R.layout.condition_common_slide,
                R.layout.condition_common_slide,
                R.layout.condition_common_slide,
                R.layout.condition_common_slide,
        };

        viewGroup = new ViewGroup[layouts.length];

        galleryImages = new ArrayList<>();

        loadConditions();
    }

    private void launchConditionScreen() {
        Intent intent = new Intent(getApplicationContext(), Conditions1.class);
        finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_left);
        intent.putExtra("ProgressBar", true);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            launchConditionScreen();
        }
        return super.onKeyDown(keyCode, event);
    }

    public String removeNull(String _data) {
        return _data.equals("null") ? "" : _data;
    }

    private void showBigImgPopup(View v) {
        View view = (View) findViewById(v.getId());
        int _position = parseInt(view.getTag().toString());

        Bundle bundle = new Bundle();
        bundle.putSerializable("images", galleryImages); //loadImages();
        bundle.putInt("position", _position);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(ft, "slideshow");
    }

    private void loadConditions() {
        progress.show();
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("limit", 10);
            _obj.put("page", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.Conditions + "/" + condition_id, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        JSONObject condObj = (JSONObject) response.getJSONObject("data");
                        conditionName.setText(removeNull(condObj.getString("name")));
                        if (!condObj.isNull("web_links") && !condObj.getString("web_links").isEmpty() && !condObj.getString("web_links").equals("null")) {
                            moreInfoLink.setTag(removeNull(condObj.getString("web_links")));
                        } else {
                            moreInfoLink.setVisibility(View.GONE);
                        }
                        feedList(condObj);
                    } else {
                        Toast.makeText(Conditions2.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        progress.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progress.dismiss();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progress.dismiss();
            }
        });

        // Adding request to volley request queue
        jsonReq.setShouldCache(false);
        mRequestQueue.add(jsonReq);
    }

    private void feedList(JSONObject condObj) {
        try {
            for (int i = 0; i < layouts.length; i++) {
                View preview = layoutInflater.inflate(layouts[i], null);

                TextView titleTxt = preview.findViewById(R.id.titleTxt);
                TextView descTxt = preview.findViewById(R.id.descTxt);
                LinearLayout condListWrap = preview.findViewById(R.id.condListWrap);

                if (i == 0) {
                    titleTxt.setText("Overview");
                    descTxt.setText(removeNull(condObj.getString("desc")));
                } else if (i == 1) {
                    titleTxt.setText("Symptoms");
                    descTxt.setVisibility(View.GONE);
                    condListWrap.setVisibility(View.VISIBLE);
                    JSONArray condition_symptoms = (JSONArray) condObj.getJSONArray("condition_symptoms");
                    for (int k = 0; k < condition_symptoms.length(); k++) {
                        JSONObject feedObj = (JSONObject) condition_symptoms.get(k);
                        JSONObject symptomObj = (JSONObject) feedObj.getJSONObject("symptom");
                        View itemView = layoutInflater.inflate(R.layout.all_conditions_item, null);

                        TextView itemTxt = (TextView) itemView.findViewById(R.id.itemTxt);
                        itemTxt.setText(removeNull(symptomObj.getString("name")));

                        condListWrap.addView(itemView);
                    }
                } else if (i == 2) {
                    titleTxt.setText("Risks");
                    descTxt.setText(removeNull(condObj.getString("risks")));
                } else if (i == 3) {
                    titleTxt.setText("Treatment");
                    descTxt.setText(removeNull(condObj.getString("treatment")));
                } else if (i == 4) {
                    titleTxt.setText("Diagnosis");
                    descTxt.setText(removeNull(condObj.getString("diagnosis")));
                } else if (i == 5) {
                    titleTxt.setText("Preventions/Causes/Triggers");
                    descTxt.setText(removeNull(condObj.getString("preventions")));
                } else if (i == 6) {
                    titleTxt.setText("Problems");
                    descTxt.setVisibility(View.GONE);
                    condListWrap.setVisibility(View.VISIBLE);
                    JSONArray condition_problem = (JSONArray) condObj.getJSONArray("condition_problems");
                    for (int k = 0; k < condition_problem.length(); k++) {
                        JSONObject feedObj = (JSONObject) condition_problem.get(k);
                        JSONObject problemObj = (JSONObject) feedObj.getJSONObject("problem");
                        View itemView = layoutInflater.inflate(R.layout.all_conditions_item, null);

                        TextView itemTxt = (TextView) itemView.findViewById(R.id.itemTxt);
                        itemTxt.setText(removeNull(problemObj.getString("name")));

                        condListWrap.addView(itemView);
                    }
                } else if (i == 7) {
                    titleTxt.setText("Images");
                    descTxt.setVisibility(View.GONE);
                    condListWrap.setVisibility(View.VISIBLE);
                    JSONArray conditions_files = (JSONArray) condObj.getJSONArray("conditions_files");
                    for (int k = 0; k < conditions_files.length(); k++) {
                        JSONObject feedObj = (JSONObject) conditions_files.get(k);
                        View itemView = layoutInflater.inflate(R.layout.all_condition_files_row, null);

                        final ImageView condition_image = (ImageView) itemView.findViewById(R.id.condition_image);

                        String img_url = API_URL.ConditionGetImage + feedObj.getString("file");
                        new DownLoadImageTask(condition_image).execute(img_url);

                        ImageSlide image = new ImageSlide();
                        image.setName("New image");
                        image.setSmall(img_url);
                        image.setMedium(img_url);
                        image.setLarge(img_url);
                        galleryImages.add(image);

                        condition_image.setTag(k);
                        condition_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showBigImgPopup(condition_image);
                            }
                        });
                        condListWrap.addView(itemView);
                    }
                }

                viewGroup[i] = (ViewGroup) preview;
            }

            initSlider();

        } catch (JSONException e) {
            e.printStackTrace();
            progress.dismiss();
        }

    }

    private void initSlider() {
        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        progress.dismiss();
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(28);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
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
            if (position == layouts.length - 1) {
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
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View slideView = viewGroup[position];
            container.addView(slideView);
            return slideView;
        }

        @Override
        public int getCount() {
            return layouts.length;
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

    private void initMenu() {
        RelativeLayout llyt_home = (RelativeLayout) findViewById(R.id.llyt_home);
        RelativeLayout llyt_appointment = (RelativeLayout) findViewById(R.id.llyt_appointment);
        RelativeLayout llyt_setting = (RelativeLayout) findViewById(R.id.llyt_setting);

        View home_view = (View) findViewById(R.id.home_view);
        View home_appoitment = (View) findViewById(R.id.home_appoitment);
        View home_setting = (View) findViewById(R.id.home_setting);

        ImageView img_home = (ImageView) findViewById(R.id.img_home);
        ImageView img_appoint = (ImageView) findViewById(R.id.img_appoint);
        ImageView img_setting = (ImageView) findViewById(R.id.img_setting);

        home_view.setBackgroundColor(getResources().getColor(R.color.gcolor));
        home_appoitment.setBackgroundColor(getResources().getColor(R.color.gcolor));
        home_setting.setBackgroundColor(getResources().getColor(R.color.color_blue));


        img_home.setBackgroundResource(R.drawable.ic_patient);
        img_appoint.setBackgroundResource(R.drawable.ic_cal);
        img_setting.setBackgroundResource(R.drawable.ic_user_circle_blue);

        llyt_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        llyt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllPatients.class);
                startActivity(intent);
            }
        });
    }

}
