package com.example.geofencing.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.geofencing.retrofit.Api;
import com.example.geofencing.retrofit.RetrofitClient;
import com.example.geofencing.util.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotification {
    private static final String TAG = "CekNotification";
    private final String userFcmToken;
    private final String title;
    private final String body;
    private final Context context;
    private final String postUrl = "https://fcm.googleapis.com/v1/projects/geofencing-dbb5e/messages:send";

    public SendNotification(String userFcmToken, String title, String body, Context context) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.context = context;
    }

    public void sendNotification() {

        NotificationBody notificationBody = new NotificationBody(title, body);

        MessageBody messageBody = new MessageBody(userFcmToken, notificationBody);

        String accessToken = AccessToken.getAccessToken();
        Log.d(TAG, "sendNotification: "+accessToken);

        Call<ResponseBody> call = RetrofitClient.getInstance().
                getApi().
                sendNotification("Bearer "+accessToken, "application/json",messageBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: cek response : " + response);

                if (response.isSuccessful()) {
                    Toast.makeText(context, "Notification sent", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse: "+response.body().toString());
                }else{
                    Log.d(TAG, "onResponse: "+response);
                    Toast.makeText(context, "Failed to send notification", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });
    }

//    public void sendNotification() {
//
//        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        JSONObject mainObj = new JSONObject();
//
//        try{
//            JSONObject messageObject = new JSONObject();
//            JSONObject notificationObject = new JSONObject();
//
//            notificationObject.put("title", title);
//            notificationObject.put("body", body);
//            messageObject.put("token", userFcmToken);
//            messageObject.put("message", messageObject);
//            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, response -> {
//                // Handle the response
//                Log.d(TAG, "sendNotification: success : "+ response.toString());
//            }, error -> {
//                // Handle the error
//                Log.d(TAG, "sendNotification: "+ error.getMessage());
//            }){
//                @Override
//                public Map<String, String> getHeaders() {
//                    AccessToken accessToken = new AccessToken();
//                    String accessKey = accessToken.getAccessToken();
//                    Map<String, String> headers = new HashMap<>();
//                    headers.put("Content-Type", "application/json");
//                    headers.put("Authorization", "Bearer " + accessKey);
//                    return headers;
//                }
//
//            };
//
//            requestQueue.add(request);
//        } catch (JSONException e) {
//            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//
//    }

}
