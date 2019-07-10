package com.medinin.medininapp.docmod.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.medinin.medininapp.BaseActivity;
import com.medinin.medininapp.BaseFragment;
import com.medinin.medininapp.R;
import com.medinin.medininapp.docmod.adapter.AppointmentListAdapter;
import com.medinin.medininapp.docmod.fragment.BookAppointmentFragment.AppointmentBookingActivity;
import com.medinin.medininapp.docmod.fragment.BookAppointmentFragment.SelectPatientForAppointment;
import com.medinin.medininapp.docmod.request.AppointmentRequest;
import com.medinin.medininapp.docmod.request.PatientsRequest;
import com.medinin.medininapp.docmod.responce.PatientListResponce;
import com.medinin.medininapp.docmod.responce.appointment.AppointmentData;
import com.medinin.medininapp.docmod.responce.appointment.AppointmentResponse;
import com.medinin.medininapp.docmod.responce.appointment.AppointmentStatus;
import com.medinin.medininapp.network.RestClient;
import com.medinin.medininapp.network.URLData;
import com.medinin.medininapp.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AllAppointment extends BaseFragment implements View.OnClickListener {

    Unbinder unbinder;
    private View view;
    private TextView total_appointment_count , dateInput;
    private ImageView addAppointmentSec;
    private SuperRecyclerView recyclerView;
    private AppointmentListAdapter appointmentListAdapter;
    private String totalappointment, dateInputStr;



    public AllAppointment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      //  EventBus.getDefault().unregister(this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_appointmentlist, container, false);
        recyclerView = view.findViewById(R.id.listofappointment);
        total_appointment_count = view.findViewById(R.id.total_appointment_count);
        addAppointmentSec = view.findViewById(R.id.addAppointmentSec);

        dateInput = view.findViewById(R.id.dateInput);
        addAppointmentSec.setOnClickListener(this);
        dateInput.setOnClickListener(this);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

      //  initializeView();
       // handleLocation();
        makeAppointment("");
    }


    //date dilog picker ;

    public void openDateSelectDialog() {
        // Get Current Date

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(
                            DatePicker view,
                            int year,
                            int monthOfYear,
                            int dayOfMonth
                    ) {
                        dateInput.setText( dayOfMonth + "-" + (monthOfYear + 1) + "-" +  year);
                        dateInputStr = (year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                        makeAppointment(dateInputStr);

                    }
                }, mYear, mMonth, mDay);


        datePickerDialog.show();
    }

    private void addTotalAppointment(String totalappointment){

        total_appointment_count.setText(totalappointment);

    }



    private void makeAppointment(String date) {
        if (Utils.isOnline(getActivity())) {
            RestClient restClient = RestClient.getInstance();

            AppointmentRequest req = new AppointmentRequest();
            req.did = "8d5e11325965864fa17c082eb9a6f014" ; //TODO i have hardcode the value ;
            req.date = date;

            restClient.post(getActivity(), req, AppointmentResponse.class, this, URLData.URL_LIST_DOCTOR_APPOINTMENT);


        } else {



            //  recyclerView.setEmptyAdapter(R.drawable.no_internetfound, false, 0);

        }
    }


    @Override
    public void onSuccess(String responce, Object data, int urlId, int position) {
        if (urlId == URLData.URL_LIST_DOCTOR_APPOINTMENT.getUrlId()) {

            AppointmentResponse appointmentResponse = (AppointmentResponse) data;
            if (appointmentResponse != null) {
                if(appointmentResponse.statuscode == 1){
                    AppointmentData appointmentData ;

                    appointmentData =  appointmentResponse.data;

                    ArrayList<AppointmentStatus> orders1 =  appointmentData.appointment_status ;
                    AppointmentListAdapter  appointmentListAdapter = new AppointmentListAdapter(getActivity(), appointmentData.appointment_status);

                   String total = Integer.toString(appointmentData.total_appointments);
                    addTotalAppointment(total);

                    recyclerView.setAdapter(appointmentListAdapter);


                    Toast.makeText(getActivity()," Your Appointment list have been loaded .",Toast.LENGTH_LONG).show();

                }else if(appointmentResponse.statuscode == 0){

                    Toast.makeText(getActivity(), " Sorry the appointment have not been loaded ", Toast.LENGTH_SHORT).show();
                }




            }

        }else {

        }
    }


    @Override
    public void onClick(View view) {
if(view.getId() == R.id.dateInput){

    openDateSelectDialog();
}


else if (view.getId() == R.id.addAppointmentSec){

   // Intent intent = new Intent(getActivity(), AppointmentBookingActivity.class);
    //startActivity(intent);


    /*this making added appointment for the doctor */

    Fragment fragment = null;
    fragment = new SelectPatientForAppointment();
    replaceFragment(fragment);


}
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



}
