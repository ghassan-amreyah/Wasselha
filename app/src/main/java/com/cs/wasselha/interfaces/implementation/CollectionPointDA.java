package com.cs.wasselha.interfaces.implementation;

import android.os.StrictMode;
import android.util.Log;

import com.cs.wasselha.model.CollectionPoint;
import com.cs.wasselha.model.CollectionPointProvider;
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

public class CollectionPointDA {

    OkHttpClient client = new OkHttpClient();
    ArrayList<CollectionPoint> collectionPDAListGlobal = new ArrayList<>();

    Gson gson = new Gson();

    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }


    public ArrayList<CollectionPoint> getCollectionPDAListGlobal() {
        return collectionPDAListGlobal;
    }


    //    @Override
    public ArrayList<CollectionPoint> getCollectionPs() throws IOException {


        String url = "http://176.119.254.198:8000/wasselha/collection-points/";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            Type collectionPsListType = new TypeToken<ArrayList<CollectionPoint>>() {
            }.getType();
            collectionPDAListGlobal = new ArrayList<>();
            collectionPDAListGlobal = gson.fromJson(response.peekBody(5048).string(), collectionPsListType);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }


        return collectionPDAListGlobal;


    }
    //    @Override
    public ArrayList<CollectionPoint> getCollectionPsByCPPid(int id) throws IOException {


        String url = "http://176.119.254.198:8000/wasselha/collection-points/?collectionpointprovider="+id;
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            Type collectionPsListType = new TypeToken<ArrayList<CollectionPoint>>() {
            }.getType();
            collectionPDAListGlobal = new ArrayList<>();
            collectionPDAListGlobal = gson.fromJson(response.peekBody(5048).string(), collectionPsListType);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }


        return collectionPDAListGlobal;


    }


    // @Override
    public CollectionPoint getCollectionP(int id) throws IOException {

        String url = "http://176.119.254.198:8000/wasselha/collection-points/" + id + "/";
        Request request = new Request.Builder()
                .url(url)
                .build();
        CollectionPoint collectionPP;
        try (Response response = client.newCall(request).execute()) {

            //Type customerListType = new TypeToken<ArrayList<Customer>>() {}.getType();
            //customerListGlobal = new ArrayList<>();
            Log.d("104CP",response.peekBody(5048).string());

            collectionPP = gson.fromJson(response.peekBody(5048).string(), CollectionPoint.class);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }

        return collectionPP;
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // @Override
    public String saveCollectionP(CollectionPoint collectionPP) throws IOException {
        String url = "http://176.119.254.198:8000/wasselha/collection-points/";
        RequestBody body = RequestBody.create(gson.toJson(collectionPP), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            Log.d("cppDA2", response.peekBody(2048).string());
            return response.peekBody(2048).string();
        }
    }
    public String updateCollectionP(int id, CollectionPoint updatedCollectionP) throws IOException {
        String url = "http://176.119.254.198:8000/wasselha/collection-points/" + id + "/";
        RequestBody body = RequestBody.create(gson.toJson(updatedCollectionP), JSON);
        Request request = new Request.Builder()
                .url(url)
                .put(body)  // Use PUT for update
                .build();
        try (Response response = client.newCall(request).execute()) {
            Log.d("updateCollectionP", response.peekBody(2048).string());
            return response.peekBody(2048).string();
        }
    }


}
