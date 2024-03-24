package com.cs.wasselha.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cs.wasselha.R;
import com.cs.wasselha.Signup.TypeSignupActivity;

public class TypeLoginActivity extends AppCompatActivity {
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";

    Button loginAsCustomerBtn, loginAsTransporterBtn, loginAsCollectionPointProviderBtn;
    TextView signupQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_login);
        getSupportActionBar().hide();

        //References
        setupReference();
        loginAsCustomerBtnSetup();
        loginAsTransporterBtnSetup();
        loginAsCollectionPointProviderBtnSetup();
        signupQuestionBtnSetup();

    }



    //--------------Methods---------------------------------------------------------

    //References
    private void setupReference()
    {
        loginAsCustomerBtn = findViewById(R.id.loginAsCustomerBtn);
        loginAsTransporterBtn = findViewById(R.id.loginAsTransporterBtn);
        loginAsCollectionPointProviderBtn = findViewById(R.id.loginAsCollectionPointProviderBtn);
        signupQuestion = findViewById(R.id.signupQuestionBtnInTypeLoginPage);
    }


    private void loginAsCustomerBtnSetup() {
        loginAsCustomerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(LOGIN_TYPE_KEY, "customer");
                editor.apply();
                Intent intent = new Intent(TypeLoginActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    private void loginAsTransporterBtnSetup() {
        loginAsTransporterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(LOGIN_TYPE_KEY, "transporter");
                editor.apply();
                Intent intent = new Intent(TypeLoginActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    private void loginAsCollectionPointProviderBtnSetup() {
        loginAsCollectionPointProviderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(LOGIN_TYPE_KEY, "collectionpointprovider");
                editor.apply();
                Intent intent = new Intent(TypeLoginActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    private void signupQuestionBtnSetup()
    {
        signupQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TypeLoginActivity.this, TypeSignupActivity.class);
                startActivity(intent);
            }
        });
    }

}