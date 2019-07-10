package com.medinin.medininapp.login.datamodel.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;


import com.medinin.medininapp.BaseFragment;
import com.medinin.medininapp.R;
import com.medinin.medininapp.interfaces.TaskCompleteListener;
import com.medinin.medininapp.utils.PrefManager;
import com.medinin.medininapp.utils.UIUtility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**

 */
public class SignUpFragment extends BaseFragment implements TaskCompleteListener {

    @BindView(R.id.doc_select)
    ConstraintLayout clDocSelect;

    @BindView(R.id.patient_select)
    ConstraintLayout clPatSelect;

    @BindView(R.id.hospital_select)
    ConstraintLayout clHospSelect;

    @BindView(R.id.clinic_select)
    ConstraintLayout clClinicSelect;

    @BindView(R.id.lab_select)
    ConstraintLayout clLabSelect;

    @BindView(R.id.pharmacy_select)
    ConstraintLayout clPharmSelect;


    @BindView(R.id.docRadioBtn)
    RadioButton rbdocBtn;

    @BindView(R.id.patientRadioBtn)
    RadioButton rbPatBtn;

    @BindView(R.id.hospitalRadioBtn)
    RadioButton rbHospBtn;

    @BindView(R.id.clinicRadioBtn)
    RadioButton rbClinicBtn;

    @BindView(R.id.labRadioBtn)
    RadioButton rbLabBtn;

    @BindView(R.id.pharmacyRadioBtn)
    RadioButton rbPharmBtn;

    @BindView(R.id.next_btn_wrap)
    RelativeLayout rlNxtBtn;

    private int selectedPos ;
    private int prevselectedPos = -1 ;

    private PrefManager prefManager = new PrefManager();

    private RadioButton[] radioButtons;
    private UserDetails userDetails;
    private LoginFragment loginFragment;




    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);


        userDetails = new UserDetails();

        loginFragment = new LoginFragment();

        radioButtons = new RadioButton[]{rbdocBtn,rbPatBtn,rbHospBtn,rbClinicBtn,rbLabBtn,rbPharmBtn};

        if(Build.VERSION.SDK_INT>=21) {
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_enabled}, //disabled
                            new int[]{android.R.attr.state_enabled} //enabled
                    },
                    new int[] {
                            Color.BLACK //disabled
                            , Color.GREEN //enabled
                    }
            );
            for(int i=0;i<radioButtons.length;i++){
                radioButtons[i].setButtonTintList(colorStateList);//set the color tint list
                radioButtons[i].invalidate(); //could not be necessary
            }
        }
    }


    @OnClick({R.id.doc_select, R.id.patient_select, R.id.hospital_select, R.id.clinic_select, R.id.lab_select, R.id.pharmacy_select})
    void OnClickListener(View v){
        switch (v.getId()){
            case R.id.doc_select:
                selectedPos =0;
                prefManager.setMySignUpRole("is_doc");
                break;

            case R.id.patient_select:
                prefManager.setMySignUpRole("is_patient");
                selectedPos = 1;
                break;

            case R.id.hospital_select:
                prefManager.setMySignUpRole("is_hospital");
                selectedPos = 2;
                break;

            default:
                selectedPos = 10;
                break;
        }
        updateIconColor(selectedPos);
    }

    @OnClick(R.id.next_btn_wrap)
    void onClick(View v){
        updateFragments();
    }

    private void updateFragments() {
        if(prevselectedPos == -1){
            UIUtility.showToastMsg_short(getActivity(), "Select your role");
            return;
        }

        if( selectedPos == 10){
            UIUtility.showToastMsg_short(getActivity(), "Select your role");
            return;
        }


        FragmentTransaction trx = getActivity().getSupportFragmentManager().beginTransaction();
        trx.hide(SignUpFragment.this);
        //TODO:  change the condition
        if (1==1) {
            trx.add(R.id.container, loginFragment);
        }
        trx.addToBackStack(null);
        trx.show(loginFragment).commit();






    }

    private void updateIconColor(int selectedPos) {
        if(prevselectedPos == selectedPos)
            return;

        if(selectedPos == 10){
            UIUtility.showToastMsg_short(getActivity(),"Coming Soon !!");
            return;
        }

        for(int i=0;i<radioButtons.length;i++){
            if(selectedPos == i){
                radioButtons[i].setChecked(true);
            }else {
                radioButtons[i].setChecked(false);
            }
        }
        prevselectedPos = selectedPos;
    }

    @Override
    public void onTaskCompleted(Context context, Intent intent) {

    }
}
