package com.medinin.medininapp.docmod.fragment.BookAppointmentFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medinin.medininapp.BaseFragment;
import com.medinin.medininapp.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SelectBookAppointment extends BaseFragment {


  Unbinder unbinder;
  private View view;

  public  SelectBookAppointment(){

    }



  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//       EventBus.getDefault().register(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    //      EventBus.getDefault().unregister(this);
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_appointment_booked, container, false);




    unbinder = ButterKnife.bind(this, view);
    return view;
  }

}
