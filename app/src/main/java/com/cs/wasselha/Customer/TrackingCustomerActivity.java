package com.cs.wasselha.Customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cs.wasselha.R;
import com.cs.wasselha.interfaces.implementation.CollectionPointDA;
import com.cs.wasselha.interfaces.implementation.DeliveryServiceDetailsDA;
import com.cs.wasselha.interfaces.implementation.LocationDA;
import com.cs.wasselha.interfaces.implementation.ServiceDA;
import com.cs.wasselha.model.CollectionPoint;
import com.cs.wasselha.model.DeliveryServiceDetails;
import com.cs.wasselha.model.Location;
import com.cs.wasselha.model.Service;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TrackingCustomerActivity extends AppCompatActivity implements OnMapReadyCallback{



    private static final String BASE_URL = "http://176.119.254.198:8000/wasselha";
    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private static int transporterID;
    private int customerId;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final long UPDATE_INTERVAL = 5000; // 5 seconds
    private static final long FASTEST_INTERVAL = 2000; // 2 seconds
    private List<LatLng> customerLocations = new ArrayList<>();
    private List<LatLng> collectionPointsLocations = new ArrayList<>();

    private int currentTransporterLocationId;
    private Location currentTransLocation;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker transporterMarker;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_customer);


        getSupportActionBar().hide();

        //MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST, listener);

        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String id = preferences.getString(ID_KEY, null);
        customerId = Integer.parseInt(id.trim());
        try {
            prepareAndGetLocation();
            setDestLatLog();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //requestQueue = Volley.newRequestQueue(this);
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
                                    .position(new LatLng(currentTransLocation.getLatitude(), currentTransLocation.getLongitude()))
                                    .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                    .anchor(0.5f, 0.5f)
                                    .title("Transporter"));


                            drawRouteToDestination(new LatLng(currentTransLocation.getLatitude(), currentTransLocation.getLongitude()),
                                    destLatLng);
                        }
                    });

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.trackingCustomerActivityMapFrag);
            mapFragment.getMapAsync(this);

            //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        } catch (Exception e) {
            Toast.makeText(this, "Network failed or location permission denied", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            this.mMap = googleMap;
            // Check location permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // Show normal layer
                if (isNetworkConnected()) {

                    startLocationUpdates();
                   /* new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new AsyncTask<String, Void, Boolean>() {
                                @Override
                                protected Boolean doInBackground(String... params) {
                                    try {
                                       // addCustomersLocations(transporterID);
                                       // addCollectionPointLocations();
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

                                                //addCustomerAndCollectionPointsMarkers();
                                                //fetchDestinationCoordinates(transporterID);
                                                //drawRouteToDestination(transporterMarker.getPosition(),latLng);
                                               // setDestLatLog();

                                                drawRouteToDestination(transporterMarker.getPosition(), destLatLng);
                                                Log.d("addtoMap","latitude=" + destLatLng.latitude+", longitude="+ destLatLng.longitude);
                                            }

                                        }, 5000);
                                    }
                                }
                            }.execute();


                        }
                    }, 2000);*/
                } else {
                    Toast.makeText(TrackingCustomerActivity.this, "No internet connection. Please check your network settings.", Toast.LENGTH_SHORT).show();
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Network failed or location permission denied", Toast.LENGTH_LONG).show();
        }

    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    //startLocationUpdates();
                }
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }*/


    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    int count =0;
    private void startLocationUpdates() {
        /*LocationRequest locationRequest = LocationRequest.create();
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
                            updateTransporterLocationOnMap(location);
                        }
                    }
                }
            }, null);
        }*/


        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {


                com.cs.wasselha.model.Location location = null;
                try {
                    location = prepareAndGetLocation();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //setDestLatLog();
                        updateTransporterLocationOnMap(currentTransLocation);

                        drawRouteToDestination(new LatLng(currentTransLocation.getLatitude(), currentTransLocation.getLongitude()), destLatLng);
                    }
                });


                /*while (count<1){
                    Handler handler1 = new Handler();
                    handler1.postDelayed(this,UPDATE_INTERVAL);
                    //handler.postDelayed(this, UPDATE_INTERVAL);
                    count++;
                    Log.d("logCount" , count+"");
                    SystemClock.sleep(UPDATE_INTERVAL);
                }*/


            }
        });
        count=0;
    }

    LatLng destLatLng;
    private LatLng setDestLatLog() {

        Location locationDest;
        if (deliveryServiceDetailsCurrent.getDestination_collection_point() != 0){

            CollectionPoint collectionPoint;
            try {
                collectionPoint=  new CollectionPointDA().getCollectionP(deliveryServiceDetailsCurrent.getDestination_collection_point());
                locationDest = new LocationDA().getLocation(collectionPoint.getLocation());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }else if(deliveryServiceDetailsCurrent.getDestination_place() !=null  ){


            try {
                locationDest = new LocationDA().getLocation(Integer.parseInt(deliveryServiceDetailsCurrent.getDestination_place()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }else {
            try {
                locationDest = new LocationDA().getLocation(1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

         destLatLng = new LatLng(locationDest.getLatitude(),locationDest.getLongitude());
        //destLatLng = new LatLng(32.2243079,35.2270797); // nablus
        return destLatLng;
    }

    private boolean initialZoomSet = false;

    private void updateTransporterLocationOnMap(com.cs.wasselha.model.Location location) {
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
        }
        if (!initialZoomSet) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
            initialZoomSet = true;
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }

    }


    public DeliveryServiceDetails getTheLastServiceForTheCustomer(){

         ArrayList<DeliveryServiceDetails> delivaryDetailsReservationsForCustomerData;

        try {
            delivaryDetailsReservationsForCustomerData = new DeliveryServiceDetailsDA().getDSDsForACustomer(customerId);
            delivaryDetailsReservationsForCustomerData.sort(new Comparator<DeliveryServiceDetails>() {
                @Override
                public int compare(DeliveryServiceDetails o1, DeliveryServiceDetails o2) {
                    return  o2.getId()-o1.getId();
                }
            });

            for (DeliveryServiceDetails dsd:delivaryDetailsReservationsForCustomerData){
                if (dsd.isResponsed() &&dsd.isAccepted())
                    return dsd;
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return delivaryDetailsReservationsForCustomerData.get(0);
    }

    private DeliveryServiceDetails deliveryServiceDetailsCurrent;
    public void getServiceAndSetLocationIdForTransporter() throws IOException {
        deliveryServiceDetailsCurrent = getTheLastServiceForTheCustomer();


       int serviceServiceId =  deliveryServiceDetailsCurrent.getService();
        Log.d("sId1",serviceServiceId+"");
      Service service =  new ServiceDA().getService(serviceServiceId);

      currentTransporterLocationId = service.getTransporter_location();

      Log.d("transporter_location",currentTransporterLocationId+"");

    }

    public com.cs.wasselha.model.Location prepareAndGetLocation() throws IOException {

        getServiceAndSetLocationIdForTransporter();


       currentTransLocation = new LocationDA().getLocation(currentTransporterLocationId);
       // currentTransLocation = new LocationDA().getLocation(2);


        return currentTransLocation;

    }


    private void drawRouteToDestination(LatLng transporterPosition, LatLng destination) {
        String apiKey = getString(R.string.MAPS_API_KEY); //AIzaSyCc0zpvvElMnKIwU1wL0GzUSjd0ALk2b28
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
}