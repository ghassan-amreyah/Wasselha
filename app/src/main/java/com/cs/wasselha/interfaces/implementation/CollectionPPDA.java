package com.cs.wasselha.interfaces.implementation;

import android.os.StrictMode;
import android.util.Log;

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

public class CollectionPPDA {

    OkHttpClient client = new OkHttpClient();
    ArrayList<CollectionPointProvider> collectionPPDAListGlobal = new ArrayList<>();

    Gson gson = new Gson();

    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }


    public ArrayList<CollectionPointProvider> getCollectionPPDAListGlobal() {
        return collectionPPDAListGlobal;
    }


    //    @Override
    public ArrayList<CollectionPointProvider> getCollectionPPs() throws IOException {


        String url = "http://176.119.254.198:8000/wasselha/collection-point-providers/";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            Type collectionPPsListType = new TypeToken<ArrayList<CollectionPointProvider>>() {
            }.getType();
            collectionPPDAListGlobal = new ArrayList<>();
            collectionPPDAListGlobal = gson.fromJson(response.peekBody(2048).string(), collectionPPsListType);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }


        return collectionPPDAListGlobal;


    }


    // @Override
    public CollectionPointProvider getCollectionPP(int id) throws IOException {

        String url = "http://176.119.254.198:8000/wasselha/collection-point-providers/" + id + "/";
        Request request = new Request.Builder()
                .url(url)
                .build();
        CollectionPointProvider collectionPP;
        try (Response response = client.newCall(request).execute()) {

            //Type customerListType = new TypeToken<ArrayList<Customer>>() {}.getType();
            //customerListGlobal = new ArrayList<>();
            collectionPP = gson.fromJson(response.peekBody(2048).string(), CollectionPointProvider.class);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }

        return collectionPP;
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // @Override
    public String saveCollectionPP(CollectionPointProvider collectionPP) throws IOException {
        String url = "http://176.119.254.198:8000/wasselha/collection-point-providers/";
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
}
