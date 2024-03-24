package com.cs.wasselha.Transporter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs.wasselha.R;
import com.cs.wasselha.interfaces.implementation.ClaimsDA;
import com.cs.wasselha.model.Claim;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

public class AddReviewForCustomerActivity extends AppCompatActivity
{
    EditText customerReview, notes;
    Button addReviewBtn;
    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private int writer_id;
    private int writenToId;
    private int delSerDetId;
    private String writenToType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review_for_customer);
        getSupportActionBar().hide();
        Intent intent = getIntent();
         delSerDetId = intent.getIntExtra("deliveryservicedetails", 0);
        writenToId = intent.getIntExtra("writenToId", 0);
        writenToType=intent.getStringExtra("writenToType");
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String id = preferences.getString(ID_KEY, null);
        int transporterID=Integer.parseInt(id.trim());
        writer_id=transporterID;
        //calls
        setupRefernces();
        btnSetup();
    }



    //---------------Methods----------------------------------------------------
    private void setupRefernces()
    {
        customerReview = findViewById(R.id.reviewAboutCustomerEditText);
        notes = findViewById(R.id.noteAboutCustomer);
        addReviewBtn = findViewById(R.id.addReviewAboutCustomerBtn);
    }


    private void btnSetup() {
        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String msg = notes.getText().toString();
                        String review  = customerReview.getText().toString();
                        if (msg.equals("") || review.equals("")){

                            Toast.makeText(AddReviewForCustomerActivity.this, "Please add input, Thanks!", Toast.LENGTH_LONG).show();
                            return;
                        }


                        Date nowDate = new Date();
                        String dateTextNow = (nowDate.getYear()+1900) +"-"
                                +(nowDate.getMonth()+1) + "-" + (nowDate.getDate());
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


                        Claim claim = new Claim(0, delSerDetId, writer_id, writenToId,
                                "transporter",writenToType,msg,Integer.parseInt(review),serviceDateNow);

                        try {
                            new ClaimsDA().saveClaim(claim);
                            Toast.makeText(AddReviewForCustomerActivity.this, "Review added, Thanks!", Toast.LENGTH_LONG).show();
                            finish();
                        } catch (IOException e) {
                            Toast.makeText(AddReviewForCustomerActivity.this, "Please, try again!", Toast.LENGTH_LONG).show();

                            throw new RuntimeException(e);
                        }

                    }
                });
            }
        });
    }

}