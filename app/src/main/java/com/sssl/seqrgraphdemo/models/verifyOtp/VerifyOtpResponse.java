package com.sssl.seqrgraphdemo.models.verifyOtp;

import com.google.gson.annotations.SerializedName;

public class VerifyOtpResponse {
    @SerializedName("success")
    public boolean success;

    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public ResponseData data;

    public class ResponseData {
        public int id;
        public String access_token;

        public String username;
        public int verify_by;

        public ResponseData(int id, String access_token, String username, int verify_by) {
            this.id = id;
            this.access_token = access_token;
            this.username = username;
            this.verify_by = verify_by;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getVerify_by() {
            return verify_by;
        }

        public void setVerify_by(int verify_by) {
            this.verify_by = verify_by;
        }
    }

    public VerifyOtpResponse(boolean success, int status, String message, ResponseData data) {
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
