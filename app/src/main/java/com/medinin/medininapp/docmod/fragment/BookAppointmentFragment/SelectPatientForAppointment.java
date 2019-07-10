package com.medinin.medininapp.docmod.fragment.BookAppointmentFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.medinin.medininapp.BaseFragment;
import com.medinin.medininapp.R;
import com.medinin.medininapp.docmod.adapter.PatientListAdapter;
import com.medinin.medininapp.docmod.adapter.PatientListAdapterAppointment;
import com.medinin.medininapp.docmod.request.PatientsRequest;
import com.medinin.medininapp.docmod.responce.PatientData;
import com.medinin.medininapp.docmod.responce.PatientList;
import com.medinin.medininapp.docmod.responce.PatientListResponce;
import com.medinin.medininapp.network.RestClient;
import com.medinin.medininapp.network.URLData;
import com.medinin.medininapp.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SelectPatientForAppointment extends BaseFragment implements View.OnClickListener  {


    public SelectPatientForAppointment(){

    }

    Unbinder unbinder;
    private View view;
    private SuperRecyclerView recyclerView;



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
        view = inflater.inflate(R.layout.fragment_select_patient, container, false);

        recyclerView = view.findViewById(R.id.listofpatient);


        unbinder = ButterKnife.bind(this, view);
        return view;
    }





    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        initializeView();
        //handleLocation();
        makePatientList();
    }


    private void initializeView() {

    }

    // api call for patientlist of doctor ;

    private void makePatientList() {
        if (Utils.isOnline(getActivity())) {
            RestClient restClient = RestClient.getInstance();

            PatientsRequest req = new PatientsRequest();
            req.doc_id = "ed3202810f1407e97b53fb759d631c29" ; //TODO i have hardcode the value ;

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

                    ArrayList<PatientList> orders1 =  patientList.patient_list ;
                    PatientListAdapterAppointment patientListAdapter = new PatientListAdapterAppointment(getActivity(), patientList.patient_list);

                    recyclerView.setAdapter(patientListAdapter);


                    Toast.makeText(getActivity()," Got All the patients ",Toast.LENGTH_LONG).show();

                }else if(patientData.statuscode == 0){

                    Toast.makeText(getActivity(), "Sorry You don't have any patients .", Toast.LENGTH_SHORT).show();
                }


            }

        }else {

        }
    }





    @Override
    public void onClick(View view) {

    }
}
