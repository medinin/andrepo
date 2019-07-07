package com.medinin.medininapp.docmod.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medinin.medininapp.MainActivity;
import com.medinin.medininapp.R;
import com.medinin.medininapp.docmod.PatientdetailFragment;
import com.medinin.medininapp.docmod.responce.PatientList;
import com.medinin.medininapp.utils.OnitemClickLIstener;
import com.medinin.medininapp.utils.RecyclerView_OnClickListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientListAdapter  extends RecyclerView.Adapter<PatientListAdapter.PatientListHolder> implements Filterable {

    private OnitemClickLIstener onItemClickListener;
    private final LayoutInflater mInflater;
    private Context mcontext;
    public ArrayList<PatientList> patientLists;




    public PatientListAdapter(Context mcontext, ArrayList<PatientList> patientLists) {
        this.patientLists = patientLists;
        this.mcontext = mcontext;
        mInflater = LayoutInflater.from(mcontext);

    }




    @Override
    public PatientListHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View mainGroup = mInflater.inflate(R.layout.all_patient_row, parent, false);
        return new PatientListHolder(mainGroup);

    }



    @Override
    public void onBindViewHolder(final PatientListHolder holder, int position) {


        final PatientList patientList =patientLists.get(position);
        holder.patient_name_txt.setText(patientList.name);
        holder.patDOBTxt.setText(patientList.dob);
        holder.patAgeTxt.setText(patientList.age);
        holder.patGenderTxt.setText(patientList.gender);
        holder.patMobileTxt.setText(patientList.mobile);


        //check the user is mail or female

        if(patientList.gender.equals("male")){

            holder.patientImg.setImageResource(R.drawable.male_user);

        }else{
            holder.patientImg.setImageResource(R.drawable.female_user);
        }



        //detailview of patient history ;
        holder.viewPatDetailsImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mcontext, MainActivity.class);

                mcontext.startActivity(intent);
                ((Activity) mcontext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });



        //animation part .


        final AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setFillAfter(true);
        animSet.setFillEnabled(true);
        final Animation slide_down = AnimationUtils.loadAnimation(mcontext,
                R.anim.slide_down);

        final Animation slide_up = AnimationUtils.loadAnimation(mcontext,
                R.anim.slide_up);

        holder.dropDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String _tag = holder.dropDownBtn.getTag().toString();
                if (_tag.equals("0")) {
                    holder.dropDownBtn.setTag(1);
                    holder.pat_details_wrap.startAnimation(slide_up);
                    holder.pat_details_wrap.setVisibility(View.VISIBLE);
                } else {
                    holder.dropDownBtn.setTag(0);
                    holder.pat_details_wrap.startAnimation(slide_down);
                    holder.pat_details_wrap.setVisibility(View.GONE);
                }

            }
        });


    }




    @Override
    public int getItemCount() {


        return (null != patientLists ? patientLists.size() : 0);
    }

    @Override
    public Filter getFilter() {
        return null;
    }





    /// view holder will work here

    public class PatientListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private  RecyclerView_OnClickListener.OnClickListener onClickListener;
        private View rowItem;

        private CircleImageView patientImg;
        private TextView patient_name_txt;
        private  ImageView viewPatDetailsImgBtn;
        private TextView patDOBTxt;
        private LinearLayout dropDownBtn;
        private ConstraintLayout pat_details_wrap;
        private  TextView patAgeTxt; // age of patient ;
        private TextView patGenderTxt ; // gender
        private TextView patMobileTxt; // cel phone number ;



        public PatientListHolder(View itemView) {
            super(itemView);
            rowItem = itemView;
            viewPatDetailsImgBtn = (ImageView) itemView.findViewById(R.id.viewPatDetailsImgBtn);
            patientImg = (CircleImageView) itemView.findViewById(R.id.patientImg);
            patient_name_txt = (TextView) itemView.findViewById(R.id.patient_name_txt);
            patDOBTxt = (TextView)itemView.findViewById(R.id.patDOBTxt);
            dropDownBtn = (LinearLayout) itemView.findViewById(R.id.dropDownBtn);
            pat_details_wrap = (ConstraintLayout) itemView.findViewById(R.id.pat_details_wrap);
            patAgeTxt =(TextView) itemView.findViewById(R.id.patAgeTxt);
            patGenderTxt = (TextView) itemView.findViewById(R.id.patGenderTxt);
            patMobileTxt = (TextView) itemView.findViewById(R.id.patMobileTxt);
            viewPatDetailsImgBtn = (ImageView) itemView.findViewById(R.id.viewPatDetailsImgBtn);


        }


        @Override
        public void onClick(View view) {

        }

    }
}
