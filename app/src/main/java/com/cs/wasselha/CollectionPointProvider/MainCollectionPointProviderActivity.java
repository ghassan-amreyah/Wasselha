package com.cs.wasselha.CollectionPointProvider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.cs.wasselha.CollectionPointProvider.AddCollectionPointFragment;
import com.cs.wasselha.CollectionPointProvider.HomeCollectionPointProviderFragment;
import com.cs.wasselha.CollectionPointProvider.NotificationsCollectionPointProviderFragment;
import com.cs.wasselha.CollectionPointProvider.ProfileCollectionPointProviderFragment;
import com.cs.wasselha.R;
import com.cs.wasselha.databinding.ActivityMainCollectionPointProviderBinding;

public class MainCollectionPointProviderActivity extends AppCompatActivity {

    ActivityMainCollectionPointProviderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityMainCollectionPointProviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeCollectionPointProviderFragment());


        binding.bottomBarInCollectionPointProviderMainPage.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.nav_home_collection_point_provider:
                    replaceFragment(new HomeCollectionPointProviderFragment());
                    break;

                case R.id.nav_add_collection_point:
                    replaceFragment(new AddCollectionPointFragment());
                    break;

                case R.id.nav_notifications_collection_point_provider:
                    replaceFragment(new NotificationsCollectionPointProviderFragment());
                    break;

                case R.id.nav_profile_collection_point_provider:
                    replaceFragment(new ProfileCollectionPointProviderFragment());
                    break;
            }

            return true;
        });

        //References
        setupReference();
    }

    //References
    private void setupReference()
    {

    }

//    private void replaceFragment(Fragment fragment)
//    {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.mainCollectionPointProviderLayout,fragment);
//        fragmentTransaction.commit();
//    }
    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainCollectionPointProviderLayout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}