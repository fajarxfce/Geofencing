package com.example.geofencing.retrofit;
/*
Author  : Nurul Fajar
Email   : cirebonredhat@gmail.com
*/

import com.example.geofencing.model.MessageBody;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Api {
    @POST("messages:send")
    Call<ResponseBody> sendNotification(
            @Header("Authorization") String authorization,
            @Header("Content-Type") String contentType,
            @Body MessageBody messageBody);
}