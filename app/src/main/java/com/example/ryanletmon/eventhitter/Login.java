package com.example.ryanletmon.eventhitter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.rey.material.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ryanletmon.eventhitter.Model.Users;
import com.example.ryanletmon.eventhitter.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class Login extends AppCompatActivity {

    // variables
    private EditText InputPhoneNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;

    private TextView AdminLink, NotAdminLink;

    private String parentDbName = "Users";
    private CheckBox chkBoxRememberMe;

    String projectToken = "350bf7f0c9cc4f212aabed42dfab1204";   // mixpanel api key


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initializing
        LoginButton = (Button) findViewById(R.id.login_btn);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);

        loadingBar = new ProgressDialog(this);

        chkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this); //Initializing the paper library

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins"; // to use the database that stores the admins
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users"; // to use the database that stores the general users
            }
        });
    }

    private void LoginUser() {

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, projectToken);  // setting up mixpanel
        mixpanel.track("LoginUser", null);
        mixpanel.flush();

        if(Prevalent.isConnectedToInternet(getBaseContext())) {   // if users app is connected to the internet

            String phone = InputPhoneNumber.getText().toString();  // getting the data and converting it to a string
            String password = InputPassword.getText().toString();

            // validation
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
            } else {
                loadingBar.setTitle("Login Account");
                loadingBar.setMessage("Please wait, while we are checking the credentials.");  // showing the process of logging in
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();


                AllowAccessToAccount(phone, password);
            }
        }
        else{
            Toast.makeText(Login.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void AllowAccessToAccount(final String phone, final String password) {

        // to store the phone key and password
        if(chkBoxRememberMe.isChecked()) {

            Paper.book().write(Prevalent.UserPhoneKey, phone);        // stores users information to the memory
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;                           // creating database reference
        RootRef = FirebaseDatabase.getInstance().getReference();  // accessing the data in the database

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(parentDbName).child(phone).exists())  // see if the user is available or not
                {
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone))  // seeing if the users correct phone is stored in the database
                    {
                        if (usersData.getPassword().equals(password))  // seeing if the users correct passsword is stored in the database
                        {
                            if (parentDbName.equals("Admins"))  // if the AdminLink is selected
                            {
                                Toast.makeText(Login.this, "Welcome Admin, you are logged in Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(Login.this, AdminCategory.class);
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("Users"))  // if the Users is selected
                            {
                                Toast.makeText(Login.this, "logged in Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(Login.this, Home.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(Login.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(Login.this, "Account with this " + phone + " number do not exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}