package com.cs.wasselha.interfaces.implementation;

import android.os.StrictMode;

import com.cs.wasselha.interfaces.ICustomerDA;
import com.cs.wasselha.model.Customer;
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

public class CustomerDA implements ICustomerDA {
    OkHttpClient client = new OkHttpClient();
    ArrayList<Customer> customerListGlobal = new ArrayList<>();

    Gson gson = new Gson();

    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }


    public ArrayList<Customer> getCustomerArrList() {
        return customerListGlobal;
    }


    @Override
    public ArrayList<Customer> getCustomers() throws IOException {


        String url = "http://176.119.254.198:8000/wasselha/customers/";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            Type customerListType = new TypeToken<ArrayList<Customer>>() {
            }.getType();
            customerListGlobal = new ArrayList<>();
            customerListGlobal = gson.fromJson(response.peekBody(2048).string(), customerListType);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }


        return customerListGlobal;


    }


    @Override
    public Customer getCustomer(int id) throws IOException {

        String url = "http://176.119.254.198:8000/wasselha/customers/" + id + "/";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Customer customer;
        try (Response response = client.newCall(request).execute()) {

            //Type customerListType = new TypeToken<ArrayList<Customer>>() {}.getType();
            //customerListGlobal = new ArrayList<>();
            customer = gson.fromJson(response.peekBody(2048).string(), Customer.class);
            // todo: here i should fill the data into the activity
            //Log.d("response.body().string()", response.body().string());

        }

        return customer;
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    public String saveCustomer(Customer customer) throws IOException {
        String url = "http://176.119.254.198:8000/wasselha/customers/";
        RequestBody body = RequestBody.create(gson.toJson(customer), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.peekBody(2048).string();
        }
    }


}
