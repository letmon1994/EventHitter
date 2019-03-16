package com.example.ryanletmon.eventhitter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ryanletmon.eventhitter.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {

    //defining and initialising my text views etc
    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, addressEditText;
    private TextView profileChangeTextButton,  closeTextButton, saveTextButton;

    private Uri imageUri;         // creating imageUri variable
    private String myUrl = "";    // stores the image Url
    private String check = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //defining and initialising my text views etc
        profileImageView = (CircleImageView) findViewById(R.id.settings_profile_image);
        fullNameEditText = (EditText) findViewById(R.id.settings_full_name);
        userPhoneEditText = (EditText) findViewById(R.id.settings_phone_number);
        addressEditText = (EditText) findViewById(R.id.settings_address);
        profileChangeTextButton = (TextView) findViewById(R.id.profile_image_change_button);
        closeTextButton = (TextView) findViewById(R.id.close_settings_button);
        saveTextButton = (TextView) findViewById(R.id.update_account_settings_button);

        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures"); // accessing the data in the firebase storage


        // calls InfoDisplay method
        infoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText);

        closeTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check.equals("clicked")) {    // if button clicked, call method
                    infoSaved();
                } else {
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)  // when the profileChangeTextButton is clicked then crop them image that is selected
                        .start(Settings.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)  // checking requestCode with an if statement
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);  //getting the result
            imageUri = result.getUri();  // stores the result into the variable

            profileImageView.setImageURI(imageUri);     // displaying image in the image View
        }
        else
        {
            Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(Settings.this, Settings.class));
            finish();
        }
    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users"); //for storing it inside the Users

        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("name", fullNameEditText.getText().toString());
        userMap. put("address", addressEditText.getText().toString());        // storing data into the database
        userMap. put("phoneOrder", userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);  // add the data into the database for the user whos phone number was logged in

        startActivity(new Intent(Settings.this, Main.class));
        Toast.makeText(Settings.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }



    //allow the user to update their information
    private void infoSaved(){
        if (TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            Toast.makeText(this, "Must enter name.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Must enter address.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Must enter phone.", Toast.LENGTH_SHORT).show();
        }
        else if(check.equals("clicked"))
        {
            ImageUpload();
        }
    }


    private void ImageUpload() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");  // showing the process of updating profile in
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)  // if imageUri not equal to null
        {
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");  // to store the users new image into the firebase storage

            uploadTask = fileRef.putFile(imageUri); // sets the path and saves the file to the storage

            uploadTask.continueWithTask(new Continuation() {     // getting the result of the upload task
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) // getting the Url and storing it into my firebase database
                        {
                            if (task.isSuccessful())
                            {
                                Uri downloadUrl = task.getResult();   // getting the result of the Url and stroing it into the variable downloadUrl
                                myUrl = downloadUrl.toString();     // converting downloadUrl

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users"); // for storing it inside the Users

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap. put("name", fullNameEditText.getText().toString());
                                userMap. put("address", addressEditText.getText().toString());        // putting the data into the database
                                userMap. put("phoneOrder", userPhoneEditText.getText().toString());
                                userMap. put("image", myUrl);
                                ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);  // add the data into the database for the user whos phone number was logged in

                                progressDialog.dismiss();

                                startActivity(new Intent(Settings.this, Main.class));
                                Toast.makeText(Settings.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(Settings.this, "Error.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }


    // creates InfoDisplay method
    private void infoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());  // accessing the data in the database under users for the user logged in

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())    // if the data in the database is there
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();     // displaying the data that already exists
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);   // gets the saved image and displays it
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}


