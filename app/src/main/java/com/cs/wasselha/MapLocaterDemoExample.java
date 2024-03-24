package com.cs.wasselha;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;

public class MapLocaterDemoExample extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private GoogleMap mMap;
    private Marker mMarker;
    private LatLng lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_locater_demo_example);
        getSupportActionBar().hide();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button saveLocationButton = findViewById(R.id.saveLocationButton);
        saveLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastLocation != null) {
                    // Do something with the last location (lastLocation)
                    showToastWithLatLng(lastLocation);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        LatLng initialPosition = new LatLng(31.9038, 35.2034); // default Example: Ramallah
//        mMarker = mMap.addMarker(new MarkerOptions().position(initialPosition).draggable(true));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 10));

        // Get the user's current location using GPS
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastKnownLocation != null) {
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();

                LatLng initialPosition = new LatLng(latitude, longitude);
                mMarker = mMap.addMarker(new MarkerOptions().position(initialPosition).draggable(true));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 10));
            }
        }else {
            // Request location permission from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        LatLng initialPosition = new LatLng(31.9038, 35.2034); // default Example: Ramallah
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Location permission granted, try accessing the user's location again
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Log.d("MapLocaterDemoExample", lastKnownLocation.toString());
                    if (lastKnownLocation != null) {
                        double latitude = lastKnownLocation.getLatitude();
                        double longitude = lastKnownLocation.getLongitude();

                        LatLng initialPosition = new LatLng(latitude, longitude);
                        mMarker = mMap.addMarker(new MarkerOptions().position(initialPosition).draggable(true));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 10));
                    }
                }
            } else {
                // Location permission denied, handle it accordingly (e.g., show a message or disable location-related functionality)
            }
        }
    }


    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        mMarker.setPosition(latLng);
        reverseGeocode(latLng);
//        showToastWithLatLng(latLng);
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        showToastWithLatLng(marker.getPosition());
    }
    private void reverseGeocode(LatLng position) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                String locationName = address.getAddressLine(0);
                showToastWithLocation(position, locationName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showToastWithLocation(LatLng position, String locationName) {
        String msg = "Location: " + locationName + "\nLatitude: " + position.latitude + ", Longitude: " + position.longitude;
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void showToastWithLatLng(LatLng latLng) {
        String msg = "Latitude: " + latLng.latitude + ", Longitude: " + latLng.longitude;
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
