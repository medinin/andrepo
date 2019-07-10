package com.medinin.medininapp.docmod.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.medinin.medininapp.BaseFragment;
import com.medinin.medininapp.R;
import com.medinin.medininapp.docmod.docSetting.Forms;


import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DocSetting extends BaseFragment  implements View.OnClickListener {
    Unbinder unbinder;
    private View view;
    LinearLayout profile_card;
    LinearLayout education_card;
    LinearLayout clinic_card;
    LinearLayout conditions_card;
    LinearLayout scan_card;
    LinearLayout campaign_card;
    LinearLayout chat_card;
    LinearLayout models_card;
    LinearLayout settings_card;
    LinearLayout forms_card;

    public DocSetting(){

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      //  EventBus.getDefault().unregister(this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.doc_setting, container, false);
        profile_card = (LinearLayout) view.findViewById(R.id.profile_card);
        education_card = (LinearLayout) view.findViewById(R.id.education_card);
        clinic_card = (LinearLayout) view.findViewById(R.id.clinic_card);
        conditions_card = (LinearLayout) view.findViewById(R.id.conditions_card);
        scan_card = (LinearLayout) view.findViewById(R.id.scan_card);
        campaign_card = (LinearLayout) view.findViewById(R.id.campaign_card);
        chat_card = (LinearLayout) view.findViewById(R.id.chat_card);
        models_card = (LinearLayout) view.findViewById(R.id.models_card);
        settings_card = (LinearLayout) view.findViewById(R.id.settings_card);
        forms_card = (LinearLayout) view.findViewById(R.id.forms_card);


        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       // initializeView();
     //   handleLocation();
        listners();
    }


    public void listners(){
        profile_card.setOnClickListener(this);
        education_card.setOnClickListener(this);
        clinic_card.setOnClickListener(this);
        conditions_card.setOnClickListener(this);
        scan_card.setOnClickListener(this);
        campaign_card.setOnClickListener(this);
        chat_card.setOnClickListener(this);
        models_card.setOnClickListener(this);
        settings_card.setOnClickListener(this);
        forms_card.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.profile_card:
                Toast.makeText(getActivity(),"woring",Toast.LENGTH_LONG).show();
                break;
            case R.id.education_card:Toast.makeText(getActivity(),"edu",Toast.LENGTH_LONG).show();
                break;
            case R.id.clinic_card:
                Toast.makeText(getActivity(),"clinc",Toast.LENGTH_LONG).show();
                break;
            case R.id.conditions_card:
                Toast.makeText(getActivity(),"cond",Toast.LENGTH_LONG).show();
                break;
            case R.id.scan_card:
                Toast.makeText(getActivity(),"scan",Toast.LENGTH_LONG).show();
                break;
            case R.id.campaign_card:
                Toast.makeText(getActivity(),"compaign",Toast.LENGTH_LONG).show();
                break;
            case R.id.chat_card:
                Toast.makeText(getActivity(),"chat",Toast.LENGTH_LONG).show();
                break;
            case R.id.models_card:
                Toast.makeText(getActivity(),"models",Toast.LENGTH_LONG).show();
                break;
            case R.id.settings_card:
                Toast.makeText(getActivity(),"setting",Toast.LENGTH_LONG).show();
                break;
            case R.id.forms_card:
                Toast.makeText(getActivity(),"forms",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), Forms.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;



        }

    }


}
