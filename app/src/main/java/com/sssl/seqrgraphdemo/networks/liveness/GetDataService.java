package com.sssl.seqrgraphdemo.networks.liveness;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface GetDataService {
    @Multipart
    @POST
    Call<LivenessResponseModel> checkLiveness(@Url String url, @Part MultipartBody.Part data);


//    @Multipart
//    @POST
//    Call<LivenessResponse> checkLivenessInHouse(@Url String url, @Part MultipartBody.Part data);

}
