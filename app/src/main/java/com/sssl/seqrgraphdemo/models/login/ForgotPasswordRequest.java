package com.sssl.seqrgraphdemo.models.login;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordRequest {
    @SerializedName("type")
    public String type;

    @SerializedName("email_id")
    public String email_id;

    @SerializedName("user_type")
    public int user_type;

    public ForgotPasswordRequest(String type, String email_id, int user_type) {
        this.type = type;
        this.email_id = email_id;
        this.user_type = user_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }
}
