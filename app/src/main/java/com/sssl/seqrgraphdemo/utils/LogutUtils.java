package com.sssl.seqrgraphdemo.utils;

import android.util.Log;

import com.sssl.seqrgraphdemo.models.login.LogoutResponse;
import com.sssl.seqrgraphdemo.networks.ApiSeQRClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogutUtils {

    public static Object logoutAsVerifier(String access_token , String api_key){

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("accesstoken", access_token);
        headers.put("apikey", api_key);

        Log.d("Tag" , "Header " + headers);


        Map<String, String> apiResponse = new HashMap<>();

        ApiSeQRClient.getInstance().seqrCodeService.logout(headers).enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                LogoutResponse logoutResponse = response.body();
                Log.d("Tag" , "ApiResponse 1 : " + logoutResponse.success);
                Log.d("Tag" , "ApiResponse 2 : " + logoutResponse.message);

                Log.d("Tag" , "ApiResponse 3 : " + logoutResponse.status);

                apiResponse.put("message", logoutResponse.message);
                apiResponse.put("status", logoutResponse.status);
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                Log.d("Tag" , "Error " + t.getLocalizedMessage());
            }
        });
        return apiResponse;
    }

}
