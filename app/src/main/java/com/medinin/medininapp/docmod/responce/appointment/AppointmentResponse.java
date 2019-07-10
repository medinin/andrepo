package com.medinin.medininapp.docmod.responce.appointment;

import java.io.Serializable;

public class AppointmentResponse implements Serializable {

    public Integer statuscode;
    public String statusMessage;
    public AppointmentData data;
}
