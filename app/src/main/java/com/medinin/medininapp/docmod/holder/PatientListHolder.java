package com.medinin.medininapp.docmod.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.medinin.medininapp.R;
import com.medinin.medininapp.utils.RecyclerView_OnClickListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public RecyclerView_OnClickListener.OnClickListener onClickListener;
    public View rowItem;

 public CircleImageView patientImg;
 public TextView patient_name_txt;
 public ImageView viewPatDetailsImgBtn;



  //  patientImg

    //patient_name_txt

    //viewPatDetailsImgBtn



    //constrainLlayot pat_details_wrap

    //tv patDOBTxt
    // patAgeTxt   ,patGenderTxt  ,patMobileTxt  ,lastVisitDateTxt


    public PatientListHolder(View itemView) {
            super(itemView);
            rowItem = itemView;
        viewPatDetailsImgBtn = (ImageView) itemView.findViewById(R.id.viewPatDetailsImgBtn);
        patientImg = (CircleImageView) itemView.findViewById(R.id.patientImg);
        patient_name_txt = (TextView) itemView.findViewById(R.id.patient_name_txt);


    }


    @Override
    public void onClick(View view) {

    }
}
