package com.example.ryanletmon.eventhitter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import io.paperdb.Paper;

public class AdminCategory extends AppCompatActivity {

    // variables
    private ImageView nightclub;
    private ImageView musicFestival;

    private TextView logout;
    private TextView viewOrders;

    private Button checkOdersBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        //Initializing
        logout = (TextView) findViewById(R.id.adminLogout);
        viewOrders = (TextView) findViewById(R.id.adminViewOrders);

        nightclub = (ImageView) findViewById(R.id.nightclub);
        musicFestival = (ImageView) findViewById(R.id.musicFestival);

        // what happens when the nightclub picture is clicked
        nightclub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this, AdminAddNewEvent.class);
                intent.putExtra("category", "nightclub");
                startActivity(intent);

            }
        });

        musicFestival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this, AdminAddNewEvent.class);
                intent.putExtra("category", "musicFestival");
                startActivity(intent);

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logoutIntent  = new Intent(AdminCategory.this, Login.class);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);   // clears any other existing tasks associated with the activity
                startActivity(logoutIntent);
                Paper.book().destroy(); // deletes admins saved log in
                finish();
            }
        });

        viewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewOrdersIntent  = new Intent(AdminCategory.this, AdminOrders.class);
                startActivity(viewOrdersIntent);
            }
        });

    }
}
