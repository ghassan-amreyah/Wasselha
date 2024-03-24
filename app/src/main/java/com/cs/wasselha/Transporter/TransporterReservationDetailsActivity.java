package com.cs.wasselha.Transporter;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cs.wasselha.Adapters.ReservationsAdapter;
import com.cs.wasselha.R;
import com.cs.wasselha.interfaces.implementation.ClaimsDA;
import com.cs.wasselha.interfaces.implementation.DeliveryServiceDetailsDA;
import com.cs.wasselha.interfaces.implementation.NotificationsDA;
import com.cs.wasselha.model.Claim;
import com.cs.wasselha.model.DeliveryServiceDetails;
import com.cs.wasselha.model.Notification;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TransporterReservationDetailsActivity extends AppCompatActivity {

    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private static String BASE_URL="http://176.119.254.198:8000/wasselha";
    private static String transporterName="me";
    private ProgressDialog progressDialog ;
//    AppCompatButton addReviewAboutCustomerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporter_reservation_details);
        getSupportActionBar().hide();
        progressDialog = new ProgressDialog(this);

        Intent intent = getIntent();
        final int deliveryServiceDetailsId = intent.getIntExtra("deliveryservicedetails", 0);
        final int customerId = intent.getIntExtra("customerid", 0);
        final Spinner deliveryTypeSpinner = findViewById(R.id.deliveryTypeSpinner);
        final EditText personNameEditText = findViewById(R.id.personNameInReservationsDetailsPage);
        final EditText sendMessageToCustomerIfAnyProblemBox = findViewById(R.id.sendMessageToCustomerIfAnyProblemBox);
        AppCompatButton reserveButton = findViewById(R.id.reserveBtnServiceDetailsPage);
        AppCompatButton addReviewAboutCustomerBtn = findViewById(R.id.addReviewAboutCustomerBtn);
        AppCompatButton sendMessageToCustomerBtn = findViewById(R.id.sendMessageToCustomerBtn);
        RadioGroup radioGroup = findViewById(R.id.radioGroupTransporter);
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String id = preferences.getString(ID_KEY, null);
        int transporterID=Integer.parseInt(id.trim());
        setAndGetName(this,transporterID);

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actionTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                String collectionFrom = transporterName;
                String handoverTo = personNameEditText.getText().toString();

                if ("Collect from".equals(deliveryTypeSpinner.getSelectedItem().toString())) {
                    collectionFrom = personNameEditText.getText().toString();
                    handoverTo = transporterName;
                }
                personNameEditText.setText("");
                progressDialog.setMessage("change package status...");
                progressDialog.show();
                sendPostRequest(deliveryServiceDetailsId,actionTime,collectionFrom,handoverTo,customerId);
            }
        });
        sendMessageToCustomerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationsDA nDA=new NotificationsDA();
                try {
                    Date nowDate = new Date();
                    String dateTextNow = (nowDate.getYear()+1900) +"-"
                            +(nowDate.getMonth()+1) + "-" + (nowDate.getDate());
                    String dateTimeStringNow = String.format(
                            "%sT%02d:%02d:00", // Format as "yyyy-MM-ddTHH:mm:ss"
                            dateTextNow,
                            nowDate.getHours(),
                            nowDate.getMinutes()
                    );

                    nDA.saveNotification(new Notification(customerId,"customer","important massage from transporter",sendMessageToCustomerIfAnyProblemBox.getText().toString(),dateTimeStringNow));
                    sendMessageToCustomerIfAnyProblemBox.setText("");
                    Toast.makeText(TransporterReservationDetailsActivity.this, "Notification Send Successfully", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(TransporterReservationDetailsActivity.this, "There is Network error in send Notification, please try again Later", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        addReviewAboutCustomerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliveryServiceDetailsDA dsdDA=new DeliveryServiceDetailsDA();
                DeliveryServiceDetails dsd=null;
                try {
                    dsd=dsdDA.getDSD(deliveryServiceDetailsId);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int to_id=-1;
                String type="customer";
                //Then, get the id of the selected RadioButton
                int selectedId = radioGroup.getCheckedRadioButtonId();

                //Check which RadioButton was selected
                if (selectedId == R.id.customerReviewInTransporterReservationDetailsRadio) {
                    //The "Customer" RadioButton was selected
                    to_id = dsd.getCustomer();
                    type="collectionpointprovider";
                } else if (selectedId == R.id.srccollectionPointReviewInTransporterReservationDetailsRadio) {
                    //The "SRC Collection Point Provider" RadioButton was selected
                    to_id = dsd.getSource_collection_point();
                    type="collectionpointprovider";
                } else if (selectedId == R.id.dstcollectionPointReviewInTransporterReservationDetailsRadio) {
                    //The "DST Collection Point Provider" RadioButton was selected
                    to_id = dsd.getDestination_collection_point();
                    type="customer";
                }
                try {
                    if(to_id==-1) {
                        if (isExistReview(deliveryServiceDetailsId, to_id)) {
                            Intent intent = new Intent(getApplicationContext(), AddReviewForCustomerActivity.class);
                            intent.putExtra("deliveryservicedetails", deliveryServiceDetailsId);
                            intent.putExtra("writenToId", to_id);
                            intent.putExtra("writenToType", type);
                            startActivity(intent);
                        } else {
                            Toast.makeText(TransporterReservationDetailsActivity.this, "You're already review the person", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(TransporterReservationDetailsActivity.this, "You doesn't put the package in this collection point", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isExistReview(int deliveryServiceDetailsId, int to_id) throws IOException {
        ClaimsDA cDA=new ClaimsDA();
        ArrayList<Claim> c=cDA.getClaimsByWritenToIdAndDeliveryServiceDetails(to_id,deliveryServiceDetailsId);
        if(c==null){
            return true;
        }else if(c.size()<=0){
            return true;
        }else{
            return false;
        }
    }

    public void setAndGetName(Context context, int transporterID) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = BASE_URL + "/transporters/" + transporterID + "/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String firstName = response.getString("first_name");
                            String lastName = response.getString("last_name");
                            String fullName = firstName + " " + lastName;

                            // Assuming that 'name' is a TextView instance you want to set the name to.
                            transporterName=fullName;

                        } catch (Exception e) {
                            Log.e("profile","Error:"+e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("profile","Error:"+error.toString());
                // Handle error here
                error.printStackTrace();
            }
        });

        queue.add(jsonObjectRequest);
    }

    private void sendPostRequest(final int deliveryServiceDetailsId, final String actionTime, final String collectionFrom, final String handoverTo,final int customerId) {
        String url = BASE_URL + "/delivery-status/";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending data...");
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        sendNotificationToCustomer(customerId,"update package status","package collected from "+collectionFrom+" to "+handoverTo);
                        Toast.makeText(getApplicationContext(), "Data sent successfully!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error sending data", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("delivery_service_details", String.valueOf(deliveryServiceDetailsId));
                params.put("action_time", actionTime);
                params.put("collection_from", collectionFrom);
                params.put("handover_to", handoverTo);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }

    private void sendNotificationToCustomer(final int customerId, final String title, final String description) {
        String url = BASE_URL + "/notifications/";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(getApplicationContext(), MainTransporterActivity.class);
                        getApplicationContext().startActivity(intent);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Notification sent successfully!", Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Intent intent = new Intent(getApplicationContext(), MainTransporterActivity.class);
                startActivity(intent);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error sending notification", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(customerId));
                params.put("user_type", "customer");
                params.put("title", title);
                params.put("description", description);
                params.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); // Current date and time
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }

    //-------------------Methods--------------------------------------------------------------

//    private void addReviewAboutCustomerSetup()
//    {
//
//    }
}