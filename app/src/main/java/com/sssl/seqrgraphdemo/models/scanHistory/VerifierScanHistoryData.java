package com.sssl.seqrgraphdemo.models.scanHistory;

import com.google.gson.annotations.SerializedName;

public class VerifierScanHistoryData {

    @SerializedName("id")
    public int id;

    @SerializedName("date_time")
    public String date_time;

    @SerializedName("device_type")
    public String device_type;

    @SerializedName("scanned_data")
    public String scanned_data;

    @SerializedName("scan_by")
    public String scan_by;

    @SerializedName("scan_result")
    public int scan_result;

    @SerializedName("site_id")
    public int site_id;

    @SerializedName("document_id")
    public String document_id;

    @SerializedName("document_status")
    public String document_status;

    @SerializedName("created_at")
    public String created_at;

    @SerializedName("pdf_url")
    public String pdf_url;

    public VerifierScanHistoryData(int id, String date_time, String device_type, String scanned_data, String scan_by, int scan_result, int site_id, String document_id, String document_status, String created_at, String pdf_url) {
        this.id = id;
        this.date_time = date_time;
        this.device_type = device_type;
        this.scanned_data = scanned_data;
        this.scan_by = scan_by;
        this.scan_result = scan_result;
        this.site_id = site_id;
        this.document_id = document_id;
        this.document_status = document_status;
        this.created_at = created_at;
        this.pdf_url = pdf_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getScanned_data() {
        return scanned_data;
    }

    public void setScanned_data(String scanned_data) {
        this.scanned_data = scanned_data;
    }

    public String getScan_by() {
        return scan_by;
    }

    public void setScan_by(String scan_by) {
        this.scan_by = scan_by;
    }

    public int getScan_result() {
        return scan_result;
    }

    public void setScan_result(int scan_result) {
        this.scan_result = scan_result;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getDocument_status() {
        return document_status;
    }

    public void setDocument_status(String document_status) {
        this.document_status = document_status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getPdf_url() {
        return pdf_url;
    }

    public void setPdf_url(String pdf_url) {
        this.pdf_url = pdf_url;
    }
}
