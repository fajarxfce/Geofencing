package com.example.geofencing.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.geofencing.retrofit.RetrofitClient;
import com.example.geofencing.util.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendNotification {
    private static final String TAG = "CekNotification";

    private final String accessToken;
    private final String userFcmToken;
    private final String title;
    private final String body;
    private final String postUrl = "https://fcm.googleapis.com/v1/projects/geofencing-dbb5e/messages:send";

    public SendNotification(String accessToken, String userFcmToken, String title, String body) {
        this.accessToken = accessToken;
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
    }

    public void sendNotification() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                JSONObject message = new JSONObject();
                JSONObject notification = new JSONObject();
                JSONObject jsonNotif = new JSONObject();


                try {
                    notification.put("title", title);
                    notification.put("body", body);
                    jsonNotif.put("notification", notification);
                    jsonNotif.put("token", userFcmToken);
                    message.put("message", jsonNotif);
                    Log.d("Notif", message.toString());

                } catch (JSONException e) {
                    Log.d("FCM ERROR", e.toString());
                }

                RequestBody rBody = RequestBody.create(message.toString(), mediaType);
                Request request = new Request.Builder()
                        .url(postUrl)
                        .post(rBody)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .addHeader("Content-Type", "application/json")
                        .build();

                try {
                    Response response = client.newCall(request).execute();

                    Log.d("GGWP", response.toString());
                } catch (IOException e) {
                    Log.d("FCM ERROR", e.toString());
                }

                return null;
            }
        }.execute();
    }

//    public void sendNotification() {
//
//        NotificationBody notificationBody = new NotificationBody(title, body);
//
//        MessageBody messageBody = new MessageBody(userFcmToken, notificationBody);
//
//        String accessToken = AccessToken.getAccessToken();
//        Log.d(TAG, "sendNotification: "+accessToken);
//
//        Call<ResponseBody> call = RetrofitClient.getInstance().
//                getApi().
//                sendNotification("Bearer "+accessToken, "application/json",messageBody);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Log.d(TAG, "onResponse: cek response : " + response);
//
//                if (response.isSuccessful()) {
//                    Toast.makeText(context, "Notification sent", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "onResponse: "+response.body().toString());
//                }else{
//                    Log.d(TAG, "onResponse: "+response);
//                    Toast.makeText(context, "Failed to send notification", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.d(TAG, "onFailure: "+t.getMessage());
//            }
//        });
//    }
}
