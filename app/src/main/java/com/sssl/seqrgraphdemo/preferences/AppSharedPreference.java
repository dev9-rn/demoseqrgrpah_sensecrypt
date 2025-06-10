package com.sssl.seqrgraphdemo.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPreference {
    private static final String IS_AUTO_CAPTURE = "IS_AUTO_CAPTURE";
    private static final boolean DEFAULT_AUTO_CAPTURE = true;
    private final SharedPreferences sharedPref;

    public static final String CRYPTO_API_BASE_URL = "https://eval-idencode.tech5.tech/v1/";

    public static final String GET_PUBLIC_KEYS_URL = "cryptograph/digitalSignature/publicKeys/jwks.json";

    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    private static final String API_KEY = "API_KEY";

    private static final String USER_ID = "USER_ID";

    private static final String USER_NAME = "USER_NAME";

    public String APP_NAME = "Demo SeQR Scan";

    public boolean IS_VERIFIER = true;

    public static final String API_ACCESS_TOKEN = null;

    public static final String API_API_KEY = "GSka~2nu@D,knVOfz{+/RL1WMF{bka";

    public static final int LOGIN_USERID = 0;

    private static final String LOGIN_USER_NAME = "";

    public String LOGIN_USER_TYPE = null;

    public String TYPE_VERIFIER = "VERIFIER";

    public String TYPE_INSTITUTE = "INSTITUTE";

    private static final String IS_FACE_LIVENESS_ENABLED = "IS_FACE_LIVENESS_ENABLED";
    private static final boolean DEFAULT_VALUE_IS_FACE_LIVENESS_ENABLED = true;

    private static final String USE_BACK_CAMERA_FOR_FACE = "USE_BACK_CAMERA_FOR_FACE";
    private static final boolean DEFAULT_VALUE_FOR_USE_BACK_CAMERA_FOR_FACE = false;


    public AppSharedPreference(Context context) {
        sharedPref = context.getSharedPreferences("idencode_prefs", Context.MODE_PRIVATE); //NOSONAR
    }

    public String getCryptoApiBaseUrl() {
        return (sharedPref.getString("CRYPTO_BASE_URL", CRYPTO_API_BASE_URL));
    }

    public String getAppName(){return  (sharedPref.getString("APP_NAME" , APP_NAME)); }

    public boolean getIsVerifier(){
        return (sharedPref.getBoolean("IS_VERIFIER" , IS_VERIFIER));
    }

    public void setIsVerifier(boolean value){
        sharedPref.edit().putBoolean("IS_VERIFIER", value).apply();
    }

    public String getAccessToken(){return  (sharedPref.getString(ACCESS_TOKEN , API_ACCESS_TOKEN)); }

    public void setAccessToken(String accessToken) {

        sharedPref.edit().putString(ACCESS_TOKEN, accessToken).apply();

    }

    public String getLoginUserType(){return  (sharedPref.getString("LOGIN_USER_TYPE" , LOGIN_USER_TYPE)); }

    public void setLoginUserType(String type) {

        sharedPref.edit().putString("LOGIN_USER_TYPE", type).apply();

    }
    public String getApiKey(){return  (sharedPref.getString(API_KEY , API_API_KEY)); }

    public void setApiKey() {
        sharedPref.edit().putString(API_KEY, API_API_KEY).apply();
    }

    public int getUserId(){return  (sharedPref.getInt(USER_ID , LOGIN_USERID)); }

    public void setUserId(int userId) {

        sharedPref.edit().putInt(USER_ID, userId).apply();

    }

    public String getUserName(){return  (sharedPref.getString(USER_NAME , LOGIN_USER_NAME)); }

    public void setUserName(String userName) {

        sharedPref.edit().putString(USER_NAME, userName).apply();

    }

    public String getPublicKeyJson() {
        return (sharedPref.getString("PUBLIC_KEY_JSON", ""));
    }

    public void setPublicKeyJson(String json) {

        sharedPref.edit().putString("PUBLIC_KEY_JSON", json).apply();

    }

    public boolean isAutoCapture() {
        return sharedPref.getBoolean(IS_AUTO_CAPTURE, DEFAULT_AUTO_CAPTURE);
    }

    public void setAutoCapture(boolean isAutoCapture) {
        sharedPref.edit().putBoolean(IS_AUTO_CAPTURE, isAutoCapture).apply();

    }

    public boolean isFaceLivenessEnabled() {
        return sharedPref.getBoolean(IS_FACE_LIVENESS_ENABLED, DEFAULT_VALUE_IS_FACE_LIVENESS_ENABLED);
    }

    public void setFaceLiveness(boolean isFaceLivenessEnabled) {
        sharedPref.edit().putBoolean(IS_FACE_LIVENESS_ENABLED, isFaceLivenessEnabled).apply();

    }

    public boolean isUseBackCamera() {
        return sharedPref.getBoolean(USE_BACK_CAMERA_FOR_FACE, DEFAULT_VALUE_FOR_USE_BACK_CAMERA_FOR_FACE);
    }

    public void setUseBackCamera(boolean useBackCamera) {
        sharedPref.edit().putBoolean(USE_BACK_CAMERA_FOR_FACE, useBackCamera).apply();
    }
}
