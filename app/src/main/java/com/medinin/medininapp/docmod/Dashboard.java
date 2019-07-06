package com.medinin.medininapp.docmod;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
       // setContentView(R.layout.dashboard_context);
       // findViewById();
    }


    @Override
    public void onClick(View view) {

    }
}
