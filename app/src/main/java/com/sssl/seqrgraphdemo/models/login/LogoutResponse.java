package com.sssl.seqrgraphdemo.models.login;

import com.google.gson.annotations.SerializedName;

public class LogoutResponse {
    @SerializedName("message")
    public String message;

    @SerializedName("status")
    public String status;

    @SerializedName("success")
    public boolean success;

    public LogoutResponse(String message, String status, boolean success) {
        this.message = message;
        this.status = status;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
