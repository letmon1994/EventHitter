package com.example.ryanletmon.eventhitter.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ryanletmon.eventhitter.Interface.EventListner;
import com.example.ryanletmon.eventhitter.R;

//access cart list layout and all the events
public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txt_cart_name, txt_price, img_cart_count;
    public ImageView img_cart_count1;

    private EventListner itemClickListener;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_cart_name = (TextView)itemView.findViewById(R.id.cart_item_name);
        txt_price = (TextView)itemView.findViewById(R.id.cart_item_price);
        img_cart_count = (TextView)itemView.findViewById(R.id.cart_item_count);
        img_cart_count1 = (ImageView)itemView.findViewById(R.id.cart_item_count1);

    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);

    }

    public void setItemClickListener(EventListner itemClickListener){
        this.itemClickListener = itemClickListener;
    }
}