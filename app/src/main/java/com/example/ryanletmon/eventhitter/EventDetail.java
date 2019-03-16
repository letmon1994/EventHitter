package com.example.ryanletmon.eventhitter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ryanletmon.eventhitter.Model.Events;
//import com.example.ryanletmon.eventhitter.Model.MapInformation;
import com.example.ryanletmon.eventhitter.Prevalent.Prevalent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class EventDetail extends AppCompatActivity implements OnMapReadyCallback,LocationListener,GoogleMap.OnMarkerClickListener {

    //Variables
    TextView eventName, eventPrice, eventDescription, eventImageText, eventLocation, eventAddress, eventTime, eventDate, eventLocation2;
    ImageView eventImage;
    FloatingActionButton btnCart;
    FloatingActionButton btnFav;
    ElegantNumberButton numberButton;
    Button postComment;
    EditText userName, userComment;


    String eventId = "";

    FirebaseDatabase database;
    DatabaseReference events;

    private ProgressDialog loadingBar;

    String name, comment, saveCurrentDate, saveCurrentTime;

    String shipped = "Normal";

    private GoogleMap mMap;
    private DatabaseReference mPoints;
    private ChildEventListener mChildEventListener;
    Marker marker;

    Events currentEvent;

    private String eventRandomKey;

    public  double latx;
    public  double longx;

    int markerHeight = 90;
    int markerWidth = 90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        eventId = getIntent().getStringExtra("eid");

        //Firebase
        database = FirebaseDatabase.getInstance();
        events = database.getReference("Events");

        // init view
        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        btnCart = (FloatingActionButton) findViewById(R.id.btnCart);
        btnFav = (FloatingActionButton) findViewById(R.id.btnFav);

        eventDescription = (TextView) findViewById(R.id.eventDescription);
        eventName = (TextView) findViewById(R.id.eventName);
        eventAddress = (TextView) findViewById(R.id.eventAddress);
        eventImage = (ImageView) findViewById(R.id.img_event);
        eventLocation = (TextView) findViewById(R.id.eventLocation);
        eventDate = (TextView) findViewById(R.id.eventDate);
        eventTime = (TextView) findViewById(R.id.eventTime);
        eventPrice = (TextView) findViewById(R.id.eventTicketPrice);
        eventLocation2 = (TextView) findViewById(R.id.eventLocation3);
        eventImageText = (TextView) findViewById(R.id.event_image2);

        loadingBar = new ProgressDialog(this);

        userName = (EditText) findViewById(R.id.postName);
        userComment = (EditText) findViewById(R.id.postComment);
        postComment = (Button) findViewById(R.id.postBtn);

        getEventDetails(eventId);   // calling the method getEventDetails

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mPoints = FirebaseDatabase.getInstance().getReference().child("Events").child(eventId);
        ChildEventListener mChildEventListener;
        mPoints.push().setValue(marker);

        if(Prevalent.isConnectedToInternet(this))   // if app is connected to the internet
            getEventDetails(eventId);
        else{
            Toast.makeText(EventDetail.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
            return;
        }


//        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
//        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
//        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

//        //Get Event Id from intent
//        if(getIntent() != null)
//            eventId = getIntent().getStringExtra("eid");
//        if(!eventId.isEmpty())
//        {
//            getEventDetails(eventId);
//        }


//        btnCart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new Database(getBaseContext()).addToCart(new Order(
//                        eventId,
//                        currentEvent.getTitle(),
//                        numberButton.getNumber(),
//                        currentEvent.getPrice(),
//                        currentEvent.getCategory()
//                ));
//            }
//        });

        // what happens when the btnCart button is clicked
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (shipped.equals("Orders Placed")) {
                    Toast.makeText(EventDetail.this, "You can purchase more tickets once your payment is verified and shipped", Toast.LENGTH_LONG).show();
                }
                else {
                    addingToCartList();
                }
            }
        });

        // what happens when the btnFav button is clicked
        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingToFavList();
            }
        });

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateEventData();
            }
        });

    }

    private void getEventDetails(String eventId) {
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference().child("Events");   // accessing the data in the database under Events

        eventsRef.child(eventId).addValueEventListener(new ValueEventListener() {  // passing the event id
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Events events = dataSnapshot.getValue(Events.class);

                    eventName.setText(events.getTitle());
                    eventPrice.setText(events.getPrice());
                    eventDescription.setText(events.getDescription());     // getting the vales in the database for each event
                    eventDate.setText(events.getDateOfEvent());
                    eventTime.setText(events.getTimeOfEvent());
                    eventAddress.setText(events.getAddress());
                    eventLocation.setText(events.getLocation());
                    eventLocation2.setText(events.getLocation());
                    latx = events.getLatitude();
                    longx = events.getLongitude();
                    eventImageText.setText(events.getImage());
                    Picasso.get().load(events.getImage()).into(eventImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        mPoints.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LatLng location = new LatLng(latx, longx);   // getting the latitude and longitude values
                BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.mainlogo);   // displaying my logo as the marker on the map
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, markerWidth, markerHeight, false);
                    mMap.addMarker(new MarkerOptions().position(location).title("Event Location")).setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));  // setting the size of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder()   //Build camera position
                            .target(location)
                            .zoom(10).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));   //Zoom in and animate the camera.
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    protected void onStart(){
        super.onStart();

        shippingOrders();
    }




    private void addingToCartList(){
        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());                     // getting the present time and date
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

       final DatabaseReference cartList = FirebaseDatabase.getInstance().getReference().child("Cart List");  // used for reading and writing data thats under Car List in the database

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("eid", eventId);
        cartMap.put("eventName", eventName.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);                        // adding these values into the database once the user clicks the add to cart floating action button
        cartMap.put("price", eventPrice.getText().toString());
        cartMap.put("quantity", numberButton.getNumber());
        cartMap.put("discount", "");

        // setting up the users view in the database
        cartList.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Events").child(eventId).updateChildren(cartMap).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            // setting up the admin view in the database
                            cartList.child("Admin View").child(Prevalent.currentOnlineUser.getPhone()).child("Events").child(eventId).updateChildren(cartMap).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(EventDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(EventDetail.this, Home.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void addingToFavList(){
        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());                   // getting the present time and date
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference favList = FirebaseDatabase.getInstance().getReference().child("Favourites List");  // used for reading and writing data thats under Favourites List in the database

        final HashMap<String, Object> favMap = new HashMap<>();
        favMap.put("eid", eventId);
        favMap.put("eventName", eventName.getText().toString());
        favMap.put("date", saveCurrentDate);
        favMap.put("time", saveCurrentTime);
        favMap.put("price", eventPrice.getText().toString());              // adding these values into the database once the user clicks the add to fav floating action button
        favMap.put("quantity", numberButton.getNumber());
        favMap.put("description", eventDescription.getText().toString());
        favMap.put("dateOfEvent", eventDate.getText().toString());
        favMap.put("location", eventLocation.getText().toString());
        favMap.put("image", eventImageText.getText().toString());

        // setting up the users view in the database
        favList.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Events").child(eventId).updateChildren(favMap).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            // setting up the admin view in the database
                            favList.child("Admin View").child(Prevalent.currentOnlineUser.getPhone()).child("Events").child(eventId).updateChildren(favMap).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(EventDetail.this, "Added to Favourites", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void ValidateEventData() {

        // putting the text inputs to a String
        name = userName.getText().toString();
        comment = userComment.getText().toString();


        // Validation
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please write your name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(comment)){
            Toast.makeText(this, "Please write your comment", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreEventInfo();
        }

    }

    public void StoreEventInfo() {

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");  // pass the date format in
        saveCurrentDate = currentDate.format(calendar.getTime());                    // get the current date
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");   // get the current time
        saveCurrentTime = currentTime.format(calendar.getTime());                   // pass the time format in

        eventRandomKey = saveCurrentDate + saveCurrentTime; // cannot be retrieved

        addingComment();
    }

    private void addingComment(){
        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());                   // getting the present time and date
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference commentList = FirebaseDatabase.getInstance().getReference().child("User Comments");  // used for reading and writing data thats under Favourites List in the database

        final HashMap<String, Object> favMap = new HashMap<>();
        favMap.put("eid", eventId);
        favMap.put("userName", name);
        favMap.put("date", saveCurrentDate);      // adding these values into the database once the user clicks the add to fav floating action button
        favMap.put("time", saveCurrentTime);
        favMap.put("comment", comment);

        // setting up the users view in the database
        commentList.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Comments").child(eventId).updateChildren(favMap).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            // setting up the admin view in the database
                            commentList.child("Admin View").child(Prevalent.currentOnlineUser.getPhone()).child("Comments").child(eventId).updateChildren(favMap).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(EventDetail.this, "Thank you for your comment", Toast.LENGTH_SHORT).show();
                                                userComment.setText(null);
                                                userName.setText(null);
                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private void shippingOrders(){
        DatabaseReference orders;

        // used for reading and writing data thats under orders then under the users phone number in the database
        orders = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        orders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shippedState = dataSnapshot.child("shipped").getValue().toString();

                    if (shippedState.equals("no")){   // if the shipped value in the database is equal to no then the String shipped variable is equal to "Orders Placed"
                        shipped = "Orders Placed";
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


//    private void getEventDetails(String eventId) {
//        events.child(eventId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                currentEvent = dataSnapshot.getValue(Events.class);
//
//                Picasso.get().load(currentEvent.getImage())
//                        .into(eventImage);
//
//                collapsingToolbarLayout.setTitle(currentEvent.getTitle());
//
//                eventPrice.setText(currentEvent.getPrice());
//
//                eventName.setText(currentEvent.getTitle());
//
//                eventDescription.setText(currentEvent.getDescription());
//            }
//
//
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }


}
