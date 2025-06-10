package com.sssl.seqrgraphdemo.models.scanHistory;

import com.google.gson.annotations.SerializedName;

public class VerifierScanHistoryRequest {
    @SerializedName("device_type")
    String device_type;

    @SerializedName("user_id")
    int user_id;

    public VerifierScanHistoryRequest(String device_type, int user_id) {
        this.device_type = device_type;
        this.user_id = user_id;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
