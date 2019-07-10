package com.medinin.medininapp.login.datamodel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetOtpRequest implements Serializable {
    @SerializedName("mobile")
    @Expose
    private String mobile;

    @SerializedName("module")
    @Expose
    private String module;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getModule() { return module; }

    public void setModule(String module){
        this.module = module;
    }
}
