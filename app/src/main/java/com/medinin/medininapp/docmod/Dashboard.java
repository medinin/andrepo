package com.medinin.medininapp.docmod;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.medinin.medininapp.BaseActivity;
import com.medinin.medininapp.R;
import com.medinin.medininapp.docmod.fragment.AllAppointment;
import com.medinin.medininapp.docmod.fragment.Allpatients;
import com.medinin.medininapp.docmod.fragment.DocSetting;

public class Dashboard  extends BaseActivity implements View.OnClickListener{


    private ImageView[] imagebuttons;
    private TextView[] textviews;
    private int index;
    private int currentTabIndex;
    private Fragment[] fragments;
    private AllAppointment allAppointmentFragment;
    private Allpatients allpatientsFragment;
    private DocSetting docSettingFragment;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
       // findViewById();
        intitTabview();

    }



    private void intitTabview() {
        allAppointmentFragment = new AllAppointment();
        allpatientsFragment = new Allpatients();
        docSettingFragment = new DocSetting();


        fragments = new Fragment[]{
                allpatientsFragment, allAppointmentFragment, docSettingFragment};


        imagebuttons = new ImageView[3];
        imagebuttons[0] = (ImageView) findViewById(R.id.img_home);
        imagebuttons[1] = (ImageView) findViewById(R.id.img_appoint);
        imagebuttons[2] = (ImageView) findViewById(R.id.img_setting);
        imagebuttons[0].setSelected(true);


        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, allpatientsFragment)
                .add(R.id.fragment_container, allAppointmentFragment)
                .add(R.id.fragment_container, docSettingFragment)

                .hide(allAppointmentFragment).hide(docSettingFragment)
                .show(allpatientsFragment).commit();

    }



    public void onTabClicked(View view) {

        switch (view.getId()) {
            case R.id.llyt_home:

                Toast.makeText(getApplicationContext(), "ONe", Toast.LENGTH_LONG).show();
                index = 0;


                break;
            case R.id.llyt_appointment:
                index = 1;
                Toast.makeText(getApplicationContext(), "two", Toast.LENGTH_LONG).show();


                break;
            case R.id.llyt_setting:
                index = 2;
                Toast.makeText(getApplicationContext(), "three", Toast.LENGTH_LONG).show();

                break;


        }


        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        imagebuttons[currentTabIndex].setSelected(false);

       // imagebuttons[index].setSelected(true);
       // textviews[currentTabIndex].setTextColor(0xFF999999);
        //textviews[index].setTextColor(0xFF45C01A);
        currentTabIndex = index;
    }

    @Override
    public void onClick(View view) {

    }
}
