package com.cs.wasselha.Transporter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.cs.wasselha.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class VehicleInformationActivity extends AppCompatActivity {
    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private static String apiURL="http://176.119.254.198:8000/wasselha";
    ImageView vehicleImage,vehicleLicense;
    TextView vehicleNumber,vehicleType;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_vehicle_information);
            getSupportActionBar().hide();

            setupReference();
            SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
            String id = preferences.getString(ID_KEY, null);
            int transporterID=Integer.parseInt(id.trim());
            getAndSetCarInfo(transporterID);

            progressDialog = new ProgressDialog(VehicleInformationActivity.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    progressDialog.dismiss();
                }

            }, 1000);
        }
        catch (Exception e)
        {
            Log.e("error:",e.toString());
        }


    }

    private void getAndSetCarInfo(int transporterID) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = apiURL + "/vehicles/?transporter="+transporterID;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Directly loop through the JSON Array response
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject vehicle = response.getJSONObject(i);
                                if (vehicle.getInt("transporter") == transporterID) {
                                    setvehicleImage(apiURL + vehicle.getString("vehicle_image"));
                                    setvehicleLicense(apiURL + vehicle.getString("vehicle_license"));
                                    vehicleNumber.setText(vehicle.getString("vehicle_number"));
                                    vehicleType.setText(vehicle.getString("vehicle_type"));
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            Log.e("profile","Transporter not found");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("profile", "Error: " + error.toString());
                if (error.networkResponse != null) {
                    Log.e("profile", "Status code: " + error.networkResponse.statusCode);
                }
                Log.e("profile","failed loading image(Network Issue )");
            }
        });

        queue.add(jsonArrayRequest);
    }
    void setvehicleImage(String vehicleImageUrl){
        Glide.with(this)
                .load(vehicleImageUrl)
                .into(vehicleImage);
    }
    void setvehicleLicense(String vehicleLicenseUrl){
        Glide.with(this)
                .load(vehicleLicenseUrl)
                .into(vehicleLicense);
    }

    private void setupReference()
    {
        vehicleImage=findViewById(R.id.vehicleImageInCarInfoPage);
        vehicleLicense=findViewById(R.id.vehicleLicenseImageInCarInfoPage);
        vehicleNumber=findViewById(R.id.vehicleNumberInCarInfoPage);
        vehicleType=findViewById(R.id.vehicleTypeInCarInfoPage);
    }




    //--------------Methods----------------------------------------------




}