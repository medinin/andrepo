package com.medinin.medininapp.docmod.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.medinin.medininapp.R;
import com.medinin.medininapp.docmod.responce.appointment.AppointmentStatus;
import com.medinin.medininapp.utils.OnitemClickLIstener;
import com.medinin.medininapp.utils.RecyclerView_OnClickListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AppointmentListAdapter  extends RecyclerView.Adapter<AppointmentListAdapter.AppointmentListHolder> implements Filterable  {

    private OnitemClickLIstener onItemClickListener;
    private final LayoutInflater mInflater;
    private Context mcontext;
    public ArrayList<AppointmentStatus> appointmentStatusArrayList;



//constructor
   public  AppointmentListAdapter(Context mcontext,ArrayList<AppointmentStatus> appointmentStatusArrayList){

       this.mcontext = mcontext;
       this.appointmentStatusArrayList = appointmentStatusArrayList;
       mInflater = LayoutInflater.from(mcontext);

    }


    @Override
    public AppointmentListHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View mainGroup = mInflater.inflate(R.layout.card_appointment_list, parent, false);

        return new AppointmentListHolder(mainGroup);
    }




    @Override
    public void onBindViewHolder(@NonNull AppointmentListHolder holder, int position) {


     final AppointmentStatus appointmentStatusList = appointmentStatusArrayList.get(position);
     holder.date_txt.setText(appointmentStatusList.date);
     holder.month_txt.setText("");
     holder.appointment_time_txt.setText(appointmentStatusList.time);
     holder.patient_name_txt.setText(appointmentStatusList.patient_info.name);
     holder.pat_mobile_txt.setText(appointmentStatusList.patient_info.mobile);
     holder.pat_gender_txt.setText(appointmentStatusList.patient_info.gender);
     holder.pat_age_txt.setText(appointmentStatusList.patient_info.age);
     holder.arrow.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

             Toast.makeText(mcontext,"working",Toast.LENGTH_LONG).show();

         }
     });




    }

    @Override
    public int getItemCount() {
        return (null != appointmentStatusArrayList ? appointmentStatusArrayList.size() : 0);
    }

    @Override
    public Filter getFilter() {
        return null;
    }




    // holder to assign the activity of the application .

    public class AppointmentListHolder extends RecyclerView.ViewHolder{


        private  RecyclerView_OnClickListener.OnClickListener onClickListener;
        private View rowItem;

        private TextView date_txt,month_txt,appointment_time_txt,patient_name_txt;
        private TextView pat_age_txt , pat_gender_txt , pat_mobile_txt ;
        private TextView appointment_time_txt2 ;
        private ImageView arrow ;




        public AppointmentListHolder(View itemView) {
            super(itemView);


            date_txt = (TextView) itemView.findViewById(R.id.date_txt);
            month_txt = (TextView) itemView.findViewById(R.id.month_txt);
            appointment_time_txt = (TextView) itemView.findViewById(R.id.appointment_time_txt);
            patient_name_txt = (TextView) itemView.findViewById(R.id.patient_name_txt);
            pat_age_txt = (TextView) itemView.findViewById(R.id.pat_age_txt);
            pat_gender_txt = (TextView) itemView.findViewById(R.id.pat_gender_txt);
            pat_mobile_txt = (TextView) itemView.findViewById(R.id.pat_mobile_txt);
            appointment_time_txt2 = (TextView) itemView.findViewById(R.id.appointment_time_txt2);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);

        }
    }




}
