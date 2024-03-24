package com.cs.wasselha.Customer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cs.wasselha.LanguageSelection;
import com.cs.wasselha.R;
import com.cs.wasselha.Transporter.TransporterSettingActivity;
import com.cs.wasselha.Transporter.UpdateTransporterInformationActivity;

public class CustomerSettingActivity extends AppCompatActivity
{
    private Button updateBtn, changeLanguageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_setting);
        getSupportActionBar().hide();

        //Calls
        setUpRefernces();
        updateInfoSetup();
        changeLanguageBtnSetup();

    }



    //------------------------Methods---------------------------------------------
    private void setUpRefernces()
    {
        updateBtn = findViewById(R.id.updateCustomerInformationBtnInSettingPage);
        changeLanguageBtn = findViewById(R.id.changeLanguageBtnInCustomerSettingPage);
    }

    private void updateInfoSetup()
    {
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(CustomerSettingActivity.this, UpdateCustomerInformationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void changeLanguageBtnSetup()
    {
        changeLanguageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(CustomerSettingActivity.this, LanguageSelection.class);
                startActivity(intent);
            }
        });
    }
}