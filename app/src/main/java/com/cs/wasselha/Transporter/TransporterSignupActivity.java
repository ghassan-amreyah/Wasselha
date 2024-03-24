package com.cs.wasselha.Transporter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.cs.wasselha.Login.TypeLoginActivity;
import com.cs.wasselha.R;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

import com.cs.wasselha.R;

public class TransporterSignupActivity extends AppCompatActivity {
    public static int transporterID,vehicleID;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText nationalIDEditText;
    private EditText phoneNumberEditText;
    private EditText vehicleNumberEditText;
    private Spinner vehicleTypeSpinner;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;
    private Button vehiclePhotoUploadBtn;
    private Button vehicleLicenseUploadPhotoBtn;
    private Button personalLicenseUploadPhotoBtn;
    private Button signupTransporterBtn;
    private TextView errorMessage;

    private static final int PICK_IMAGE = 1;
    private static final int PICK_VEHICLE_IMAGE = 1;
    private static final int PICK_VEHICLE_LICENSE = 2;
    private static final int PICK_DRIVING_LICENSE = 3;
    private Uri selectedVehicleImageUri;
    private Uri selectedVehicleLicenseUri;
    private Uri selectedDrivingLicenseUri;
    private Bitmap selectedVehicleImagebitmap;
    private Bitmap selectedVehicleLicensebitmap;
    private Bitmap selectedDrivingLicensebitmap;
    private String transportersUrl="http://176.119.254.198:8000/wasselha/transporters/";
    private String vehiclesUrl="http://176.119.254.198:8000/wasselha/vehicles/";
    RequestQueue requestQueue;
    String boundary = "*****";
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    private boolean createSuccefully;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporter_signup);
        getSupportActionBar().hide();
        requestQueue = Volley.newRequestQueue(this);
        firstNameEditText = findViewById(R.id.firstNameTransporter);
        lastNameEditText = findViewById(R.id.lastNameTransporter);
        nationalIDEditText = findViewById(R.id.nationalIDTransporterSignup);
        phoneNumberEditText = findViewById(R.id.phoneNumberTransporterSignup);
        vehicleNumberEditText = findViewById(R.id.vehicleNumberTransporterSignup);
        vehicleTypeSpinner = findViewById(R.id.vehicleTypeSpinner);
        emailEditText = findViewById(R.id.emailTransporterSignup);
        passwordEditText = findViewById(R.id.passwordTransporterSignup);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordTransporterSignup);
        vehiclePhotoUploadBtn = findViewById(R.id.vehiclePhotoUploadBtn);
        vehicleLicenseUploadPhotoBtn = findViewById(R.id.vehicleLicenseUploadPhotoBtn);
        personalLicenseUploadPhotoBtn = findViewById(R.id.personalLicenseUploadPhotoBtn);
        signupTransporterBtn=findViewById(R.id.signupTransporterBtn);
        errorMessage=findViewById(R.id.errorMessageInTransporterSignup);
        vehiclePhotoUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_VEHICLE_IMAGE);
                // save the selected uri for further use
                selectedVehicleImageUri = intent.getData();
            }
        });

        vehicleLicenseUploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_VEHICLE_LICENSE);
                // save the selected uri for further use
                selectedVehicleLicenseUri = intent.getData();
            }
        });

        personalLicenseUploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_DRIVING_LICENSE);
                // save the selected uri for further use
                selectedDrivingLicenseUri = intent.getData();
            }
        });
        signupTransporterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(verifyInputsAndSubmit())
                {
                    new CreateTransporterTask().execute();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            String fileName = getImageName(imageUri);

            switch (requestCode) {
                case PICK_VEHICLE_IMAGE:
                    selectedVehicleImageUri = imageUri;
                    vehiclePhotoUploadBtn.setText(fileName);
                    break;
                case PICK_VEHICLE_LICENSE:
                    selectedVehicleLicenseUri = imageUri;
                    vehicleLicenseUploadPhotoBtn.setText(fileName);
                    break;
                case PICK_DRIVING_LICENSE:
                    selectedDrivingLicenseUri = imageUri;
                    personalLicenseUploadPhotoBtn.setText(fileName);
                    break;
            }
        }
    }

    private String getImageName(Uri uri) {
        String fileName = "";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex >= 0) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return fileName;
    }
    private String getFilePathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};

        // Use the ContentResolver to query for the actual file path
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        String filePath = null;
        if (cursor != null && cursor.moveToFirst()) {
            // Get the index of the DATA column
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            // Retrieve the file path from the cursor
            if(columnIndex>=0)
                filePath = cursor.getString(columnIndex);
            cursor.close();
        }

        return filePath;
    }


    private String createTransporter() {
        try {
            try {
                selectedDrivingLicensebitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),selectedDrivingLicenseUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(TransporterSignupActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            selectedDrivingLicensebitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
//            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
//            selectedDrivingLicensebitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
//            byte[] bytes=byteArrayOutputStream.toByteArray();
//            final String base64Image= Base64.encodeToString(bytes,Base64.DEFAULT);
            URL url = new URL(transportersUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"first_name\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(firstNameEditText.getText().toString().trim());
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"last_name\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(lastNameEditText.getText().toString().trim());
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"email\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(emailEditText.getText().toString().trim());
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"password\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(passwordEditText.getText().toString().trim());
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"phone_number\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(phoneNumberEditText.getText().toString().trim());
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"is_verified\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes("false");
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"status\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes("Available");
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"national_id\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(nationalIDEditText.getText().toString().trim());
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"review\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes("0");
            outputStream.writeBytes(lineEnd);

            // Repeat for other text fields: last_name, email, etc...

            // File part
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"driving_license\";filename=\"driving_license.png\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            Log.d("imageee",byteArray.toString());
            outputStream.write(byteArray);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                String responseString = response.toString();
                Log.d("responseee",responseString);
                JSONObject jsonResponse = new JSONObject(responseString);


                // Extract transporter id from the JSON response
                // Note: You have to know the exact key under which the id is stored in the JSON response
                String transporterId = jsonResponse.getString("id"); // replace 'transporter_id' with the correct key
                Log.d("iddddd",transporterId);
                TransporterSignupActivity.transporterID=Integer.parseInt(transporterId.trim());
                return transporterId;
            }else{
                errorMessage.setText("Invalid Information, there are reputation of email or national id!");
                return null;
            }
        } catch (IOException | JSONException e) {
            Log.e("UploadTransporterTask", "Error: " + e.getMessage());
            return null;
        }
    }


    private String createVehicle(int transporterId) {
        try {
            try {
                selectedVehicleImagebitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),selectedVehicleImageUri);
                selectedVehicleLicensebitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),selectedVehicleLicenseUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(TransporterSignupActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return null;

            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            selectedVehicleImagebitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
            selectedVehicleLicensebitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream2);
            byte[] byteArray2 = byteArrayOutputStream2.toByteArray();
            URL url = new URL(vehiclesUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"transporter\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(transporterId+"");
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"vehicle_number\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(vehicleNumberEditText.getText().toString().trim());
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"vehicle_type\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(vehicleTypeSpinner.getSelectedItem().toString().trim().toLowerCase());
            outputStream.writeBytes(lineEnd);

            // Repeat for other text fields: last_name, email, etc...

            // File part
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"vehicle_image\";filename=\"driving_license.png\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.write(byteArray);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"vehicle_license\";filename=\"driving_license.png\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.write(byteArray2);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                String responseString = response.toString();
                Log.d("responseee",responseString);
                JSONObject jsonResponse = new JSONObject(responseString);


                // Extract transporter id from the JSON response
                // Note: You have to know the exact key under which the id is stored in the JSON response
                String vehicleId = jsonResponse.getString("id"); // replace 'transporter_id' with the correct key
                Log.d("iddddd",vehicleId);
                return vehicleId+"";
            }else{
                errorMessage.setText("Invalid Information, there are reputation of vehicle number!");

                return null;
            }
        } catch (IOException | JSONException e) {
            Log.e("UploadTransporterTask", "Error: " + e.getMessage());
            Toast.makeText(TransporterSignupActivity.this, "UploadTransporterTask:Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }



    }
    private boolean sendDeleteRequest(String url) {
        try {
            URL deleteUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) deleteUrl.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setDoOutput(true);

            // Add any additional headers if needed
            // connection.setRequestProperty("HeaderKey", "HeaderValue");

            int responseCode = connection.getResponseCode();
            // Handle the response code as per your requirement
            connection.disconnect();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return true;

            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any exceptions that occur during the request
            return false;
        }
    }

    private class CreateTransporterTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(TransporterSignupActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(Void... params) {
            String transporterId=createTransporter();
            if(transporterId != null){
                transporterId=transporterId.trim();
                int transporterID=Integer.parseInt(transporterId);
                String vehicleId=createVehicle(transporterID);
                if(vehicleId!=null){
                    createSuccefully=true;
                }else{
                    createSuccefully=false;
                    sendDeleteRequest(transportersUrl+transporterId+"/");
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if(createSuccefully){
                Intent intent = new Intent(TransporterSignupActivity.this, TypeLoginActivity.class);
                startActivity(intent);
                Toast.makeText(TransporterSignupActivity.this, getString(R.string.done_signup) + "", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean isEditTextFilled(EditText editText) {
        String text = editText.getText().toString().trim();
        return !text.isEmpty();
    }

    private boolean isEmailValid(String email) {
        // Perform email validation logic here
        // You can use regular expressions or any other method you prefer
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showToastMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean verifyInputsAndSubmit() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String nationalID = nationalIDEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String vehicleNumber = vehicleNumberEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String repeatPassword = repeatPasswordEditText.getText().toString().trim();
        String vehiclePhotoBtnText = vehiclePhotoUploadBtn.getText().toString().trim();
        String vehicleLicenseBtnText = vehicleLicenseUploadPhotoBtn.getText().toString().trim();
        String personalLicenseBtnText = personalLicenseUploadPhotoBtn.getText().toString().trim();

        if (!isEditTextFilled(firstNameEditText)) {
            showToastMessage("Please fill in the First Name field!");
            errorMessage.setText("Please fill in the First Name field!");
            return false;
        } else if (!isEditTextFilled(lastNameEditText)) {
            showToastMessage("Please fill in the Last Name field!");
            errorMessage.setText("Please fill in the Last Name field!");
            return false;
        } else if (!isEditTextFilled(nationalIDEditText))
        {
            showToastMessage("Please fill in the National ID field!");
            errorMessage.setText("Please fill in the National ID field!");
            return false;
        } else if (!isEditTextFilled(phoneNumberEditText)) {
            showToastMessage("Please fill in the Phone Number field!");
            errorMessage.setText("Please fill in the Phone Number field!");
            return false;
        } else if (!isEditTextFilled(vehicleNumberEditText)) {
            errorMessage.setText("Please fill in the Vehicle Number field!");
            showToastMessage("Please fill in the Vehicle Number field.");
            return false;
        } else if (!isEditTextFilled(emailEditText)) {
            showToastMessage("Please fill in the Email field!");
            errorMessage.setText("Please fill in the Email field!");
            return false;
        } else if (!isEditTextFilled(passwordEditText)) {
            showToastMessage("Please fill in the Password field!");
            errorMessage.setText("Please fill in the Password field!");
            return false;
        } else if (!isEditTextFilled(repeatPasswordEditText)) {
            showToastMessage("Please fill in the Repeat Password field!");
            errorMessage.setText("Please fill in the Repeat Password field!");
            return false;
        } else if (!isEmailValid(email)) {
            showToastMessage("Please enter a valid email address!");
            errorMessage.setText("Please enter a valid email address!");
            return false;
        } else if (!password.equals(repeatPassword)) {
            showToastMessage("Passwords does not match!");
            errorMessage.setText("Passwords does not match!");
            return false;
        } else if (vehiclePhotoBtnText.equals("Upload image")) {
            showToastMessage("Please upload a vehicle photo!");
            errorMessage.setText("Please upload a vehicle photo!");
            return false;
        } else if (vehicleLicenseBtnText.equals("Upload image")) {
            showToastMessage("Please upload a vehicle license photo!");
            errorMessage.setText("Please upload a vehicle license photo!");
            return false;
        } else if (personalLicenseBtnText.equals("Upload image")) {
            showToastMessage("Please upload a personal license photo!");
            errorMessage.setText("Please upload a personal license photo!");
            return false;
        }

        return true;
    }




}