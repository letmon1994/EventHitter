package com.example.ryanletmon.eventhitter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ryanletmon.eventhitter.Prevalent.Config;
import com.example.ryanletmon.eventhitter.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmOrder extends AppCompatActivity {

    private static final int PAYPAL_REQUEST_CODE = 999;
    private EditText name, phone, address;
    private Button conformOrderButton;

    private String total = "";

    String eventId = "";

    FirebaseDatabase database;
    DatabaseReference events;

    //PayPal payment
    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) // use the sandbox for testing
            .clientId(Config.PAYPAL_CLIENT_ID);
    String usersAddress, usersName, usersPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        //Firebase
        database = FirebaseDatabase.getInstance();
        events = database.getReference("Events");

        total = getIntent().getStringExtra("Total Price");  // retrieving the "Total Price" data from the intent
        eventId = getIntent().getStringExtra("eid");        // retrieving the "eid" data from the intent

        conformOrderButton = (Button) findViewById(R.id.confirm_final_order_btn);
        name = (EditText) findViewById(R.id.order_name);
        phone = (EditText) findViewById(R.id.order_phone);
        address = (EditText) findViewById(R.id.order_address);

        // init PayPal
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        //getEventDetails(eventId);

        conformOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFields();
            }
        });

        if(Prevalent.isConnectedToInternet(this))    // if the user is connected to the internet
            checkFields();
        else{
            Toast.makeText(ConfirmOrder.this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            Intent intentConfirm = new Intent(ConfirmOrder.this, Login.class);
            startActivity(intentConfirm);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE){
            if (resultCode == RESULT_OK ){

                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                confirmOrder();
                Toast.makeText(ConfirmOrder.this, "Thank You, Order is Placed", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else if (requestCode == Activity.RESULT_CANCELED)
            Toast.makeText(this, "Payment Cancel", Toast.LENGTH_SHORT).show();
        else if(requestCode ==PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "Invalid Payment", Toast.LENGTH_SHORT).show();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == PAYPAL_REQUEST_CODE){
//            if (resultCode == RESULT_OK ){
//
//                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
//
//                if (confirmation != null){
//                    try{
//                        String paymeantDetail = confirmation.toJSONObject().toString(4);
//                        JSONObject jsonObject = new JSONObject(paymeantDetail);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//
//    }

//    private void getEventDetails(String eventId) {
//        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference().child("Events");
//
//        eventsRef.child(eventId).addValueEventListener(new ValueEventListener() {  // passing the event id
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    Events events = dataSnapshot.getValue(Events.class);
//
//                    eventName.setText(events.getTitle());
//                    eventPrice.setText(events.getPrice());
//                    eventDescription.setText(events.getDescription());
//                    eventDate.setText(events.getDateOfEvent());
//                    eventTime.setText(events.getTimeOfEvent());
//                    eventAddress.setText(events.getAddress());
//                    eventLocation.setText(events.getLocation());
//                    eventLocation2.setText(events.getLocation());
//                    eventImageText.setText(events.getImage());
//
//
//                    Picasso.get().load(events.getImage()).into(eventImage);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void payPal() {
        // show Paypal to payment
        usersName = name.getText().toString();
        usersAddress = address.getText().toString();
        usersPhone = phone.getText().toString();

        String formatAmount = total;

        // PayPal implementation
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAmount),"EUR", "EventHitter", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(getApplicationContext(),PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    // validating the required fields
    private void checkFields() {
        if (TextUtils.isEmpty(name.getText().toString())){
            Toast.makeText(this, "Please Enter Your Name",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone.getText().toString())) {
            Toast.makeText(this, "Please Enter Your Phone Number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(address.getText().toString())) {
            Toast.makeText(this, "Please Enter Your Address", Toast.LENGTH_SHORT).show();
        }
        else {
            payPal();
        }

    }

    // getting the date and time on which the  user has ordered the event ticket
    private void confirmOrder() {
        final  String saveCurrentDate;
        final String saveCurrentTime;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());                    // getting the present time and date
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference orders = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());  // used for reading and writing data in the database under the orders

        HashMap<String, Object> insertOrders = new HashMap<>();
        insertOrders.put("totalAmount", total);
        insertOrders.put("name", name.getText().toString());
        insertOrders.put("date", saveCurrentDate);                         // adding these values into the database once the users payment is complete
        insertOrders.put("time", saveCurrentTime);
        insertOrders.put("phone", phone.getText().toString());
        insertOrders.put("address", address.getText().toString());
        insertOrders.put("payPal", "Successful");
        insertOrders.put("shipped", "no");


        // once the user orders the tickets the cart becomes empty and notify the user by adding an addOnCompleteListener
        orders.updateChildren(insertOrders).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(Prevalent.currentOnlineUser.getPhone()).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(ConfirmOrder.this, "Order placed", Toast.LENGTH_SHORT).show();

                                    Intent confirmOrderIntent = new Intent(ConfirmOrder.this, Home.class);
                                    // so the user cant go back to the activity
                                    confirmOrderIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(confirmOrderIntent);
                                    finish();
                                }
                            }
                        });
            }
        });

    }
}



