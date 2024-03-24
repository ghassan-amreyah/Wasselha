package com.cs.wasselha.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.cs.wasselha.Login.LoginActivity;
import com.cs.wasselha.R;
import com.cs.wasselha.Transporter.HomeTransporterFragment;
import com.cs.wasselha.Transporter.Services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ServicesAdapter extends ArrayAdapter<Services>
{
    private static String apiURL="http://176.119.254.198:8000/wasselha";
    private Context mContext;
    private int mResource;

    public ServicesAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Services> objects)
    {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater layoutInFlater = LayoutInflater.from(mContext);
        convertView = layoutInFlater.inflate(mResource, parent, false);

        ImageView imageView = convertView.findViewById(R.id.imageInCustomerNotificationsListView);
        TextView sourceCity = convertView.findViewById(R.id.sourceCityInListViewMainPageTransporter);
        TextView destinationCity = convertView.findViewById(R.id.destinationCityInListViewMainPageTransporter);
        TextView time = convertView.findViewById(R.id.timeOfServiceInListViewMainPageTransporter);
        TextView delete = convertView.findViewById(R.id.textView9);
        getVehicleImageURLAndSetImage(mContext,getItem(position).getTransporterId(),imageView);

        getLocationTitleById(getItem(position).getSourceCityId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String title) {
                // Do something with the location title
                sourceCity.setText(title);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Log.e("Error", "Failed to get location title");
            }
        });
        getLocationTitleById(getItem(position).getDestinationCityId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String title) {
                // Do something with the location title
                destinationCity.setText(title);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Log.e("Error", "Failed to get location title");
            }
        });
        time.setText(getItem(position).getTime());
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                sendDeleteRequest(apiURL+"/services/"+getItem(position).getId()+"/");
                HomeTransporterFragment.servicesData.remove(position);
                // Notify the adapter that the data has changed
                notifyDataSetChanged();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //intent to transfare it to transporter service details, with put extran info of service data(id)
            }
        });

        return convertView;
    }
    private void sendDeleteRequest(String url) {
        new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                String url = params[0];
                try {
                    URL deleteUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) deleteUrl.openConnection();
                    connection.setRequestMethod("DELETE");
                    connection.setDoOutput(true);

                    int responseCode = connection.getResponseCode();
                    // Handle the response code as per your requirement
                    connection.disconnect();
                    return responseCode == HttpURLConnection.HTTP_OK;
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle any exceptions that occur during the request
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                // Handle the result after the background operation completes
                if (success) {
                    // Delete request was successful
                } else {
                    // Delete request failed
                }
            }
        }.execute(url);
    }

    public void getVehicleImageURLAndSetImage(Context context, int transporterID,ImageView imageView) {
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
                                    setImage(apiURL + vehicle.getString("vehicle_image"),imageView);
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
    void setImage(String imageUrl,ImageView imageView){
        Glide.with(mContext)
                .load(imageUrl)
                .into(imageView);
    }
    public void getLocationTitleById(int locationId, final Response.Listener<String> onSuccess, final Response.ErrorListener onError) {
        String baseUrl = "http://176.119.254.198:8000/wasselha/locations/";
        String url = baseUrl + locationId + "/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String title = response.getString("title");
                            onSuccess.onResponse(title);
                        } catch (JSONException e) {
                            onError.onErrorResponse(new VolleyError("JSON parsing error"));
                        }
                    }
                },
                onError
        );

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(mContext).add(jsonObjectRequest);
    }


}
