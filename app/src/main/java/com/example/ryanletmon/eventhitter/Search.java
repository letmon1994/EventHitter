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
import android.widget.Button;
import android.widget.EditText;

import com.example.ryanletmon.eventhitter.Model.Events;
import com.example.ryanletmon.eventhitter.ViewHolder.EventViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Search extends AppCompatActivity {

    // Variables
    private Button search;
    private EditText inputText;
    private String searchInput;
    private RecyclerView listSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // init
        inputText = findViewById(R.id.searchName);
        search = findViewById(R.id.searchButton);
        listSearch = findViewById(R.id.searchList);
        listSearch.setLayoutManager(new LinearLayoutManager(Search.this));


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchInput = inputText.getText().toString();
                onStart();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Events"); // used for reading and writing data in the database under Events

        // Sets a query to get the title from whats in the event class
        FirebaseRecyclerOptions<Events> options = new FirebaseRecyclerOptions.Builder<Events>().setQuery(reference.orderByChild("title") .startAt(searchInput), Events.class).build();

        FirebaseRecyclerAdapter<Events, EventViewHolder> searchAdapter = new FirebaseRecyclerAdapter<Events, EventViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull final Events model) {
                holder.eventTitle.setText(model.getTitle());
                holder.eventDate.setText(model.getDateOfEvent());    // used for getting the data in the database then displaying it on the card view
                holder.eventLocation.setText(model.getLocation());
                //Picasso is library used to retrive images from the databases and display it
                Picasso.get().load(model.getImage()).into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Search.this, EventDetail.class);
                        intent.putExtra("eid", model.getEid());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_layout, parent, false);  // used to instantiate the layout XML file
                EventViewHolder holder = new EventViewHolder(view);
                return holder;
            }
        };

        listSearch.setAdapter(searchAdapter);
        searchAdapter.startListening();

    }
}
