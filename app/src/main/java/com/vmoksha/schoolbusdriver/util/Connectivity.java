package com.vmoksha.schoolbusdriver.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by anshikas on 22-01-2016.
 */
public class Connectivity {
    /**
     * Checking Internet connection
     *
     * @param Context
     * @return boolean true,it is connected to Internet else return false
     */
    @SuppressWarnings("deprecation")
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        // checking are we connected to the internet
        if (connectivityManager != null) {
            // checking for WI-FI connection
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            // checking for mobile data connection
            if (!networkInfo.isConnected()) {
                networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            }
        }
        return networkInfo == null ? false : networkInfo.isConnected();
    }

}
