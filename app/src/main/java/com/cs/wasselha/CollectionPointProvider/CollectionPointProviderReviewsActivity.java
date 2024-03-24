package com.cs.wasselha.CollectionPointProvider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cs.wasselha.Adapters.ClaimsCollectionPPAdapter;
import com.cs.wasselha.Adapters.ClaimsTransporterAdapter;
import com.cs.wasselha.Claims.Claims;
import com.cs.wasselha.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CollectionPointProviderReviewsActivity extends AppCompatActivity {


    ListView claimsListView;

    private ArrayList<Claims> claimsCollectionPPData;
    //private ArrayList<Claim> claimsDACustomerData;

    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private int collectionPPId;
    private String userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claims_transporter);
        getSupportActionBar().hide();

        //calls
        getFromSharedPref();
        setupReference();
        populateClaimsData();
    }
    void getFromSharedPref(){
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        userType = preferences.getString(LOGIN_TYPE_KEY, null);
        collectionPPId =Integer.parseInt( preferences.getString(ID_KEY,""));


    }
    private void setupReference()
    {
        claimsListView = findViewById(R.id.claimsTransporterListView);
    }

    private static String apiURL="http://176.119.254.198:8000/wasselha";

    private void populateClaimsData( ) {
        claimsCollectionPPData = new ArrayList<>();

        //String url =  apiURL+ "/claims/?written_to_type=transporter&written_to_id=" + transporterID;
        String url = apiURL + "/claims/?written_to_type=collectionpointprovider&written_to_id="+ collectionPPId ;

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
                                            claimsCollectionPPData.add(new Claims(R.drawable.ic_claim, String.valueOf(review),
                                                    message, displayDate, firstName + " " + lastName));
                                            //ClaimsCollectionPPAdapter claimsCollectionPPAdapter = new ClaimsCollectionPPAdapter(getApplicationContext(), R.layout.reviews_collectionpp_list_view, claimsCollectionPPData);
                                            ClaimsTransporterAdapter claimsTransporterAdapter = new ClaimsTransporterAdapter(getApplicationContext(), R.layout.claims_list_view, claimsCollectionPPData,null);

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