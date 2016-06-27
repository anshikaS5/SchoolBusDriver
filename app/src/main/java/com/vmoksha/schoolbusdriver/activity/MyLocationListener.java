package com.vmoksha.schoolbusdriver.activity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.LocationListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by anshikas on 05-02-2016.
 */
public class MyLocationListener implements LocationListener {
    @Override
    public void onLocationChanged(Location loc) {





        String longitude = "Longitude: " + loc.getLongitude();

        String latitude = "Latitude: " + loc.getLatitude();


        /*------- To get city name from coordinates -------- */
        String cityName = null;
       /* Geocoder gcd = new Geocoder(this, Locale.getDefault());*/
       /* Geocoder gcd=new Geocoder();
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                + cityName;*/

    }

  /* @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}*/
}


