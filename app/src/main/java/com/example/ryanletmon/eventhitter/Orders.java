package com.example.ryanletmon.eventhitter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ryanletmon.eventhitter.Model.OrderList;
import com.example.ryanletmon.eventhitter.Prevalent.Prevalent;
import com.example.ryanletmon.eventhitter.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Orders extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        //Init
        recyclerView = findViewById(R.id.listUsersOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(Prevalent.isConnectedToInternet(this))  // if the user is connected to the internet then call the onStart method
            onStart();
        else{
            Toast.makeText(Orders.this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Orders.this, Login.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart(){

        super.onStart();

        final DatabaseReference ordersList = FirebaseDatabase.getInstance().getReference().child("Orders");  // used for reading and writing data in the database under Orders

        FirebaseRecyclerOptions<OrderList> options = new FirebaseRecyclerOptions.Builder<OrderList>().setQuery(ordersList, OrderList.class).build();

        FirebaseRecyclerAdapter<OrderList, OrderViewHolder> adapter = new FirebaseRecyclerAdapter<OrderList, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull final OrderList model) {
                holder.txt_user_name.setText(model.getName());
                holder.txt_user_price.setText(model.getTotalAmount());  // used for getting the data in the database then displaying it on the card view
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ordersuser_layout, parent, false);  // used to instantiate the layout XML file
                OrderViewHolder holder = new OrderViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
