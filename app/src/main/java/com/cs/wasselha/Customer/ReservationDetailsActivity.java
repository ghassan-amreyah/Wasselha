package com.cs.wasselha.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cs.wasselha.R;
import com.cs.wasselha.interfaces.implementation.CollectionPointDA;
import com.cs.wasselha.interfaces.implementation.LocationDA;
import com.cs.wasselha.model.CollectionPoint;
import com.cs.wasselha.model.DeliveryServiceDetails;
import com.cs.wasselha.model.Location;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReservationDetailsActivity extends AppCompatActivity  implements GoogleMap.OnMarkerDragListener{

    EditText  packageTypeInReservationsDetails;
    EditText  personHandoverName;
    EditText  packageWeightInReservationsDetails;
    RadioButton collectFromLocationRadioButtonInReservationsDetails;
    RadioButton collectFromCollectionPointRadioButtonInReservationsDetails;

    Spinner collectFromCollectionPointSpinner;

    RadioButton handOverToLocationRadioButtonInReservationsDetails;
    RadioButton handOverToCollectionPointRadioButtonInReservationsDetails;

    Spinner handOverToCollectionPointSpinner;

    Button reserveBtnReservationDetailsPage;

    ArrayList<CollectionPoint> collectionPoints;
    ArrayList<String> spinnerStrings = new ArrayList<>();


    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private static String BASE_URL="http://176.119.254.198:8000/wasselha";
    private static final LatLng DESTINATION_LATLNG = new LatLng(31.908167, 35.210383);
    private static final float ZOOM_LEVEL = 15f;
    ScrollView scrollView ;
    private GoogleMap source_mMap;
    private GoogleMap destination_mMap;
    private Marker sourceMarker;
    private Marker destinationMarker;
    private LatLng sourceLatLng;
    private LatLng destinationLatLng;
    private FusedLocationProviderClient fusedLocationProviderClient;
    int fromCPLocationId;
    int toCPLocationId;

    int fromLocationId;
    int toLocationId;


    Intent intent=new Intent();

    public ReservationDetailsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_details);
        getSupportActionBar().hide();

        setUpViews();




        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                onClickRadioCollectionPointForSpinners();
            }
        });

        /*collectFromCollectionPointRadioButtonInReservationsDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // onClickRadioCollectionPointForSpinners();
            }
        });*/
       /* handOverToCollectionPointRadioButtonInReservationsDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/




        reserveBtnReservationDetailsPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (collectFromLocationRadioButtonInReservationsDetails.isChecked()){

                    if (sourceLatLng != null ){

                        try {
                            createSingleLocation(sourceLatLng.latitude,sourceLatLng.longitude,true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        //intent.putExtra("fromLocationId",""+fromLocationId+"");

                    }else{
                        Toast.makeText(ReservationDetailsActivity.this,
                                "Click on the map to choose a src location! ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // todo: here i have to handle the google map
                }else {
                    // the collectionPoint is checked

                    fromCPLocationId = Integer.parseInt(collectFromCollectionPointSpinner.getSelectedItem().toString().split(" ")[0]);

                    intent.putExtra("fromCPLocationId",String.valueOf(fromCPLocationId));

                }


                if (handOverToLocationRadioButtonInReservationsDetails.isChecked()){

                    if (destinationLatLng != null){
                        try {
                            createSingleLocation(destinationLatLng.latitude,destinationLatLng.longitude,false);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        //intent.putExtra("toLocationId",""+toLocationId);

                    }

                    else{
                        Toast.makeText(ReservationDetailsActivity.this,
                                "Click on the map to choose a dest location! ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // todo: here i have to handle the google map
                }else {
                    // the collectionPoint is checked

                    toCPLocationId = Integer.parseInt(collectFromCollectionPointSpinner.getSelectedItem().toString().split(" ")[0]);
                    intent.putExtra("toCPLocationId",String.valueOf(toCPLocationId));


                }

                //DeliveryServiceDetails deliveryServiceDetails = new DeliveryServiceDetails();
                //deliveryServiceDetails

                if (packageTypeInReservationsDetails.getText().toString().equals("") ||
                        packageWeightInReservationsDetails.getText().toString().equals("")){

                    Toast.makeText(ReservationDetailsActivity.this,
                            "Fill the gaps inputs! ", Toast.LENGTH_SHORT).show();
                    return;
                }







                if (intent.getStringExtra("fromCPLocationId") == null &&
                        intent.getStringExtra("toCPLocationId") ==null &&
                        intent.getStringExtra("fromLocationId") == null&&
                        intent.getStringExtra("toLocationId") == null){
                    Toast.makeText(ReservationDetailsActivity.this,
                            "Wait!, location is being created! ", Toast.LENGTH_SHORT).show();
                return;
                }



                //createSingleLocation(sourceLatLng.latitude,sourceLatLng.longitude,true);
                //createSingleLocation(destinationLatLng.latitude,destinationLatLng.longitude,false);
                String packType = packageTypeInReservationsDetails.getText().toString();
                String packWeight = packageWeightInReservationsDetails.getText().toString();

                intent.putExtra("packWeight",packWeight);
                intent.putExtra("packType",packType);
                intent.putExtra("personHandoverName",personHandoverName.getText().toString());

               // intent.putExtra("packType",packType);
                //SystemClock.sleep(2000);
                setResult(2,intent);
                finish();//finishing activity



            }
        });



        View sourceMapCollectFromView = findViewById(R.id.collectFromReservationMapView);
        View destinationMapHandoverToView = findViewById(R.id.handOverToReservationMapView);

        sourceMapCollectFromView.setOnTouchListener(new View.OnTouchListener() {
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
        destinationMapHandoverToView.setOnTouchListener(new View.OnTouchListener() {
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
        SupportMapFragment sourceMapFragment = (SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.collectFromReservationMap);
        SupportMapFragment destinationMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.handOverToReservationMap); // Note: This should have a different ID than sourceMapFragment

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
                            Toast.makeText(ReservationDetailsActivity.this, "Source: "+sourceLatLng.toString()+","+getAddressFromCoordinates(sourceLatLng.latitude,sourceLatLng.longitude), Toast.LENGTH_SHORT).show();

                        } else {
                            sourceLatLng = latLng;
                            sourceMarker.setPosition(sourceLatLng);
                            Toast.makeText(ReservationDetailsActivity.this, "Source: "+sourceLatLng.toString()+","+getAddressFromCoordinates(sourceLatLng.latitude,sourceLatLng.longitude), Toast.LENGTH_SHORT).show();
                        }
                        //createLocation(sourceLatLng.latitude,sourceLatLng.longitude,true);
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
                            Toast.makeText(ReservationDetailsActivity.this, "Destination:"+destinationLatLng.toString()+","+getAddressFromCoordinates(destinationLatLng.latitude,destinationLatLng.longitude), Toast.LENGTH_SHORT).show();

                        } else {
                            destinationLatLng = latLng;
                            destinationMarker.setPosition(destinationLatLng);
                            Toast.makeText(ReservationDetailsActivity.this, "Destination:"+destinationLatLng.toString()+","+getAddressFromCoordinates(destinationLatLng.latitude,destinationLatLng.longitude), Toast.LENGTH_SHORT).show();
                        }
                        //createLocation(destinationLatLng.latitude,destinationLatLng.longitude,false);

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
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(android.location.Location location) {
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
    private void createLocation(double src_latitude, double src_longitude,boolean isSource) throws IOException {
        createSingleLocation(src_latitude, src_longitude, isSource);
    }

    private void createSingleLocation(double latitude, double longitude,boolean isSource) throws IOException {
        String title = getAddressTitleFromCoordinates(latitude, longitude);
        String description = getAddressFromCoordinates(latitude, longitude);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = BASE_URL + "/locations/";


        Location location = new Location();
        location.setDescription(description);
        location.setTitle(title);
        location.setLatitude(latitude);
        location.setLongitude(longitude);

      LocationDA locationDA =   new LocationDA();
       int locationID = locationDA.saveLocation(location);

        if (isSource) {
            fromLocationId = locationID;
            intent.putExtra("fromLocationId",""+fromLocationId+"");
            //intent
            //intent.putExtra("sourceLocationId",String.valueOf(locationID));
            Log.d("11fromLocationId",String.valueOf(fromLocationId));
            //Toast.makeText(FilterActivity.this, "Source Location Created Successfully", Toast.LENGTH_SHORT).show();
            // Create destination location after the source has been created
            //createSingleLocation(dst_latitude, dst_longitude, false, locationID, transporterID, 0, 0);
        } else {
            toLocationId = locationID;
            intent.putExtra("toLocationId",""+toLocationId+"");
            //intent.putExtra("destLocationId",String.valueOf(locationID));
            Log.d("11toLocationId",String.valueOf(toLocationId));

            // Toast.makeText(FilterActivity.this, "Destination Location Created Successfully", Toast.LENGTH_SHORT).show();
            // Code to handle successful creation of both source and destination locations
            //createService(transporterID, sourceLocationId, locationID);
        }
       /* JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("title", title);
            jsonObject.put("description", description);
            jsonObject.put("latitude", latitude+"");
            jsonObject.put("longitude", longitude+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("jsonObjectValues:",jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int locationID = response.getInt("id");

                            if (locationID > 0) {
                                if (isSource) {
                                    fromLocationId = locationID;
                                    intent.putExtra("fromLocationId",""+fromLocationId+"");
                                    //intent
                                    //intent.putExtra("sourceLocationId",String.valueOf(locationID));
                                    Log.d("11fromLocationId",String.valueOf(fromLocationId));
                                    //Toast.makeText(FilterActivity.this, "Source Location Created Successfully", Toast.LENGTH_SHORT).show();
                                    // Create destination location after the source has been created
                                    //createSingleLocation(dst_latitude, dst_longitude, false, locationID, transporterID, 0, 0);
                                } else {
                                    toLocationId = locationID;
                                    intent.putExtra("toLocationId",""+toLocationId+"");
                                    //intent.putExtra("destLocationId",String.valueOf(locationID));
                                    Log.d("11toLocationId",String.valueOf(toLocationId));

                                    // Toast.makeText(FilterActivity.this, "Destination Location Created Successfully", Toast.LENGTH_SHORT).show();
                                    // Code to handle successful creation of both source and destination locations
                                    //createService(transporterID, sourceLocationId, locationID);
                                }
                            } else {
                                Toast.makeText(ReservationDetailsActivity.this, "The information is not correct, try again!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ReservationDetailsActivity.this, "Network error, please try again later!", Toast.LENGTH_LONG).show();
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

        requestQueue.add(jsonObjectRequest);*/
    }

    private String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String address = "Rand";

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
        String address = "Rand";

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



    private void setUpViews() {

        packageTypeInReservationsDetails = findViewById(R.id.packageTypeInReservationsDetails);
        packageWeightInReservationsDetails = findViewById(R.id.packageWeightInReservationsDetails);
        collectFromLocationRadioButtonInReservationsDetails =
                findViewById(R.id.collectFromLocationRadioButtonInReservationsDetails);

        scrollView = findViewById(R.id.scroll_view_idRd);
        collectFromCollectionPointRadioButtonInReservationsDetails =
                findViewById(R.id.collectFromCollectionPointRadioButtonInReservationsDetails);

        collectFromCollectionPointRadioButtonInReservationsDetails.setSelected(true);
        collectFromCollectionPointSpinner = findViewById(R.id.collectFromCollectionPointSpinner);

        handOverToLocationRadioButtonInReservationsDetails =
                findViewById(R.id.handOverToLocationRadioButtonInReservationsDetails);

        handOverToCollectionPointRadioButtonInReservationsDetails =
                findViewById(R.id.handOverToCollectionPointRadioButtonInReservationsDetails);
        handOverToCollectionPointRadioButtonInReservationsDetails.setSelected(true);
        handOverToCollectionPointSpinner = findViewById(R.id.handOverToCollectionPointSpinner);

        reserveBtnReservationDetailsPage = findViewById(R.id.reserveBtnReservationDetailsPage);
        personHandoverName = findViewById(R.id.personHandoverName);


    }

    private void onClickRadioCollectionPointForSpinners(){
        CollectionPointDA collectionPointDA = new CollectionPointDA();

        try {
            collectionPoints = collectionPointDA.getCollectionPs();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//
  //      LocationDA locationDA = new LocationDA();
        for (CollectionPoint c: collectionPoints) {


            //try {
               // Location location = locationDA.getLocation(.getLocation());
                spinnerStrings.add( c.getId()+" "+c.getName());
            //} catch (IOException e) {
              //  throw new RuntimeException(e);
            //}
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerStrings);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        handOverToCollectionPointSpinner.setAdapter(arrayAdapter);
        collectFromCollectionPointSpinner.setAdapter(arrayAdapter);
    }
}