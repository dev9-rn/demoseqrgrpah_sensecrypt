package com.sssl.seqrgraphdemo.networks.liveness;

public class ApiUrl {

    public static String BASE_URL = "https://liveness.tech5.tech/";
    public static String CHECK_LIVENESS_REQUEST = "check_liveness";
    public static String getCheckLivenessUrl() {
        return BASE_URL + CHECK_LIVENESS_REQUEST;
    }


    public static String getInhouseLivenessUrl() {
        return "http://eval-facesdk.tech5.tech:5000/?img";
    }



}
