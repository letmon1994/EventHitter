package com.example.ryanletmon.eventhitter.Prevalent;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.ryanletmon.eventhitter.Model.Users;

public class Prevalent {
    public static Users currentOnlineUser;  // gets the current logged in user

    public static final String UserPhoneKey = "UserPhone";       // contains all the data of the user for the remember me check box
    public static final String UserPasswordKey = "UserPassword";

    public static boolean isConnectedToInternet(Context context){   // method to see if the user is connected to the internet
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for(int i=0; i<info.length; i++){
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}
