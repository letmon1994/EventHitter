package com.example.ryanletmon.eventhitter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.ryanletmon.eventhitter.Model.Order;
import com.example.ryanletmon.eventhitter.Prevalent.Prevalent;
//import com.example.ryanletmon.eventhitter.ViewHolder.CartAdapter;
import com.example.ryanletmon.eventhitter.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//import info.hoang8f.widget.FButton;

public class Cart extends AppCompatActivity {

    // Variables
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private TextView txtTotalPrice;
    private Button btnPlace;
    private TextView txtMessage;

    private int overTotalPrice = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        //Initializing
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = (TextView) findViewById(R.id.total_price);
        btnPlace = (Button) findViewById(R.id.next_process_button);
        txtMessage = (TextView) findViewById(R.id.theMessage) ;

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTotalPrice.setText("Total Price = " + String.valueOf(overTotalPrice));
                Intent orderIntent = new Intent(Cart.this, ConfirmOrder.class);
                orderIntent.putExtra("Total Price", String.valueOf(overTotalPrice));
                startActivity(orderIntent);
                finish();
            }
        });

        if(Prevalent.isConnectedToInternet(this))  // if the user is connected to the internet call the onStart method
            onStart();
        else{
            Toast.makeText(Cart.this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Cart.this, Login.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart(){

        super.onStart();

        shippingOrders();  // calls shippingOrders method

        final  DatabaseReference cartList = FirebaseDatabase.getInstance().getReference().child("Cart List");  // used for reading and writing data in the database under Cart List

        final FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>()
                // getting into the users view and getting the currents users phone, then their events that they added to the cart
                .setQuery(cartList.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Events"), Order.class).build();

        FirebaseRecyclerAdapter<Order, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Order, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Order model) {
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(""+model.getQuantity(), Color.BLUE);
                holder.img_cart_count1.setImageDrawable(drawable);
                holder.img_cart_count.setText(model.getQuantity());       // used for getting the data in the database then displaying it on the card view
                holder.txt_cart_name.setText(model.getEventName());
                holder.txt_price.setText(model.getPrice());

                // gets the price of each event added to the cart
                int eventPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                overTotalPrice = overTotalPrice + eventPrice;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {          // alert dialog appears once the card view is clicked
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Edit",
                                        "Delete"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(Cart.this);
                        builder.setTitle("Options");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){
                                    Intent intent = new Intent(Cart.this, EventDetail.class);  // brings you to the event details activity so the user can add or get rid of a number of tickets
                                    intent.putExtra("eid", model.getEid());
                                    startActivity(intent);
                                }
                                if(which ==1){
                                    cartList.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Events").child(model.getEid()).removeValue()   // removes the tickets from the users cart
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(Cart.this, "Removed from Cart", Toast.LENGTH_SHORT).show();

                                                        Intent intent = new Intent(Cart.this, Home.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_layout, parent, false);  // used to instantiate the layout XML file
                CartViewHolder holder = new CartViewHolder(itemView);
                return holder;


            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void shippingOrders(){
        DatabaseReference orders;
        orders = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());  // used for reading and writing data in the database under orders and the users phone number

        orders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shippedState = dataSnapshot.child("shipped").getValue().toString();  // getting the value of shipped inside the database
                    String name = dataSnapshot.child("name").getValue().toString();

                    if (shippedState.equals("no")){    // if the value is eaqual to "no"
                        txtTotalPrice.setText("Hi " + name + ", you can purchase more tickets once your tickets have been shipped.");
                        recyclerView.setVisibility(View.GONE);

                        txtMessage.setVisibility(View.VISIBLE);   // setting the visibilities to display in the xml file
                        btnPlace.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void onStop(){
        super.onStop();
        overTotalPrice = 0;
    }

//    private void loadListEvent() {
//        cart = new Database(this).getCarts();
//        adapter = new CartAdapter(cart,this);
//        recyclerView.setAdapter(adapter);
//
//        // To calculate total price
//        int total = 0;
//        for(Order order:cart)
//            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
//        Locale locale = new Locale("en","US");
//        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
//
//        txtTotalPrice.setText(fmt.format(total));
//    }
}
