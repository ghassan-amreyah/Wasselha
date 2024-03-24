package com.cs.wasselha.interfaces.implementation;

import android.os.StrictMode;
import android.util.Log;

import com.cs.wasselha.model.Package;
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

public class PackageDA {

    OkHttpClient client = new OkHttpClient();
    ArrayList<Package> PackageListGlobal = new ArrayList<>();

    Gson gson = new Gson();


    public PackageDA() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

    }

    public ArrayList<Package> getPackageListGlobal() {
        return PackageListGlobal;
    }


    //    @Override
    public ArrayList<Package> getPackages() throws IOException {


        String url = "http://176.119.254.198:8000/wasselha/packages/";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            Type packagesListType = new TypeToken<ArrayList<Package>>() {
            }.getType();
            PackageListGlobal = new ArrayList<>();
            PackageListGlobal = gson.fromJson(response.peekBody(2048).string(), packagesListType);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }


        return PackageListGlobal;


    }


    // @Override
    public Package getPackage(int id) throws IOException {

        String url = "http://176.119.254.198:8000/wasselha/packages/" + id + "/";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Package Package;
        try (Response response = client.newCall(request).execute()) {

            //Type customerListType = new TypeToken<ArrayList<Customer>>() {}.getType();
            //customerListGlobal = new ArrayList<>();
            Package = gson.fromJson(response.peekBody(2048).string(), Package.class);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }

        return Package;
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // @Override
    public String savePackage(Package Package) throws IOException {

        // Gson gson = new GsonBuilder()
        //       .excludeFieldsWithoutExposeAnnotation()
        //     .create();
        String url = "http://176.119.254.198:8000/wasselha/packages/";
        RequestBody body = RequestBody.create(gson.toJson(Package), JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            Log.d("toJsonTransLocations2",gson.toJson(Package));
            Log.d("res2",response.peekBody(2048).string());
            return response.peekBody(2048).string();
        }

    }
}
