package com.cs.wasselha.Transporter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateTransporterInformationActivity extends AppCompatActivity {

    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText phoneNumberEditText;
    private Button updateBtn;
    private TextView errorMessageTextView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_update_transporter_information);
            getSupportActionBar().hide();

            SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
            String id = preferences.getString(ID_KEY, null);
            int transporterID = Integer.parseInt(id.trim());

            //Calls
            setUpRefernces();

            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateInputs()) {
                        updateTransporterInformation(transporterID);
                    }
                }
            });

            progressDialog = new ProgressDialog(UpdateTransporterInformationActivity.this);
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
        catch (Exception e)
        {
            Log.e("error:",e.toString());
        }
    }



    //-------Methods------------------------------------------------------------------------
    private void setUpRefernces()
    {
        emailEditText = findViewById(R.id.newTransporterEmail);
        passwordEditText = findViewById(R.id.newTransporterPassword);
        phoneNumberEditText = findViewById(R.id.newTransporterPhoneNumber);
        updateBtn = findViewById(R.id.updateTransporterInformationBtn);
        errorMessageTextView = findViewById(R.id.errorMessageInUpdateTransporterInformation);
    }


    private void updateTransporterInformation(int transporterID) {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();

        // Create a JSON object with the updated information
        JSONObject requestData = new JSONObject();
        try {
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
        String url = "http://176.119.254.198:8000/wasselha/transporters/"+transporterID+"/"; // Replace with your API endpoint URL
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(UpdateTransporterInformationActivity.this, MainTransporterActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UpdateTransporterInformationActivity.this, "Email or phone is used please select another email or phone number", Toast.LENGTH_SHORT).show();
                        emailEditText.setText("");
                        passwordEditText.setText("");
                        phoneNumberEditText.setText("");
                    }
                });

        // Add the request to the request queue
        Volley.newRequestQueue(this).add(request);
    }
    private boolean validateInputs() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();

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