package com.cs.wasselha.CollectionPointProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cs.wasselha.Customer.ReservationDetailsActivity;
import com.cs.wasselha.R;
import com.cs.wasselha.interfaces.implementation.CollectionPointDA;
import com.cs.wasselha.interfaces.implementation.LocationDA;
import com.cs.wasselha.model.CollectionPoint;
import com.cs.wasselha.model.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddCollectionPointFragment extends Fragment implements GoogleMap.OnMarkerDragListener{


    String addressGeoCoder;
    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private static String BASE_URL="http://176.119.254.198:8000/wasselha";

    int collectionPointPId;

    ScrollView scrollView ;
    private GoogleMap source_mMap;
    private Marker sourceMarker;
    private LatLng sourceLatLng;

    EditText sourceCityInAddNewCollectionP;

    NumberPicker hoursPickerOpenTime, minutesPickerOpenTime, amPmPickerOpenTime, hoursPickerCloseTime, minutesPickerCloseTime, amPmPickerCloseTime;
    Button addLocationBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_collection_point, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String id = preferences.getString(ID_KEY, null);
        collectionPointPId = Integer.parseInt(id.trim());

        //References
        setUpReferences(view);

        View mapLocationView = view.findViewById(R.id.collectionPointLocationMapView);

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

        SupportMapFragment sourceMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.collectionPointLocationMap);

        sourceMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                source_mMap = googleMap;
                setupMap(source_mMap); // true means this is for the source map
            }
        });

        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Handler handler = new Handler();

                handler.post(new Runnable() {
                    @Override
                    public void run() {



                        if (sourceLatLng != null){
                            try {
                                createSingleLocation(sourceLatLng.latitude,sourceLatLng.longitude);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            //intent.putExtra("toLocationId",""+toLocationId);

                        }

                        else{
                            Toast.makeText(getContext(),
                                    "Click on the map to choose a location! ", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        CollectionPointDA collectionPointDA = new CollectionPointDA();
                        CollectionPoint collectionPoint = new CollectionPoint();
                        collectionPoint.setCollection_point_provider(collectionPointPId);
                        collectionPoint.setName(sourceCityInAddNewCollectionP.getText().toString());
                        collectionPoint.setLocation(locationID);
                        collectionPoint.setStatus("open");

                        int selectedHours = hoursPickerOpenTime.getValue();
                        if (amPmPickerOpenTime.getValue() == 1) { // PM
                            selectedHours = (selectedHours % 12) + 12;
                        }

                        // Obtain the selected minutes
                        int selectedMinutes = minutesPickerOpenTime.getValue();

                        int selectedHours2 = hoursPickerCloseTime.getValue();
                        if (amPmPickerCloseTime.getValue() == 1) { // PM
                            selectedHours2 = (selectedHours2 % 12) + 12;
                        }

                        // Obtain the selected minutes
                        int selectedMinutes2 = minutesPickerCloseTime.getValue();

                        String openTime = selectedHours+":"+selectedMinutes+":00";
                        String closeTime2 = selectedHours2+":"+selectedMinutes2+":00";

                        collectionPoint.setOpen_time(openTime);
                        collectionPoint.setClose_time(closeTime2);
                        try {
                            collectionPointDA.saveCollectionP(collectionPoint);
                            Toast.makeText(getContext(), "Collection Point Added Successfully: ", Toast.LENGTH_LONG).show();

                            replaceFragment(new HomeCollectionPointProviderFragment());


                        } catch (IOException e) {
                            Toast.makeText(getContext(), "Error, try again!", Toast.LENGTH_LONG).show();

                            throw new RuntimeException(e);
                        }

                    }
                });



            }
        });
        return view;
    }

    private void replaceFragment(Fragment fragment)
    {
        //FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainCollectionPointProviderLayout,fragment);
        fragmentTransaction.commit();
    }
    private String getAddressTitleFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
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

    int locationID;
    private void createSingleLocation(double latitude, double longitude) throws IOException {
        String title = getAddressTitleFromCoordinates(latitude, longitude);
        String description = getAddressFromCoordinates(latitude, longitude);

       // RequestQueue requestQueue = Volley.newRequestQueue(getContext());
       // String url = BASE_URL + "/locations/";


        Location location = new Location();
        location.setDescription(description);
        location.setTitle(title);
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        LocationDA locationDA =   new LocationDA();
         locationID = locationDA.saveLocation(location);


            //fromLocationId = locationID;
            //intent.putExtra("fromLocationId",""+fromLocationId+"");
            //intent
            //intent.putExtra("sourceLocationId",String.valueOf(locationID));
            //Log.d("11fromLocationId",String.valueOf(fromLocationId));
            //Toast.makeText(FilterActivity.this, "Source Location Created Successfully", Toast.LENGTH_SHORT).show();
            // Create destination location after the source has been created
            //createSingleLocation(dst_latitude, dst_longitude, false, locationID, transporterID, 0, 0);


            // Toast.makeText(FilterActivity.this, "Destination Location Created Successfully", Toast.LENGTH_SHORT).show();
            // Code to handle successful creation of both source and destination locations
            //createService(transporterID, sourceLocationId, locationID);


    }


    private void setupMap(GoogleMap googleMap) {
        googleMap.setOnMarkerDragListener(this);
        googleMap.getUiSettings().setZoomControlsEnabled(true); // Enable zoom controls
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);

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

                             addressGeoCoder = getAddressFromCoordinates(sourceLatLng.latitude,sourceLatLng.longitude);
                        }
                        Toast.makeText(getContext(), "Location: "+sourceLatLng.toString()+","+addressGeoCoder, Toast.LENGTH_SHORT).show();

                    }
                });


    }}



    //References
    private void setUpReferences(View view)
    {
        hoursPickerOpenTime = view.findViewById(R.id.numPickerHourInOpenTimeInAddNewCollectionPoint);
        hoursPickerOpenTime.setTextColor(Color.WHITE);
        hoursPickerOpenTime.setMinValue(1);
        hoursPickerOpenTime.setMaxValue(12);

        minutesPickerOpenTime = view.findViewById(R.id.numPickerMinInOpenTimeInAddNewCollectionPoint);
        minutesPickerOpenTime.setTextColor(Color.WHITE);
        minutesPickerOpenTime.setMinValue(0);
        minutesPickerOpenTime.setMaxValue(59);

        amPmPickerOpenTime = view.findViewById(R.id.numPickerAMPMInOpenTimeInAddNewCollectionPoint);
        amPmPickerOpenTime.setTextColor(Color.WHITE);
        amPmPickerOpenTime.setMinValue(0);
        amPmPickerOpenTime.setMaxValue(1);
        amPmPickerOpenTime.setDisplayedValues(new String[] {"AM", "PM"});

        hoursPickerCloseTime = view.findViewById(R.id.numPickerHourInCloseTimeInAddNewCollectionPoint);
        hoursPickerCloseTime.setTextColor(Color.WHITE);
        hoursPickerCloseTime.setMinValue(1);
        hoursPickerCloseTime.setMaxValue(12);

        minutesPickerCloseTime = view.findViewById(R.id.numPickerMinInCloseTimeInAddNewCollectionPoint);
        minutesPickerCloseTime.setTextColor(Color.WHITE);
        minutesPickerCloseTime.setMinValue(0);
        minutesPickerCloseTime.setMaxValue(59);

        amPmPickerCloseTime = view.findViewById(R.id.numPickerAMPMInCloseTimeInAddNewCollectionPoint);
        amPmPickerCloseTime.setTextColor(Color.WHITE);
        amPmPickerCloseTime.setMinValue(0);
        amPmPickerCloseTime.setMaxValue(1);
        amPmPickerCloseTime.setDisplayedValues(new String[] {"AM", "PM"});


        sourceCityInAddNewCollectionP = view.findViewById(R.id.sourceCityInAddNewCollectionP);
        addLocationBtn = view.findViewById(R.id.AddBtnInAddNewCollectionPointPage);
        scrollView = view.findViewById(R.id.scroll_view_id);



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