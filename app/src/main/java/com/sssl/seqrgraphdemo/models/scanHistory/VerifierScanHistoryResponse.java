package com.sssl.seqrgraphdemo.models.scanHistory;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VerifierScanHistoryResponse {

    @SerializedName("success")
    public String success;

    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public List<VerifierScanHistoryData> data;


//    public VerifierScanHistoryResponse(String success, int status, String message, VerifierScanHistoryData data) {
//        this.success = success;
//        this.status = status;
//        this.message = message;
//        this.data = data;
//    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
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

    public List<VerifierScanHistoryData> getData() {
        return data;
    }

    public void setData(List<VerifierScanHistoryData> data) {
        this.data = data;
    }

//    public List<VerifierScanHistoryData> getData() {
//        if (data instanceof List<?>) {
//            //noinspection unchecked
//            return (List<VerifierScanHistoryData>) data;
//        }
//        return null;
//    }
//
//    public void setData(VerifierScanHistoryData data) {
//        this.data = data;
//    }
}
