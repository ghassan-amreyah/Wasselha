package com.cs.wasselha.interfaces.implementation;

import android.os.StrictMode;
import android.util.Log;

import com.cs.wasselha.model.Location;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LocationDA {

    OkHttpClient client = new OkHttpClient();/*.Builder()
            .connectTimeout(5, TimeUnit.MINUTES) // connect timeout
            .writeTimeout(5, TimeUnit.MINUTES) // write timeout
            .readTimeout(5, TimeUnit.MINUTES) // read timeout
            .build();*/
    ArrayList<Location> locationListGlobal = new ArrayList<>();

    Gson gson = new Gson();


    public LocationDA() {
       // OkHttpClient client = new OkHttpClient();
        //client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
        //client.setReadTimeout(30, TimeUnit.SECONDS);    // socket timeout
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

    }

    public ArrayList<Location> getLocationListGlobal() {
        return locationListGlobal;
    }


    //    @Override
    public ArrayList<Location> getLocations() throws IOException {


        String url = "http://176.119.254.198:8000/wasselha/locations/";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            Type transportersListType = new TypeToken<ArrayList<Location>>() {
            }.getType();
            locationListGlobal = new ArrayList<>();
            locationListGlobal = gson.fromJson(response.peekBody(2048).string(), transportersListType);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }


        return locationListGlobal;


    }


    // @Override
    public Location getLocation(int id) throws IOException {

        String url = "http://176.119.254.198:8000/wasselha/locations/" + id + "/";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Location transporter;
        try (Response response = client.newCall(request).execute()) {

            //Type customerListType = new TypeToken<ArrayList<Customer>>() {}.getType();
            //customerListGlobal = new ArrayList<>();
            transporter = gson.fromJson(response.peekBody(2048).string(), Location.class);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }

        return transporter;
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // @Override
    public int saveLocation(Location location) throws IOException {

       // Gson gson = new GsonBuilder()
         //       .excludeFieldsWithoutExposeAnnotation()
           //     .create();
        String url = "http://176.119.254.198:8000/wasselha/locations/";
        RequestBody body = RequestBody.create(gson.toJson(location), JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            Log.d("locationPost",gson.toJson(location));
            Location locationResponse = gson.fromJson(response.peekBody(8048).string(),Location.class);
            //Log.d("locationPostResponse",);
            return locationResponse.getId();
        }

    }
}
