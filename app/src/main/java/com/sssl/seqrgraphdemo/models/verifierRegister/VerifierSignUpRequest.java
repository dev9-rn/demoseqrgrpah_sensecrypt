package com.sssl.seqrgraphdemo.models.verifierRegister;

import com.google.gson.annotations.SerializedName;

public class VerifierSignUpRequest {
    @SerializedName("username")
    public String username;

    @SerializedName("fullname")
    public String fullname;

    @SerializedName("password")
    public String password;

    @SerializedName("email_id")
    public String email_id;

    @SerializedName("mobile_no")
    public String mobile_no;

    @SerializedName("verify_by")
    public int verify_by;

    public VerifierSignUpRequest(String username, String fullname, String password, String email_id, String mobile_no, int verify_by) {
        this.username = username;
        this.fullname = fullname;
        this.password = password;
        this.email_id = email_id;
        this.mobile_no = mobile_no;
        this.verify_by = verify_by;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public int getVerify_by() {
        return verify_by;
    }

    public void setVerify_by(int verify_by) {
        this.verify_by = verify_by;
    }
}
