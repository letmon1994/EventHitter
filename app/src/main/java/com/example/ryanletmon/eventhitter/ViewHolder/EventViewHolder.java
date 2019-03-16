package com.example.ryanletmon.eventhitter.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ryanletmon.eventhitter.Interface.EventListner;
import com.example.ryanletmon.eventhitter.R;

//access event list layout and all the events
public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView eventTitle, eventDate, eventLocation, eventImage;
    public ImageView imageView;
    public EventListner listner;

    public EventViewHolder(View itemView)
    {
        super(itemView);


        imageView = (ImageView) itemView.findViewById(R.id.event_image);
        eventTitle = (TextView) itemView.findViewById(R.id.event_title);
        eventDate = (TextView) itemView.findViewById(R.id.event_date);
        eventLocation = (TextView) itemView.findViewById(R.id.event_location);
//        eventImage = (TextView) itemView.findViewById(R.id.event_image2);
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
