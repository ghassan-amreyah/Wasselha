package com.cs.wasselha.Transporter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;


import com.cs.wasselha.LanguageSelection;
import com.cs.wasselha.R;


public class TransporterSettingActivity extends AppCompatActivity
{

    private Button updateTransporterInfoBtn, changeTransporterLanguageBtn;

    protected void onCreate(Bundle savedInstanceState)
    {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transporter_setting);
            getSupportActionBar().hide();

            //Calls
            setUpRefernces();
            updateInfoSetup();
            changeLanguageBtnSetup();

    }

    //------------------------Methods---------------------------------------------
    private void setUpRefernces()
    {
        updateTransporterInfoBtn = findViewById(R.id.updateTransporterInformationBtnInSettingPage);
        changeTransporterLanguageBtn = findViewById(R.id.changeLanguageBtnInTransporterSettingPage);
    }

    private void updateInfoSetup()
    {
        updateTransporterInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(TransporterSettingActivity.this, UpdateTransporterInformationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void changeLanguageBtnSetup()
    {
        changeTransporterLanguageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(TransporterSettingActivity.this, LanguageSelection.class);
                startActivity(intent);
            }
        });
    }


}
