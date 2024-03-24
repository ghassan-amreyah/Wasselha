package com.cs.wasselha.interfaces.implementation;

import android.os.StrictMode;
import android.util.Log;

import com.cs.wasselha.model.Location;
import com.cs.wasselha.model.Service;
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

public class ServiceDA {

    OkHttpClient client = new OkHttpClient();
    ArrayList<Service> ServiceListGlobal = new ArrayList<>();

    Gson gson = new Gson();


    public ServiceDA() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

    }

    public ArrayList<Service> getServiceListGlobal() {
        return ServiceListGlobal;
    }


    //    @Override
    public ArrayList<Service> getServices() throws IOException {


        String url = "http://176.119.254.198:8000/wasselha/services/?time=upper";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            Type transportersListType = new TypeToken<ArrayList<Service>>() {
            }.getType();
            ServiceListGlobal = new ArrayList<>();
            ServiceListGlobal = gson.fromJson(response.peekBody(2048).string(), transportersListType);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }


        return ServiceListGlobal;


    }


    // @Override
    public Service getService(int id) throws IOException {

        String url = "http://176.119.254.198:8000/wasselha/services/" + id + "/";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Service service;
        try (Response response = client.newCall(request).execute()) {

            //Type customerListType = new TypeToken<ArrayList<Customer>>() {}.getType();
            //customerListGlobal = new ArrayList<>();
            service = gson.fromJson(response.peekBody(2048).string(), Service.class);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }

        return service;
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // @Override
    public String saveService(Service service) throws IOException {

        // Gson gson = new GsonBuilder()
        //       .excludeFieldsWithoutExposeAnnotation()
        //     .create();
        String url = "http://176.119.254.198:8000/wasselha/services/";
        RequestBody body = RequestBody.create(gson.toJson(service), JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            Log.d("toJsonTransLocations2",gson.toJson(service));
            Log.d("res2",response.peekBody(2048).string());
            return response.peekBody(2048).string();
        }

    }
}
