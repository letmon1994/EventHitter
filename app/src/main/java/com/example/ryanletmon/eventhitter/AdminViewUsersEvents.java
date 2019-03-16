package com.example.ryanletmon.eventhitter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.ryanletmon.eventhitter.Model.Order;
import com.example.ryanletmon.eventhitter.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class AdminViewUsersEvents extends AppCompatActivity {
    private RecyclerView eventsList;
    private DatabaseReference cartReference;
    RecyclerView.LayoutManager layoutManager;

    private String userID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_users_events);

        userID = getIntent().getStringExtra("userId");

        // accessing the information in the database under whats in the cart list, then admin view etc
        cartReference = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View").child(userID).child("Events");

        eventsList = findViewById(R.id.eventList);
        layoutManager = new LinearLayoutManager(this);
        eventsList.setHasFixedSize(true);
        eventsList.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>().setQuery(cartReference, Order.class).build();

        FirebaseRecyclerAdapter<Order, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Order, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Order model) {
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(""+model.getQuantity(), Color.BLUE);
                holder.img_cart_count1.setImageDrawable(drawable);
                holder.img_cart_count.setText(model.getQuantity());
                holder.txt_cart_name.setText(model.getEventName());
                holder.txt_price.setText(model.getPrice());
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int view) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(itemView);
                return holder;
            }
        };

        eventsList.setAdapter(adapter);
        adapter.startListening();
    }
}
