package com.sssl.seqrgraphdemo.models.scanview;

import com.google.gson.annotations.SerializedName;

public class ViewCertificateResponse {

    @SerializedName("success")
    public boolean success;

    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public ResponseData data;

    public class ResponseData{
        @SerializedName("fileUrl")
        public String fileUrl;

        @SerializedName("serialNo")
        public String serialNo;

        @SerializedName("serial_no")
        public String serial_no;

        @SerializedName("scan_result")
        public String scan_result;

        @SerializedName("key")
        public String key;


        public ResponseData(String fileUrl, String serialNo, String serial_no, String scan_result, String key) {
            this.fileUrl = fileUrl;
            this.serialNo = serialNo;
            this.serial_no = serial_no;
            this.scan_result = scan_result;
            this.key = key;
        }

        public String getSerial_no() {
            return serial_no;
        }

        public void setSerial_no(String serial_no) {
            this.serial_no = serial_no;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(String serialNo) {
            this.serialNo = serialNo;
        }

        public String getScan_result() {
            return scan_result;
        }

        public void setScan_result(String scan_result) {
            this.scan_result = scan_result;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public ViewCertificateResponse(boolean success, int status, String message, ResponseData data) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
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

    public ResponseData getData() {
        return data;
    }

    public void setData(ResponseData data) {
        this.data = data;
    }
}
