package com.example.ryanletmon.eventhitter;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminOrders extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        ordersReference = FirebaseDatabase.getInstance().getReference().child("Orders");

        ordersList = findViewById(R.id.listNewOrders);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<com.example.ryanletmon.eventhitter.Model.AdminOrders> options = new FirebaseRecyclerOptions.Builder<com.example.ryanletmon.eventhitter.Model.AdminOrders>().setQuery(ordersReference, com.example.ryanletmon.eventhitter.Model.AdminOrders.class).build();

        FirebaseRecyclerAdapter<com.example.ryanletmon.eventhitter.Model.AdminOrders, AdminOrdersViewHolder> adapter = new FirebaseRecyclerAdapter<com.example.ryanletmon.eventhitter.Model.AdminOrders, AdminOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final com.example.ryanletmon.eventhitter.Model.AdminOrders model) {
                holder.usersName.setText("Name: " + model.getName());
                holder.usersPhone.setText("Phone Number: " + model.getPhone());
                holder.usersAdress.setText("Address: " + model.getAddress());

                holder.showOrders.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uID = getRef(position).getKey();  // the string to show the orders from the account pnce the button is clicked
                        Intent intent = new Intent(AdminOrders.this, AdminViewUsersEvents.class);
                        intent.putExtra("userId", uID);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]{    // alert dialog appears once the card view is clicked
                                "yes",
                                "no"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminOrders.this);
                        builder.setTitle("Tickets have been shipped?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void  onClick (DialogInterface dialogInterface, int which) {
                                if (which == 0){
                                    String uID = getRef(position).getKey();   // remove the order from the database
                                    RemoveOrder(uID);
                                }
                                else {
                                    finish();
                                }
                            }
                        });
                        builder.show();

                    }
                });
            }

            @NonNull
            @Override
            public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                return new AdminOrdersViewHolder(view);
            }
        };
        ordersList.setAdapter(adapter);
        adapter.startListening();
    }

    private void RemoveOrder(String uID) {

        ordersReference.child(uID).removeValue();

    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder{

        public TextView usersName, usersPhone, usersTotal, usersAdress;
        public Button showOrders;

        public AdminOrdersViewHolder(View itemView) {
            super(itemView);

            usersName = itemView.findViewById(R.id.users_name);
            usersPhone = itemView.findViewById(R.id.users_phone);
            usersTotal = itemView.findViewById(R.id.users_name);
            usersAdress = itemView.findViewById(R.id.users_adress);

            showOrders = itemView.findViewById(R.id.show_ordersBtn);
        }
    }
}
