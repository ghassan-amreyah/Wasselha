package com.cs.wasselha.Transporter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cs.wasselha.Adapters.ClaimsTransporterAdapter;
import com.cs.wasselha.Adapters.RequestsAdapter;
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


public class RequestsTransporterFragment extends Fragment {

    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private static String BASE_URL="http://176.119.254.198:8000/wasselha";
    ListView listView;
    public static ArrayList<Requests> requestsData;
    private RequestQueue requestQueue;
    private Gson gson = new Gson();
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        try {
        View view = inflater.inflate(R.layout.fragment_requests_transporter, container, false);
        listView = view.findViewById(R.id.listViewInRequestsTransporterFragment);
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String id = preferences.getString(ID_KEY, null);
        int transporterID=Integer.parseInt(id.trim());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        populateRequestsData(transporterID,getContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }

        }, 1000);

        return view;
        }catch (Exception e){
            Log.e("error:",e.toString());
            View view = inflater.inflate(R.layout.fragment_requests_transporter, container, false);
            return view;
        }
    }


    private void populateRequestsData()
    {
        requestsData = new ArrayList<>();

        requestsData.add(new Requests(1,"Not available!", "Not available!","Not available!", "Not available!", "Not available!", "Not available!"));
        requestsData.add(new Requests(1,"Not available!", "Not available!","Not available!", "Not available!", "Not available!", "Not available!"));
        requestsData.add(new Requests(1,"Not available!", "Not available!","Not available!", "Not available!", "Not available!", "Not available!"));

    }
    private void populateRequestsData(int transporterID, Context context) {
        requestQueue = Volley.newRequestQueue(context);
        requestsData = new ArrayList<>();

        String url = BASE_URL + "/services/?transporter=" + transporterID + "&time=upper";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject service = response.getJSONObject(i);
                                int serviceId = service.getInt("id");
                                String serviceDate = service.getString("service_date");

                                String deliveryUrl = BASE_URL + "/delivery-service-details/?service=" + serviceId;
                                getDeliveryDetails(serviceId, serviceDate);
                            } catch (JSONException e) {
                                Log.e("populateRequestsData", "JSON exception", e);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("populateRequestsData", "Error: " + error.getMessage(), error);
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void getDeliveryDetails(int serviceId, String serviceDate) {
        String deliveryUrl = BASE_URL + "/delivery-service-details/?service=" + serviceId;

        JsonArrayRequest deliveryDetailsRequest = new JsonArrayRequest(Request.Method.GET, deliveryUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray deliveryResponse) {
                        for (int ii = 0; ii < deliveryResponse.length(); ii++) {
                            try {
                                JSONObject deliveryDetail = deliveryResponse.getJSONObject(ii);
                                boolean responsed = deliveryDetail.getBoolean("responsed");
                                String price = deliveryDetail.getString("price");
                                int deliveryDetailsId = deliveryDetail.getInt("id");

                                if (!responsed) {
                                    processUnresponsedDetails(deliveryDetail, serviceDate, deliveryDetailsId, price);
                                }
                            } catch (JSONException e) {
                                Log.e("getDeliveryDetails", "JSON exception", e);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("getDeliveryDetails", "Error: " + error.getMessage(), error);
                    }
                }
        );

        requestQueue.add(deliveryDetailsRequest);
    }

    private void processUnresponsedDetails(JSONObject deliveryDetail, String serviceDate, int deliveryDetailsId, String price) throws JSONException {
        int customerId = deliveryDetail.getInt("customer");
        String customerUrl = BASE_URL + "/customers/" + customerId + "/";
        // Fetch customer details
        JsonObjectRequest customerRequest = new JsonObjectRequest(Request.Method.GET, customerUrl, null,
                new Response.Listener<JSONObject>() {
//                    @Sure, let's continue refactoring the code as below:
//
//                            ```java
                    @Override
                    public void onResponse(JSONObject customerResponse) {
                        try {
                            String customerName = customerResponse.getString("first_name") + " " + customerResponse.getString("last_name");
                            String customerReview = String.valueOf(customerResponse.getInt("review"));

                            int sourcePlace = deliveryDetail.optInt("source_place", -1);
                            int srcCollectionPoint = deliveryDetail.optInt("source_collection_point", -1);
                            int destinationPlace = deliveryDetail.optInt("destination_place", -1);
                            int dstCollectionPoint = deliveryDetail.optInt("destination_collection_point", -1);

                            getCityName(sourcePlace, srcCollectionPoint, new CityNameCallback() {
                                @Override
                                public void onCityNameReceived(String sourceCity) {
                                    getCityName(destinationPlace, dstCollectionPoint, new CityNameCallback() {
                                        @Override
                                        public void onCityNameReceived(String destinationCity) {
                                            String dateTime = formatDate(serviceDate);
                                            Requests request = new Requests(deliveryDetailsId, customerName, customerReview, sourceCity, destinationCity, price, dateTime);
                                            requestsData.add(request);
                                            RequestsAdapter requestsAdapter = new RequestsAdapter(getContext(), R.layout.requests_list_view, requestsData);
                                            listView.setAdapter(requestsAdapter);
                                        }
                                    });
                                }
                            });

                        } catch (JSONException e) {
                            Log.e("processUnresponsedDetails", "JSON exception", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("processUnresponsedDetails", "Error: " + error.getMessage(), error);
                    }
                }
        );

        requestQueue.add(customerRequest);
    }

    private void getCityName(int place, int collectionPoint, CityNameCallback callback) {
        String url = "";

        if (place != -1) {
            url = BASE_URL + "/locations/" + place + "/";
        } else if (collectionPoint != -1) {
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
                            if(place>0) {
                                title = response.getString("title");
                            }else if(collectionPoint>0){
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

//...

//    private void populateRequestsData(int transporterID, Context context) {
//        requestQueue = Volley.newRequestQueue(context);
//        requestsData = new ArrayList<>();
//
//        String url = BASE_URL + "/services/?transporter=" + transporterID + "&time=upper";
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                for (int i = 0; i < response.length(); i++) {
//                    try {
//                        JSONObject service = response.getJSONObject(i);
//                        int serviceId = service.getInt("id");
//                        String serviceDate = service.getString("service_date");
//
//                        String deliveryUrl = BASE_URL + "/delivery-service-details/?service=" + serviceId ;
//
//                        // Nested JsonArrayRequest
//                        JsonArrayRequest deliveryDetailsRequest = new JsonArrayRequest(Request.Method.GET, deliveryUrl, null, new Response.Listener<JSONArray>() {
//                            @Override
//                            public void onResponse(JSONArray deliveryResponse) {
//                                for (int ii = 0; ii < deliveryResponse.length(); ii++) {
//                                try {
//                                    JSONObject deliveryDetail = deliveryResponse.getJSONObject(ii);
//
//                                    boolean responsed = deliveryDetail.getBoolean("responsed");
//                                    String price = deliveryDetail.getString("price");
//                                    int deliveryDetailsId = deliveryDetail.getInt("id");
//
//                                    final String[] customerName = {"Not available!"};
//                                    final String[] customerReview = {"Not available!"};
//                                    Log.e("claimmm", "responsed" + responsed);
//                                    if (!responsed) {
//                                        int customerId = deliveryDetail.getInt("customer");
//                                        String customerUrl = BASE_URL + "/customers/" + customerId + "/";
//                                        // Nested JsonObjectRequest
//                                        JsonObjectRequest customerRequest = new JsonObjectRequest(Request.Method.GET, customerUrl, null, new Response.Listener<JSONObject>() {
//                                            @Override
//                                            public void onResponse(JSONObject customerResponse) {
//                                                try {
//                                                    Log.e("claimmm", "get name and review");
//                                                    customerName[0] = customerResponse.getString("first_name") + " " + customerResponse.getString("last_name");
//                                                    customerReview[0] = String.valueOf(customerResponse.getInt("review"));
//                                                } catch (JSONException e) {
//                                                    Log.e("claimmm", "error in get name and review(exception)");
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        }, new Response.ErrorListener() {
//                                            @Override
//                                            public void onErrorResponse(VolleyError error) {
//                                                Log.e("claimmm", "error in get name and review");
//                                                // Handle error
//                                            }
//                                        });
//
//                                        requestQueue.add(customerRequest);
//
//                                        int sourcePlace =-1;// = deliveryDetail.getInt("source_place");
//                                        int src_collectionPoint=-1;
////                                        JsonElement collectionPoint = gson.fromJson(deliveryDetail.toString(), JsonObject.class).get("source_collection_point");
//                                        if (deliveryDetail.has("source_place") && deliveryDetail.get("source_place") !=null) {
//                                            sourcePlace = deliveryDetail.getInt("source_place");
//                                        } else if (deliveryDetail.has("source_collection_point") && deliveryDetail.get("source_collection_point") !=null) {
//                                            src_collectionPoint = deliveryDetail.getInt("source_collection_point");
//                                            // Use the location of the collection point
//                                            // For example: sourcePlace = collectionPoint.getInt("location");
//                                        } else {
//                                            sourcePlace=1;
//                                            // Handle the case when both source_place and source_collection_point are null
//                                        }
//                                        getCityName(sourcePlace, src_collectionPoint, new CityNameCallback() {
//                                            @Override
//                                            public void onCityNameReceived(String sourceCity) throws JSONException {
//                                                int dstPlace =-1;// = deliveryDetail.getInt("source_place");
//                                                int dst_collectionPoint=-1;
////                                        JsonElement collectionPoint = gson.fromJson(deliveryDetail.toString(), JsonObject.class).get("source_collection_point");
//                                                if (deliveryDetail.has("source_place") && deliveryDetail.get("source_place") !=null) {
//                                                    dstPlace = deliveryDetail.getInt("source_place");
//                                                } else if (deliveryDetail.has("source_collection_point") && deliveryDetail.get("source_collection_point") !=null) {
//                                                    dst_collectionPoint = deliveryDetail.getInt("source_collection_point");
//                                                    // Use the location of the collection point
//                                                    // For example: sourcePlace = collectionPoint.getInt("location");
//                                                } else {
//                                                    dstPlace=1;
//                                                    // Handle the case when both source_place and source_collection_point are null
//                                                }
//                                                // This is called when the source city name is retrieved
//                                                getCityName(dstPlace, dst_collectionPoint, new CityNameCallback() {
//                                                    @Override
//                                                    public void onCityNameReceived(String destinationCity) {
//                                                        // This is called when the destination city name is retrieved
//
//                                                        // Now you have both sourceCity and destinationCity
//                                                        // You can use them here to add to your requestsData list
//                                                        String dateTime = formatDate(serviceDate);
//                                                        Log.e("claimmm", "add");
//                                                        Requests request = new Requests(deliveryDetailsId, customerName[0], customerReview[0], sourceCity, destinationCity, price, dateTime);
//                                                        Log.e("request-value", request.toString());
//                                                        requestsData.add(request);
//                                                        try{
//                                                            RequestsAdapter requestsAdapter = new RequestsAdapter(requireContext(), R.layout.requests_list_view, requestsData);
//                                                            listView.setAdapter(requestsAdapter);
//                                                            progressDialog.dismiss();
//                                                            Log.e("claimmm", "dismiss");
//                                                        }catch (Exception e) {
//                                                            Log.e("error:", e.toString());
//                                                        }
//                                                    }
//                                                });
//                                            }
//                                        });
//
//                                    }
////                                    String sourceCity = getCityName(sourcePlace, collectionPoint);
////                                    String destinationCity = getCityName(deliveryDetail.getInt("destination_place"), collectionPoint);
////                                    String dateTime = formatDate(serviceDate);
////
////                                    requestsData.add(new Requests(customerName[0], customerReview[0], sourceCity, destinationCity, price, dateTime));
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            }
//                        }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                // Handle error
//                            }
//                        });
//
//                        requestQueue.add(deliveryDetailsRequest);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // Handle error
//            }
//        });
//
//        requestQueue.add(jsonArrayRequest);
//    }
//    private void getCityName(int place, int collectionPoint, CityNameCallback callback) throws JSONException {
//        if (place != 0) {
//            String url = BASE_URL + "/locations/" + place + "/";
//
//            JsonObjectRequest locationRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    try {
//                        String title = response.getString("title");
//                        callback.onCityNameReceived(title);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        try {
//                            callback.onCityNameReceived("Not available!");
//                        } catch (JSONException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    try {
//                        callback.onCityNameReceived("Not available!");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//            requestQueue.add(locationRequest);
//
//        } else if (collectionPoint >0) {
//            int collectionPointId = collectionPoint;
//            String url = BASE_URL + "/collection-points/" + collectionPointId + "/";
//
//            JsonObjectRequest collectionPointRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    try {
//                        String title = response.getString("title");
//                        callback.onCityNameReceived(title);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        try {
//                            callback.onCityNameReceived("Not available!");
//                        } catch (JSONException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    try {
//                        callback.onCityNameReceived("Not available!");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//            requestQueue.add(collectionPointRequest);
//
//        } else {
//            callback.onCityNameReceived("Not available!");
//        }
//    }

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

}