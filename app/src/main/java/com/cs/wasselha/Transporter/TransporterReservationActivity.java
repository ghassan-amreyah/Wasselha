package com.cs.wasselha.Transporter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cs.wasselha.Adapters.RequestsAdapter;
import com.cs.wasselha.Adapters.ReservationsAdapter;
import com.cs.wasselha.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TransporterReservationActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private static String BASE_URL="http://176.119.254.198:8000/wasselha";
    private RequestQueue requestQueue;
    private Gson gson = new Gson();
    ListView listView;
    private ArrayList<Reservations> reservationsData;
    //reservationsData

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transporter_reservation);
            getSupportActionBar().hide();
            SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
            String id = preferences.getString(ID_KEY, null);
            int transporterID = Integer.parseInt(id.trim());

            //Calls
            progressDialog = new ProgressDialog(TransporterReservationActivity.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
            setupRefernces();
            populateReservationsData(transporterID);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }

            }, 1000);
        }catch (Exception e){
            Log.e("TransporterReservationsListView",e.toString());
            Toast.makeText(this, "Networking error, please try later", Toast.LENGTH_SHORT).show();
        }


    }

    private void setupRefernces()
    {
        listView = findViewById(R.id.transporterReservationListView);
    }

    private void populateReservationsData(int transporterID) {
        reservationsData = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        String url = BASE_URL + "/services/?transporter=" + transporterID + "&time=upper";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject service = response.getJSONObject(i);
                        int serviceId = service.getInt("id");
                        String serviceDate = service.getString("service_date");

                        processServiceDetails(serviceId, serviceDate);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private void processServiceDetails(int serviceId, String serviceDate) {
        String deliveryUrl = BASE_URL + "/delivery-service-details/?service=" + serviceId ;

        JsonArrayRequest deliveryDetailsRequest = new JsonArrayRequest(Request.Method.GET, deliveryUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray deliveryResponse) {
                for (int ii = 0; ii < deliveryResponse.length(); ii++){
                    try {
                        JSONObject deliveryDetail = deliveryResponse.getJSONObject(ii);
                        boolean responsed = deliveryDetail.getBoolean("responsed");
                        boolean accepted = deliveryDetail.getBoolean("accepted");  // I guess you want to get accepted, not responsed
                        int deliveryDetailsId = deliveryDetail.getInt("id");

                        if (responsed & accepted) {
                            Log.e("reserveee",deliveryDetail.toString());
                            processAcceptedServiceDetails(serviceDate, deliveryDetail, deliveryDetailsId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
            }
        });

        requestQueue.add(deliveryDetailsRequest);
    }

    private void processAcceptedServiceDetails(String serviceDate, JSONObject deliveryDetail, int deliveryDetailsId) {
        try {
            int customerId = deliveryDetail.getInt("customer");
            String customerUrl = BASE_URL + "/customers/" + customerId + "/";
            String packageUrl = BASE_URL + "/packages/?deliveryservicedetails=" + deliveryDetailsId;

            JsonObjectRequest customerRequest = new JsonObjectRequest(Request.Method.GET, customerUrl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject customerResponse) {
                    try {
                        String customerName = customerResponse.getString("first_name") + " " + customerResponse.getString("last_name");

                        JsonArrayRequest packageRequest = new JsonArrayRequest(Request.Method.GET, packageUrl, null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray packageResponse) {
                                try {
                                    String packageType="there is no package found";
                                    if(packageResponse != null && packageResponse.length()>0) {
                                        JSONObject package_resource = packageResponse.getJSONObject(0);
                                        if(package_resource.has("type"))
                                        packageType = package_resource.getString("type");
                                    }

                                    processLocations(serviceDate, deliveryDetail, deliveryDetailsId, customerName, packageType,customerId);
                                } catch (JSONException e) {
                                    Log.e("claimmm", "error in getting package details");
                                    e.printStackTrace();
                                }
                            }
                        }, error -> {
                            Log.e("claimmm", "error in getting package details");
                        });

                        requestQueue.add(packageRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, error -> {
                Log.e("claimmm", "error in getting customer details");
            });

            requestQueue.add(customerRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processLocations(String serviceDate, JSONObject deliveryDetail, int deliveryDetailsId, String customerName, String packageType,int customerId) throws JSONException {
        int sourcePlace = deliveryDetail.optInt("source_place", -1);
        int sourceCollectionPoint = deliveryDetail.optInt("source_collection_point", -1);
        getCityName(sourcePlace, sourceCollectionPoint, new RequestsTransporterFragment.CityNameCallback() {
            @Override
            public void onCityNameReceived(String sourceCity) {
                int destinationPlace = deliveryDetail.optInt("destination_place", -1);
                int destinationCollectionPoint = deliveryDetail.optInt("destination_collection_point", -1);
                getCityName(destinationPlace, destinationCollectionPoint, new RequestsTransporterFragment.CityNameCallback() {
                    @Override
                    public void onCityNameReceived(String destinationCity) {
                        String dateTime = formatDate(serviceDate);
                        Reservations reservation = new Reservations(deliveryDetailsId, customerName, packageType, sourceCity, destinationCity, dateTime, customerId);
                        Log.e("reserveee",reservation.toString());
                        reservationsData.add(reservation);

                        ReservationsAdapter requestsAdapter = new ReservationsAdapter(getApplicationContext(), R.layout.transporter_reservation_list_view, reservationsData);
                        listView.setAdapter(requestsAdapter);
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void getCityName(int place, int collectionPoint, RequestsTransporterFragment.CityNameCallback callback) {
        String url = "";

        if (place > 0) {
            url = BASE_URL + "/locations/" + place + "/";
        } else if (collectionPoint > 0) {
            url = BASE_URL + "/collection-points/" + collectionPoint + "/";
        } else {
            try {
                callback.onCityNameReceived("Not available!");
            } catch (JSONException e) {
                Log.e("getCityName", "JSON exception", e);
            }
            return;
        }

        JsonObjectRequest locationRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String title = "Not available!";
                            if(place > 0) {
                                title = response.getString("title");
                            } else if(collectionPoint > 0){
                                title = response.getString("name");
                            }
                            callback.onCityNameReceived(title);
                        } catch (JSONException e) {
                            Log.e("getCityName", "JSON exception", e);
                            try {
                                callback.onCityNameReceived("Not available!");
                            } catch (JSONException ex) {
                                Log.e("getCityName", "JSON exception", ex);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("getCityName", "Error: " + error.getMessage(), error);
                        try {
                            callback.onCityNameReceived("Not available!");
                        } catch (JSONException e) {
                            Log.e("getCityName", "JSON exception", e);
                        }
                    }
                }
        );

        requestQueue.add(locationRequest);
    }


    private String formatDate(String serviceDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        LocalDateTime serviceDateTime = LocalDateTime.parse(serviceDate, formatter);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        String date = serviceDateTime.format(dateFormatter);
        String time = serviceDateTime.format(timeFormatter);
        // You can implement this function to format the date and time as per your requirement
        // serviceDate is in the format: "2023-06-18T10:00:00+03:00"
        // You need to split it and concatenate as "yyyy/mm/dd, hh:mm:ss"
        return date+", "+time;
    }
    public interface CityNameCallback {
        void onCityNameReceived(String cityName) throws JSONException;
    }

//        reservationsData.add(new Reservations("Not available!", "Not available!", "Not available!", "Not available!", "Not available!"));
//        reservationsData.add(new Reservations("Not available!", "Not available!","Not available!", "Not available!", "Not available!"));
//        reservationsData.add(new Reservations("Not available!", "Not available!","Not available!", "Not available!", "Not available!"));
//
//    }
}