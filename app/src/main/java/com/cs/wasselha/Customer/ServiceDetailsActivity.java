package com.cs.wasselha.Customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.cs.wasselha.R;
import com.cs.wasselha.interfaces.implementation.CollectionPointDA;
import com.cs.wasselha.interfaces.implementation.CustomerDA;
import com.cs.wasselha.interfaces.implementation.LocationDA;
import com.cs.wasselha.interfaces.implementation.NotificationsDA;
import com.cs.wasselha.interfaces.implementation.PackageDA;
import com.cs.wasselha.interfaces.implementation.TransporterDA;
import com.cs.wasselha.model.CollectionPoint;
import com.cs.wasselha.model.Customer;
import com.cs.wasselha.model.DeliveryServiceDetails;
import com.cs.wasselha.model.Location;
import com.cs.wasselha.model.Notification;
import com.cs.wasselha.model.Package;
import com.cs.wasselha.model.Service;
import com.cs.wasselha.model.Transporter;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ServiceDetailsActivity extends AppCompatActivity {

    Service service;

    TextView transporterNameInServiceDet;
    TextView timeInCustomer;
    TextView srcCity;
    TextView destCity;
    TextView vehicleTypeInServiceDet;
    TextView priceInServiceDet;
    Button reserveBtnServiceDetailsPage;
    ImageView imageViewCar;

    TextView transporterReviewTXT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);
        getSupportActionBar().hide();

        setUpViews();
        try {
            getFromSharedPref();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Intent intent = getIntent();
        Log.d("inSDA1", intent.toString());
        if (intent != null) {

            Log.d("inSDA2", intent.toString());
            String strObj = intent.getStringExtra("serviceDet");
            String transporterName = intent.getStringExtra("transporterName");


            Gson gson = new Gson();
            service = gson.fromJson(strObj, Service.class);
            String vehicleType = intent.getStringExtra("vehicleType");

            String imageUrl = intent.getStringExtra("imageUrl");

            String transporterId = intent.getStringExtra("transporterId");
            String transporterReview = intent.getStringExtra("reviewT");
            String srcCityName = intent.getStringExtra("srcCity");
            String destCityName = intent.getStringExtra("destCity");

            transporterReviewTXT.setText(transporterReview);
            transporterNameInServiceDet.setText(transporterName);
            Log.d("serv89",service.getId()+"");
            timeInCustomer.setText(service.getService_date().toString());


            srcCity.setText(srcCityName);

            destCity.setText(destCityName);

            vehicleTypeInServiceDet.setText(vehicleType);
            priceInServiceDet.setText(String.valueOf(service.getPrice()));
            Log.d("inSDA3", service.getPrice() + "");

            Glide.with(this)
                    .load(imageUrl)
                    .into(imageViewCar);
        }

        reserveBtnServiceDetailsPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("before1212", "before go to the reservetion det");
                Intent intent = new Intent(ServiceDetailsActivity.this, ReservationDetailsActivity.class);
                startActivityForResult(intent, 2);// Activity is started with requestCode 2
                Log.d("after1212", "after go to the reservetion det");

                // todo: here i should route it to the reserveAService activity Page


                // todo: then issue a notification and store it in the database

            }
        });


    }

    private void setUpViews() {

        transporterNameInServiceDet = findViewById(R.id.transporterNameInServiceDetailsPage);
        timeInCustomer = findViewById(R.id.timeInInServiceDetailsPage);
        srcCity = findViewById(R.id.sourceCityInServiceDetailsPage);

        destCity = findViewById(R.id.destinationCityInServiceDetailsPage);
        vehicleTypeInServiceDet = findViewById(R.id.vehicleTypeInServiceDetailsPage);

        priceInServiceDet = findViewById(R.id.priceInServiceDetailsPage);
        reserveBtnServiceDetailsPage = findViewById(R.id.reserveBtnServiceDetailsPage);

        imageViewCar = findViewById(R.id.imageViewVehicleInServiceDetails);
        transporterReviewTXT = findViewById(R.id.transporterReviewInInServiceDetailsPage);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2) {
            // String message=data.getStringExtra("MESSAGE");


            String fromCPLocationId = data.getStringExtra("fromCPLocationId");
            Log.d("fromCPLocationId",fromCPLocationId+"");
            String toCPLocationId = data.getStringExtra("toCPLocationId");
            Log.d("toCPLocationId",toCPLocationId+"");

            String packWeight = data.getStringExtra("packWeight");
            String packType = data.getStringExtra("packType");
            String personHandoverNameStr = data.getStringExtra("personHandoverNameStr");

            Log.d("SDApackWeight",packWeight);


            String sourceLocationId = data.getStringExtra("fromLocationId");
            String destLocationId = data.getStringExtra("toLocationId");

            Log.d("SDAsourceLocationId",sourceLocationId+"");
            Log.d("SDAdestLocationId",destLocationId+"");

            addDelServiceDetails(fromCPLocationId, toCPLocationId, packWeight, packType, sourceLocationId, destLocationId,personHandoverNameStr);

            // textView1.setText(message);
        }
    }

    private static String BASE_URL = "http://176.119.254.198:8000/wasselha";
    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private int customerId;
    private String userType;

    //Customer customerObj;
    //Location locationOfCustomer;
    void getFromSharedPref() throws IOException {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        userType = preferences.getString(LOGIN_TYPE_KEY, null);
        customerId = Integer.parseInt(preferences.getString(ID_KEY, ""));

        // customerObj = new CustomerDA().getCustomer(customerId);

        // locationOfCustomer=  new LocationDA().getLocation(customerObj.getLocation());

    }

    private void addDelServiceDetails(String fromCPLocationId,
                                      String toCPLocationId, String packWeight,
                                      String packType, String sourceLocationId,
                                      String destLocationId,
                                        String personHandoverNameStr) {
        // String title = getAddressTitleFromCoordinates(latitude, longitude);
        //String description = getAddressFromCoordinates(latitude, longitude);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = BASE_URL + "/delivery-service-details/";

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("service", service.getId());
            jsonObject.put("customer", String.valueOf(customerId));

            if (fromCPLocationId != null) {
                jsonObject.put("source_collection_point", Integer.parseInt(fromCPLocationId));
            }
            if (toCPLocationId != null) {
                jsonObject.put("destination_collection_point", Integer.parseInt(toCPLocationId));
            }
            if (sourceLocationId != null) {
                jsonObject.put("source_place", Integer.parseInt(sourceLocationId));
            }
            if (destLocationId != null) {
                jsonObject.put("destination_place", Integer.parseInt(destLocationId));
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
            String dateTextCollection = (service.getService_date().getYear()+1900) +"-"
                    +(service.getService_date().getMonth()) + "-" + (service.getService_date().getDate());
           // String dateTextHandover = (service.getService_date().getYear()+1900) +"-"
             //       +(service.getService_date().getMonth()+1) + "-" + (service.getService_date().getDate());
          //  Date selectedDateTimeCollection = sdf.parse(dateTextCollection + " " +
               //     service.getService_date().getHours() + ":" + service.getService_date().getMinutes() + " " + "AM");

           // Date selectedDateTimeHandover = sdf.parse(dateTextCollection + " " +
             //       service.getService_date().getHours()+1 + ":" + service.getService_date().getMinutes() + " " + "AM");

            String dateTimeString = String.format(
                    "%sT%02d:%02d:00", // Format as "yyyy-MM-ddTHH:mm:ss"
                    dateTextCollection,
                    service.getService_date().getHours(),
                    service.getService_date().getMinutes()
            );

            // Obtain the time zone offset
            TimeZone timeZone = TimeZone.getDefault();
            long offsetInMillis = timeZone.getOffset(System.currentTimeMillis());
            int offsetHours = (int) (offsetInMillis / 3600000); // Convert to hours
            int offsetMinutes = (int) (offsetInMillis / 60000) % 60; // Convert to minutes

            // Combine the date-time string with the time zone offset
            String serviceDateCollection = String.format(
                    "%s%+03d:%02d", // Format as "yyyy-MM-ddTHH:mm:ss+HH:mm"
                    dateTimeString,
                    offsetHours,
                    offsetMinutes
            );


            String dateTimeStringHand = String.format(
                    "%sT%02d:%02d:00", // Format as "yyyy-MM-ddTHH:mm:ss"
                    dateTextCollection,
                    service.getService_date().getHours(),
                    service.getService_date().getMinutes()
            );
            String serviceDateHand= String.format(
                    "%s%+03d:%02d", // Format as "yyyy-MM-ddTHH:mm:ss+HH:mm"
                    dateTimeString,
                    offsetHours,
                    offsetMinutes
            );
            jsonObject.put("price", service.getPrice());
            jsonObject.put("collection_time", serviceDateCollection);
            jsonObject.put("handover_time", serviceDateHand);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("jsonObjectValues:", jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int dsdID = response.getInt("id");

                            if (dsdID > 0) {
                                Toast.makeText(ServiceDetailsActivity.this,
                                        "Reference Number= " + dsdID + "\n Reservation request done!", Toast.LENGTH_LONG).show();


                                Handler handler = new Handler();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Package pack  = new Package(dsdID,packType,Double.parseDouble(packWeight));
                                        try {
                                            new PackageDA().savePackage(pack);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }

                                        Date nowDate = new Date();
                                        String dateTextNow = (nowDate.getYear()+1900) +"-"
                                                +(nowDate.getMonth()+1) + "-" + (nowDate.getDate());
                                        String dateTimeStringNow = String.format(
                                                "%sT%02d:%02d:00", // Format as "yyyy-MM-ddTHH:mm:ss"
                                                dateTextNow,
                                                nowDate.getHours(),
                                                nowDate.getMinutes()
                                        );

                                        TimeZone timeZone = TimeZone.getDefault();
                                        long offsetInMillis = timeZone.getOffset(System.currentTimeMillis());
                                        int offsetHours = (int) (offsetInMillis / 3600000); // Convert to hours
                                        int offsetMinutes = (int) (offsetInMillis / 60000) % 60; // Convert to minutes
                                        String serviceDateNow= String.format(
                                                "%s%+03d:%02d", // Format as "yyyy-MM-ddTHH:mm:ss+HH:mm"
                                                dateTimeStringNow,
                                                offsetHours,
                                                offsetMinutes
                                        );


                                        String customerName ;
                                        String phoneNumber;
                                        try {
                                            Customer customer = (new  CustomerDA()).getCustomer(customerId);
                                            customerName=  customer.getFirst_name();
                                            phoneNumber =  customer.getPhone_number();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        Notification notifTrans = new Notification(service.getTransporter(),"transporter",
                                                ("Request a service: id= "+ service.getId() ),
                                                ("Customer = "+customerName +" wants to requests this delivery service From= " + srcCity.getText().toString() + ", To= " +
                                                        destCity.getText().toString() + ", His Phone No. = " + phoneNumber + ", deliver it to person called "+personHandoverNameStr ),
                                                serviceDateNow);

                                        Notification notifCPPSrc;
                                        Notification notifCPPDest;
                                        Transporter transporter = null;
                                        if (fromCPLocationId != null) {
                                            CollectionPoint collectionPointSrcc;
                                            try {
                                                collectionPointSrcc= new CollectionPointDA().getCollectionP(Integer.parseInt(fromCPLocationId));
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }

                                            try {
                                                 transporter =  new TransporterDA().getTransporter(service.getTransporter());
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                            notifCPPSrc = new Notification(collectionPointSrcc.getCollection_point_provider(),"collectionpointprovider",
                                                    ("Request a Collection Point: id= "+ fromCPLocationId ),
                                                    ("Customer = "+customerName +" wants to requests this collection point as a src located at= " +collectionPointSrcc.getName() + ", His Phone No. = " + phoneNumber
                                                            + ", transporter = "+transporter.getFirst_name()+" will collect it"),
                                                    serviceDateNow);

                                            try {
                                                new  NotificationsDA().saveNotification(notifCPPSrc);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }

                                        }
                                        if (toCPLocationId != null) {
                                            CollectionPoint collectionPointDestt;
                                            try {
                                                collectionPointDestt= new CollectionPointDA().getCollectionP(Integer.parseInt(toCPLocationId));
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                            notifCPPDest = new Notification(collectionPointDestt.getCollection_point_provider(),"collectionpointprovider",
                                                    ("Request a Collection Point: id= "+ toCPLocationId ),
                                                    ("Customer = "+customerName +" wants to requests this collection point as a dest located at= ," +collectionPointDestt.getName() + ", His Phone No. = " + phoneNumber
                                                    + " transporter =" + transporter.getFirst_name() +" will deliver it, and handover it to person called = " + personHandoverNameStr),
                                                    serviceDateNow);

                                            try {
                                                new  NotificationsDA().saveNotification(notifCPPDest);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }

                                        try {
                                            new  NotificationsDA().saveNotification(notifTrans);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }

                                    }
                                });




                            } else {
                                Toast.makeText(ServiceDetailsActivity.this, "The information is not correct, try again!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ServiceDetailsActivity.this, "Network error, please try again later!", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }


}