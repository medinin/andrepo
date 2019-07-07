package com.medinin.medininapp.docmod.responce.appointment;

import java.io.Serializable;

public class AppointmentResponse implements Serializable {

    private Integer statuscode;
    private String statusMessage;
    private AppointmentData data;
}
