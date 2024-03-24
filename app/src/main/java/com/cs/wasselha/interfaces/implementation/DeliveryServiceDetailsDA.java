package com.cs.wasselha.interfaces.implementation;

import android.os.StrictMode;
import android.util.Log;

import com.cs.wasselha.model.DeliveryServiceDetails;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeliveryServiceDetailsDA {


    OkHttpClient client = new OkHttpClient();
    ArrayList<DeliveryServiceDetails> dsdListGlobal = new ArrayList<>();

    Gson gson = new Gson();


    public DeliveryServiceDetailsDA() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

    }

    public ArrayList<DeliveryServiceDetails> getDSDListGlobal() {
        return dsdListGlobal;
    }


    //    @Override
    public ArrayList<DeliveryServiceDetails> getDSDs() throws IOException {


        //String url = "http://176.119.254.198:8000/wasselha/locations/";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            Type dsdListType = new TypeToken<ArrayList<DeliveryServiceDetails>>() {
            }.getType();
            dsdListGlobal = new ArrayList<>();
            dsdListGlobal = gson.fromJson(response.peekBody(2048).string(), dsdListType);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }


        return dsdListGlobal;


    }

    public ArrayList<DeliveryServiceDetails> getDSDsForACustomer(int customerId) throws IOException {


        //String url = "http://176.119.254.198:8000/wasselha/locations/";
        String url = "http://176.119.254.198:8000/wasselha/delivery-service-details/?customer=" + customerId;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            Type dsdListType = new TypeToken<ArrayList<DeliveryServiceDetails>>() {
            }.getType();
            dsdListGlobal = new ArrayList<>();
            dsdListGlobal = gson.fromJson(response.peekBody(5048).string(), dsdListType);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }


        return dsdListGlobal;


    }

    public ArrayList<DeliveryServiceDetails> getDSDsForAService(int serviceId) throws IOException {


        //String url = "http://176.119.254.198:8000/wasselha/locations/";
        String url = "http://176.119.254.198:8000/wasselha/delivery-service-details/?service=" + serviceId;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            Type dsdListType = new TypeToken<ArrayList<DeliveryServiceDetails>>() {
            }.getType();
            dsdListGlobal = new ArrayList<>();
            dsdListGlobal = gson.fromJson(response.peekBody(5048).string(), dsdListType);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }


        return dsdListGlobal;


    }


    // @Override
    public DeliveryServiceDetails getDSD(int id) throws IOException {

        //url= url  + id + "/";
        Request request = new Request.Builder()
                .url(url+ id + "/")
                .build();
        DeliveryServiceDetails deliveryServiceDetails;
        try (Response response = client.newCall(request).execute()) {
            String responseString = response.body().string();

            JsonElement jsonElement = JsonParser.parseString(responseString);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            // Check if "id" is null and set to -1 if it is
            if (jsonObject.get("source_collection_point").isJsonNull()) {
                jsonObject.addProperty("source_collection_point", -1);
            }
            // Check if "id" is null and set to -1 if it is
            if (jsonObject.get("destination_collection_point").isJsonNull()) {
                jsonObject.addProperty("destination_collection_point", -1);
            }
            // Check if "id" is null and set to -1 if it is
            if (jsonObject.get("source_place").isJsonNull()) {
                jsonObject.addProperty("source_place", -1);
            }
            // Check if "id" is null and set to -1 if it is
            if (jsonObject.get("destination_place").isJsonNull()) {
                jsonObject.addProperty("destination_place", -1);
            }


            //Type customerListType = new TypeToken<ArrayList<Customer>>() {}.getType();
            //customerListGlobal = new ArrayList<>();
            deliveryServiceDetails = gson.fromJson(jsonObject, DeliveryServiceDetails.class);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }

        return deliveryServiceDetails;
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    String url = "http://176.119.254.198:8000/wasselha/delivery-service-details/";
    // @Override
    public String saveDSD(DeliveryServiceDetails deliveryServiceDetails) throws IOException {

        // Gson gson = new GsonBuilder()
        //       .excludeFieldsWithoutExposeAnnotation()
        //     .create();
        //String url = "http://176.119.254.198:8000/wasselha/delivery-service-details/";
        RequestBody body = RequestBody.create(gson.toJson(deliveryServiceDetails), JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            Log.d("toJsonTransDSDs2",gson.toJson(deliveryServiceDetails));
            Log.d("res2",response.peekBody(2048).string());
            return response.peekBody(2048).string();
        }

    }
}
