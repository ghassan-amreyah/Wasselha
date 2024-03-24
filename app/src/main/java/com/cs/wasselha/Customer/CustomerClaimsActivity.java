package com.cs.wasselha.Customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cs.wasselha.Adapters.ClaimsTransporterAdapter;
import com.cs.wasselha.Claims.Claims;
import com.cs.wasselha.R;
import com.cs.wasselha.model.Claim;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CustomerClaimsActivity extends AppCompatActivity {

    ListView claimsListView;

    private ArrayList<Claims> claimsCustomerData;
    private ArrayList<Claim> claimsDACustomerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claims_transporter);
        getSupportActionBar().hide();

        Intent intent = getIntent();

        String customerId = intent.getStringExtra("customerId");
        //Calls
        setupReference();
        populateClaimsData(customerId);

       // ArrayAdapter<Claim> claimsAdapterItems = new ArrayAdapter<Claim>(this,
         //       android.R.layout.simple_list_item_1, claimsCustomerData);
        //ClaimsTransporterAdapter claimsCAdapter = new ClaimsTransporterAdapter(getApplicationContext(), R.layout.claims_list_view,
          //      claimsCustomerData,claimsDACustomerData);
        //claimsListView.setAdapter(claimsCAdapter);

    }


    //--------------Methods---------------------------------------------------------
    //References
    private void setupReference()
    {
        claimsListView = findViewById(R.id.claimsTransporterListView);
    }

    private static String apiURL="http://176.119.254.198:8000/wasselha";

    /*private void populateClaimsData(Context context,String customerId)
    {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = apiURL + "/claims/?written_to_type=customer&written_to_id="+ customerId ;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Directly loop through the JSON Array response
                            // Convert JSONArray to a list of JSONObjects
                            List<JSONObject> responseList = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject claim = response.getJSONObject(i);
                                responseList.add(claim);
                            }

                            // Sort the list of JSONObjects by date
                            Collections.sort(responseList, new Comparator<JSONObject>() {
                                @Override
                                public int compare(JSONObject obj1, JSONObject obj2) {
                                    try {
                                        String date1 = obj1.getString("date");
                                        String date2 = obj2.getString("date");
                                        return date2.compareTo(date1); // Sort in descending order
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    return 0;
                                }
                            });
                            for (int i = 0; i < responseList.size(); i++) {
                                int id=responseList.get(i).getInt("id");
                                int delivery_service_details=responseList.get(i).getInt("delivery_service_details");
                                String writer_id=responseList.get(i).getString("writer_id");
                                String writer_type=responseList.get(i).getString("writer_type");
                                String written_to_id=responseList.get(i).getString("written_to_id");
                                String written_to_type=responseList.get(i).getString("written_to_type");
                                String review=responseList.get(i).getString("review");
                                String message=responseList.get(i).getString("message");
                                String date1=responseList.get(i).getString("date");

                                DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                                LocalDateTime claimsDateTime = LocalDateTime.parse(date1, formatter);
                                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                                String date = claimsDateTime.format(dateFormatter);
                                String time = claimsDateTime.format(timeFormatter);
                                claimsDACustomerData.add(new Claim(id,delivery_service_details,
                                        Integer.parseInt(writer_id),Integer.parseInt(written_to_id),writer_type,written_to_type,
                                        message,Integer.parseInt(review),claimsDateTime));


                                claimsCustomerData.add(new Claims(id,review,message,date+time,writer_type));

                            }
                            ClaimsTransporterAdapter claimsCAdapter = new ClaimsTransporterAdapter(getApplicationContext(), R.layout.claims_list_view,
                                    claimsCustomerData,claimsDACustomerData);
                            claimsListView.setAdapter(claimsCAdapter);

                        } catch (Exception e) {
                            Log.e("notification","notification not found");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("notification", "Error: " + error.toString());
                if (error.networkResponse != null) {
                    Log.e("notification", "Status code: " + error.networkResponse.statusCode);
                }
            }
        });

        queue.add(jsonArrayRequest);

    }*/

    private void populateClaimsData( String customerId) {
        claimsCustomerData = new ArrayList<>();

        //String url =  apiURL+ "/claims/?written_to_type=transporter&written_to_id=" + transporterID;
        String url = apiURL + "/claims/?written_to_type=customer&written_to_id="+ customerId ;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.e("claimmm","length"+response.length());
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject claimObject = response.getJSONObject(i);

                            int writerId = claimObject.getInt("writer_id");
                            String writerType = claimObject.getString("writer_type");
                            int review = claimObject.getInt("review");
                            String message = claimObject.getString("message");
                            String dateTime = claimObject.getString("date");

                            String[] dateSplit = dateTime.split("T");
                            String displayDate = dateSplit[0] + ", " + dateSplit[1].substring(0, dateSplit[1].indexOf('+'));

                            String writerUrl = "";
                            if ("customer".equals(writerType)) {
                                writerUrl = apiURL + "/customers/" + writerId + "/";
                            } else if ("collectionpointprovider".equals(writerType)) {
                                writerUrl = apiURL + "/collection-point-providers/" + writerId + "/";
                            } else {
                                writerUrl = apiURL + "/transporters/" + writerId + "/";
                            }

                            JsonObjectRequest writerRequest = new JsonObjectRequest(Request.Method.GET, writerUrl, null,
                                    writerResponse -> {
                                        try {
                                            String firstName = writerResponse.getString("first_name");
                                            String lastName = writerResponse.getString("last_name");
                                            Log.e("claimmm","add to claims list");
                                            claimsCustomerData.add(new Claims(R.drawable.ic_claim, String.valueOf(review),
                                                    message, displayDate, firstName + " " + lastName));
                                            ClaimsTransporterAdapter claimsTransporterAdapter = new ClaimsTransporterAdapter(getApplicationContext(), R.layout.claims_list_view, claimsCustomerData,null);
                                            claimsListView.setAdapter(claimsTransporterAdapter);
                                            //progressDialog.dismiss();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Log.e("claimmm",e.toString());
                                        }
                                    },
                                    error -> {
                                        // Handle error for writer request
                                        error.printStackTrace();
                                        Log.e("claimmm",error.toString());
                                    }
                            );

                            RequestQueue requestQueue = Volley.newRequestQueue(this);
                            requestQueue.add(writerRequest);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("claimmm",e.toString());
                        }
                    }
                },
                error -> {
                    // Handle error
                    error.printStackTrace();
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
}
