package com.sssl.seqrgraphdemo.models.scanview;

import com.google.gson.annotations.SerializedName;

public class ViewCertificateRequest {

    @SerializedName("key")
    public String key;

    @SerializedName("device_type")
    public String device_type;

    @SerializedName("scanned_by")
    public String scanned_by;

    @SerializedName("user_id")
    public int user_id;

    public ViewCertificateRequest(String key, String device_type, String scanned_by, int user_id) {
        this.key = key;
        this.device_type = device_type;
        this.scanned_by = scanned_by;
        this.user_id = user_id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getScanned_by() {
        return scanned_by;
    }

    public void setScanned_by(String scanned_by) {
        this.scanned_by = scanned_by;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
