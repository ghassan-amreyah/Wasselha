package com.cs.wasselha.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cs.wasselha.Dialogs.FinishAddCustomerDialog;
import com.cs.wasselha.Dialogs.LoadingDialog;

import com.cs.wasselha.R;
import com.cs.wasselha.interfaces.implementation.LocationDA;
import com.cs.wasselha.model.Location;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomerSignupActivity extends AppCompatActivity implements GoogleMap.OnMarkerDragListener {

    Button signupBtnInCustomerSignupPage;
    EditText firstName, lastName,email, phoneNumber, password, repeatPassword;
    TextView errorMessage;

    ScrollView scrollView;
    private ProgressDialog progressDialog;

    private GoogleMap source_mMap;
    private Marker sourceMarker;
    private LatLng sourceLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_signup);
        getSupportActionBar().hide();

        //References
        setupReference();

        View mapLocationView = findViewById(R.id.customerAddressMapView);

        mapLocationView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    scrollView.requestDisallowInterceptTouchEvent(true);
                    return false;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    scrollView.requestDisallowInterceptTouchEvent(false);
                    return true;
                }
                return true;
            }
        });

        SupportMapFragment sourceMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.customerAddressMap);

        sourceMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                source_mMap = googleMap;
                setupMap(source_mMap); // true means this is for the source map
            }
        });

        customerSignupSetup();
    }


    //--------------Methods---------------------------------------------------------
    //References
    private void setupReference()
    {
        signupBtnInCustomerSignupPage = findViewById(R.id.signupCustomerBtn);
        firstName = findViewById(R.id.firstNameCustomer);
        lastName = findViewById(R.id.lastNameCustomer);
        email = findViewById(R.id.emailCustomerSignup);
        phoneNumber = findViewById(R.id.phoneNumberCustomerSignup);
        password = findViewById(R.id.passwordCustomerSignup);
        repeatPassword = findViewById(R.id.repeatPasswordCustomerSignup);
        errorMessage = findViewById(R.id.errorMessageInCustomerSignup);
        scrollView = findViewById(R.id.scroll_view_id);
        //loadingProgressBar = findViewById(R.id.loadProgressBar);

    }


    private void customerSignupSetup()
    {
        signupBtnInCustomerSignupPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    if (sourceLatLng != null)
                    {
                            createSingleLocation(sourceLatLng.latitude,sourceLatLng.longitude);
                    }
                    else
                    {
                            Toast.makeText(CustomerSignupActivity.this, getString(R.string.select_location)+"", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                if (email.length() > 0 && firstName.length() > 0 && lastName.length() > 0 &&  password.length() > 0 && phoneNumber.length() > 0 && repeatPassword.length() > 0)
                {
                    if (password.getText().length() > 8 && repeatPassword.getText().length() > 8)
                    {
                        if(password.getText().toString().equals(repeatPassword.getText().toString()))
                        {
                            errorMessage.setText("");
                            addCustomer();
                        }
                        else
                        {
                            errorMessage.setText(getString(R.string.pass_not_match));
                            Toast.makeText(CustomerSignupActivity.this, getString(R.string.pass_not_match)+"", Toast.LENGTH_SHORT).show();

                        }
                    }
                    else
                    {
                        errorMessage.setText(getString(R.string.pass_more_than_8chr));
                        Toast.makeText(CustomerSignupActivity.this, getString(R.string.pass_more_than_8chr)+"", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    errorMessage.setText(getString(R.string.fill_fields));
                    Toast.makeText(CustomerSignupActivity.this, getString(R.string.fill_fields) + "", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    //--------------Add customer method----------------------------------------------------------
    private void addCustomer()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://176.119.254.198:8000/wasselha/customers/";

        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put("email", email.getText().toString());
            jsonObject.put("first_name", firstName.getText().toString());
            jsonObject.put("last_name", lastName.getText().toString());
            jsonObject.put("password", password.getText().toString());
            jsonObject.put("phone_number", phoneNumber.getText().toString());
            jsonObject.put("is_verified", false);
            jsonObject.put("review", 0);
            jsonObject.put("location", locationID);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        // Create the POST request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            int id = response.getInt("id");

                            if (id > 0 )
                            {
                                progressDialog = new ProgressDialog(CustomerSignupActivity.this);
                                progressDialog.setMessage(getString(R.string.create_new_account));
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        progressDialog.dismiss();
                                    }
                                }, 2000);

                                openFinishAddCustomerDialog();
                                email.setText("");
                                firstName.setText("");
                                lastName.setText("");
                                phoneNumber.setText("");
                                password.setText("");
                                repeatPassword.setText("");
                            }
                            else
                            {
                                errorMessage.setText(getString(R.string.not_correct));                                    }
                        }
                        catch (JSONException e)
                        {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(CustomerSignupActivity.this, getString(R.string.already_exist) + "", Toast.LENGTH_SHORT).show();
                        errorMessage.setText(getString(R.string.already_exist));
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }


    private void openFinishAddCustomerDialog()
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                FinishAddCustomerDialog finishAddCustomer = new FinishAddCustomerDialog();
                finishAddCustomer.show(getSupportFragmentManager(), "AddCustomer");
            }
        }, 1000);

    }

    String addressGeoCoder;
    private void setupMap(GoogleMap googleMap)
    {
        googleMap.setOnMarkerDragListener(this);
        googleMap.getUiSettings().setZoomControlsEnabled(true); // Enable zoom controls

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            googleMap.setMyLocationEnabled(true);

            // This block is for setting up the source map
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (sourceMarker == null)
                    {
                        sourceLatLng = latLng;
                        sourceMarker = googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
                    }
                    else
                    {
                        sourceLatLng = latLng;
                        sourceMarker.setPosition(sourceLatLng);

                        addressGeoCoder = getAddressFromCoordinates(sourceLatLng.latitude,sourceLatLng.longitude);
                    }

                    Toast.makeText(getApplicationContext(), "Location: "+sourceLatLng.toString()+","+addressGeoCoder, Toast.LENGTH_SHORT).show();

                }
            });


        }
    }

    int locationID;

    private String getAddressTitleFromCoordinates(double latitude, double longitude)
    {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "Rand";

        try
        {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty())
            {
                Address address1 = addresses.get(0);
                String locationName = address1.getAddressLine(0);
                address=locationName;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return address;
    }
    private void createSingleLocation(double latitude, double longitude) throws IOException {
        String title = getAddressTitleFromCoordinates(latitude, longitude);
        String description = getAddressFromCoordinates(latitude, longitude);

        Location location = new Location();
        location.setDescription(description);
        location.setTitle(title);
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        LocationDA locationDA =   new LocationDA();
        locationID = locationDA.saveLocation(location);

    }
    private String getAddressFromCoordinates(double latitude, double longitude)
    {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 100);
            if (addresses != null && addresses.size() > 0) {
                Address fetchedAddress = addresses.get(0);
                StringBuilder stringBuilder = new StringBuilder();

                // Get additional address details
                String featureName = fetchedAddress.getFeatureName(); // Name of the feature (such as building, park, etc.)
                String thoroughfare = fetchedAddress.getThoroughfare(); // Street name
                String subThoroughfare = fetchedAddress.getSubThoroughfare(); // Building number
                String locality = fetchedAddress.getLocality(); // Locality (city)
                String subLocality = fetchedAddress.getSubLocality(); // Sublocality (district)
                String postalCode = fetchedAddress.getPostalCode(); // Postal code
                String countryName = fetchedAddress.getCountryName(); // Country name

                // Append the address details to the StringBuilder
                if (featureName != null) {
                    stringBuilder.append(featureName).append(", ");
                }
                if (thoroughfare != null) {
                    stringBuilder.append(thoroughfare).append(", ");
                }
                if (subThoroughfare != null) {
                    stringBuilder.append(subThoroughfare).append(", ");
                }
                if (locality != null) {
                    stringBuilder.append(locality).append(", ");
                }
                if (subLocality != null) {
                    stringBuilder.append(subLocality).append(", ");
                }
                if (postalCode != null) {
                    stringBuilder.append(postalCode).append(", ");
                }
                if (countryName != null) {
                    stringBuilder.append(countryName);
                }

                address = stringBuilder.toString().trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

    }
}