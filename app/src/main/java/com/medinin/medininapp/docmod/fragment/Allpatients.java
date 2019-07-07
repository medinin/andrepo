package com.medinin.medininapp.docmod.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.medinin.medininapp.BaseFragment;
import com.medinin.medininapp.R;
import com.medinin.medininapp.docmod.adapter.PatientListAdapter;
import com.medinin.medininapp.docmod.request.PatientsRequest;
import com.medinin.medininapp.docmod.responce.PatientData;
import com.medinin.medininapp.docmod.responce.PatientList;
import com.medinin.medininapp.docmod.responce.PatientListResponce;
import com.medinin.medininapp.network.RestClient;
import com.medinin.medininapp.network.URLData;
import com.medinin.medininapp.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class Allpatients extends BaseFragment {

    @BindView(R.id.listofpatient)
    RecyclerView recyclerView;

    Unbinder unbinder;
    private View view;





    

    private PatientListAdapter patientListAdapter;


    public Allpatients(){

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
        view = inflater.inflate(R.layout.activity_all_patients, container, false);

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeView();
        //handleLocation();
        makePatientList();
    }




    private void initializeView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       // recyclerView.setEmptyListener(this);

    }

    // api call for patientlist of doctor ;

    private void makePatientList() {
        if (Utils.isOnline(getActivity())) {
            RestClient restClient = RestClient.getInstance();

            PatientsRequest req = new PatientsRequest();
            req.doc_id = "111" ; //TODO i have hardcode the value ;

            restClient.post(getActivity(), req, PatientListResponce.class, this, URLData.URL_PATIENTS_NO_PAGE);


        } else {

          //  recyclerView.setEmptyAdapter(R.drawable.no_internetfound, false, 0);

        }
    }






    @Override
    public void onSuccess(String responce, Object data, int urlId, int position) {
        if (urlId == URLData.URL_PATIENTS_NO_PAGE.getUrlId()) {

            PatientListResponce patientData = (PatientListResponce) data;
            if (patientData != null) {
                if(patientData.statuscode == 1){
                    PatientData patientList ;

                    patientList =  patientData.data;

                    ArrayList<PatientList> orders1 =  patientList.patientList ;
                    patientListAdapter = new PatientListAdapter(getActivity(), patientList.patientList);

                    recyclerView.setAdapter(patientListAdapter);


                    Toast.makeText(getActivity()," Yes order has been shown ",Toast.LENGTH_LONG).show();

                }else if(patientData.statuscode == 0){

                    Toast.makeText(getActivity(), "No order has been found", Toast.LENGTH_SHORT).show();
                }




            }

        }else {

        }
    }




}
