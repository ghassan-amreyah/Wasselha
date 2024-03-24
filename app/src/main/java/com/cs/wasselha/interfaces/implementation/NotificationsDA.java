package com.cs.wasselha.interfaces.implementation;

import android.os.StrictMode;
import android.util.Log;

import com.cs.wasselha.model.Notification;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationsDA {


    OkHttpClient client = new OkHttpClient();
    ArrayList<Notification> NotificationListGlobal = new ArrayList<>();

    Gson gson = new Gson();


    public NotificationsDA() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

    }

    public ArrayList<Notification> getNotificationListGlobal() {
        return NotificationListGlobal;
    }


    //    @Override


    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // @Override
    public String saveNotification(Notification Notification) throws IOException {

        // Gson gson = new GsonBuilder()
        //       .excludeFieldsWithoutExposeAnnotation()
        //     .create();
        String url = "http://176.119.254.198:8000/wasselha/notifications/";
        RequestBody body = RequestBody.create(gson.toJson(Notification), JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            Log.d("toJsonTransLocations2",gson.toJson(Notification));
            Log.d("res2",response.peekBody(2048).string());
            return response.peekBody(2048).string();
        }

    }
}
