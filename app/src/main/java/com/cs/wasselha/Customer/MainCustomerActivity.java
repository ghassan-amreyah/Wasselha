package com.cs.wasselha.Customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.cs.wasselha.Models.ServicesModel;
import com.cs.wasselha.R;
import com.cs.wasselha.databinding.ActivityMainCustomerBinding;
import com.cs.wasselha.interfaces.implementation.LocationDA;
import com.cs.wasselha.interfaces.implementation.ServiceDA;
import com.cs.wasselha.interfaces.implementation.TransporterDA;
import com.cs.wasselha.model.Service;

import java.io.IOException;
import java.util.ArrayList;

public class MainCustomerActivity extends AppCompatActivity {

    ImageView searchImg;
    ActivityMainCustomerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityMainCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intentFromProfileForHistoryOrFilter = getIntent();
        Handler handler  = new Handler();
        if (intentFromProfileForHistoryOrFilter != null ){

            if ( intentFromProfileForHistoryOrFilter.getStringExtra("fromProfile") != null ){

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        replaceFragment(new ReservationsCustomerFragment() );

                    }
                });
            }
            else if (intentFromProfileForHistoryOrFilter.getStringExtra("fromFilter") != null &&
                    intentFromProfileForHistoryOrFilter.getStringExtra("fromFilter").equals("true")){

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        replaceFragment(new HomeCustomerFragment(MainCustomerActivity.this,true,intentFromProfileForHistoryOrFilter));

                    }
                });
            }
            else
            {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        replaceFragment(new HomeCustomerFragment(MainCustomerActivity.this));

                    }
                });
            }

        }

        else
        {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    replaceFragment(new HomeCustomerFragment(MainCustomerActivity.this));

                }
            });
        }

        getSupportActionBar().hide();

        binding.bottomBarInCustomerMainPage.setOnItemSelectedListener(item -> {

            switch(item.getItemId())
            {
                case R.id.nav_home_transporter:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            replaceFragment(new HomeCustomerFragment(MainCustomerActivity.this));

                        }
                    });


                    break;

                case R.id.nav_history_transporter:
                    replaceFragment(new ReservationsCustomerFragment());
                    break;

                case R.id.nav_track:
                    replaceFragment(new TrackingCustomerFragment());
                    break;

                case R.id.nav_notifications_transporter:
                    replaceFragment(new NotificationsCustomerFragment());
                    break;

                case R.id.nav_profile_transporter:
                    replaceFragment(new ProfileCustomerFragment());
                    break;
            }

            return true;
        });

        //References
        setupReference();
        clickOnSearchImgSetup();
        //detailsBtnSetup();


    }
    ArrayList<Service> servicesModelDAList = new ArrayList<>();
    ArrayList<ServicesModel> servicesModelList = new ArrayList<>();
    private ArrayList<Service> getServiceFromDA() throws IOException {

        ServiceDA serviceDA = new ServiceDA();
        return serviceDA.getServices();
    }

   /* private void servicesModelSetup() throws IOException {
        String[] transportersNames = getResources().getStringArray(R.array.services);
        String[] times = getResources().getStringArray(R.array.times);
        String[] sourceCities = getResources().getStringArray(R.array.sourceCities);
        String[] destinationCities = getResources().getStringArray(R.array.destinationCities);

        servicesModelDAList = getServiceFromDA();

        for(int i = 0 ; i < servicesModelDAList.size() ; i++)
        {
            servicesModelList.add(new ServicesModel(new TransporterDA().getTransporter(servicesModelDAList.get(i).getTransporter()).getFirst_name(),
                    servicesModelDAList.get(i).getTransporter(),
                    servicesModelDAList.get(i).getService_date().toString(),
                    new LocationDA().getLocation(servicesModelDAList.get(i).getSource_place()).getTitle(),
                    new LocationDA().getLocation(servicesModelDAList.get(i).getDestination_place()).getTitle()));
        }

    }*/
   /* private void doReplaceFragmentWithImageVehicle() {

        String apiURL="http://176.119.254.198:8000/wasselha";
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = apiURL + "vehicles/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Assume the vehicles are inside a JSONArray called "vehicles" in the response
                            JSONArray vehicles = response.getJSONArray("vehicles");
                            for (int i = 0; i < vehicles.length(); i++) {
                                JSONObject vehicle = vehicles.getJSONObject(i);
                                if (vehicle.getInt("transporter") == transporterID) {

                                    setImage(apiURL+vehicle.getString("vehicle_image"));
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            Log.e("profile","Transporter not found");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("profile","failed loading image(Network Issue )");
            }
        });

        queue.add(jsonObjectRequest);

         String apiURL="http://176.119.254.198:8000/wasselha";
        //public void getVehicleImageURLAndSetImage(Context context, int transporterID) {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = apiURL + "vehicles/";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Assume the vehicles are inside a JSONArray called "vehicles" in the response
                                JSONArray vehicles = response.getJSONArray("vehicles");
                                for (int i = 0; i < vehicles.length(); i++) {
                                    JSONObject vehicle = vehicles.getJSONObject(i);
                                    if (vehicle.getInt("transporter") == transporterID) {
                                        setImage(apiURL+vehicle.getString("vehicle_image"));
                                        return;
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("profile","Transporter not found");
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("profile","failed loading image(Network Issue )");
                }
            });

            queue.add(jsonObjectRequest);
        }
        replaceFragment(new HomeCustomerFragment(this));
    }*/

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainCustomerLayout,fragment);
        fragmentTransaction.commit();

    }


    //References
    private void setupReference()
    {
        searchImg = findViewById(R.id.searchImg);
        //detailsBtn = findViewById(R.id.detailsBtnInCustomerCardRecyclerView);

    }

    private void clickOnSearchImgSetup()
    {
            searchImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(MainCustomerActivity.this, FilterActivity.class);
                    startActivity(intent);
                }
            });

    }


}