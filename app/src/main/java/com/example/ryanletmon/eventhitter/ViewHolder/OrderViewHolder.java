package com.example.ryanletmon.eventhitter.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ryanletmon.eventhitter.Interface.EventListner;
import com.example.ryanletmon.eventhitter.R;

//access orders list layout and all the events
public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txt_user_name, txt_user_price, txt_user_count;
    public ImageView img_user_count1;

    private EventListner itemClickListener;

    public void setTxt_user_name(TextView txt_user_name) {
        this.txt_user_name = txt_user_name;
    }

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_user_name = (TextView)itemView.findViewById(R.id.order_user_name);
        txt_user_price = (TextView)itemView.findViewById(R.id.order_user_price);
        txt_user_count = (TextView)itemView.findViewById(R.id.order_user_count);
        img_user_count1 = (ImageView)itemView.findViewById(R.id.order_user_count2);

    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);

    }

    public void setItemClickListener(EventListner itemClickListener){
        this.itemClickListener = itemClickListener;
    }
}
