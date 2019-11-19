package com.example.cityfit;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class City {

    public static String getAddress(Context context, double LATITUDE, double LONGITUDE) {

//Set Address

            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            String city="";
            try {
                List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
                if (addresses != null && addresses.size() > 0) {
                    city = addresses.get(0).getLocality();
                    Log.d("CITY", "getAddress:  city" + city);
                }
            }

            catch (IOException e){
                Log.e("CITY", e.getLocalizedMessage());
            }


             return city;
            }


    }

