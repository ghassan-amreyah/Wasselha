package com.cs.wasselha.interfaces.implementation;

import android.os.StrictMode;
import android.util.Log;

import com.cs.wasselha.model.Claim;
import com.cs.wasselha.model.DeliveryStatus;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClaimsDA {



    OkHttpClient client = new OkHttpClient();
    ArrayList<DeliveryStatus> dsdListGlobal = new ArrayList<>();

    Gson gson = new Gson();


    public ClaimsDA() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

    }
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    String url = "http://176.119.254.198:8000/wasselha/claims/";
    // @Override
    public String saveClaim(Claim claim) throws IOException {


        RequestBody body = RequestBody.create(gson.toJson(claim), JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            Log.d("claim",gson.toJson(claim));
            Log.d("claim res",response.peekBody(3048).string());
            return response.peekBody(3048).string();
        }

    }
    public ArrayList<Claim> getClaimsByServiceDetails(int id) throws IOException {
        Request request = new Request.Builder()
                .url(url+"?delivery_service_details_id="+id)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Log.d("claims response", responseBody);

            // Now convert the JSON string into Claim objects
            Claim[] claimArray = gson.fromJson(responseBody, Claim[].class);

            // Convert array to ArrayList and return
            ArrayList<Claim> claimList = new ArrayList<>(Arrays.asList(claimArray));
            return claimList;
        }
    }

    public ArrayList<Claim> getClaimsByWritenToId(int id) throws IOException {
        Request request = new Request.Builder()
                .url(url+"?written_to_id="+id)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Log.d("claims response", responseBody);

            // Now convert the JSON string into Claim objects
            Claim[] claimArray = gson.fromJson(responseBody, Claim[].class);

            // Convert array to ArrayList and return
            ArrayList<Claim> claimList = new ArrayList<>(Arrays.asList(claimArray));
            return claimList;
        }
    }

    public ArrayList<Claim> getClaims() throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Log.d("claims response", responseBody);

            // Now convert the JSON string into Claim objects
            Claim[] claimArray = gson.fromJson(responseBody, Claim[].class);

            // Convert array to ArrayList and return
            ArrayList<Claim> claimList = new ArrayList<>(Arrays.asList(claimArray));
            return claimList;
        }
    }


    public ArrayList<Claim> getClaimsByWritenToIdAndDeliveryServiceDetails(int writenToId,int deliveryServiceDetailsId) throws IOException {
        Request request = new Request.Builder()
                .url(url+"?written_to_id="+writenToId+"&delivery_service_details_id="+deliveryServiceDetailsId)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Log.d("claims response", responseBody);

            // Now convert the JSON string into Claim objects
            Claim[] claimArray = gson.fromJson(responseBody, Claim[].class);

            // Convert array to ArrayList and return
            ArrayList<Claim> claimList = new ArrayList<>(Arrays.asList(claimArray));
            return claimList;
        }
    }

}
