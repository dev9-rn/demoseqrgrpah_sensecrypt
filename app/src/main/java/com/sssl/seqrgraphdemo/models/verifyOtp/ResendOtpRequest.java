package com.sssl.seqrgraphdemo.models.verifyOtp;

import com.google.gson.annotations.SerializedName;

public class ResendOtpRequest {
    @SerializedName("mobile_no")
    public String mobile_no;

    public ResendOtpRequest(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }
}
