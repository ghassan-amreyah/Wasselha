package com.cs.wasselha;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import com.cs.wasselha.CollectionPointProvider.MainCollectionPointProviderActivity;
import com.cs.wasselha.Customer.MainCustomerActivity;
import com.cs.wasselha.Transporter.MainTransporterActivity;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);
            getSupportActionBar().hide();

            SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
            // SharedPreferences.Editor editor = preferences.edit();
            String loginType = preferences.getString(LOGIN_TYPE_KEY, null);
            String id = preferences.getString(ID_KEY, null);
//            String lang = preferences.getString("Settings", null);

            String langPref = "my_lang";
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("Settings", Activity.MODE_PRIVATE);
            String lang = prefs.getString(langPref, null);
            if(lang!=null){
                Log.e("laaaanguage","language: "+lang);
                setLocale(lang);
            }
            if (loginType != null && id != null)
            {
                if (loginType.equals("customer"))
                {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run()
                        {
                            Intent intent = new Intent(SplashActivity.this, MainCustomerActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 1500);

                }
                else if (loginType.equals("transporter"))
                {
                    Handler handler = new Handler();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run()
                        {
                            Intent intent = new Intent(SplashActivity.this, MainTransporterActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 1500);

                }
                else if (loginType.equals("collectionpointprovider"))
                {
                    Handler handler = new Handler();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run()
                        {
                            Intent intent = new Intent(SplashActivity.this, MainCollectionPointProviderActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 1500);
                }
            }
            else
            {
                buildSplash();
            }
        }
         catch (Exception e)
        {
            Log.e("error:",e.toString());
        }


    }


    //Build Splash
    private void buildSplash() {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        res.updateConfiguration(conf, dm);
        getApplicationContext().getSharedPreferences("Settings", 0).edit().putString("my_lang", lang).apply();
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}