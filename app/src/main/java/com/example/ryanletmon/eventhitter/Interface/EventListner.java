package com.example.ryanletmon.eventhitter.Interface;

import android.view.View;

// holds static constants and abstract methods
public interface EventListner {
    void onClick(View view, int position, boolean isLongClick);
}
