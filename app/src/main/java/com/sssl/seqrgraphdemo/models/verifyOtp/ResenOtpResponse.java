package com.sssl.seqrgraphdemo.models.verifyOtp;

import com.google.gson.annotations.SerializedName;

public class ResenOtpResponse {
    @SerializedName("success")
    public boolean success;

    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    public ResenOtpResponse(boolean success, int status, String message) {
        this.success = success;
        this.status = status;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
