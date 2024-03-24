package com.cs.wasselha.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cs.wasselha.CollectionPointProvider.MainCollectionPointProviderActivity;
import com.cs.wasselha.Customer.MainCustomerActivity;
import com.cs.wasselha.R;
import com.cs.wasselha.Signup.TypeSignupActivity;
import com.cs.wasselha.Transporter.MainTransporterActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    // Flag to indicate if the user is logged in
    private boolean isUserLoggedIn = false;
    private TextView signupQuestionBtn, errorMessage;
    private EditText email, password;
    private Button loginBtn;
    RequestQueue requestQueue;
    //boolean isUserLoggedIn;

    String loginBaseURL = "http://176.119.254.198:8000/wasselha/login/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String loginType = preferences.getString(LOGIN_TYPE_KEY, null);
        if(loginType !=null){
            loginBaseURL+=loginType+"/";
        }else{
            loginBaseURL+="customer/";
        }
        //References
        setupReference();
        loginSetup();
        signupQuestionBtnSetup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Only delete LOGIN_TYPE_KEY if the user is not logged in
        if (!isUserLoggedIn) {
            SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
            if (preferences.contains(LOGIN_TYPE_KEY)) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(LOGIN_TYPE_KEY);
                editor.apply();
            }
        }
    }




    //--------------Methods---------------------------------------------------------

    //References
    private void setupReference()
    {
        signupQuestionBtn = findViewById(R.id.signupQuestionBtn);
        loginBtn = findViewById(R.id.loginBtnInLoginPage);
        email = findViewById(R.id.emailInLoginPage);
        password = findViewById(R.id.passwordInLoginPage);
        errorMessage = findViewById(R.id.errorMessage);

    }

    private void loginSetup() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (email.length() > 0 && password.length() > 0)
                {
                    LoginUser();
                }
                else
                {
                    Toast.makeText(LoginActivity.this, getString(R.string.fill_fields)+"", Toast.LENGTH_SHORT).show();
                    errorMessage.setText(getString(R.string.fill_fields));
                }
            }
        });
    }



    private void LoginUser()
    {
                requestQueue = Volley.newRequestQueue(this);
                final String value = "";

                JSONObject jsonObject = new JSONObject();

                try
                {
                    Log.d("ressss",email.getText().toString());
                    Log.d("ressss",password.getText().toString());
                    jsonObject.put("email", email.getText().toString());
                    jsonObject.put("password", password.getText().toString());
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                // Create the POST request
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, loginBaseURL, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                try
                                {
                                    int id = response.getInt("id");

                                    if (id > 0)
                                    {
                                        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString(ID_KEY, id+"");
                                        editor.apply();
                                        String loginType = preferences.getString(LOGIN_TYPE_KEY, null);

                                        if(loginType !=null)
                                        {
                                            isUserLoggedIn = true;

                                            if(loginType.equals("customer"))
                                            {
                                                Intent intent = new Intent(LoginActivity.this, MainCustomerActivity.class);
                                                startActivity(intent);
                                                finish();
                                                email.setText("");
                                                password.setText("");
                                                errorMessage.setText("");
                                            }
                                            else if(loginType.equals("transporter"))
                                            {
                                                Intent intent = new Intent(LoginActivity.this, MainTransporterActivity.class);
                                                startActivity(intent);
                                                finish();
                                                email.setText("");
                                                password.setText("");
                                                errorMessage.setText("");
                                            }
                                            else if(loginType.equals("collectionpointprovider"))
                                            {
                                                Intent intent = new Intent(LoginActivity.this, MainCollectionPointProviderActivity.class);
                                                startActivity(intent);
                                                finish();
                                                email.setText("");
                                                password.setText("");
                                                errorMessage.setText("");
                                            }
                                        }
                                        Toast.makeText(LoginActivity.this, getString(R.string.done_login)+"", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        errorMessage.setText(getString(R.string.not_correct));
                                        Toast.makeText(LoginActivity.this, getString(R.string.not_correct)+"", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                errorMessage.setText(getString(R.string.not_correct));
                                Toast.makeText(LoginActivity.this, getString(R.string.not_correct)+"", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };

                // Add the request to the RequestQueue
                requestQueue.add(jsonObjectRequest);
    }


    private void signupQuestionBtnSetup()
    {
        signupQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(LoginActivity.this, TypeSignupActivity.class);
                startActivity(intent);
            }
        });
    }
}