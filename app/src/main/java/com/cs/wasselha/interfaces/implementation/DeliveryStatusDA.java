package com.cs.wasselha.interfaces.implementation;

import android.os.StrictMode;
import android.util.Log;

import com.cs.wasselha.model.DeliveryStatus;
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

public class DeliveryStatusDA {



    OkHttpClient client = new OkHttpClient();
    ArrayList<DeliveryStatus> dsdListGlobal = new ArrayList<>();

    Gson gson = new Gson();


    public DeliveryStatusDA() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

    }

    public ArrayList<DeliveryStatus> getDSDListGlobal() {
        return dsdListGlobal;
    }


    //    @Override
    public ArrayList<DeliveryStatus> getDSDs() throws IOException {


        //String url = "http://176.119.254.198:8000/wasselha/locations/";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            Type dsdListType = new TypeToken<ArrayList<DeliveryStatus>>() {
            }.getType();
            dsdListGlobal = new ArrayList<>();
            dsdListGlobal = gson.fromJson(response.peekBody(2048).string(), dsdListType);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }


        return dsdListGlobal;


    }

    public ArrayList<DeliveryStatus> getDelStatuses(int deliveryServiceDetls) throws IOException {


        //String url = "http://176.119.254.198:8000/wasselha/locations/";
        String url = "http://176.119.254.198:8000/wasselha/delivery-status/?delivery_service_details=" + deliveryServiceDetls;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            Type dsdListType = new TypeToken<ArrayList<DeliveryStatus>>() {
            }.getType();
            dsdListGlobal = new ArrayList<>();
            dsdListGlobal = gson.fromJson(response.peekBody(5048).string(), dsdListType);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }


        return dsdListGlobal;


    }




    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    String url = "http://176.119.254.198:8000/wasselha/delivery-service-details/";
    // @Override
    public String saveDSD(DeliveryStatus DeliveryStatus) throws IOException {

        // Gson gson = new GsonBuilder()
        //       .excludeFieldsWithoutExposeAnnotation()
        //     .create();
        //String url = "http://176.119.254.198:8000/wasselha/delivery-service-details/";
        RequestBody body = RequestBody.create(gson.toJson(DeliveryStatus), JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            Log.d("toJsonTransDSDs2",gson.toJson(DeliveryStatus));
            Log.d("res2",response.peekBody(2048).string());
            return response.peekBody(2048).string();
        }

    }
}
