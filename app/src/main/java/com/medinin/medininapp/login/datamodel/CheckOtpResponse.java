package com.medinin.medininapp.login.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class CheckOtpResponse implements Serializable {

    @SerializedName("statuscode")
    @Expose
    private Integer statuscode;
    @SerializedName("statusMessage")
    @Expose
    private String statusMessage;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(Integer statuscode) {
        this.statuscode = statuscode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public class Data {

        @SerializedName("apikey")
        @Expose
        private String apikey;
        @SerializedName("mobile")
        @Expose
        private String mobile;
        @SerializedName("doc_status")
        @Expose
        private String docStatus;
        @SerializedName("clinic_status")
        @Expose
        private String clinicStatus;
        @SerializedName("hospital_status")
        @Expose
        private String hospitalStatus;
        @SerializedName("lab_status")
        @Expose
        private String labStatus;
        @SerializedName("patient_status")
        @Expose
        private String patientStatus;

        public String getApikey() {
            return apikey;
        }

        public void setApikey(String apikey) {
            this.apikey = apikey;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getDocStatus() {
            return docStatus;
        }

        public void setDocStatus(String docStatus) {
            this.docStatus = docStatus;
        }

        public String getClinicStatus() {
            return clinicStatus;
        }

        public void setClinicStatus(String clinicStatus) {
            this.clinicStatus = clinicStatus;
        }

        public String getHospitalStatus() {
            return hospitalStatus;
        }

        public void setHospitalStatus(String hospitalStatus) {
            this.hospitalStatus = hospitalStatus;
        }

        public String getLabStatus() {
            return labStatus;
        }

        public void setLabStatus(String labStatus) {
            this.labStatus = labStatus;
        }

        public String getPatientStatus() {
            return patientStatus;
        }

        public void setPatientStatus(String patientStatus) {
            this.patientStatus = patientStatus;
        }

}

}

