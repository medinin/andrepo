package com.medinin.medininapp.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


import com.medinin.medininapp.EulaActivity;
import com.medinin.medininapp.LoginActivity;
import com.medinin.medininapp.Myapp;
import com.medinin.medininapp.R;
import com.medinin.medininapp.utils.PrefManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import static com.medinin.medininapp.utils.UIUtility.ANIMATING_TIME;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    private static PrefManager mLocalSession;

    @BindView(R.id.logo_img)
    ImageView bgLogo;

    @BindView(R.id.version)
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocalSession = new PrefManager(getApplicationContext());
        if (Myapp.isTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mLocalSession.saveAccessToken("hello");

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
       // tvVersion.setText("Version: "+Myapp.getAppVersion());
       // startAnimation();
    }


    private void startAnimation() {

        final Animation zoomAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.zoom);
        bgLogo.startAnimation(zoomAnimation);
        zoomAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Handler mHandler = new Handler(getMainLooper());
                Runnable mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        startFunctions();
                    }
                };
                mHandler.postDelayed(mRunnable, ANIMATING_TIME);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void startFunctions() {
        if(mLocalSession.getTCstatus()==true){
            //TODO: change login activty later
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }else{
            startActivity(new Intent(this, EulaActivity.class));
            finish();
        }
    }
}
