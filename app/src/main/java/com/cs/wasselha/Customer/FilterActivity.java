package com.cs.wasselha.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cs.wasselha.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FilterActivity extends AppCompatActivity implements GoogleMap.OnMarkerDragListener{

    //NumberPicker hoursPicker, minutesPicker, amPmPicker;


    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private static String BASE_URL="http://176.119.254.198:8000/wasselha";
    private static final LatLng DESTINATION_LATLNG = new LatLng(31.908167, 35.210383);
    private static final float ZOOM_LEVEL = 20f;

    private EditText price;
    private NumberPicker hoursPicker, minutesPicker, amPmPicker;
    private Button searchInsideFilter;
    private TextView dateService;
    ScrollView scrollView ;
    private GoogleMap source_mMap;
    private GoogleMap destination_mMap;
    private Marker sourceMarker;
    private Marker destinationMarker;
    private LatLng sourceLatLng;
    private LatLng destinationLatLng;

    private ProgressDialog progressDialog;
    private Spinner spinnerPref;
    private Spinner spinnerPackType;


    private FusedLocationProviderClient fusedLocationProviderClient;
//    int hours = hoursPicker.getValue();
//    int minutes = minutesPicker.getValue();
//    String amPm = amPmPicker.getDisplayedValues()[amPmPicker.getValue()];

    Intent intentToGoSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        getSupportActionBar().hide();


        intentToGoSearch = new Intent(this, MainCustomerActivity.class);
        //References
        setUpReferences();
        searchOnClickMethod();

        View sourceMapTransparentView = findViewById(R.id.sourceCityInFilterActivityMapView);
        View destinationMapTransparentView = findViewById(R.id.destinationCityInFilterActivityMapView);

        sourceMapTransparentView.setOnTouchListener(new View.OnTouchListener() {
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
        destinationMapTransparentView.setOnTouchListener(new View.OnTouchListener() {
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


        //todo hint may be wrong 21June: Setup the two map fragments with separate callbacks
        SupportMapFragment sourceMapFragment = (SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.sourceCityInFilterActivityMap);
        SupportMapFragment destinationMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.destinationCityInFilterActivityMap); // Note: This should have a different ID than sourceMapFragment

        sourceMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                source_mMap = googleMap;
                setupMap(source_mMap, true); // true means this is for the source map
            }
        });

        destinationMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                destination_mMap = googleMap;
                setupMap(destination_mMap, false); // false means this is for the destination map
            }
        });
    }

    private void searchOnClickMethod(){
        searchInsideFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (sourceLatLng == null ||destinationLatLng == null)
                    return;

                intentToGoSearch.putExtra("fromFilter","true");



                createSingleLocation(sourceLatLng.latitude,sourceLatLng.longitude,true);
                createSingleLocation(destinationLatLng.latitude,destinationLatLng.longitude,false);

                intentToGoSearch.putExtra("srcLat",sourceLatLng.latitude+"");
                intentToGoSearch.putExtra("srcLong",sourceLatLng.longitude+"");

                intentToGoSearch.putExtra("destLat",destinationLatLng.latitude+"");
                intentToGoSearch.putExtra("destLong",destinationLatLng.longitude+"");

                intentToGoSearch.putExtra("prefSpinner",spinnerPref.getSelectedItem().toString());
                intentToGoSearch.putExtra("spinnerPackType",spinnerPackType.getSelectedItem().toString());
                //int priceMaxValue =  Integer.parseInt(price.getText().toString());
                if(price.getText().toString().equals(""))
                    intentToGoSearch.putExtra("priceMaxValue",10000000+"");
                else
                    intentToGoSearch.putExtra("priceMaxValue",price.getText().toString());
                startActivity(intentToGoSearch);

            }
        });
    }
    private void setupMap(GoogleMap googleMap, boolean isSourceMap) {
        googleMap.setOnMarkerDragListener(this);
        googleMap.getUiSettings().setZoomControlsEnabled(true); // Enable zoom controls

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);

            if (isSourceMap) {
                // This block is for setting up the source map
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        if (sourceMarker == null) {
                            sourceLatLng = latLng;
                            sourceMarker = googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
                        } else {
                            sourceLatLng = latLng;
                            sourceMarker.setPosition(sourceLatLng);
                            Toast.makeText(FilterActivity.this, "Source: "+sourceLatLng.toString()+","+getAddressFromCoordinates(sourceLatLng.latitude,sourceLatLng.longitude), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                // Set initial position for source map
                setInitialPositionForSourceMap(googleMap);
            } else {
                // This block is for setting up the destination map
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        if (destinationMarker == null) {
                            destinationLatLng = latLng;
                            destinationMarker = googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true));

                        } else {
                            destinationLatLng = latLng;
                            destinationMarker.setPosition(destinationLatLng);
                            Toast.makeText(FilterActivity.this, "Destination:"+destinationLatLng.toString()+","+getAddressFromCoordinates(destinationLatLng.latitude,destinationLatLng.longitude), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                // Set an initial position for the destination marker
                destinationMarker = googleMap.addMarker(new MarkerOptions().position(DESTINATION_LATLNG).draggable(true));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DESTINATION_LATLNG, ZOOM_LEVEL));
            }
        }
    }


    private void setInitialPositionForSourceMap(final GoogleMap googleMap) {
        if (fusedLocationProviderClient == null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        sourceMarker = googleMap.addMarker(new MarkerOptions().position(currentLatLng).draggable(true));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM_LEVEL));
                    } else {
                        // Handle situation where location is null (could be due to GPS not being enabled or other issues)

                      //  sourceMarker = googleMap.addMarker(new MarkerOptions().position(DESTINATION_LATLNG).draggable(true));
                        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DESTINATION_LATLNG, ZOOM_LEVEL));
                    }
                }
            });
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        // Code to handle the event when marker drag starts
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        // Code to handle the event when marker is being dragged
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // Code to handle the event when marker drag ends
    }
    private void createLocation(double src_latitude, double src_longitude,boolean isSource) {
        createSingleLocation(src_latitude, src_longitude, isSource);
    }

    private void createSingleLocation(double latitude, double longitude,boolean isSource) {
        String title = getAddressTitleFromCoordinates(latitude, longitude);
        String description = getAddressFromCoordinates(latitude, longitude);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = BASE_URL + "/locations/";

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("title", title);
            jsonObject.put("description", description);
            jsonObject.put("latitude", latitude+"");
            jsonObject.put("longitude", longitude+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("jsonObjectValues:",jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int locationID = response.getInt("id");

                            if (locationID > 0) {
                                if (isSource) {
                                    intentToGoSearch.putExtra("sourceLocationId",locationID);
                                    //Toast.makeText(FilterActivity.this, "Source Location Created Successfully", Toast.LENGTH_SHORT).show();
                                    // Create destination location after the source has been created
                                    //createSingleLocation(dst_latitude, dst_longitude, false, locationID, transporterID, 0, 0);
                               } else {
                                    intentToGoSearch.putExtra("destLocationId",locationID);

                                    // Toast.makeText(FilterActivity.this, "Destination Location Created Successfully", Toast.LENGTH_SHORT).show();
                                    // Code to handle successful creation of both source and destination locations
                                    //createService(transporterID, sourceLocationId, locationID);
                               }
                            } else {
                                Toast.makeText(FilterActivity.this, "The information is not correct, try again!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FilterActivity.this, "Network error, please try again later!", Toast.LENGTH_LONG).show();
                        if(!isSource){
                            //sendDeleteRequest(BASE_URL+"/locations/"+sourceLocationId+"/");
                        }
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }};

        requestQueue.add(jsonObjectRequest);
    }

    private String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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
    private String getAddressTitleFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String address = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address1 = addresses.get(0);
                String locationName = address1.getAddressLine(0);
                address=locationName;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }
    //References
    private void setUpReferences()
    {



        spinnerPref = findViewById(R.id.CustomerImportantPreferenceSpinner);
        spinnerPackType = findViewById(R.id.CustomerPackageTypeSpinnerInFilterSearch);
        price = findViewById(R.id.priceInFilterSearch);
        scrollView = findViewById(R.id.scroll_view_id);
        searchInsideFilter = findViewById(R.id.searchBtnInFilterSearch);


        hoursPicker = findViewById(R.id.numPickerHour);
        hoursPicker.setTextColor(Color.WHITE);
        hoursPicker.setMinValue(1);
        hoursPicker.setMaxValue(12);

        minutesPicker = findViewById(R.id.numPickerMin);
        minutesPicker.setTextColor(Color.WHITE);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);

        amPmPicker = findViewById(R.id.numPickerAMPM);
        amPmPicker.setTextColor(Color.WHITE);
        amPmPicker.setMinValue(0);
        amPmPicker.setMaxValue(1);
        amPmPicker.setDisplayedValues(new String[] {"AM", "PM"});

    }
}