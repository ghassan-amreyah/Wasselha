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
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cs.wasselha.Adapters.NotificationsCustomerAdapter;
import com.cs.wasselha.Adapters.ReservationsAdapter;
import com.cs.wasselha.Customer.Notifications;
import com.cs.wasselha.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationsTransporterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsTransporterFragment extends Fragment {

    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private static String apiURL="http://176.119.254.198:8000/wasselha";
    private ProgressDialog progressDialog;

    ListView notificationsListView;
    private ArrayList<Notifications> notificationsTransporterData;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static NotificationsTransporterFragment newInstance(String param1, String param2) {
        NotificationsTransporterFragment fragment = new NotificationsTransporterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        try {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_notifications_transporter, container, false);
            notificationsListView = view.findViewById(R.id.listViewTransporterNotifications);
            SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
            String id = preferences.getString(ID_KEY, null);
            int transporterID = Integer.parseInt(id.trim());

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
            populateNotificationsData(transporterID);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }

            }, 1000);

            return view;

        }catch (Exception e){
            Log.e("error:",e.toString());
            View view = inflater.inflate(R.layout.fragment_notifications_transporter, container, false);
            return view;
        }
    }
    private void populateNotificationsData(int transporterID)
    {
        notificationsTransporterData = new ArrayList<>();
        getNotifications(getContext(),transporterID);
//        notificationsCustomerData.add(new Notifications(R.drawable.notification, "Notification", "Reservation has been accepted", "11:30 PM"));
//        notificationsCustomerData.add(new Notifications(R.drawable.notification, "Notification", "Reservation has not been accepted", "10:30 PM"));
//        notificationsCustomerData.add(new Notifications(R.drawable.notification, "Notification", "Reservation has been accepted", "9:00 PM"));
    }
    public void getNotifications(Context context, int transporterID) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = apiURL + "/notifications/?user_type=transporter&user_id="+ transporterID ;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Directly loop through the JSON Array response
                            // Convert JSONArray to a list of JSONObjects
                            List<JSONObject> responseList = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject notification = response.getJSONObject(i);
                                responseList.add(notification);
                            }

                            // Sort the list of JSONObjects by date
                            Collections.sort(responseList, new Comparator<JSONObject>() {
                                @Override
                                public int compare(JSONObject obj1, JSONObject obj2) {
                                    try {
                                        String date1 = obj1.getString("date");
                                        String date2 = obj2.getString("date");
                                        return date2.compareTo(date1); // Sort in descending order
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    return 0;
                                }
                            });
                            for (int i = 0; i < responseList.size(); i++) {
                                int id=responseList.get(i).getInt("id");
                                int user_id=responseList.get(i).getInt("user_id");
                                String user_type=responseList.get(i).getString("user_type");
                                String title=responseList.get(i).getString("title");
                                String description=responseList.get(i).getString("description");
                                String Datetime=responseList.get(i).getString("date");
                                DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                                LocalDateTime notificationDateTime = LocalDateTime.parse(Datetime, formatter);
                                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                                String date = notificationDateTime.format(dateFormatter);
                                String time = notificationDateTime.format(timeFormatter);
                                notificationsTransporterData.add(new Notifications(id,user_id,title,description,time,date,user_type));

                            }
                            try{
                                NotificationsCustomerAdapter notificationsCustomerAdapter = new NotificationsCustomerAdapter(requireContext(), R.layout.notifications_customer_list_view, notificationsTransporterData);
                                notificationsListView.setAdapter(notificationsCustomerAdapter);
                                progressDialog.dismiss();
                            }catch (Exception e) {
                                Log.e("error:", e.toString());
                            }

                        } catch (Exception e) {
                            Log.e("notification","notification not found");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("notification", "Error: " + error.toString());
                if (error.networkResponse != null) {
                    Log.e("notification", "Status code: " + error.networkResponse.statusCode);
                }
            }
        });

        queue.add(jsonArrayRequest);
    }
}