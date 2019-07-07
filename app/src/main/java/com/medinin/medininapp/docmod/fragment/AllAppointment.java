package com.medinin.medininapp.docmod.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.medinin.medininapp.BaseFragment;
import com.medinin.medininapp.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AllAppointment extends BaseFragment {

    Unbinder unbinder;
    private View view;
    private TextView total_appointment_count , dateInput;
    private ImageView addAppointmentSec;



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

        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
      //  initializeView();
       // handleLocation();
    }
}
