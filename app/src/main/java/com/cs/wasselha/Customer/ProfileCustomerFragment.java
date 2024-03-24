package com.cs.wasselha.Customer;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.cs.wasselha.SplashActivity;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileCustomerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileCustomerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private static String apiURL="http://176.119.254.198:8000/wasselha";
    private ImageView settingsImg, claimsImg, logoutImg;
    private TextView name;

    private int customerId;
    private String userType;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ProgressDialog progressDialog;

    public ProfileCustomerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileCustomerFragment newInstance(String param1, String param2) {
        ProfileCustomerFragment fragment = new ProfileCustomerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    void getFromSharedPref(){
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        userType = preferences.getString(LOGIN_TYPE_KEY, null);
        customerId =Integer.parseInt( preferences.getString(ID_KEY,""));


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try
        {
             // Inflate the layout for this fragment
             View view = inflater.inflate(R.layout.fragment_profile_customer, container, false);

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

            }, 500);




        getFromSharedPref();
        //SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
       // String id = preferences.getString(ID_KEY, null);
        //int transporterID=Integer.parseInt(id.trim());
        setAndGetName(getContext());
        //getVehicleImageURLAndSetImage(getContext(),transporterID);

        //Calls
        logoutSetup();
        claimsImgSetup();
        settingBtnSetup();

            return view;
        }
        catch (Exception e)
        {
            Log.e("error:",e.toString());
            View view = inflater.inflate(R.layout.fragment_profile_customer, container, false);
            return view;
        }

        //return inflater.inflate(R.layout.fragment_profile_customer, container, false);
    }

    private void claimsImgSetup()
    {
        claimsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CustomerClaimsActivity.class);
                intent.putExtra("customerId",String.valueOf(customerId));

                startActivity(intent);
            }
        });
    }
    private void setupReference(View view)
    {
        name = view.findViewById(R.id.mainNameCustomerInProfilePage);
        settingsImg = view.findViewById(R.id.settingImageInCustomerProfile);
        claimsImg = view.findViewById(R.id.ReviewsImageInCustomerProfile);
        logoutImg = view.findViewById(R.id.logoutImageCustomer);
    }

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
                Intent intent = new Intent(getContext(), SplashActivity.class);
                getActivity().finish();
                startActivity(intent);

            }
        });
    }
    public void setAndGetName(Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = apiURL + "/customers/" + customerId + "/";

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

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainCustomerLayout,fragment);
        fragmentTransaction.commit();

    }
    private void settingBtnSetup()
    {
        settingsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CustomerSettingActivity.class);
                startActivity(intent);
            }
        });
    }

}