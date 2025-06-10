package com.sssl.seqrgraphdemo.models.verifierRegister;

import com.google.gson.annotations.SerializedName;

public class VerifierRegisterResponse {

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
        public String fullname;

        public String username;

        public String email_id;

        public String mobile_no;

        public String verify_by;

        public String status;

        public String device_type;

        public String token;

        public int OTP;

        public int site_id;

        public String updated_at;

        public String created_at;

        public String accesstoken;

        public ResponseData(int id, String fullname, String username, String email_id, String mobile_no, String verify_by, String status, String device_type, String token, int OTP, int site_id, String updated_at, String created_at, String accesstoken) {
            this.id = id;
            this.fullname = fullname;
            this.username = username;
            this.email_id = email_id;
            this.mobile_no = mobile_no;
            this.verify_by = verify_by;
            this.status = status;
            this.device_type = device_type;
            this.token = token;
            this.OTP = OTP;
            this.site_id = site_id;
            this.updated_at = updated_at;
            this.created_at = created_at;
            this.accesstoken = accesstoken;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail_id() {
            return email_id;
        }

        public void setEmail_id(String email_id) {
            this.email_id = email_id;
        }

        public String getMobile_no() {
            return mobile_no;
        }

        public void setMobile_no(String mobile_no) {
            this.mobile_no = mobile_no;
        }

        public String getVerify_by() {
            return verify_by;
        }

        public void setVerify_by(String verify_by) {
            this.verify_by = verify_by;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDevice_type() {
            return device_type;
        }

        public void setDevice_type(String device_type) {
            this.device_type = device_type;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getOTP() {
            return OTP;
        }

        public void setOTP(int OTP) {
            this.OTP = OTP;
        }

        public int getSite_id() {
            return site_id;
        }

        public void setSite_id(int site_id) {
            this.site_id = site_id;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getAccesstoken() {
            return accesstoken;
        }

        public void setAccesstoken(String accesstoken) {
            this.accesstoken = accesstoken;
        }
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
