package com.example.geofencing.retrofit;
/*
Author  : Nurul Fajar
Email   : cirebonredhat@gmail.com
*/



import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://fcm.googleapis.com/v1/projects/geofencing-dbb5e/";
//    private static final String BASE_URL = "http://192.168.40.246:8000/api/v1/";
    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public Api getApi(){
        return retrofit.create(Api.class);
    }
}
