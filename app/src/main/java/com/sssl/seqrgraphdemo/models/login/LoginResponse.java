package com.sssl.seqrgraphdemo.models.login;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("success")
    public boolean success;

    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @SerializedName("accesstoken")
    public String accesstoken;

    @SerializedName("data")
    public ResponseData data;


    public class ResponseData {
        public int id;

        public String fullname;

        public String l_name;

        public String username;

        public String email_id;

        public String mobile_no;

        public String status;

        public String access_token;

        public  String institute_username;

        public ResponseData(int id, String fullname, String l_name, String username, String email_id, String mobile_no, String status, String access_token , String institute_username) {
            this.id = id;
            this.fullname = fullname;
            this.l_name = l_name;
            this.username = username;
            this.email_id = email_id;
            this.mobile_no = mobile_no;
            this.status = status;
            this.access_token = access_token;
            this.institute_username = institute_username;
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

        public String getL_name() {
            return l_name;
        }

        public void setL_name(String l_name) {
            this.l_name = l_name;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getInstitute_username() {
            return institute_username;
        }

        public void setInstitute_username(String institute_username) {
            this.institute_username = institute_username;
        }
    }

    public LoginResponse(boolean success, int status, String message, String accesstoken, ResponseData data) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.accesstoken = accesstoken;
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

    public void setData(ResponseData data) {
        this.data = data;
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

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }
}
