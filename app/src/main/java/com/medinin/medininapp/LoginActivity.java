package com.medinin.medininapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.medinin.medininapp.login.datamodel.fragments.LoginFragment;
import com.medinin.medininapp.login.datamodel.fragments.SignUpFragment;
import com.medinin.medininapp.utils.DLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements GestureDetector.OnGestureListener {

    @BindView(R.id.container)
    FrameLayout fragmentContainer;

    @BindView(R.id.llLogin)
    LinearLayout llLogin;

    @BindView(R.id.llSignup)
    LinearLayout llSignUp;

    @BindView(R.id.loginTabLink)
    TextView tvLoginTab;

    @BindView(R.id.loginTabLinkBr)
    TextView tvLoginTabBar;

    @BindView(R.id.signUPTabLink)
    TextView tvSingupTab;

    @BindView(R.id.signUPTabLinkBr)
    TextView tvSingupTabBar;





    private static final float SWIPE_THRESHOLD = 100 ;
    private static final float SWIPE_VELOCITY_THRESHOLD = 100 ;
    private static final String TAG = "LoginActivity";
    private GestureDetector gestureDetector;
    private Context mContext;

    private int index;
    private int currentTabIndex;
    private Fragment[] fragments;
    private LoginFragment loginFragment;
    private SignUpFragment signUpFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        gestureDetector = new GestureDetector(this);
        mContext = getApplicationContext();

        loginFragment = new LoginFragment();
        signUpFragment = new SignUpFragment();

        fragments = new Fragment[]{
                loginFragment, signUpFragment};

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, loginFragment)
                .add(R.id.container, signUpFragment)

                .hide(signUpFragment)
                .show(loginFragment).commit();

//        addFragment(android.R.id.content,
//                new LoginFragment(),
//                loginFragment.getTag());

//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.container, new LoginFragment());
//        ft.commit();



    }

    @OnClick({R.id.llLogin, R.id.llSignup})
    void onClickListener(View v){
        switch (v.getId()){

            case R.id.llLogin:
                index = 0;
                tvLoginTab.setTextColor(ContextCompat.getColor(mContext, R.color.colorPurple));
                tvLoginTabBar.setVisibility(View.VISIBLE);
                tvSingupTab.setTextColor(ContextCompat.getColor(mContext, R.color.colorTab));
                tvSingupTabBar.setVisibility(View.INVISIBLE);

                break;


            case R.id.llSignup:
                index = 1;
                tvLoginTab.setTextColor(ContextCompat.getColor(mContext, R.color.colorTab));
                tvLoginTabBar.setVisibility(View.INVISIBLE);
                tvSingupTab.setTextColor(ContextCompat.getColor(mContext, R.color.colorPurple));
                tvSingupTabBar.setVisibility(View.VISIBLE);
                break;


            default:
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        currentTabIndex = index;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        boolean result = false;
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();

        if(Math.abs(diffX) > Math.abs(diffY)) {
            if(Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if(diffX > 0){
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
                result = true;
            }
        } else {
            if(Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if(diffY > 0) {
                    onSwipeBottom();
                } else {
                    onSwipeTop();
                }
                result = true;
            }
        }
        return result;
    }


    private void onSwipeBottom() {
        Log.d(TAG,"Swipe Bottom");
        finish();
    }

    private void onSwipeRight() {
        Log.d(TAG,"Swipe Right");
    }

    private void onSwipeLeft() {
        Log.d(TAG,"Swipe Left");
    }

    private void onSwipeTop() {
        Log.d(TAG,"Swipe Top");
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();
        DLog.d(TAG, "OnBack Press Count: "+count);

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }
}
