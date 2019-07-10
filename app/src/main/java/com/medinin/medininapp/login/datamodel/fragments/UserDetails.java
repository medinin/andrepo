package com.medinin.medininapp.login.datamodel.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;


import com.medinin.medininapp.BaseFragment;
import com.medinin.medininapp.R;

import com.medinin.medininapp.datamodels.AddDoctorRequest;

import com.medinin.medininapp.datamodels.AddDoctorResponse;
import com.medinin.medininapp.datamodels.SpecialityResponse;

import com.medinin.medininapp.interfaces.TaskCompleteListener;
import com.medinin.medininapp.services.NetworkOperationService;
import com.medinin.medininapp.utils.DataParser;
import com.medinin.medininapp.utils.MedininProgressDialog;
import com.medinin.medininapp.utils.NetworkConfig;
import com.medinin.medininapp.utils.PrefManager;
import com.medinin.medininapp.utils.SpinnerDropDown;
import com.medinin.medininapp.utils.StringWithTag;
import com.medinin.medininapp.utils.UIUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.medinin.medininapp.utils.UIUtility.dateFormatFun;
import static com.medinin.medininapp.utils.UIUtility.getAge;


public class UserDetails extends BaseFragment implements TaskCompleteListener {

    @BindView(R.id.signUpUserNameInput)
    EditText etUserName;

    @BindView(R.id.dob_sec)
    ConstraintLayout clDob;

    @BindView(R.id.signUpDobInput)
    EditText etDob;

    @BindView(R.id.signUpGenderSpinner)
    Spinner spinnerGender;

    @BindView(R.id.signUpdDepartmentSpinner)
    Spinner spinnerDepartment;


    @BindView(R.id.next_btn)
    RelativeLayout tvNxtBtn;


    @BindView(R.id.biometricLoginSwitchBtn)
    Switch switchBiometric;

    private String selectedDOB;
    private String age;

    private Context mContext;
    private NetworkOperationService mService;
    private PrefManager prefManager = new PrefManager();

    private UserLoginRole userLoginRole;






    List<String> gender = Arrays.asList("Select", "Male", "Female");

    public UserDetails() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        getSpecialityList();

    }

    private void getSpecialityList() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");


        Intent intent = new Intent(getActivity(), NetworkOperationService.class);
        intent.putExtra(NetworkConfig.API_URL, NetworkConfig.speciality);
        intent.putExtra(NetworkConfig.HEADER_MAP, headerMap);
        intent.putExtra(NetworkConfig.INPUT_BODY, "");
        mContext.startService(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setTaskCompleteListener(mContext, this);
        mContext = getActivity();
        mService = new NetworkOperationService();
        userLoginRole = new UserLoginRole();
        ArrayAdapter<String> adptr=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,gender);
        adptr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adptr);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @OnClick({R.id.next_btn, R.id.signUpDobInput})
    void onClickListenr(View view) {

        switch (view.getId()) {
            case R.id.next_btn:
                if(etUserName.getText().toString().length()<1){
                    UIUtility.showToastMsg_short(getActivity(), "Enter your name");
                    return;
                }

                if(etDob.getText().toString().length()< 1){
                    UIUtility.showToastMsg_short(getActivity(), "Select your Date of Birth");
                    return;
                }

                if(spinnerGender.getSelectedItemPosition() == 0){
                    UIUtility.showToastMsg_short(getActivity(), "Select Gender");
                    return;
                }

                if(spinnerDepartment.getSelectedItemPosition() == 0){
                    UIUtility.showToastMsg_short(getActivity(), "Select your Department");
                    return;
                }

                addDoctorRequest();



                break;

            case R.id.signUpDobInput:
                openDatePickerDialog();
                break;

        }

    }

    private void addDoctorRequest() {
        MedininProgressDialog.show(getActivity(), false);

        AddDoctorRequest addDoctorRequest = new AddDoctorRequest();
        addDoctorRequest.setDocId(prefManager.getApikey());
        addDoctorRequest.setGender(spinnerGender.getSelectedItem().toString());
        addDoctorRequest.setName(etUserName.toString());
        addDoctorRequest.setDob(selectedDOB);
        addDoctorRequest.setSpeciality(Integer.valueOf(SpinnerDropDown.getSpinnerItem(spinnerDepartment)));


        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");

        Intent intent = new Intent(mContext, NetworkOperationService.class);
        intent.putExtra(NetworkConfig.API_URL, NetworkConfig.addDoctor);
        intent.putExtra(NetworkConfig.HEADER_MAP, headerMap);
        intent.putExtra(NetworkConfig.INPUT_BODY, addDoctorRequest);
        mContext.startService(intent);
    }

    private void openDatePickerDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dob_select_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
        long now = System.currentTimeMillis() - 1000;
        datePicker.setMaxDate((long) (now - (6.6485e+11)));
        TextView doneBtn = (TextView) dialog.findViewById(R.id.doneBtn);


        doneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectedDOB =  (datePicker.getMonth() + 1)+ "/" +  datePicker.getDayOfMonth()+ "/" + datePicker.getYear();
                String _dateStr = dateFormatFun(datePicker.getDayOfMonth() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getYear());
                etDob.setText(_dateStr);
                age = getAge(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onTaskCompleted(Context context, Intent intent) {


        String requestType = intent.getStringExtra(NetworkConfig.REQUEST_TYPE);
        String apiUrl = intent.getStringExtra(NetworkConfig.API_URL);
        String responseString = intent.getStringExtra(NetworkConfig.RESPONSE_BODY);

        if(responseString != null && apiUrl.equalsIgnoreCase(NetworkConfig.speciality)){
            MedininProgressDialog.dismissDialog();
            SpecialityResponse response = DataParser.parseJson(responseString, SpecialityResponse.class);
            if(response.getStatuscode() == 0){
                UIUtility.showToastMsg_short(getActivity(), response.getStatusMessage());
                return;
            }else if(response.getStatuscode() == 1){
                ArrayList<StringWithTag> specialitiesList = new ArrayList<StringWithTag>();
                specialitiesList.add(new StringWithTag("Select", ""));
                for (int i = 0; i < response.getData().size(); i++) {
                    specialitiesList.add(new StringWithTag(response.getData().get(i).getName(), response.getData().get(i).getId()));
                }
                bindSpinnerDropDown(spinnerDepartment, specialitiesList);
            }
        }


        if(responseString != null && apiUrl.equalsIgnoreCase(NetworkConfig.addDoctor)){
            MedininProgressDialog.dismissDialog();
            AddDoctorResponse response = DataParser.parseJson(responseString, AddDoctorResponse.class);
            if(response.getStatuscode() == 0){
                UIUtility.showToastMsg_short(getActivity(), response.getStatusMessage());
                return;
            } else if( response.getStatuscode() == 1){
                UIUtility.showToastMsg_short(mContext, "Doctor added successfully");
                prefManager.setMySignUpRole("LOGIN");
                FragmentTransaction trx = getActivity().getSupportFragmentManager().beginTransaction();
                trx.hide(UserDetails.this);
                trx.add(R.id.container,userLoginRole);
                trx.show(userLoginRole).commit();

            }


        }


    }

    private void bindSpinnerDropDown(Spinner spinner, ArrayList<StringWithTag> list) {
        ArrayAdapter<StringWithTag> spinnerArrayAdapter = new ArrayAdapter<StringWithTag>(mContext, android.R.layout.simple_spinner_item, list);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }
}
