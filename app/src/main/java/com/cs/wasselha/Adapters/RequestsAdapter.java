package com.cs.wasselha.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cs.wasselha.Claims.Claims;
import com.cs.wasselha.R;
import com.cs.wasselha.Transporter.Requests;
import com.cs.wasselha.Transporter.RequestsTransporterFragment;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestsAdapter extends ArrayAdapter<Requests>
{
    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private static String BASE_URL="http://176.119.254.198:8000/wasselha";
    private Context context;
    private int cResource;
    ArrayList<Requests> requestsData ;

    public RequestsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Requests> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.cResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater layoutInFlater = LayoutInflater.from(context);
        convertView = layoutInFlater.inflate(cResource, parent, false);

        TextView customerName = convertView.findViewById(R.id.customerNameInRequestsListview);
        TextView customerReview = convertView.findViewById(R.id.customerReviewInRequestsListview);
        TextView sourceCity = convertView.findViewById(R.id.sourceCityInRequestsListView);
        TextView destinationCity = convertView.findViewById(R.id.destinationCityInInRequestsListView);
        TextView price = convertView.findViewById(R.id.priceInRequestsListview);
        TextView time = convertView.findViewById(R.id.timeOfServiceInListViewMainPageTransporter);
        TextView reject = convertView.findViewById(R.id.rejectRequestBtnInRequestsListView);
        TextView agree = convertView.findViewById(R.id.agreeRequestBtnInRequestsListView);

        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String id = preferences.getString(ID_KEY, null);
        int transporterID=Integer.parseInt(id.trim());
        customerName.setText(getItem(position).getCustomerName());
        customerReview.setText(getItem(position).getCustomerReview());
        sourceCity.setText(getItem(position).getSourceCity());
        destinationCity.setText(getItem(position).getDestinationCity());
        price.setText(getItem(position).getPrice());
        time.setText(getItem(position).getTime());


        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                getCurrentDetailsAndSendPutRequest(getItem(position).getId(),false,transporterID);
                RequestsTransporterFragment.requestsData.remove(position);
                notifyDataSetChanged();
            }
        });
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                getCurrentDetailsAndSendPutRequest(getItem(position).getId(),true,transporterID);
                RequestsTransporterFragment.requestsData.remove(position);
                notifyDataSetChanged();
                //intent to transfare it to transporter service details, with put extran info of service data(id)
            }
        });

        return convertView;
    }

    private void sendAgreePutRequest(int id, JSONObject currentDetails) {
        OkHttpClient client = new OkHttpClient();

        String url = BASE_URL + "/delivery-service-details/" + id + "/";

        try {
            currentDetails.put("accepted", true);
            currentDetails.put("responsed", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(currentDetails.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
            }
        });
    }

    private void sendRejectPutRequest(int id, JSONObject currentDetails) {
        OkHttpClient client = new OkHttpClient();

        String url = BASE_URL + "/delivery-service-details/" + id + "/";

        try {
            currentDetails.put("accepted", false);
            currentDetails.put("responsed", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(currentDetails.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
            }
        });
    }


    private void getCurrentDetailsAndSendPutRequest(int id, boolean isAgree, int transporterID) {
        OkHttpClient client = new OkHttpClient();

        String url = BASE_URL + "/delivery-service-details/" + id + "/";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String jsonData = response.body().string();
                try {
                    JSONObject currentDetails = new JSONObject(jsonData);

                    // Get transporter details
                    String transporterUrl = BASE_URL + "/transporters/" + transporterID + "/";
                    Request transporterRequest = new Request.Builder()
                            .url(transporterUrl)
                            .get()
                            .build();

                    client.newCall(transporterRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                throw new IOException("Unexpected code " + response);
                            }

                            String transporterData = response.body().string();
                            try {
                                JSONObject transporterDetails = new JSONObject(transporterData);
                                String transporterName = transporterDetails.getString("first_name") + " " +
                                        transporterDetails.getString("last_name");

                                // Send Notification
                                String notificationUrl = BASE_URL + "/notifications/";

                                JSONObject notificationJson = new JSONObject();
                                notificationJson.put("user_id", currentDetails.getInt("customer"));
                                notificationJson.put("user_type", "customer");
                                notificationJson.put("title", isAgree ? "request accepted" : "request rejected");
                                notificationJson.put("description", transporterName +
                                        (isAgree ? " accept your delivery request" : " reject your delivery request"));
                                notificationJson.put("date", java.time.LocalDateTime.now().toString());

                                RequestBody notificationBody = RequestBody.create(notificationJson.toString(),
                                        MediaType.parse("application/json"));

                                Request notificationRequest = new Request.Builder()
                                        .url(notificationUrl)
                                        .post(notificationBody)
                                        .build();

                                client.newCall(notificationRequest).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if (!response.isSuccessful()) {
                                            throw new IOException("Unexpected code " + response);
                                        }
                                        // Notification sent successfully.
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    if (isAgree) {
                        sendAgreePutRequest(id, currentDetails);
                    } else {
                        sendRejectPutRequest(id, currentDetails);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


//    private void getCurrentDetailsAndSendPutRequest(int id, boolean isAgree) {
//        OkHttpClient client = new OkHttpClient();
//
//        String url = BASE_URL + "/delivery-service-details/" + id + "/";
//
//        Request request = new Request.Builder()
//                .url(url)
//                .get()
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (!response.isSuccessful()) {
//                    throw new IOException("Unexpected code " + response);
//                }
//
//                String jsonData = response.body().string();
//                try {
//                    JSONObject currentDetails = new JSONObject(jsonData);
//
//                    if (isAgree) {
//                        sendAgreePutRequest(id, currentDetails);
//                    } else {
//                        sendRejectPutRequest(id, currentDetails);
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

}
