package com.sssl.seqrgraphdemo.models.verifyOtp;

import com.google.gson.annotations.SerializedName;

public class VerifyOtpRequest {
    @SerializedName("mobile_no")
    public String mobile_no;

    @SerializedName("otp")
    public String otp;

    public VerifyOtpRequest(String mobile_no, String otp) {
        this.mobile_no = mobile_no;
        this.otp = otp;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
