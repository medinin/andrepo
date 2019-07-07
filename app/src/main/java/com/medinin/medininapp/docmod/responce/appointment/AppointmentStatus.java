package com.medinin.medininapp.docmod.responce.appointment;

import java.io.Serializable;

public class AppointmentStatus implements Serializable {

    public String appointment_id;
    public String patient_id;
    public String date;
    public String time;
    public String status;
    public PatientInfo patient_info;
}
