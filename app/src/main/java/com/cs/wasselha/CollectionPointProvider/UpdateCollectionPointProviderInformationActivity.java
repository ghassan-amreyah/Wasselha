package com.cs.wasselha.CollectionPointProvider;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.cs.wasselha.interfaces.implementation.CollectionPointProviderDA;
import com.cs.wasselha.model.CollectionPointProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class UpdateCollectionPointProviderInformationActivity extends AppCompatActivity {
    EditText newCollectionPointProviderEmail, newCollectionPointProviderPassword, newCollectionPointProviderPhoneNumber;
    Button updateCollectionPointProviderInfoBtn;

    private TextView errorMessageTextView;
    private ProgressDialog progressDialog;
    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_collection_point_provider_information);
        getSupportActionBar().hide();
        setupReferences();
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String id = preferences.getString(ID_KEY, null);
        int collectionPointProviderID = Integer.parseInt(id.trim());
        updateCollectionPointProviderInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    try {
                        updateCollectionPointProviderInformation(collectionPointProviderID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        //calls

    }
    //References
    private void setupReferences()
    {
        newCollectionPointProviderEmail = findViewById(R.id.newCollectionPointProviderEmail);
        newCollectionPointProviderPassword = findViewById(R.id.newCollectionPointProviderPassword);
        newCollectionPointProviderPhoneNumber = findViewById(R.id.newCollectionPointProviderPhoneNumber);
        updateCollectionPointProviderInfoBtn = findViewById(R.id.updateCollectionPointProviderInformationBtn);
        errorMessageTextView = findViewById(R.id.errorMessageInUpdateCollectionPointProviderInformation);

    }


    private void updateCollectionPointProviderInformation(int collectionPointProviderId) throws IOException {

        CollectionPointProviderDA collectionPointProviderda=new CollectionPointProviderDA();
        CollectionPointProvider collectionPointProvider=collectionPointProviderda.getCollectionPointProviderById(collectionPointProviderId);

        String email = newCollectionPointProviderEmail.getText().toString().trim();
        String password = newCollectionPointProviderPassword.getText().toString().trim();
        String phoneNumber = newCollectionPointProviderPhoneNumber.getText().toString().trim();
        // Create a JSON object with the updated information
        JSONObject requestData = new JSONObject();
        try {

            requestData.put("email", collectionPointProvider.getEmail());
            requestData.put("first_name", collectionPointProvider.getFirst_name());
            requestData.put("last_name", collectionPointProvider.getLast_name());
            requestData.put("password", collectionPointProvider.getPassword());
            requestData.put("phone_number", collectionPointProvider.getPhone_number());
            requestData.put("is_verified", collectionPointProvider.isIs_verified());
            requestData.put("review", collectionPointProvider.getReview());
            requestData.put("national_id", collectionPointProvider.getNational_id());
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
        String url = "http://176.119.254.198:8000/wasselha/collection-point-providers/"+collectionPointProviderId+"/"; // Replace with your API endpoint URL
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(UpdateCollectionPointProviderInformationActivity.this, MainCollectionPointProviderActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UpdateCollectionPointProviderInformationActivity.this, "Email or phone is used please select another email or phone number", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(CustomerSettingActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        //Log.d("sett19",error.getMessage() );
                        newCollectionPointProviderEmail.setText("");
                        newCollectionPointProviderPassword.setText("");
                        newCollectionPointProviderPhoneNumber.setText("");
                    }
                });

        // Add the request to the request queue
        Volley.newRequestQueue(this).add(request);
    }

    private boolean validateInputs() {
        String email = newCollectionPointProviderEmail.getText().toString().trim();
        String password = newCollectionPointProviderPassword.getText().toString().trim();
        String phoneNumber = newCollectionPointProviderPhoneNumber.getText().toString().trim();

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