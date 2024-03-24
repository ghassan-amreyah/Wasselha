package com.cs.wasselha.interfaces.implementation;

import android.os.StrictMode;
import android.util.Log;

import com.cs.wasselha.model.Vehicle;
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

public class VehiclesDA {


    OkHttpClient client = new OkHttpClient();
    ArrayList<Vehicle> vehiclesListGlobal = new ArrayList<>();

    Gson gson = new Gson();


    public VehiclesDA() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

    }

    public ArrayList<Vehicle> getVehiclesListGlobal() {
        return vehiclesListGlobal;
    }


    //    @Override
    public ArrayList<Vehicle> getVehicles() throws IOException {


        String url = "http://176.119.254.198:8000/wasselha/vehicles/";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            Type vehiclesListType = new TypeToken<ArrayList<Vehicle>>() {
            }.getType();
            vehiclesListGlobal = new ArrayList<>();
            Log.d("vehDA",response.peekBody(7028).string());
            vehiclesListGlobal = gson.fromJson(response.peekBody(7048).string().trim(), vehiclesListType);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }


        return vehiclesListGlobal;


    }


    // @Override
    public Vehicle getVehicle(int id) throws IOException {

        String url = "http://176.119.254.198:8000/wasselha/vehicles/" + id + "/";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Vehicle vehicle;
        try (Response response = client.newCall(request).execute()) {

            //Type customerListType = new TypeToken<ArrayList<Customer>>() {}.getType();
            //customerListGlobal = new ArrayList<>();
            vehicle = gson.fromJson(response.peekBody(2048).string(), Vehicle.class);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }

        return vehicle;
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // @Override
    public String saveVehicle(Vehicle vehicle) throws IOException {

        // Gson gson = new GsonBuilder()
        //       .excludeFieldsWithoutExposeAnnotation()
        //     .create();
        String url = "http://176.119.254.198:8000/wasselha/vehicles/";
        RequestBody body = RequestBody.create(gson.toJson(vehicle), JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            Log.d("toJsonTransLocations2",gson.toJson(vehicle));
            Log.d("res2",response.peekBody(2048).string());
            return response.peekBody(2048).string();
        }

    }

    public String getVehicleTypeOfTransporter(int transporterID) throws IOException {

        getVehicles();

        Log.d("size11",vehiclesListGlobal.size() +"");
        Log.d("size1111", vehiclesListGlobal.toString());
       // JSONArray vehicles = response.getJSONArray("vehicles");
        for (int i = 0; i < vehiclesListGlobal.size(); i++) {
            Vehicle vehicle = vehiclesListGlobal.get(i);

           // Log.d("veh111",vehicle.toString());

            if (vehicle.getTransporter()  == transporterID) {
                Log.d("getVehicle_image",vehicle.getVehicle_image().toString());
                return vehicle.getVehicle_type();
                //setImage(apiURL+vehicle.getString("vehicle_image"));
                //return;
            }
        }
        return "null";
    }

    public String getVehicleImageURLOfTransporter(int transporterID) throws IOException {

        if (vehiclesListGlobal.size() ==0)
            getVehicles();

        //Log.d("size11",vehiclesListGlobal.size() +"");
        //Log.d("size1111", vehiclesListGlobal.toString());
        // JSONArray vehicles = response.getJSONArray("vehicles");
        for (int i = 0; i < vehiclesListGlobal.size(); i++) {
            Vehicle vehicle = vehiclesListGlobal.get(i);

           // Log.d("veh111",vehicle.toString());

            if (vehicle.getTransporter()  == transporterID) {
                Log.d("getVehicle_image",vehicle.getVehicle_image().toString());
                return vehicle.getVehicle_image();
                //setImage(apiURL+vehicle.getString("vehicle_image"));
                //return;
            }
        }
        return "null";
    }
}
