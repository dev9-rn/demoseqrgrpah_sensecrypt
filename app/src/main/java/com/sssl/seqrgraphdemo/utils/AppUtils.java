package com.sssl.seqrgraphdemo.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;

import java.util.HashMap;
import java.util.Map;

public class AppUtils {

    public static Map<String, String> getApiHeader(Context context){

        AppSharedPreference appSharedPreference = new AppSharedPreference(context);

        String accessToken = appSharedPreference.getAccessToken();
        String apiKey = appSharedPreference.getApiKey();

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("accesstoken", accessToken);
        headers.put("apikey", apiKey);

        return headers;
    }

    public static Map<String, String> getLoginApiHeader(Context context){

        AppSharedPreference appSharedPreference = new AppSharedPreference(context);

        String accessToken = appSharedPreference.getAccessToken();
        String apiKey = appSharedPreference.getApiKey();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("apikey", apiKey);

        return headers;
    }

    public static void showMessage(Context context, String message) {
        ((Activity) context).runOnUiThread(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
    }

    public static String getCertificateKey(String result){

        String someText = result.replaceAll("^\\s+|\\s+$", "");
        String key;

        if (someText.contains("\n")) {
            // Extract substring after the last '\n'
            key = someText.substring(someText.lastIndexOf("\n") + 1);
//            someText = someText.substring(0, someText.lastIndexOf("\n"));
        } else if (someText.contains(" ")) {
            // Extract substring after the last space
            key = someText.substring(someText.lastIndexOf(" ") + 1);
//            someText = someText.substring(0, someText.lastIndexOf(" "));
        } else {
            // If no newline or space, use the original string
            key = result;
//            someText = "";
        }
        return key;
    }

}
