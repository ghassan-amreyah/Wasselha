package com.cs.wasselha.CollectionPointProvider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cs.wasselha.LanguageSelection;
import com.cs.wasselha.R;
import com.cs.wasselha.Transporter.TransporterSettingActivity;
import com.cs.wasselha.Transporter.UpdateTransporterInformationActivity;

public class CollectionPointProviderSettingActivity extends AppCompatActivity
{
    private Button updateCollectionPointProviderInfoBtn, changeCollectionPointProviderLanguageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_point_provider_setting);
        getSupportActionBar().hide();

        //Calls
        setUpRefernces();
        updateInfoSetup();
        changeLanguageBtnSetup();

    }

    //------------------------Methods---------------------------------------------
    private void setUpRefernces()
    {
        updateCollectionPointProviderInfoBtn = findViewById(R.id.updateCollectionPointProviderInformationBtnInSettingPage);
        changeCollectionPointProviderLanguageBtn = findViewById(R.id.changeLanguageBtnInCollectionPointProviderSettingPage);
    }

    private void updateInfoSetup()
    {
        updateCollectionPointProviderInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(CollectionPointProviderSettingActivity.this, UpdateCollectionPointProviderInformationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void changeLanguageBtnSetup()
    {
        changeCollectionPointProviderLanguageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(CollectionPointProviderSettingActivity.this, LanguageSelection.class);
                startActivity(intent);
            }
        });
    }
}