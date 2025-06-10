package com.sssl.seqrgraphdemo.networks;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiSeQRClient {
    public static ApiSeQRClient instance;

    public SeqrCodeService seqrCodeService;

    public String BaseSeQRUrl = "https://demo.seqrdoc.com/";

    ApiSeQRClient(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseSeQRUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        seqrCodeService = retrofit.create(SeqrCodeService.class);
    }

    public static ApiSeQRClient getInstance(){
        if(instance == null){
            instance = new ApiSeQRClient();
        }
        return instance;
    }
}
