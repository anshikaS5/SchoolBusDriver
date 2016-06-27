package com.vmoksha.schoolbusdriver.service;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vmoksha.schoolbusdriver.R;
import com.vmoksha.schoolbusdriver.activity.DashBoardActivityNew;
import com.vmoksha.schoolbusdriver.httprequest.HTTP_PUT;
import com.vmoksha.schoolbusdriver.model.ModelClass;
import com.vmoksha.schoolbusdriver.util.Connectivity;
import com.vmoksha.schoolbusdriver.util.ProgressHUD;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardService extends Service implements LocationListener, DialogInterface.OnCancelListener {

    private static final int TODO =0;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    String lat;
    String provider;
    /*protected String latitude,longitude;*/
    double latitude;
    double longitude;
    protected boolean gps_enabled, network_enabled;
    private String android_id;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    String EntityCode, RouteCode, CompanyCode;
    Marker mPositionMarker;
    private ProgressHUD progressHUD;
//    GoogleMap map;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences(ModelClass.DriverLoginPreference, Context.MODE_PRIVATE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return TODO;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, this);

        EntityCode = sharedPreferences.getString(ModelClass.EntityCode, "");
        // RouteCode=sharedPreferences.getString(ModelClass.RouteCode, "");
        CompanyCode = sharedPreferences.getString(ModelClass.CompanyCode, "");
       /* Intent intent1 =((AppCompatActivity) context).getIntent();*/
        RouteCode = (String) intent.getExtras().get("RouteCode");
       /* String RouteCode = ((AppCompatActivity) context).getIntent().getExtras().getString("RouteCode");*/
        Log.e("onStartCommand", RouteCode);
        Log.e("onStartCommand", "Service started");
        return super.onStartCommand(intent, flags, startId);

    }

    @TargetApi(Build.VERSION_CODES.DONUT)
    @Override
    public void onLocationChanged(Location location) {

        //added marker to show the current location of driver

               /* mPositionMarker = DashBoardActivityNew.map.addMarker(new MarkerOptions()
                        .flat(true)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.inbetween_stops_pin))
                        .position(
                                new LatLng(location.getLatitude(), location
                                        .getLongitude())).snippet("SNIPET"));*/


        android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        List<Address> listAdd = getGeocoderAddress(getBaseContext(), location);
        //   Log.i("",listAdd.toString());
        if (listAdd != null && listAdd.size() > 0) {
            Address currentAddress = listAdd.get(0);

            String strLoc = currentAddress.getThoroughfare();
            String strSubLocality = currentAddress.getSubLocality();
            String strPostalCode = currentAddress.getLocality();
            String strState = currentAddress.getAdminArea();
            String strCountry = currentAddress.getCountryCode();

            final String Address = strLoc + "," + strSubLocality + "," + strPostalCode + "," + strState + "," + strCountry;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Log.e("Handler", "Calling Thread");
                    ThreadDemo td = new ThreadDemo(latitude, longitude, Address);
                    td.start();
                }
            }, 15000);

        }

    }

    public List<Address> getGeocoderAddress(Context context, Location location) {
        if (location != null) {
            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
                return addresses;
            } catch (IOException e) {
                // e.printStackTrace();
                Log.e("Error : Geocoder", "Impossible to connect to Geocoder",
                        e);
            }
        }

        return null;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    private class ThreadDemo extends Thread {
        Double longitude;
        Double latitude;
        String Address;

        public ThreadDemo(Double longitude, Double latitude, String Address) {
            this.longitude = longitude;
            this.latitude = latitude;
            this.Address = Address;
        }

        @TargetApi(Build.VERSION_CODES.CUPCAKE)
        @Override
        public void run() {
            super.run();
            try {
                mPositionMarker = DashBoardActivityNew.map.addMarker(new MarkerOptions()
                        .flat(true)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.inbetween_stops_pin))
                        .position(
                                new LatLng(latitude, longitude)).snippet("SNIPET"));
                getCurrentDate();
                // JSONObject
                Log.e("ThreadDemo", "Thread called");

                if (Connectivity.isConnected(getApplicationContext())) {

                    try {
                        String TravelInfoCode = sharedPreferences.getString(ModelClass.CurrentTravelInfoCode, "");
                        JSONObject serviceObject = new JSONObject();
                        serviceObject.put("LastLocation", Address);

                        serviceObject.put("Code", TravelInfoCode);
                        /*serviceObject.put("BusCode", EntityCode);
                        serviceObject.put("RouteCode",RouteCode);
                        serviceObject.put("DriverCode", Constant.DRIVER_ROLE);*/
                        serviceObject.put("CurrentLatitude", latitude);
                        serviceObject.put("CurrentLongitude", longitude);
                        serviceObject.put(ModelClass.CompanyCode, CompanyCode);
                        /*serviceObject.put("TravelStartTime",getCurrentTime() );*/
                        //serviceObject.put("TravelEndTime",getCurrentTime());
                       /* serviceObject.put("TravelStatusCode",Constant.JOURNEY_IN_PROGRESS_CODE );*/
                        //serviceObject.put("Status","1" );
                        serviceObject.put("UserID", "1");
                        serviceObject.put("MethodType", "POST");
                       /* serviceObject.put("TravelDate",getCurrentDate());
                        serviceObject.put("DeviceCode",android_id );*/


                        HTTP_PUT http_put = new HTTP_PUT() {

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                if (progressHUD == null || !progressHUD.isShowing())

                                    progressHUD = ProgressHUD.show(DashboardService.this, "", true, true, DashboardService.this);

                            }

                            // get login response from rest API
                            @Override
                            protected void onPostExecute(String result) {
                                super.onPostExecute(result);
                            if (progressHUD.isShowing()) {
                                progressHUD.dismiss();
                            }

                            if (result != null) {
                                    try {
                                        JSONObject response = new JSONObject(result);
                                        String success = response.getString("Success");
                                        if (success.contentEquals("true")) {
                                            JSONObject Message = response.getJSONObject("Message");

                                        } else {
                                           /* Toast.makeText((), "Login Failed! Please check your Username and Password", Toast.LENGTH_SHORT)
                                                    .show();*/
                                        }
                                    } catch (Exception e) {
                                        Log.e("ThreadDemo onPostExecute", e.getMessage().toString());
                                    }
                                } else {
                                   /* Toast.makeText(getActivity(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show()*/
                                    ;
                                }
                            }
                        };
                        http_put.execute(ModelClass.USER_CURRENT_TRAVEL_INFO_UPDATE, serviceObject.toString());
                    } catch (Exception e) {
                        Log.e("ThreadDemo", e.getMessage().toString());
                    }
                } else {
                    /*Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();*/
                }


            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    private String getCurrentDate() {
        String dateInString = null;
        try {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            dateInString = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
        } catch (Exception e) {
            Log.e("getCurrentDate", e.getMessage().toString());
        }
        return dateInString;
    }

    /*private String getCurrentTime() {
        String timeInString=null;
        try {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            timeInString=new java.text.SimpleDateFormat(" HH:mm").format(cal.getTime());

        } catch (Exception e) {
            Log.e("getCurrentTime", e.getMessage().toString());
        }
        return timeInString;
    }*/
}

