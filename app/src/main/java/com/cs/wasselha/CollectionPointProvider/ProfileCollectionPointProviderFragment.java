package com.cs.wasselha.CollectionPointProvider;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cs.wasselha.R;
import com.cs.wasselha.RegistrationActivity;

import org.json.JSONObject;


public class ProfileCollectionPointProviderFragment extends Fragment {

    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";

    private ImageView settingCollectionPointProviderImg, ReviewsCollectionPointProviderImg, logoutCollectionPointProviderImg;
    private ProgressDialog progressDialog;
    String userType;
    int cppId;
    TextView mainNameCollectionPointProviderProfilePage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try
        {
            View view = inflater.inflate(R.layout.fragment_profile_collection_point_provider, container, false);
            setupReference(view);

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }

            }, 1000);


            getFromSharedPref();
            setAndGetName(getContext());
            //Calls
            logoutCollectionPointProviderSetup();
            settingCollectionPointProviderSetup();
            reviewsCollectionPointProviderSetup();

            return view;

        }
        catch (Exception e)
        {
            Log.e("error:",e.toString());
            View view = inflater.inflate(R.layout.fragment_profile_collection_point_provider, container, false);
            return view;
        }
    }
    private static String apiURL="http://176.119.254.198:8000/wasselha";

    public void setAndGetName(Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = apiURL + "/collection-point-providers/" + cppId  + "/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String firstName = response.getString("first_name");
                            String lastName = response.getString("last_name");
                            String fullName = firstName + " " + lastName;

                            // Assuming that 'name' is a TextView instance you want to set the name to.
                            mainNameCollectionPointProviderProfilePage.setText(fullName);

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

    //-----------Methods-------------------------------------------------------
    private void setupReference(View view)
    {
        settingCollectionPointProviderImg = view.findViewById(R.id.settingImageInCollectionPointProviderProfile);
        ReviewsCollectionPointProviderImg = view.findViewById(R.id.ReviewsImageInCollectionPointProviderProfile);
        logoutCollectionPointProviderImg = view.findViewById(R.id.logoutImageInCollectionPointProviderProfile);
        mainNameCollectionPointProviderProfilePage = view.findViewById(R.id.mainNameCollectionPointProviderProfilePage);
    }

    private void logoutCollectionPointProviderSetup()
    {
        logoutCollectionPointProviderImg.setOnClickListener(new View.OnClickListener() {
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

    private void settingCollectionPointProviderSetup()
    {
        settingCollectionPointProviderImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), CollectionPointProviderSettingActivity.class);
                startActivity(intent);
            }
        });
    }

    void getFromSharedPref(){
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        userType = preferences.getString(LOGIN_TYPE_KEY, null);
        cppId =Integer.parseInt( preferences.getString(ID_KEY,""));


    }

    private void reviewsCollectionPointProviderSetup()
    {
        ReviewsCollectionPointProviderImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), CollectionPointProviderReviewsActivity.class);
                startActivity(intent);
            }
        });
    }
}