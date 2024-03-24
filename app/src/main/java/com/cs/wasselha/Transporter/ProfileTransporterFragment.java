package com.cs.wasselha.Transporter;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.JsonArrayRequest;
import com.cs.wasselha.LanguageSelection;
import com.cs.wasselha.R;

import android.content.Context;
import android.content.Intent;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import com.cs.wasselha.RegistrationActivity;


public class ProfileTransporterFragment extends Fragment {

    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private static String apiURL="http://176.119.254.198:8000/wasselha";
    private ImageView mainImage, settingsImg, claimsImg, carsImg, statusImg, logoutImg, reservationImg,changeLanguage;
    private TextView name;

    private ProgressDialog progressDialog;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ProfileTransporterFragment() {
        // Required empty public constructor
    }

    public static ProfileTransporterFragment newInstance(String param1, String param2)
    {
        ProfileTransporterFragment fragment = new ProfileTransporterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        try {
            View view = inflater.inflate(R.layout.fragment_profile_transporter, container, false);
            setupReference(view);

            SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
            String id = preferences.getString(ID_KEY, null);
            int transporterID = Integer.parseInt(id.trim());

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }

            }, 1000);

            setAndGetName(getContext(), transporterID);
            getVehicleImageURLAndSetImage(getContext(), transporterID);

            //Calls
            logoutSetup();
            carInfoSetup();
            settingSetup();
            claimsImgSetup();
            statusImgSetup();
            reservationsSetup();

            return view;

        }catch (Exception e){
            Log.e("error:",e.toString());
            View view = inflater.inflate(R.layout.fragment_profile_transporter, container, false);
            return view;
        }
    }


    //-----------Methods---------------------------------------------------------------

    //References
    private void setupReference(View view)
    {
        mainImage = view.findViewById(R.id.mainPhotoInProfileTransporterPage);
        name = view.findViewById(R.id.mainNameTransporterProfilePage);
        settingsImg = view.findViewById(R.id.settingImageInTransporterProfile);
        claimsImg = view.findViewById(R.id.claimsImageTransporter);
        carsImg = view.findViewById(R.id.vehicleInfoImgInTransporterProfile);
        statusImg = view.findViewById(R.id.statusImage);
        logoutImg = view.findViewById(R.id.logoutTransporterImage);
        reservationImg = view.findViewById(R.id.reservationsImgInCTransporterProfile);
    }

    //-----------Logout Setup------------------------------------------------------
    private void logoutSetup()
    {
        logoutImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //delete info from shared preferences
                SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(ID_KEY);
                editor.remove(LOGIN_TYPE_KEY);
                editor.apply();
                Intent intent = new Intent(getContext(), RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void settingSetup()
    {
        settingsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TransporterSettingActivity.class);
                startActivity(intent);
            }
        });
    }


    private void reservationsSetup()
    {
        reservationImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TransporterReservationActivity.class);
                startActivity(intent);
            }
        });
    }


    private void carInfoSetup()
    {
        carsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), VehicleInformationActivity.class);
               startActivity(intent);
            }
        });
    }

    private void claimsImgSetup()
    {
        claimsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TransporterClaimsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void statusImgSetup()
    {
        statusImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TransporterTrackRoad.class);
                startActivity(intent);
            }
        });
    }


    public void setAndGetName(Context context, int transporterID) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = apiURL + "/transporters/" + transporterID + "/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String firstName = response.getString("first_name");
                            String lastName = response.getString("last_name");
                            String fullName = firstName + " " + lastName;

                            // Assuming that 'name' is a TextView instance you want to set the name to.
                            name.setText(fullName);

                        } catch (Exception e) {
                            Log.e("profile","Error:"+e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("profile","Error:"+error.toString());
                // Handle error here
                error.printStackTrace();
            }
        });

        queue.add(jsonObjectRequest);
    }

    public void getVehicleImageURLAndSetImage(Context context, int transporterID) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = apiURL + "/vehicles/?transporter="+transporterID;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Directly loop through the JSON Array response
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject vehicle = response.getJSONObject(i);
                                if (vehicle.getInt("transporter") == transporterID) {
                                    setMainImage(apiURL + vehicle.getString("vehicle_image"));
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
                Log.e("profile", "Error: " + error.toString());
                if (error.networkResponse != null) {
                    Log.e("profile", "Status code: " + error.networkResponse.statusCode);
                }
                Log.e("profile","failed loading image(Network Issue )");
            }
        });

        queue.add(jsonArrayRequest);
    }
    void setMainImage(String imageUrl){
        Glide.with(this)
                .load(imageUrl)
                .into(mainImage);
    }

}