package com.sssl.seqrgraphdemo.models.login;

import com.google.gson.annotations.SerializedName;

public class InstituteLoginRequest {

    @SerializedName("institute_username")
    public String institute_username;

    @SerializedName("password")
    public String password;

    public InstituteLoginRequest(String institute_username, String password) {

        this.institute_username = institute_username;
        this.password = password;
    }

    public String getInstitute_username() {
        return institute_username;
    }

    public void setInstitute_username(String institute_username) {
        this.institute_username = institute_username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
