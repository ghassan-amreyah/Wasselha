package com.cs.wasselha.Transporter;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.Manifest;

import com.cs.wasselha.Adapters.ClaimsTransporterAdapter;
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

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AddServiceTransporterFragment extends Fragment implements GoogleMap.OnMarkerDragListener{
    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private static String BASE_URL="http://176.119.254.198:8000/wasselha";
    private static final LatLng DESTINATION_LATLNG = new LatLng(31.908167, 35.210383);
    private static final float ZOOM_LEVEL = 20f;

    private EditText price;
    private NumberPicker hoursPicker, minutesPicker, amPmPicker;
    private Button create;
    private TextView dateService;
    ScrollView scrollView ;
    private GoogleMap source_mMap;
    private GoogleMap destination_mMap;
    private Marker sourceMarker;
    private Marker destinationMarker;
    private LatLng sourceLatLng;
    private LatLng destinationLatLng;

    private ProgressDialog progressDialog;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_add_service_transporter, container, false);
            SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
            String id = preferences.getString(ID_KEY, null);
            int transporterID = Integer.parseInt(id.trim());

            setUpReferences(view);
            dateServiceSetup();
            View sourceMapTransparentView = view.findViewById(R.id.sourceMapTransparentView);
            View destinationMapTransparentView = view.findViewById(R.id.destinationMapTransparentView);

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

            // Setup the two map fragments with separate callbacks
            SupportMapFragment sourceMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.sourceCityInAddServiceMap);
            SupportMapFragment destinationMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.destinationCityInAddServiceMap); // Note: This should have a different ID than sourceMapFragment

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

            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (verifyInputsAndSubmit()) {
                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setMessage("Loading...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        new AsyncTask<String, Void, Boolean>() {
                                @Override
                                protected Boolean doInBackground(String... params) {
                                    try {
                                        if (sourceLatLng != null && destinationLatLng != null) {
                                            createLocation(sourceLatLng.latitude, sourceLatLng.longitude, destinationLatLng.latitude, destinationLatLng.longitude, transporterID);
                                        } else {
                                            Toast.makeText(getContext(), "Please select both source and destination locations", Toast.LENGTH_SHORT).show();
                                        }
                                        return true;
                                    } catch (Exception e) {
                                        return false;
                                    }
                                }
                            @Override
                            protected void onPostExecute(Boolean success) {
                                progressDialog.dismiss();
                                if (success) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                        }

                                    }, 3000);
                                }
                            }
                        }.execute();
                    }
                }
            });
            return view;
        }catch(Exception e){
            Toast.makeText(getContext(), "Network Error, please try in anther time", Toast.LENGTH_SHORT).show();
            View view = inflater.inflate(R.layout.fragment_add_service_transporter, container, false);
            return view;
            }
    }

    private void setupMap(GoogleMap googleMap, boolean isSourceMap) {
        googleMap.setOnMarkerDragListener(this);
        googleMap.getUiSettings().setZoomControlsEnabled(true); // Enable zoom controls

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);

            if (isSourceMap) {
                // This block is for setting up the source map
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (sourceMarker == null) {
                            sourceLatLng = latLng;
                            sourceMarker = googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
                        } else {
                            sourceLatLng = latLng;
                            sourceMarker.setPosition(sourceLatLng);
                            Toast.makeText(getContext(), "Source: "+sourceLatLng.toString()+","+getAddressFromCoordinates(sourceLatLng.latitude,sourceLatLng.longitude), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                // Set initial position for source map
                setInitialPositionForSourceMap(googleMap);
            } else {
                // This block is for setting up the destination map
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (destinationMarker == null) {
                            destinationLatLng = latLng;
                            destinationMarker = googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
                        } else {
                            destinationLatLng = latLng;
                            destinationMarker.setPosition(destinationLatLng);
                            Toast.makeText(getContext(), "Destination:"+destinationLatLng.toString()+","+getAddressFromCoordinates(destinationLatLng.latitude,destinationLatLng.longitude), Toast.LENGTH_SHORT).show();
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
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        sourceMarker = googleMap.addMarker(new MarkerOptions().position(currentLatLng).draggable(true));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM_LEVEL));
                    } else {
                        // Handle situation where location is null (could be due to GPS not being enabled or other issues)
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

    private void dateServiceSetup() {
        // Set up date picker
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            dateService.setText(sdf.format(calendar.getTime()));
        };

        dateService.setOnClickListener(v -> new DatePickerDialog(getContext(), date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

private void createLocation(double src_latitude, double src_longitude, double dst_latitude, double dst_longitude, int transporterID) {
    createSingleLocation(src_latitude, src_longitude, true,false, 0,0, transporterID, dst_latitude, dst_longitude);
}
private void createSingleLocation(double latitude, double longitude, boolean isSource,boolean isTransporterLocationCreated, int sourceLocationId, int dst_LocationId, int transporterID, double dst_latitude, double dst_longitude) {
    String title = getAddressTitleFromCoordinates(latitude, longitude);
    String description = getAddressFromCoordinates(latitude, longitude);

    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                                Toast.makeText(getContext(), "Source Location Created Successfully", Toast.LENGTH_SHORT).show();
                                // Create destination location after the source has been created
                                createSingleLocation(dst_latitude, dst_longitude, false,false, locationID,0, transporterID, latitude, longitude);
                            } else {
                                Toast.makeText(getContext(), "Destination Location Created Successfully", Toast.LENGTH_SHORT).show();
                                // Code to handle successful creation of both source and destination locations
                                if(!isTransporterLocationCreated){
                                    createSingleLocation(dst_latitude, dst_longitude, false,true, sourceLocationId,locationID, transporterID, 0, 0);
                                }else{
                                    createService(transporterID, sourceLocationId,dst_LocationId, locationID);
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "The information is not correct, try again!", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Network error, please try again later!", Toast.LENGTH_LONG).show();
                    if(!isSource){
                        sendDeleteRequest(BASE_URL+"/locations/"+sourceLocationId+"/");
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



    private void createService(int transporterId, int sourcePlace, int destinationPlace,int transporterPlace) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url = BASE_URL +"/services/";
        // Obtain the selected date
        String selectedDate = dateService.getText().toString();

        // Obtain the selected hours (convert to 24-hour format if PM is selected)
        int selectedHours = hoursPicker.getValue();
        if (amPmPicker.getValue() == 1) { // PM
            selectedHours = (selectedHours % 12) + 12;
        }

        // Obtain the selected minutes
        int selectedMinutes = minutesPicker.getValue();
        Log.d("TestMin", selectedMinutes +  "");

        // Combine the date and time
        String dateTimeString = String.format(
                "%sT%02d:%02d:00", // Format as "yyyy-MM-ddTHH:mm:ss"
                selectedDate,
                selectedHours,
                selectedMinutes
        );

        // Obtain the time zone offset
        TimeZone timeZone = TimeZone.getDefault();
        long offsetInMillis = timeZone.getOffset(System.currentTimeMillis());
        int offsetHours = (int) (offsetInMillis / 3600000); // Convert to hours
        int offsetMinutes = (int) (offsetInMillis / 60000) % 60; // Convert to minutes

        // Combine the date-time string with the time zone offset
        String serviceDate = String.format(
                "%s%+03d:%02d", // Format as "yyyy-MM-ddTHH:mm:ss+HH:mm"
                dateTimeString,
                offsetHours,
                offsetMinutes
        );

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("service_date", serviceDate);
            jsonObject.put("price", Double.parseDouble(price.getText().toString()));
            jsonObject.put("transporter", transporterId);
            jsonObject.put("source_place", sourcePlace);
            jsonObject.put("destination_place", destinationPlace);
            jsonObject.put("transporter_location", transporterPlace);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int id = response.getInt("id");

                            if (id > 0) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Service Created Successfully", Toast.LENGTH_SHORT).show();
                                replaceFragment(new HomeTransporterFragment());
                            } else {
                                Toast.makeText(getContext(), "The information is not correct, try again!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Network error, please try again later!", Toast.LENGTH_LONG).show();
                        sendDeleteRequest(BASE_URL+"/locations/"+sourcePlace+"/");
                        sendDeleteRequest(BASE_URL+"/locations/"+destinationPlace+"/");
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

    private void setUpReferences(View view) {
        scrollView = view.findViewById(R.id.scroll_view_id);
        dateService = view.findViewById(R.id.dateNewServiceTransporter);
        dateService.setClickable(true);
        dateService.setFocusable(true);
        create = view.findViewById(R.id.addServiceBtnInAddNewServicePage);
        price = view.findViewById(R.id.priceInAddNewService);
        hoursPicker = view.findViewById(R.id.numPickerHourInAddNewService);
        hoursPicker.setTextColor(Color.WHITE);
        hoursPicker.setMinValue(1);
        hoursPicker.setMaxValue(12);

        minutesPicker = view.findViewById(R.id.numPickerMinInAddNewService);
        minutesPicker.setTextColor(Color.WHITE);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);

        amPmPicker = view.findViewById(R.id.numPickerAMPMInAddNewService);
        amPmPicker.setTextColor(Color.WHITE);
        amPmPicker.setMinValue(0);
        amPmPicker.setMaxValue(1);
        amPmPicker.setDisplayedValues(new String[]{"AM", "PM"});
    }
    private String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
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
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
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
    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainTransporterLayout,fragment);
        fragmentTransaction.commit();
    }
    private boolean verifyInputsAndSubmit() {
        Context context = getContext();

        // Check if price is filled
        String priceText = price.getText().toString();
        if (priceText.isEmpty()) {
            Toast.makeText(context, "Please enter the price", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if date and time is valid and not in the past
        String dateText = dateService.getText().toString();
        if (dateText.isEmpty()) {
            Toast.makeText(context, "Please select a date", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            try {
                // Get the selected time
                int hour = hoursPicker.getValue();
                int minute = minutesPicker.getValue();
                Log.d("TestMints", minute + "");
                String amPm = amPmPicker.getValue() == 0 ? "AM" : "PM";
                if (amPm.equals("PM") && hour < 12) {
                    hour += 12;
                }


                // Combine date and time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
                Date selectedDateTime = sdf.parse(dateText + " " + hour + ":" + minute + " " + amPm);
                Date currentDateTime = new Date();
                if (selectedDateTime != null && selectedDateTime.before(currentDateTime)) {
                    Toast.makeText(context, "Date and time should be in the future", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (ParseException e) {
                Toast.makeText(context, "Invalid date or time format", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // Check if source and destination markers are placed on the map
        if (sourceLatLng == null) {
            Toast.makeText(context, "Please place the source marker on the map", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (destinationLatLng == null) {
            Toast.makeText(context, "Please place the destination marker on the map", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
