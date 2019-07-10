package com.medinin.medininapp.docmod.fragment.BookAppointmentFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.medinin.medininapp.BaseActivity;
import com.medinin.medininapp.R;
import com.medinin.medininapp.docmod.fragment.AllAppointment;
import com.medinin.medininapp.docmod.fragment.Allpatients;
import com.medinin.medininapp.docmod.fragment.DocSetting;

public class AppointmentBookingActivity extends BaseActivity {

    private int index;
    private int currentTabIndex;
    private Fragment[] fragments;
    private Fragment[]  orginalFragment;
    private ImageView[] imagebuttons;

    private SelectPatientForAppointment selectPatientForAppointmentFragment;
    private SelectPatientTimeSlot selectPatientTimeSlotFragment;
    private SelectBookAppointment selectBookAppointment;
    private AllAppointment allAppointmentFragment;
    private Allpatients allpatientsFragment;
    private DocSetting docSettingFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_appointment_fragment_container);
        // findViewById();
        intitTabview();

    }

    private void intitTabview() {
        selectPatientForAppointmentFragment = new SelectPatientForAppointment();
        selectPatientTimeSlotFragment = new SelectPatientTimeSlot();
        selectBookAppointment = new SelectBookAppointment();




        allAppointmentFragment = new AllAppointment();
        allpatientsFragment = new Allpatients();
        docSettingFragment = new DocSetting();

        orginalFragment = new Fragment[]{
                allpatientsFragment,docSettingFragment};



        fragments = new Fragment[]{
                 selectPatientForAppointmentFragment, selectPatientTimeSlotFragment,selectBookAppointment};


        imagebuttons = new ImageView[3];
        imagebuttons[0] = (ImageView) findViewById(R.id.img_home);
        imagebuttons[1] = (ImageView) findViewById(R.id.img_appoint);
        imagebuttons[2] = (ImageView) findViewById(R.id.img_setting);
        imagebuttons[0].setSelected(true);


        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_addappointment, selectPatientForAppointmentFragment)
                .add(R.id.fragment_container_addappointment,selectPatientTimeSlotFragment)
                .add(R.id.fragment_container_addappointment,selectBookAppointment)
                .hide(selectPatientTimeSlotFragment).hide(selectBookAppointment)
                .show(selectPatientForAppointmentFragment).commit();

    }


    public void onTabClicked(View view) {

        switch (view.getId()) {
            case R.id.llyt_home:

                Toast.makeText(getApplicationContext(), "ONe", Toast.LENGTH_LONG).show();
                //index = 0;


                break;
            case R.id.llyt_appointment:
               // index = 1;
                Toast.makeText(getApplicationContext(), "two", Toast.LENGTH_LONG).show();


                break;
            case R.id.llyt_setting:
                Fragment  f = null ;
                f = new DocSetting();

                replaceFragment(f);

                break;


        }


        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container_addappointment, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
      //  imagebuttons[currentTabIndex].setSelected(false);

        // imagebuttons[index].setSelected(true);
        // textviews[currentTabIndex].setTextColor(0xFF999999);
        //textviews[index].setTextColor(0xFF45C01A);
        currentTabIndex = index;
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
