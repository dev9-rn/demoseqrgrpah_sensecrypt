package com.sssl.seqrgraphdemo.networks;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    private ApiClient() {
        Log.d("TAG", "RetrofitClientInstance");
    }

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {


            OkHttpClient.Builder client = new OkHttpClient.Builder();


            client
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS).build();


            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.addInterceptor(loggingInterceptor);


            retrofit = new Retrofit.Builder()
                    .baseUrl(AppSharedPreference.CRYPTO_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();
        }
        return retrofit;
    }
}
