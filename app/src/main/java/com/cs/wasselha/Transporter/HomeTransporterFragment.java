package com.cs.wasselha.Transporter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.cs.wasselha.Adapters.ReservationsAdapter;
import com.cs.wasselha.R;
import com.cs.wasselha.Adapters.ServicesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;


public class HomeTransporterFragment extends Fragment {

    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private ProgressDialog progressDialog;
    ListView listView;
    public static ArrayList<Services> servicesData;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        try {
        View view = inflater.inflate(R.layout.fragment_home_transporter, container, false);
        listView = view.findViewById(R.id.listViewInMainPageTransporter);
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String id = preferences.getString(ID_KEY, null);
        int transporterID=Integer.parseInt(id.trim());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        populateServicesData(transporterID);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }

            }, 1000);

        return view;
        }
        catch (Exception e)
        {
        Log.e("error:",e.toString());
        View view = inflater.inflate(R.layout.fragment_home_transporter, container, false);
        return view;
    }
    }

    private void populateServicesData(int transporterID)
    {
        servicesData = new ArrayList<>();

        String url = "http://176.119.254.198:8000/wasselha/services/?transporter="+transporterID+ "&time=upper";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject service = response.getJSONObject(i);

                                String serviceDate = service.getString("service_date");
                                DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                                LocalDateTime serviceDateTime = LocalDateTime.parse(serviceDate, formatter);

                                if (serviceDateTime.isAfter(LocalDateTime.now())) {
                                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                                    String date = serviceDateTime.format(dateFormatter);
                                    String time = serviceDateTime.format(timeFormatter);

                                    int id = service.getInt("id");
                                    int transporterId = service.getInt("transporter");
                                    int sourcePlace = service.getInt("source_place");
                                    int destinationPlace = service.getInt("destination_place");
                                    double price = Double.parseDouble(service.getString("price"));

                                    servicesData.add(new Services(id, transporterId, sourcePlace, destinationPlace, date, time, price));
                                }
                            }
                            try {
                                ServicesAdapter servicesAdapter = new ServicesAdapter(requireContext(), R.layout.home_page_transporter_list_view, servicesData);
                                listView.setAdapter(servicesAdapter);
                                progressDialog.dismiss();
                            }catch (Exception e) {
                                Log.e("error:", e.toString());
                            }
                        } catch (JSONException | DateTimeParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                }
        );

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(requireContext()).add(jsonArrayRequest);
    }
}



//        servicesData.add(new Services(R.drawable.car1, "Ramallah", "Nablus", "9:00 AM"));
//        servicesData.add(new Services(R.drawable.car1, "Nablus", "Jenin", "9:30 AM"));
//        servicesData.add(new Services(R.drawable.car1, "Birzeit", "Ramallah", "10:00 AM"));
//        servicesData.add(new Services(R.drawable.car1, "Jenin", "Nablus", "10:30 AM"));