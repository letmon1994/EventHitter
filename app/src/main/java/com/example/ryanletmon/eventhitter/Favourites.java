package com.example.ryanletmon.eventhitter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.ryanletmon.eventhitter.Prevalent.Prevalent;
//import com.example.ryanletmon.eventhitter.ViewHolder.CartAdapter;
import com.example.ryanletmon.eventhitter.ViewHolder.FavViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

//import info.hoang8f.widget.FButton;

public class Favourites extends AppCompatActivity {

    // Variables
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button deleteFav;
    FloatingActionButton btnFav;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);


        btnFav = (FloatingActionButton) findViewById(R.id.btnFav);

        //Init
        recyclerView = findViewById(R.id.listFavourites);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(Prevalent.isConnectedToInternet(this))  // if the user is connected to the internet then call the onStart method
            onStart();
        else{
            Toast.makeText(Favourites.this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Favourites.this, Login.class);
            startActivity(intent);
        }

    }

    @Override
    protected void onStart(){

        super.onStart();

        final  DatabaseReference favouritesList = FirebaseDatabase.getInstance().getReference().child("Favourites List");  // used for reading and writing data thats under Favourites List in the database

        FirebaseRecyclerOptions<com.example.ryanletmon.eventhitter.Model.Favourites> options = new FirebaseRecyclerOptions.Builder<com.example.ryanletmon.eventhitter.Model.Favourites>()
                // getting into the users view and getting the currents users phone, then their events that they added to the cart and setting a query
                .setQuery(favouritesList.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Events"), com.example.ryanletmon.eventhitter.Model.Favourites.class).build();

        FirebaseRecyclerAdapter<com.example.ryanletmon.eventhitter.Model.Favourites, FavViewHolder> adapter = new FirebaseRecyclerAdapter<com.example.ryanletmon.eventhitter.Model.Favourites, FavViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FavViewHolder holder, int position, @NonNull final com.example.ryanletmon.eventhitter.Model.Favourites model) {
                holder.eventTitle.setText(model.getEventName());
                holder.eventDate.setText(model.getDateOfEvent());            // used for getting the data in the database then displaying it on the card view
                holder.eventLocation.setText(model.getLocation());
                //Picasso is library used to retrive images from the databases and display it
                Picasso.get().load(model.getImage()).into(holder.imageView);

             holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {       // alert dialog appears once the card view is clicked
                    CharSequence options[] = new CharSequence[]
                            {
                                    "View Event",
                                    "Remove from favourites"
                            };
                    AlertDialog.Builder builder = new AlertDialog.Builder(Favourites.this);
                    builder.setTitle("Options");

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0){
                                Intent intent = new Intent(Favourites.this, EventDetail.class);  // brings you to the event details activity for that event clicked
                                intent.putExtra("eid", model.getEid());
                                startActivity(intent);
                            }
                            if(which ==1){
                                favouritesList.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Events").child(model.getEid()).removeValue()  // removes the event from the users favourite list
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(Favourites.this, "Removed from favourites", Toast.LENGTH_SHORT).show();
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
            public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourites_layout, parent, false);  // used to instantiate the layout XML file
                FavViewHolder holder = new FavViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}
