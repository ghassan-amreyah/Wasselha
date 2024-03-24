package com.cs.wasselha.CollectionPointProvider;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cs.wasselha.R;
import com.cs.wasselha.interfaces.implementation.CollectionPointDA;
import com.cs.wasselha.interfaces.implementation.LocationDA;
import com.cs.wasselha.model.CollectionPoint;
import com.cs.wasselha.model.Location;

import java.io.IOException;

public class CollectionPointInformationActivity extends AppCompatActivity {

    private TextView collectionPointName;
    private Switch changeStatusSwitch;
    private TextView openTimeCollectionPoint;
    private TextView closeTimeCollectionPoint;
    private TextView locationCollectionPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_point_information);
        getSupportActionBar().hide();

        
        setupReferences();
        int id=getIntent().getIntExtra("id",-1);
        CollectionPointDA cA=new CollectionPointDA();
        if(id>0){
            try {
                CollectionPoint collectionPoint=cA.getCollectionP(id);
                collectionPointName.setText(collectionPoint.getName());
                openTimeCollectionPoint.setText(collectionPoint.getOpen_time());
                closeTimeCollectionPoint.setText(collectionPoint.getClose_time());
                changeStatusSwitch.setChecked(collectionPoint.getStatus().equals("open"));
                LocationDA lda=new LocationDA();
                Location l=lda.getLocation(collectionPoint.getLocation());
                locationCollectionPoint.setText(l.getTitle());
                changeStatusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // Handle switch status change
                        // If the switch is checked, we set the status to true, otherwise false.
                        // Assuming that there's a setStatus(boolean status) method in CollectionPoint class
                        collectionPoint.setStatus(isChecked?"open" : "closed");

                        try {
                            int id = 1; // Replace this with the ID of the collection point you want to update
                            cA.updateCollectionP(id, collectionPoint);
                            Toast.makeText(CollectionPointInformationActivity.this, getString(R.string.update_status)+"", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void setupReferences(){
        // Setup references
        collectionPointName = (TextView) findViewById(R.id.collectionPointNameInInformationPage);
        changeStatusSwitch = (Switch) findViewById(R.id.changeStatusSwitchInCollectionPointInformationPage);
        openTimeCollectionPoint = (TextView) findViewById(R.id.openTimeCollectionPointInInformationPage);
        closeTimeCollectionPoint = (TextView) findViewById(R.id.closeTimeCollectionPointInInformationPage);
        locationCollectionPoint = (TextView) findViewById(R.id.locationCollectionPointInInformationPage);

    }
}