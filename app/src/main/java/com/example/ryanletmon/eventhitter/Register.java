package com.example.ryanletmon.eventhitter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ryanletmon.eventhitter.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    // variables
    private Button CreateAccountButton;
    private EditText InputName, InputPhoneNumber, InputPassword;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initializing
        CreateAccountButton = (Button) findViewById(R.id.register_btn);
        InputName = (EditText) findViewById(R.id.register_username_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.register_phone_number_input);
        loadingBar = new ProgressDialog(this);

        // when the create account button is clicked
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }


    private void CreateAccount() {

        if (Prevalent.isConnectedToInternet(getBaseContext())) {  // if users app is connected to the internet


            String name = InputName.getText().toString();
            String phone = InputPhoneNumber.getText().toString();
            String password = InputPassword.getText().toString();

            // validation for input fields
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Please write your name.", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Please write your phone number.", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please write your password.", Toast.LENGTH_SHORT).show();
            } else {
                loadingBar.setTitle("Create Account");
                loadingBar.setMessage("Please wait, while we are checking the credentials."); // showing the process of registering in
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                ValidatephoneNumber(name, phone, password);
            }
        }
        else{
            Toast.makeText(Register.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
            return;
        }
    }




    // storing new users data into the database
    private void ValidatephoneNumber(final String name, final String phone, final String password)
    {
        final DatabaseReference RootRef;  // creating database reference

        RootRef = FirebaseDatabase.getInstance().getReference(); // accessing the data in the database

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(phone).exists())) {
                    HashMap<String, Object> userdataMap = new HashMap<>();  // making sure there's no account in the data base that has that phoneNumber
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", password); // stores data into the databse
                    userdataMap.put("name", name);

                    RootRef.child("Users").child(phone).updateChildren(userdataMap) // creating the child. Users is the parent and then under will be the phone (child)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(Register.this, "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(Register.this, Home.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        loadingBar.dismiss();
                                        Toast.makeText(Register.this, "Network Error: Please try again after some time", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(Register.this, "This " + phone + " already exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(Register.this, "Please try again using another phone number.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Register.this, Main.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
