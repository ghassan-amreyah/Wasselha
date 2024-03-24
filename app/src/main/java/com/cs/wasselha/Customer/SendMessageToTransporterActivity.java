package com.cs.wasselha.Customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs.wasselha.R;
import com.cs.wasselha.interfaces.implementation.CustomerDA;
import com.cs.wasselha.interfaces.implementation.DeliveryServiceDetailsDA;
import com.cs.wasselha.interfaces.implementation.NotificationsDA;
import com.cs.wasselha.interfaces.implementation.ServiceDA;
import com.cs.wasselha.model.Customer;
import com.cs.wasselha.model.DeliveryServiceDetails;
import com.cs.wasselha.model.Notification;
import com.cs.wasselha.model.Service;
import com.cs.wasselha.model.Transporter;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

public class SendMessageToTransporterActivity extends AppCompatActivity {


    int delSerDetId;
    int transporterId;
    int customerId;
    EditText sendMessageToTransporterIfAnyProblemBox;
    Button sendMessageToTransporterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_to_trasnporter);
        getSupportActionBar().hide();


        Intent intent = getIntent();
        if (intent != null) {

            delSerDetId = Integer.parseInt(intent.getStringExtra("delSerDetId"));
            customerId = Integer.parseInt(intent.getStringExtra("customerId"));
            //writer_id = Integer.parseInt(intent.getStringExtra("writer_id"));
            try {
                getTheTransporterForThisDelServ();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        setRefs();
        btnSetup();



    }

    private void getTheTransporterForThisDelServ() throws IOException {

        DeliveryServiceDetailsDA da = new DeliveryServiceDetailsDA();
        DeliveryServiceDetails deliveryServiceDetails =  da.getDSD(delSerDetId);

        ServiceDA serviceDA = new ServiceDA();

         service = serviceDA.getService(deliveryServiceDetails.getService());
        transporterId=  service.getTransporter();
    }
    Service service;
    private void btnSetup() {
        sendMessageToTransporterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Customer customer;
                try {
                     customer = new CustomerDA().getCustomer(customerId);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Date nowDate = new Date();
                String dateTextNow = (nowDate.getYear()+1900) +"-"
                        +(nowDate.getMonth()) + "-" + (nowDate.getDate());
                String dateTimeStringNow = String.format(
                        "%sT%02d:%02d:00", // Format as "yyyy-MM-ddTHH:mm:ss"
                        dateTextNow,
                        nowDate.getHours(),
                        nowDate.getMinutes()
                );

                TimeZone timeZone = TimeZone.getDefault();
                long offsetInMillis = timeZone.getOffset(System.currentTimeMillis());
                int offsetHours = (int) (offsetInMillis / 3600000); // Convert to hours
                int offsetMinutes = (int) (offsetInMillis / 60000) % 60; // Convert to minutes
                String serviceDateNow= String.format(
                        "%s%+03d:%02d", // Format as "yyyy-MM-ddTHH:mm:ss+HH:mm"
                        dateTimeStringNow,
                        offsetHours,
                        offsetMinutes
                );
                Notification notifTrans = new Notification(transporterId,"transporter",
                        ("Msg from Customer = "+ customer.getFirst_name()  ),
                        ( sendMessageToTransporterIfAnyProblemBox.getText().toString()+ ", His Phone No. = " + customer.getPhone_number() ),
                        serviceDateNow);
                try {
                    new NotificationsDA().saveNotification(notifTrans);
                   // Toast.makeText(getApplicationContext(),
                    Toast.makeText(SendMessageToTransporterActivity.this, "The Msg was sent! Thanks!", Toast.LENGTH_LONG).show();
                    finish();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    private void setRefs() {
        sendMessageToTransporterIfAnyProblemBox = findViewById(R.id.sendMessageToTransporterIfAnyProblemBox);
        sendMessageToTransporterBtn = findViewById(R.id.sendMessageToTransporterBtn);

    }
}