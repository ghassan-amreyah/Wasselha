package com.cs.wasselha.Transporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cs.wasselha.R;
import com.github.javafaker.Bool;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransporterTrackRoad extends AppCompatActivity implements OnMapReadyCallback {

    private static final String BASE_URL = "http://176.119.254.198:8000/wasselha";
    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private static int transporterID;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final long UPDATE_INTERVAL = 5000; // 5 seconds
    private static final long FASTEST_INTERVAL = 2000; // 2 seconds
    private List<LatLng> customerLocations = new ArrayList<>();
    private List<LatLng> collectionPointsLocations = new ArrayList<>();
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker transporterMarker;
    private RequestQueue requestQueue;
    private Integer transporterLocationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporter_track_road);
        requestQueue = Volley.newRequestQueue(this);
        getSupportActionBar().hide();
        try {
            RequestOptions requestOptions = new RequestOptions()
                    .override(100, 100) // Adjust the desired size here
                    .circleCrop();
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.ic_car_map)
                    .apply(requestOptions)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            transporterMarker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(31.896064, 35.205644))
                                    .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                    .anchor(0.5f, 0.5f)
                                    .title("Transporter"));
                        }
                    });
            SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
            String id = preferences.getString(ID_KEY, null);
            transporterID = Integer.parseInt(id.trim());
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapFragment);
            mapFragment.getMapAsync(this);

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        } catch (Exception e) {
            Toast.makeText(this, "Network failed or location permission denied", Toast.LENGTH_LONG).show();
        }

    }

    private void addCollectionPointLocations() {
        String collectionPointsUrl = BASE_URL + "/collection-points/";

        JsonArrayRequest collectionPointsRequest = new JsonArrayRequest(
                Request.Method.GET, collectionPointsUrl, null,
                response -> {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject collectionPoint = response.getJSONObject(i);
                            String status = collectionPoint.getString("status");

                            if ("open".equalsIgnoreCase(status)) {
                                int locationId = collectionPoint.getInt("location");
                                getAndAddLocation(locationId);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    // Handle errors here, such as showing an error message to the user.
                }
        );

        requestQueue.add(collectionPointsRequest);
    }

    private void addCustomersLocations(int transporterID) {
        String servicesUrl = BASE_URL + "/services/?transporter=" + transporterID + "&time=upper";

        JsonArrayRequest servicesRequest = new JsonArrayRequest(
                Request.Method.GET, servicesUrl, null,
                response -> {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject service = response.getJSONObject(i);
                            int serviceId = service.getInt("id");
                            transporterLocationId=service.getInt("transporter_location");
                            getDeliveryServiceDetails(serviceId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    // Handle errors here, such as showing an error message to the user.
                }
        );

        requestQueue.add(servicesRequest);
    }


    private void getDeliveryServiceDetails(int serviceId) {
        String deliveryServiceDetailsUrl = BASE_URL + "/delivery-service-details/?service=" + serviceId;

        JsonArrayRequest detailsRequest = new JsonArrayRequest(
                Request.Method.GET, deliveryServiceDetailsUrl, null,
                response -> {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject detail = response.getJSONObject(i);
                            boolean accepted = detail.getBoolean("accepted");
                            boolean responsed = detail.getBoolean("responsed");

                            if (accepted && responsed) {
                                int customerId = detail.getInt("customer");
                                getCustomerLocation(customerId);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    // Handle errors here, such as showing an error message to the user.
                }
        );

        requestQueue.add(detailsRequest);
    }

    private void getCustomerLocation(int customerId) {
        String customerUrl = BASE_URL + "/customers/" + customerId + "/";

        JsonObjectRequest customerRequest = new JsonObjectRequest(
                Request.Method.GET, customerUrl, null,
                response -> {
                    try {
                        int locationId = response.getInt("location");
                        getLocationAndAdd(locationId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle errors here, such as showing an error message to the user.
                }
        );

        requestQueue.add(customerRequest);
    }

    private void getLocationAndAdd(int locationId) {
        String locationUrl = BASE_URL + "/locations/" + locationId + "/";

        JsonObjectRequest locationRequest = new JsonObjectRequest(
                Request.Method.GET, locationUrl, null,
                response -> {
                    try {
                        double latitude = Double.parseDouble(response.getString("latitude"));
                        double longitude = Double.parseDouble(response.getString("longitude"));
                        customerLocations.add(new LatLng(latitude, longitude));
                        Log.e("addCustomer",latitude+","+longitude);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle errors here, such as showing an error message to the user.
                }
        );

        requestQueue.add(locationRequest);
    }


    private void getAndAddLocation(int locationId) {
        String locationUrl = BASE_URL + "/locations/" + locationId + "/";

        JsonObjectRequest locationRequest = new JsonObjectRequest(
                Request.Method.GET, locationUrl, null,
                response -> {
                    try {
                        double latitude = Double.parseDouble(response.getString("latitude"));
                        double longitude = Double.parseDouble(response.getString("longitude"));
                        collectionPointsLocations.add(new LatLng(latitude, longitude));
                        Log.e("addCollectionPoint",latitude+","+longitude);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle errors here, such as showing an error message to the user.
                }
        );

        requestQueue.add(locationRequest);
    }

//    private void addCollectionPointLocations2() {
//        collectionPointsLocations.add(new LatLng(31.992165, 35.151601)); // deir ibze
//        collectionPointsLocations.add(new LatLng(31.993566, 35.138926)); // kobar
//        collectionPointsLocations.add(new LatLng(31.907012, 35.061517)); // safa
//    }
//
//    private void addCustomersLocations() {
//        customerLocations.add(new LatLng(31.990229, 35.158582)); // last of kobar
//        customerLocations.add(new LatLng(31.920901, 35.107598)); // between kufer nema and deir ibze
//        customerLocations.add(new LatLng(31.914855, 35.127968)); // first of deir ibze
//        customerLocations.add(new LatLng(31.917217, 35.116280)); // last of deir ibze
//    }

    private double calculateDistance(LatLng point1, LatLng point2) {
        Location location1 = new Location("");
        location1.setLatitude(point1.latitude);
        location1.setLongitude(point1.longitude);

        Location location2 = new Location("");
        location2.setLatitude(point2.latitude);
        location2.setLongitude(point2.longitude);

        return location1.distanceTo(location2);
    }

    private void addCustomerAndCollectionPointsMarkers() {

        float scalingFactor = 0.25f; // Adjust the scaling factor as needed
        float visibilityRadius = 35000; // Visibility radius in meters (35km)

        // Load smaller-sized icons for customers and collection points
        Bitmap customerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_person);
        Bitmap collectionPointBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_collectionpoint);

        int scaledWidth = (int) (customerBitmap.getWidth() * scalingFactor);
        int scaledHeight = (int) (customerBitmap.getHeight() * scalingFactor);

        Bitmap scaledCustomerBitmap = Bitmap.createScaledBitmap(customerBitmap, scaledWidth, scaledHeight, false);
        Bitmap scaledCollectionPointBitmap = Bitmap.createScaledBitmap(collectionPointBitmap, scaledWidth, scaledHeight, false);

        // Recycle the original bitmaps to free up memory
        customerBitmap.recycle();
        collectionPointBitmap.recycle();

        LatLng transporterLocation = transporterMarker.getPosition();// Replace with actual transporter location

        for (LatLng customerLocation : customerLocations) {
            double distance = calculateDistance(transporterLocation, customerLocation);
            if (distance <= visibilityRadius) {
                mMap.addMarker(new MarkerOptions()
                        .position(customerLocation)
                        .icon(BitmapDescriptorFactory.fromBitmap(scaledCustomerBitmap))
                        .title("Customer"));
            }
        }

        for (LatLng collectionPointLocation : collectionPointsLocations) {
            double distance = calculateDistance(transporterLocation, collectionPointLocation);
            if (distance <= visibilityRadius) {
                mMap.addMarker(new MarkerOptions()
                        .position(collectionPointLocation)
                        .icon(BitmapDescriptorFactory.fromBitmap(scaledCollectionPointBitmap))
                        .title("CollectionPoint"));
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            // Check location permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // Show normal layer
                if (isNetworkConnected()) {
                    startLocationUpdates();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new AsyncTask<String, Void, Boolean>() {
                                @Override
                                protected Boolean doInBackground(String... params) {
                                    try {
                                        addCustomersLocations(transporterID);
                                        addCollectionPointLocations();
                                        return true;
                                    }catch (Exception e){
                                        return false;
                                    }
                                }

                                @Override
                                protected void onPostExecute(Boolean success) {
                                    if(success){
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.e("addtoMap","latitude,longitude");
                                                addCustomerAndCollectionPointsMarkers();
                                                fetchDestinationCoordinates(transporterID);
                                            }

                                            }, 5000);
                                    }
                                }
                            }.execute();


                        }
                    }, 2000);
                } else {
                    Toast.makeText(TransporterTrackRoad.this, "No internet connection. Please check your network settings.", Toast.LENGTH_SHORT).show();
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Network failed or location permission denied", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    startLocationUpdates();
                }
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Check location permission again before requesting updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        Location location = locationResult.getLastLocation();
                        if (location != null) {
                            updateTransporterLocation(location);
                        }
                    }
                }
            }, null);
        }
    }
    private boolean initialZoomSet = false;
    private void updateTransporterLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (transporterMarker == null) {
            RequestOptions requestOptions = new RequestOptions()
                    .override(200, 200) // Adjust the desired size here
                    .circleCrop();

            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.ic_car_map)
                    .apply(requestOptions)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            transporterMarker = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                    .anchor(0.5f, 0.5f)
                                    .title("Transporter"));
                        }
                    });
        } else {
            transporterMarker.setPosition(latLng);
            // Get the current date
            Calendar calendar = Calendar.getInstance();
            // Get the current minute
            int minute = calendar.get(Calendar.MINUTE);
            // Check if the minute is divisible by 3
            if (minute % 3 == 0) {
                //send post request to this location resource transporterLocationId
                // , url="http://176.119.254.198:8000/wasselha/locations/1/"
                RequestQueue queue = Volley.newRequestQueue(this);
                String url = "http://176.119.254.198:8000/wasselha/locations/" + transporterLocationId + "/";

                // Request a JsonObject response from the provided URL.
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.PUT, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    // Assuming the returned response contains the updated values
                                    response.put("latitude", latLng.latitude);
                                    response.put("longitude", latLng.longitude);
                                    response.put("title", "transporter location");
                                    response.put("description", "location of transporter in road");

                                    // Again, make a PUT request with the updated JSON
                                    JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.PUT, url, response,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    // TODO: Handle the response
                                                    Log.d("Update Success", response.toString());
                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // TODO: Handle error
                                            Log.e("Update Error", error.toString());
                                        }
                                    });

                                    // Add the request to the RequestQueue.
                                    queue.add(updateRequest);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error
                                Log.e("Fetch Error", error.toString());
                            }
                        });

                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest);
            }

        }
        if (!initialZoomSet) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
            initialZoomSet = true;
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }

    }

    private void fetchDestinationCoordinates(int transporterId) {
        new AsyncTask<Void, Void, LatLng>() {
            @Override
            protected LatLng doInBackground(Void... voids) {
                try {
                    String serviceUrl = BASE_URL + "/services/?transporter=" + transporterId + "&time=upper";
                    URL url = new URL(serviceUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String response = reader.readLine();
                    JSONArray jsonArray = new JSONArray(response);
                    int destinationPlace = jsonArray.getJSONObject(0).getInt("destination_place");

                    String locationUrl = BASE_URL + "/locations/" + destinationPlace + "/";
                    url = new URL(locationUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    response = reader.readLine();
                    JSONObject jsonObject = new JSONObject(response);
                    double latitude = jsonObject.getDouble("latitude");
                    double longitude = jsonObject.getDouble("longitude");

                    return new LatLng(latitude, longitude);

                } catch (Exception e) {
                    e.printStackTrace();

                    Log.e("addtoMap","error in get destination");
                    return null;
                }
            }

            @Override
            protected void onPostExecute(LatLng latLng) {
                if (latLng != null) {
                    Log.e("addtoMap","latitude,longitude"+transporterMarker.getPosition().toString()+","+latLng.toString());
                    drawRouteToDestination(transporterMarker.getPosition(),latLng);
                }
            }
        }.execute();
    }
    private void drawRouteToDestination(LatLng transporterPosition, LatLng destination) {
        String apiKey = getString(R.string.MAPS_API_KEY);
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + transporterPosition.latitude + "," + transporterPosition.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&key=" + apiKey;

        new AsyncTask<String, Void, List<LatLng>>() {
            @Override
            protected List<LatLng> doInBackground(String... params) {
                try {
                    URL directionsUrl = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) directionsUrl.openConnection();
                    connection.setRequestMethod("GET");
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    // Read the complete response
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    String response = responseBuilder.toString();

                    // Log the response
                    Log.d("API_RESPONSE", response);

                    // Now parse the JSON
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray routesArray = jsonObject.getJSONArray("routes");
                    if (routesArray.length() > 0) {
                        JSONObject route = routesArray.getJSONObject(0);
                        JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                        String encodedPolyline = overviewPolyline.getString("points");
                        return decodePoly(encodedPolyline);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("addtoMap", e.toString());
                    Log.e("addtoMap", "ERROR in draw path");
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<LatLng> result) {
                if (result != null) {
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.addAll(result);
                    polylineOptions.width(15);
                    polylineOptions.color(Color.parseColor("#94e5ff"));
                    mMap.addPolyline(polylineOptions);
                }
            }
        }.execute(url);
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng latLng = new LatLng(((double) lat / 1E5),
                    ((double) lng / 1E5));
            poly.add(latLng);
        }
        return poly;
    }


    private void addDestinationMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Destination");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.addMarker(markerOptions);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}