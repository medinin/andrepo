package com.medinin.medininapp.login.datamodel.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;



import com.medinin.medininapp.MainActivity;
import com.medinin.medininapp.R;
import com.medinin.medininapp.activities.ClinicDashboardActivity;
import com.medinin.medininapp.activities.HospitalDashboardActivity;
import com.medinin.medininapp.activities.LabDashboardActivity;
import com.medinin.medininapp.activities.PatientDashboardActivity;
import com.medinin.medininapp.activities.PharmacyDashboardActivity;

import com.medinin.medininapp.docmod.Dashboard;
import com.medinin.medininapp.interfaces.TaskCompleteListener;
import com.medinin.medininapp.utils.PrefManager;
import com.medinin.medininapp.utils.UIUtility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserLoginRole extends Fragment implements TaskCompleteListener {

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

    @BindView(R.id.load_btn_wrap)
    RelativeLayout rlLoadBtn;

    private int selectedPos ;
    private int prevselectedPos = -1 ;

    private PrefManager prefManager = new PrefManager();

    private RadioButton[] radioButtons;

    private float alpha_value = 0.4f;

    public UserLoginRole() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_passcode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        radioButtons = new RadioButton[]{rbdocBtn,rbPatBtn,rbHospBtn,rbClinicBtn,rbLabBtn,rbPharmBtn};

        if(!prefManager.getDocModule()) {
            clDocSelect.setEnabled(false);
            clDocSelect.setAlpha(alpha_value);
        }

        if(!prefManager.getPatientModule()) {
            clPatSelect.setEnabled(false);
            clPatSelect.setAlpha(alpha_value);
        }

        if(!prefManager.getHospitalModule()) {
            clHospSelect.setEnabled(false);
            clHospSelect.setAlpha(alpha_value);
        }

        if(!prefManager.getClinicModule()) {
            clClinicSelect.setEnabled(false);
            clClinicSelect.setAlpha(alpha_value);
        }

        if(!prefManager.getLabMdule()) {
            clLabSelect.setEnabled(false);
            clLabSelect.setAlpha(alpha_value);
        }

        if(!prefManager.getPharmModule()) {
            clPharmSelect.setEnabled(false);
            clPharmSelect.setAlpha(alpha_value);
        }

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


    @OnClick({R.id.doc_select, R.id.patient_select, R.id.hospital_select, R.id.clinic_select, R.id.lab_select, R.id.pharmacy_select, R.id.load_btn_wrap})
    void OnClickListener(View v){
        switch (v.getId()){
            case R.id.doc_select:
                selectedPos =0;
                break;

            case R.id.patient_select:
                selectedPos = 1;
                break;

            case R.id.hospital_select:
                selectedPos = 2;
                break;

            case R.id.clinic_select:
                selectedPos = 3;
                break;

            case R.id.lab_select:
                selectedPos = 4;
                break;

            case R.id.pharmacy_select:
                selectedPos = 5;

            case R.id.load_btn_wrap:
                if(prevselectedPos == -1){
                    UIUtility.showToastMsg_short(getActivity(), "Select the dashboard you want to load");
                    return;
                }

                if( selectedPos == 10){
                    UIUtility.showToastMsg_short(getActivity(), "Select the dashboard you want to load");
                    return;
                }
                loadSelelctedDashBoard();
                break;

            default:
                selectedPos = 10;
                break;
        }
        updateIconColor(selectedPos);
    }

    private void loadSelelctedDashBoard() {
        Intent intent = new Intent();

        switch (selectedPos){
            case 0:
                UIUtility.showToastMsg_short(getActivity(), "Loading .. Doctor dashboard");
                intent = new Intent(getActivity(), Dashboard.class);
                break;

            case 1:
                UIUtility.showToastMsg_short(getActivity(), "Loading .. Patient dashboard");
                intent = new Intent(getActivity(), PatientDashboardActivity.class);
                break;

            case 2:
                UIUtility.showToastMsg_short(getActivity(), "Loading .. Hospital dashboard");
                intent = new Intent(getActivity(), HospitalDashboardActivity.class);
                break;

            case 3:
                UIUtility.showToastMsg_short(getActivity(), "Loading .. Clinic dashboard");
                intent = new Intent(getActivity(), ClinicDashboardActivity.class);
                break;

            case 4:
                UIUtility.showToastMsg_short(getActivity(), "Loading .. Lab dashboard");
                intent = new Intent(getActivity(), LabDashboardActivity.class);
                break;

            case 5:
                UIUtility.showToastMsg_short(getActivity(), "Loading .. Pharmacy dashboard");
                intent = new Intent(getActivity(), PharmacyDashboardActivity.class);
                break;

            default:
                break;
        }

        startActivity(intent);
        getActivity().finish();
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
