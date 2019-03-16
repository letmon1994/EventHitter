package com.example.ryanletmon.eventhitter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ryanletmon.eventhitter.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewEvent extends AppCompatActivity {

    // variables
    private String CategoryName;
    private ImageView InputEventImage;
    private Button AddNewEventButton;
    private EditText InputEventTitle, InputEventDescription, InputEventPrice, InputEventLocation, InputEventLatitude, InputEventLongitude, InputEventAddress, InputEventDate, InputEventTime;

    private String title, description, price, location, saveCurrentDate, saveCurrentTime, latitude, longitude, address, time, date;
    private Double latitude2, longitude5;

    private static final int GalleryPick = 1;
    private Uri ImageUri;  // creating imageUri variable

    private StorageReference EventImageRef;
    private String eventRandomKey, downloadImageUrl;

    private DatabaseReference eventRef;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_event);

        //Initializing
        CategoryName = getIntent().getExtras().get("category").toString();
        EventImageRef = FirebaseStorage.getInstance().getReference().child("Event Images");  // accessing the data in the firebase storage
        eventRef = FirebaseDatabase.getInstance().getReference().child("Events");            // accessing the data in the database under Events

        AddNewEventButton = (Button) findViewById(R.id.add_new_product);
        InputEventImage = (ImageView) findViewById(R.id.select_product_image);
        InputEventTitle = (EditText) findViewById(R.id.event_title);
        InputEventDescription = (EditText) findViewById(R.id.event_description);
        InputEventPrice = (EditText) findViewById(R.id.event_price);
        InputEventLocation = (EditText) findViewById(R.id.event_location);
        InputEventAddress = (EditText) findViewById(R.id.event_address);
        InputEventLatitude = (EditText) findViewById(R.id.event_latitude);
        InputEventLongitude = (EditText) findViewById(R.id.event_longitude);
        InputEventTime = (EditText) findViewById(R.id.event_time);
        InputEventDate = (EditText) findViewById(R.id.event_date);
        loadingBar = new ProgressDialog(this);

        InputEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OpenGallery();
            }
        });




        AddNewEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ValidateEventData();
            }
        });

        if(Prevalent.isConnectedToInternet(this))
            ValidateEventData();
        else{
            Toast.makeText(AdminAddNewEvent.this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminAddNewEvent.this, Login.class);
            startActivity(intent);
        }
    }

    private void ValidateEventData() {

        // putting the text inputs to a String
        title = InputEventTitle.getText().toString();
        description = InputEventDescription.getText().toString();
        price = InputEventPrice.getText().toString();
        location = InputEventLocation.getText().toString();
        latitude = InputEventLatitude.getText().toString();
        longitude = InputEventLongitude.getText().toString();
        address = InputEventAddress.getText().toString();
        date = InputEventDate.getText().toString();
        time = InputEventTime.getText().toString();

        // Validation
        if(ImageUri == null){
            Toast.makeText(this, "You must use a event Image", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Please write the events description", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(title)){
            Toast.makeText(this, "Please write the events title", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(price)){
            Toast.makeText(this, "Please write the events price", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(location)){
            Toast.makeText(this, "Please write the events description", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(latitude)){
            Toast.makeText(this, "Please write the events locations latitude", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(longitude)){
            Toast.makeText(this, "Please write the events locations longitude", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(address)){
            Toast.makeText(this, "Please write the events address", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(time)){
            Toast.makeText(this, "Please write the time the event is on at", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(location)){
            Toast.makeText(this, "Please write what day the event is on", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreEventInfo();
        }

    }

    public void StoreEventInfo() {
        loadingBar.setTitle("Add New Event");
        loadingBar.setMessage("Admin, please wait while we are adding the new Event.");  // showing the process of adding new event
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");  // pass the date format in
        saveCurrentDate = currentDate.format(calendar.getTime());                    // get the current date
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");   // get the current time
        saveCurrentTime = currentTime.format(calendar.getTime());                   // pass the time format in

        eventRandomKey = saveCurrentDate + saveCurrentTime; // cannot be retrieved

        final StorageReference filePath = EventImageRef.child(ImageUri.getLastPathSegment() + eventRandomKey + ".jpg");  // putting the event image into the firebase storage. Becomes an image link
        final UploadTask uploadTask = filePath.putFile(ImageUri); //storing the link of the firebase image into the database

        // incase it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(AdminAddNewEvent.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(AdminAddNewEvent.this, "Event Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {  // get the image link from firebase storage and then put it into the database
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())  // if task is not successful
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();   //Just storing the image uri and not the link
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    // getting the image link if uploaded successfully
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())  // if task successful
                        {
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AdminAddNewEvent.this, "got the Event image Url Successfully...", Toast.LENGTH_SHORT).show();

                            SaveEventInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void SaveEventInfoToDatabase() {
        HashMap<String, Object> eventIn = new HashMap<>();
        eventIn.put("eid", eventRandomKey);
        eventIn.put("date", saveCurrentDate);
        eventIn.put("time", saveCurrentTime);
        eventIn.put("title", title);              // storing data into the database
        eventIn.put("description", description);
        eventIn.put("image", downloadImageUrl);
        eventIn.put("category", CategoryName);
        eventIn.put("price", price);
        eventIn.put("location", location);
        eventIn.put("latitude", latitude);
        eventIn.put("longitude", longitude);
        eventIn.put("address", address);
        eventIn.put("timeOfEvent", time);
        eventIn.put("dateOfEvent", date);

        eventRef.child(eventRandomKey).updateChildren(eventIn) // each event will have its own unique information
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AdminAddNewEvent.this, AdminCategory.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewEvent.this, "Event is added successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewEvent.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");                     // method to allow admin to select an image for the event
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null) // checking requestCode with an if statement
        {
            ImageUri = data.getData();
            InputEventImage.setImageURI(ImageUri); // displaying image in the image View
        }
    }
}


