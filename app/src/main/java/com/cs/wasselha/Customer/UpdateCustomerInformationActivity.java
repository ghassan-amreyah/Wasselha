package com.cs.wasselha.Customer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cs.wasselha.R;
import com.cs.wasselha.interfaces.implementation.CustomerDA;
import com.cs.wasselha.model.Customer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class UpdateCustomerInformationActivity extends AppCompatActivity {

    EditText newCustomerEmail, newCustomerPassword, newCustomerPhoneNumber;
    Button updateCustomerInfoBtn;

    private TextView errorMessageTextView;
    private ProgressDialog progressDialog;
    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_customer_information_setting);
        getSupportActionBar().hide();


        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String id = preferences.getString(ID_KEY, null);
        int customerID = Integer.parseInt(id.trim());
        //Calls
        setupReferences();

        updateCustomerInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    try {
                        updateCustomerInformation(customerID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        progressDialog = new ProgressDialog(UpdateCustomerInformationActivity.this);
        progressDialog.setMessage("Loading...");
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


    //References
    private void setupReferences()
    {
        newCustomerEmail = findViewById(R.id.newCustomerEmail);
        newCustomerPassword = findViewById(R.id.newCustomerPassword);
        newCustomerPhoneNumber = findViewById(R.id.newCustomerPhoneNumber);
        updateCustomerInfoBtn = findViewById(R.id.updateCustomerInformationBtn);
        errorMessageTextView = findViewById(R.id.errorMessageInUpdateCustomerInformation);

    }

    private void updateCustomerInformation(int customerId) throws IOException {

        CustomerDA customerda=new CustomerDA();
        Customer customer=customerda.getCustomer(customerId);

        String email = newCustomerEmail.getText().toString().trim();
        String password = newCustomerPassword.getText().toString().trim();
        String phoneNumber = newCustomerPhoneNumber.getText().toString().trim();
        // Create a JSON object with the updated information
        JSONObject requestData = new JSONObject();
        try {

            requestData.put("email", customer.getEmail());
            requestData.put("first_name", customer.getFirst_name());
            requestData.put("last_name", customer.getLast_name());
            requestData.put("password", customer.getPassword());
            requestData.put("phone_number", customer.getPhone_number());
            requestData.put("is_verified", customer.isIs_verified());
            requestData.put("review", customer.getReview());
            requestData.put("location", customer.getLocation());
            if (!email.isEmpty()) {
                requestData.put("email", email);
            }
            if (!password.isEmpty()) {
                requestData.put("password", password);
            }
            if (!phoneNumber.isEmpty()) {
                requestData.put("phone_number", phoneNumber);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Send the update request to the server
        String url = "http://176.119.254.198:8000/wasselha/collection-point-providers/"+customerId+"/"; // Replace with your API endpoint URL
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(UpdateCustomerInformationActivity.this, MainCustomerActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UpdateCustomerInformationActivity.this, "Email or phone is used please select another email or phone number", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(CustomerSettingActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        //Log.d("sett19",error.getMessage() );
                        newCustomerEmail.setText("");
                        newCustomerPassword.setText("");
                        newCustomerPhoneNumber.setText("");
                    }
                });

        // Add the request to the request queue
        Volley.newRequestQueue(this).add(request);
    }
    private boolean validateInputs() {
        String email = newCustomerEmail.getText().toString().trim();
        String password = newCustomerPassword.getText().toString().trim();
        String phoneNumber = newCustomerPhoneNumber.getText().toString().trim();

        // Validating the input fields
        if (email.isEmpty() && password.isEmpty() && phoneNumber.isEmpty())
        {
            Toast.makeText(this, "please fill all fields", Toast.LENGTH_SHORT).show();
            errorMessageTextView.setText("Please fill one field at least, and try again!");
            return false;
        }

        // Email validation
        if (!email.isEmpty() && !isValidEmail(email)) {
            Toast.makeText(this, "Invalid email address!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Phone number validation
        if (!phoneNumber.isEmpty() && !isValidPhoneNumber(phoneNumber)) {
            Toast.makeText(this, "Invalid phone number!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.length() == 10 && TextUtils.isDigitsOnly(phoneNumber);
    }



}