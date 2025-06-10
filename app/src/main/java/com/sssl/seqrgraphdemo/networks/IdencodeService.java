package com.sssl.seqrgraphdemo.networks;

import com.sssl.seqrgraphdemo.models.BarCodeResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface IdencodeService {



    @Multipart
    @POST()
    Call<BarCodeResponse> createBarCode(@Url String url, @Part List<MultipartBody.Part> parts);


    @GET
    Call<ResponseBody> getPublicKeys(@Url String url);





}