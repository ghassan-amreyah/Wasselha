package com.cs.wasselha.CollectionPointProvider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cs.wasselha.Customer.CustomerSignupActivity;
import com.cs.wasselha.Login.TypeLoginActivity;
import com.cs.wasselha.R;
import com.cs.wasselha.Transporter.TransporterSignupActivity;
import com.cs.wasselha.interfaces.implementation.CollectionPPDA;
import com.cs.wasselha.model.CollectionPointProvider;

import java.io.IOException;

public class CollectionPointProviderSignupActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText editTextFName;
    private EditText editTextLName;
    private EditText editTextNId;
    private EditText editTextPhoneN;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordRe;
    private TextView errorMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_point_provider_signup);
        getSupportActionBar().hide();


        //Calls
        setUpViews();
        addCollectionPointProvider();

    }


    //--------------Methods------------------------------------------------------

    private void setUpViews() {

        registerButton = findViewById(R.id.signupCollectionPointProviderBtn);
        editTextFName = findViewById(R.id.firstNameCollectionPointProvider);
        editTextLName = findViewById(R.id.lastNameCollectionPointProvider);
        editTextNId = findViewById(R.id.nationalIDCollectionPointProviderSignup);
        editTextEmail = findViewById(R.id.emailCollectionPointProviderSignup);
        editTextPhoneN = findViewById(R.id.phoneNumberCollectionPointProviderSignup);
        editTextPassword = findViewById(R.id.passwordCollectionPointProviderSignup);
        editTextPasswordRe = findViewById(R.id.repeatPasswordCollectionPointProviderSignup);
        errorMessage = findViewById(R.id.errorMessageInCollectionPointProviderSignup);
    }




    private void addCollectionPointProvider()
    {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CollectionPointProvider cpp = new CollectionPointProvider(
                        editTextFName.getText().toString(),
                        editTextLName.getText().toString(),
                        editTextEmail.getText().toString(),
                        editTextPassword.getText().toString(),
                        editTextPhoneN.getText().toString(),
                        editTextNId.getText().toString(),false);


                if (editTextEmail.length() > 0 && editTextFName.length() > 0 && editTextLName.length() > 0 &&  editTextPassword.length() > 0 && editTextPhoneN.length() > 0 && editTextNId.length() > 0 && editTextPasswordRe.length() > 0)
                {
                    if (editTextPassword.getText().length() > 8 && editTextPasswordRe.getText().length() > 8)
                    {
                        if(editTextPassword.getText().toString().equals(editTextPasswordRe.getText().toString()))
                        {
                            errorMessage.setText("");

                            CollectionPPDA cppDa = new CollectionPPDA();

                            try
                            {
                                cppDa.saveCollectionPP(cpp);

                                editTextEmail.setText("");
                                editTextFName.setText("");
                                editTextLName.setText("");
                                editTextPassword.setText("");
                                editTextPhoneN.setText("");
                                editTextNId.setText("");
                                editTextPasswordRe.setText("");

                                Intent intent = new Intent(CollectionPointProviderSignupActivity.this, TypeLoginActivity.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(),"Added successfully!",Toast.LENGTH_SHORT).show();
                            }
                            catch (IOException e)
                            {
                                throw new RuntimeException(e);
                            }
                        }
                        else
                        {
                            errorMessage.setText("Passwords is not match!");
                        }
                    }
                    else
                    {
                        errorMessage.setText("Password must contain more than 8 characters!");
                    }

                }
                else
                {
                    Toast.makeText(CollectionPointProviderSignupActivity.this, "Fill all fields, and try again!", Toast.LENGTH_SHORT).show();
                    errorMessage.setText("Fill all fields, and try again!");
                }

            }
        });//end button action

    }//end method

}