package com.medinin.medininapp.docmod.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.medinin.medininapp.utils.StringUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

import static com.medinin.medininapp.utils.NumberUtils.dateFormatFun;
import static com.medinin.medininapp.utils.NumberUtils.dateFormatMonth;
import static com.medinin.medininapp.utils.Utils.RailToNormal;

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

       String time =  RailToNormal(appointmentStatusList.time);
       String endTime = split(appointmentStatusList.time);
       String splittime = RailToNormal(endTime);
       System.out.println("this our time "+time);
       holder.appointment_time_txt.setText(time);
       holder.appointment_time_txt2.setText(splittime);

      String name =   StringUtils.capitalizeFirstLetter(appointmentStatusList.patient_info.name);//First letter uppercase , Second Letter uppercase
        holder.patient_name_txt.setText(name);

     holder.pat_mobile_txt.setText(appointmentStatusList.patient_info.mobile);
     // captial letter with Uppercase
     holder.pat_age_txt.setText(appointmentStatusList.patient_info.age);   // zr year  . space format .

       String data =  dateFormatFun(appointmentStatusList.date);
       String month = dateFormatMonth(appointmentStatusList.date);
        holder.month_txt.setText(month);
        holder.date_txt.setText(data);

        if (appointmentStatusList.patient_info.gender.equals("male")){
            holder.pat_gender_txt.setText("M");

        }else{
            holder.pat_gender_txt.setText("F");
        }

     holder.arrow.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

             Toast.makeText(mcontext,"working",Toast.LENGTH_LONG).show();

         }
     });




    }


    private String split(String s ){
        String[] splited = s.split("\\:");
        String endTimeStr ="";

        if (!s.equals("null") && !s.isEmpty()) {

            Log.i("hour", splited[0].toString());
            Log.i("min", splited[1].toString());
            int hour = Integer.parseInt(splited[0]);
            int min = Integer.parseInt(splited[1]);
            min = min + 30;
            if (min >= 60) {
                hour = hour + 1;
                min = 0;
            }
             endTimeStr = String.format("%02d:%02d", hour, min);//hour + ":" + min;

        }
        return  endTimeStr;
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
