package com.example.ryanletmon.eventhitter.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ryanletmon.eventhitter.Interface.EventListner;
import com.example.ryanletmon.eventhitter.R;

//access favourites list layout and all the events
public class FavViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView eventTitle, eventDate, eventLocation;
    public ImageView imageView;
    public EventListner listner;

    public FavViewHolder(View itemView)
    {
        super(itemView);


        imageView = (ImageView) itemView.findViewById(R.id.fav_event_image);
        eventTitle = (TextView) itemView.findViewById(R.id.fav_event_title);
        eventDate = (TextView) itemView.findViewById(R.id.fav_event_date);
        eventLocation = (TextView) itemView.findViewById(R.id.fav_event_location);
    }

    public void setEventListner(EventListner listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view)
    {
        listner.onClick(view, getAdapterPosition(), false);
    }
}

