package com.cs.wasselha.Customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.cs.wasselha.Adapters.DeliveryStatusAdapter;
import com.cs.wasselha.R;
import com.cs.wasselha.interfaces.implementation.DeliveryStatusDA;
import com.cs.wasselha.model.DeliveryStatus;

import java.io.IOException;
import java.util.ArrayList;

public class DeliveryStatusActivity extends AppCompatActivity {


    ListView detailsListView;
    private ArrayList<DeliveryStatus> delivaryDetailsReservationsForCustomerData;


    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";

    private Button addReviewBtn, sendMessageBtn;

    private int customerId;
    private String userType;
    private int delSerDetId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_status);
        getSupportActionBar().hide();


        Intent intent = getIntent();
        if (intent != null)
            delSerDetId = Integer.parseInt(intent.getStringExtra("delSerDetId"));
        //Calls
        setupRefernces();

        sendMessageBtnSetup();



        addReviewBtnSetup();
        getFromSharedPref();

        try
        {
            populateNotificationsData();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }


    }


    //-------------Methods---------------------------------------------------------
    private void setupRefernces()
    {
        detailsListView = findViewById(R.id.deliveryStatusCustomerListView);
        addReviewBtn = findViewById(R.id.addReviewBtnInCustomerDeliveryStatusPage);
        sendMessageBtn = findViewById(R.id.sendMessageToTransporterInDeliveryStatusPageBtn);
    }

    private void addReviewBtnSetup()
    {
        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intentReview = new Intent(DeliveryStatusActivity.this, AddReviewForATransporterActivity.class);
                intentReview.putExtra("user_id",delivaryDetailsReservationsForCustomerData+"");
                intentReview.putExtra("writer_id",customerId+"");
                intentReview.putExtra("delSerDetId",delSerDetId+"");




                startActivity(intentReview);
            }
        });
    }

    private void sendMessageBtnSetup()
    {
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intentMsg = new Intent(DeliveryStatusActivity.this, SendMessageToTransporterActivity.class);

                intentMsg.putExtra("delSerDetId",delSerDetId+"");
                intentMsg.putExtra("customerId",customerId+"");

                startActivity(intentMsg);
            }
        });
    }

    void getFromSharedPref() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        userType = preferences.getString(LOGIN_TYPE_KEY, null);
        customerId = Integer.parseInt(preferences.getString(ID_KEY, ""));


    }

    private void populateNotificationsData() throws IOException {


        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    delivaryDetailsReservationsForCustomerData = new DeliveryStatusDA().getDelStatuses(delSerDetId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                DeliveryStatusAdapter deliveryStatusAdapter =
                        new DeliveryStatusAdapter(DeliveryStatusActivity.this, R.layout.delivery_status_customer_list_view, delivaryDetailsReservationsForCustomerData);
                detailsListView.setAdapter(deliveryStatusAdapter);
            }
        });

    }

}