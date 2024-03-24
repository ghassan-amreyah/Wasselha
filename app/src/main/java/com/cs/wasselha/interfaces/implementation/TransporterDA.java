package com.cs.wasselha.interfaces.implementation;

import android.os.StrictMode;
import android.util.Log;

import com.cs.wasselha.model.Transporter;
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

public class TransporterDA {


    OkHttpClient client = new OkHttpClient();
    ArrayList<Transporter> transporterListGlobal = new ArrayList<>();

    Gson gson = new Gson();


    public TransporterDA() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

    }

    public ArrayList<Transporter> getTransporterListGlobal() {
        return transporterListGlobal;
    }


    //    @Override
    public ArrayList<Transporter> getTransporters() throws IOException {


        String url = "http://176.119.254.198:8000/wasselha/transporters/";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            Type transportersListType = new TypeToken<ArrayList<Transporter>>() {
            }.getType();
            transporterListGlobal = new ArrayList<>();
            transporterListGlobal = gson.fromJson(response.peekBody(2048).string(), transportersListType);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }


        return transporterListGlobal;


    }


    // @Override
    public Transporter getTransporter(int id) throws IOException {

        String url = "http://176.119.254.198:8000/wasselha/transporters/" + id + "/";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Transporter transporter;
        try (Response response = client.newCall(request).execute()) {

            //Type customerListType = new TypeToken<ArrayList<Customer>>() {}.getType();
            //customerListGlobal = new ArrayList<>();
            transporter = gson.fromJson(response.peekBody(2048).string(), Transporter.class);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }

        return transporter;
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // @Override
    public String saveTransporter(Transporter transporter) throws IOException {

      //  Gson gson = new GsonBuilder()
        //        .excludeFieldsWithoutExposeAnnotation()
          //      .create();
        String url = "http://176.119.254.198:8000/wasselha/transporters/";
        RequestBody body = RequestBody.create(gson.toJson(transporter), JSON);
        Log.d("toJsonTrans",gson.toJson(transporter));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            Log.d("trans2",response.peekBody(2048).string());
            return response.peekBody(2048).string();
        }
    }


}
