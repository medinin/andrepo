package com.medinin.medininapp.docmod.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.medinin.medininapp.R;
import com.medinin.medininapp.docmod.holder.PatientListHolder;
import com.medinin.medininapp.docmod.responce.PatientList;
import com.medinin.medininapp.utils.OnitemClickLIstener;

import java.util.ArrayList;

public class PatientListAdapter  extends RecyclerView.Adapter<PatientListHolder> implements Filterable {

    private OnitemClickLIstener onItemClickListener;
    private final LayoutInflater mInflater;
    private Context mcontext;
    public ArrayList<PatientList> patientLists;




    public PatientListAdapter(Context mcontext, ArrayList<PatientList> patientLists) {
        this.patientLists = patientLists;
        this.mcontext = mcontext;
        mInflater = LayoutInflater.from(mcontext);

    }



    @NonNull
    @Override
    public PatientListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mainGroup = mInflater.inflate(R.layout.all_patient_row, parent, false);
        return new PatientListHolder(mainGroup);

    }





    @Override
    public void onBindViewHolder(PatientListHolder holder, int position) {
        final PatientList patientList =patientLists.get(position);
        holder.patient_name_txt.setText(patientList.name);

    }

    @Override
    public int getItemCount() {
        return (null != patientLists ? patientLists.size() : 0);
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
