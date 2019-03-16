package com.example.ryanletmon.eventhitter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ryanletmon.eventhitter.Model.Events;
import com.example.ryanletmon.eventhitter.Prevalent.Prevalent;
import com.example.ryanletmon.eventhitter.ViewHolder.EventViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference EventsRef;        // creating database reference
    private RecyclerView recyclerView;  //accessing the recyclerView
    RecyclerView.LayoutManager layoutManager;

    //search Functionality
    FirebaseRecyclerAdapter<Events, EventViewHolder> searchAdapter;
    List<String> suggestList= new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        EventsRef = FirebaseDatabase.getInstance().getReference().child("Events");  // accessing the data in the database in events

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);

        //Displaying users profile name and image
        userNameView.setText(Prevalent.currentOnlineUser.getName());

        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);


        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(Prevalent.isConnectedToInternet(this))
            onStart();
        else{
            Toast.makeText(Home.this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Home.this, Login.class);
            startActivity(intent);
        }

//        // Search
//        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
//        materialSearchBar.setHint("Enter Event Name");
//        //materialSearchBar.setSpeechMode(false);
//        loadSuggest();
//        materialSearchBar.setLastSuggestions(suggestList);
//        materialSearchBar.setCardViewElevation(10);
//        materialSearchBar.addTextChangeListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                // When the user types the suggest list will change
//                List<String> suggest = new ArrayList<String>();
//                for(String search:suggestList){
//                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
//                        suggest.add(search);
//                }
//                materialSearchBar.setLastSuggestions(suggest);
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
//            @Override
//            public void onSearchStateChanged(boolean enabled) {
//                 // when search bar is closed, restore original adapter
////                if(!enabled)
////                    recyclerView.setAdapter();
//            }
//
//            @Override
//            public void onSearchConfirmed(CharSequence text) {
//                // when search is finished
//                startSearch(text);
//
//            }
//
//
//            @Override
//            public void onButtonClicked(int buttonCode) {
//
//            }
//        });
    }

//    private void startSearch(CharSequence text) {
//        searchAdapter = new FirebaseRecyclerAdapter<Events, EventViewHolder>(Events.class,R.layout.content_home, EventViewHolder.class, EventsRef.orderByChild("EventId").equalTo(eventId)) {
//            @Override
//            protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull Events model) {
//
//            }
//
//            @NonNull
//            @Override
//            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                return null;
//            }
//        };
//    }
//
//    private void loadSuggest(String eventId) {
//        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference().child("Events");
//
//        eventsRef.child(eventId).addValueEventListener(new ValueEventListener() {  // passing the event id
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
//                    Events item = postSnapshot.getValue(Events.class);
//                    suggestList.add(item.getTitle()); //adding event name to the suggest list
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

    @Override
    protected void onStart(){
        super.onStart();

        // A query to retrieve all the events in the database
        FirebaseRecyclerOptions<Events> options = new FirebaseRecyclerOptions.Builder<Events>().setQuery(EventsRef, Events.class).build();

        FirebaseRecyclerAdapter<Events, EventViewHolder> adapter =
                new FirebaseRecyclerAdapter<Events, EventViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull final Events model) {
                        // displaying the data from the database on the text fields
                        holder.eventTitle.setText(model.getTitle());
                        holder.eventDate.setText(model.getDateOfEvent());
                        holder.eventLocation.setText(model.getLocation());
                        //Picasso is library used to retrive images from the databases and display it
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Home.this, EventDetail.class);
                                intent.putExtra("eid", model.getEid());
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_layout, parent, false); // used to instantiate the layout XML file
                        EventViewHolder holder = new EventViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.cart) {
            Intent cartIntent = new Intent(Home.this, Cart.class);
            startActivity(cartIntent);
        } else if (id == R.id.favourites) {
            Intent favIntent = new Intent(Home.this, Favourites.class);
            startActivity(favIntent);

        } else if (id == R.id.categories) {
            Intent orderIntent = new Intent(Home.this, Orders.class);
            startActivity(orderIntent);

        } else if (id == R.id.search) {
            Intent intent = new Intent(Home.this, Search.class);
            startActivity(intent);

        } else if (id == R.id.home) {
            Intent homeIntent = new Intent(Home.this, Home.class);
            startActivity(homeIntent);

        } else if (id == R.id.settings) {
            Intent intent = new Intent(Home.this, Settings.class);
            startActivity(intent);

        } else if (id == R.id.logout) {
            Intent logoutIntent  = new Intent(Home.this, Login.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // clears any other existing tasks associated with the activity
            startActivity(logoutIntent);
            Paper.book().destroy(); // deletes users saved log in
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
