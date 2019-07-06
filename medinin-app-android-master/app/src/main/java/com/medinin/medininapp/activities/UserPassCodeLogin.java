package com.medinin.medininapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.medinin.medininapp.R;

import java.lang.reflect.Method;

public class UserPassCodeLogin extends AppCompatActivity {

    LinearLayout nextBtnWrap;

    private RequestQueue mRequestQueue;
    private String med_user_id, med_user_token;
    private long back_pressed = 0;
    BottomSheetDialog dialog;
    int realWidth;
    int realHeight;
    private TextView resetBtn;
    private FrameLayout btnWrap;
    private ProgressDialog progress;
    private RelativeLayout resetpasscodeSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_passcode_login);

        mRequestQueue = Volley.newRequestQueue(this);



        //reset-passcode
        Display display = this.getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 17) {
            //new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            realWidth = realMetrics.widthPixels;
            realHeight = realMetrics.heightPixels;

        } else if (Build.VERSION.SDK_INT >= 14) {
            //reflection for this weird in-between time
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                //this may not be 100% accurate, but it's all we've got
                realWidth = display.getWidth();
                realHeight = display.getHeight();
                Log.e("Display Info", "Couldn't use reflection to get the real display metrics.");
            }
        } else {
            //This should be close, as lower API devices should not have window navigation bars
            realWidth = display.getWidth();
            realHeight = display.getHeight();
        }




        nextBtnWrap = (LinearLayout) findViewById(R.id.nextBtnWrap);

        nextBtnWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserMobileLogin.class);
                startActivity(intent);
            }
        });

        //tab links
        TextView signUPTabLink = (TextView) findViewById(R.id.signUPTabLink);
        signUPTabLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserSignup.class);
                startActivity(intent);
            }
        });
    }

    public void showBottomSheetDialog(View view) {
        View loginPasscodeView = getLayoutInflater().inflate(R.layout.reset_passcode_dialog, null);
        dialog = new BottomSheetDialog(this) {
            @Override
            public void onBackPressed() {
                if (back_pressed + 2000 > System.currentTimeMillis()) {
                    dialog.hide();
                } else {
                    Toast.makeText(getBaseContext(), "Press once again to exit", Toast.LENGTH_SHORT).show();
                    back_pressed = System.currentTimeMillis();
                }
            }
        };
        dialog.setContentView(loginPasscodeView);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) loginPasscodeView.getParent());
        mBehavior.setPeekHeight(realHeight);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dialog.hide();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        InitResetEvents(loginPasscodeView);
        dialog.show();
    }

    private void InitResetEvents(View view) {
        //current page events
        mRequestQueue = Volley.newRequestQueue(UserPassCodeLogin.this);
        resetBtn = view.findViewById(R.id.resetBtn);
        resetpasscodeSec = view.findViewById(R.id.resetpasscodeSec);


    }
}
