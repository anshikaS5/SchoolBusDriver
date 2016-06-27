package com.vmoksha.schoolbusdriver.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vmoksha.schoolbusdriver.fragment.DrawerFragment;
import com.vmoksha.schoolbusdriver.httprequest.HTTP_PUT;
import com.vmoksha.schoolbusdriver.httprequest.HttpPost;
import com.vmoksha.schoolbusdriver.model.Constant;
import com.vmoksha.schoolbusdriver.model.DropPointsModel;
import com.vmoksha.schoolbusdriver.model.MarkerModelClass;
import com.vmoksha.schoolbusdriver.R;
import com.vmoksha.schoolbusdriver.model.ModelClass;
import com.vmoksha.schoolbusdriver.model.PickupPointModel;
import com.vmoksha.schoolbusdriver.route.AbstractRouting;
import com.vmoksha.schoolbusdriver.route.Route;
import com.vmoksha.schoolbusdriver.route.Routing;
import com.vmoksha.schoolbusdriver.route.RoutingListener;
import com.vmoksha.schoolbusdriver.service.DashboardService;
import com.vmoksha.schoolbusdriver.service.GPSTrackerService;
import com.vmoksha.schoolbusdriver.util.Connectivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DashBoardActivity extends AppCompatActivity implements RoutingListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener {

    /* protected LatLng start2 = new LatLng(12.92318, 77.65050);
     protected LatLng end2= new LatLng(12.976664, 77.571256);*/
    protected GoogleMap map;
    protected LatLng start;
    protected LatLng end;
    private String LOG_TAG = "MyActivity";
    protected GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;
    private ArrayList<Polyline> polylines;
    private int[] colors = new int[]{R.color.route1, R.color.route2, R.color.route3, R.color.accent, R.color.primary_dark_material_light};
    ArrayList<MarkerModelClass> mList;

    double latitude;
    double longitude;
    private String android_id;

    /**
     * This activity loads a map and then displays the route and pushpins on it.
     */
    private Toolbar toolbar;
    private TextView mToolBar_Title, mBusName, mPickupNumbers, mChildrentoBoard;
    public static Button mStartButton;
    private ImageView mImageViewpointer;
    private RelativeLayout mLayoutDetails;
    DrawerLayout drawerLayout;
    DrawerFragment mDrawerFragment;
    public ImageView imgMenuDrawer;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ArrayList<DropPointsModel> dropPointsModelArrayList;
    String BusNumber;
    private Boolean isPickUp;
    String EntityCode, RouteCode;
    Integer TotalStudent;
    LocationManager locationManager;
    int PickupPointCount;
    public static int count;
    String Address;
    public static ArrayList<PickupPointModel> studentsArray;

    protected LatLng start1 = new LatLng(12.954647, 77.698599);
    protected LatLng end1 = new LatLng(12.923176, 77.650505);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(ModelClass.DriverLoginPreference, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_dash_board);
        BusNumber = sharedPreferences.getString(ModelClass.BusNumber, "").trim();
        Intent intent = getIntent();
        isPickUp = getIntent().getExtras().getBoolean("isPickUP");
        TotalStudent = sharedPreferences.getInt(ModelClass.Count, 0);
        initializeUI();
        sharedPreferences = getSharedPreferences(ModelClass.DriverLoginPreference, Context.MODE_PRIVATE);
        EntityCode = sharedPreferences.getString(ModelClass.EntityCode, "");
        RouteCode = sharedPreferences.getString(ModelClass.RouteCode, "");


        setSupportActionBar(toolbar);

        imgMenuDrawer = (ImageView) findViewById(R.id.menu_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerFragment = (DrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragement);
        mDrawerFragment.setUp(R.id.fragement, drawerLayout);


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mImageViewpointer.setVisibility(View.GONE);
        imgMenuDrawer.setOnClickListener(this);
        mStartButton.setOnClickListener(this);
        //TODO:taken lat and long value from drop points model class

        mList = new ArrayList<MarkerModelClass>();
        dropPointsModelArrayList = TransportActivity.mArrayList;

        //getting the lat and lang value for markup point
        if ((dropPointsModelArrayList != null) && (dropPointsModelArrayList.size() != 0)) {
            for (int i = 1; i < dropPointsModelArrayList.size() - 1; i++) {
                String name = dropPointsModelArrayList.get(i).getDropPointName();
                Double latValue = Double.parseDouble(dropPointsModelArrayList.get(i).getLatitude());
                Double longValue = Double.parseDouble(dropPointsModelArrayList.get(i).getLongitude());
                mList.add(new MarkerModelClass(latValue, longValue, name));
            }
            //for pick up point array at position is start point and for drop point array of 0th position will be end

            if (isPickUp) {
                //for pickup point start and end point
                Double latValue = Double.parseDouble(dropPointsModelArrayList.get(0).getLatitude());
                Double longValue = Double.parseDouble(dropPointsModelArrayList.get(0).getLongitude());
                start = new LatLng(latValue, longValue);

                Double deslatValue = Double.parseDouble(dropPointsModelArrayList.get(dropPointsModelArrayList.size() - 1).getLatitude());
                Double deslongValue = Double.parseDouble(dropPointsModelArrayList.get(dropPointsModelArrayList.size() - 1).getLongitude());
                end = new LatLng(deslatValue, deslongValue);
            } else {
                //for drop start and end point

                Double latValue = Double.parseDouble(dropPointsModelArrayList.get(0).getLatitude());
                Double longValue = Double.parseDouble(dropPointsModelArrayList.get(0).getLongitude());
                end = new LatLng(latValue, longValue);

                Double deslatValue = Double.parseDouble(dropPointsModelArrayList.get(dropPointsModelArrayList.size() - 1).getLatitude());
                Double deslongValue = Double.parseDouble(dropPointsModelArrayList.get(dropPointsModelArrayList.size() - 1).getLongitude());
                start = new LatLng(deslatValue, deslongValue);


            }

            mPickupNumbers.setText("" + dropPointsModelArrayList.size());
        } else {
            mPickupNumbers.setText("0");
        }


       /* String address = "http://maps.google.com/maps?daddr=" + "12.954647" + "," + " 77.698599" + "+to:" +"12.923176" + "," + "77.650505";
        Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent1);*/

        if (com.vmoksha.schoolbusdriver.util.Util.Operations.isOnline(this)) {
            routemap();
        } else {
            Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        polylines = new ArrayList<>();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        MapsInitializer.initialize(this);
        mGoogleApiClient.connect();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        map = mapFragment.getMap();



        /*
        * Updates the bounds being used by the auto complete adapter based on the position of the
        * map.
        * */
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {

                //  LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
                // mAdapter.setBounds(bounds);
            }
        });


        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(12.954647, 77.493334));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);

        map.moveCamera(center);
        map.animateCamera(zoom);

        // LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

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
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            //100,0
            @Override
            public void onLocationChanged(Location location) {
                android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                List<Address> listAdd = getGeocoderAddress(getBaseContext(), location);
                Log.i("", listAdd.toString());
                if (listAdd != null && listAdd.size() > 0) {
                    Address currentAddress = listAdd.get(0);

                    String strLoc = currentAddress.getThoroughfare();
                    String strSubLocality = currentAddress.getSubLocality();
                    String strPostalCode = currentAddress.getLocality();
                    String strState = currentAddress.getAdminArea();
                    String strCountry = currentAddress.getCountryCode();

                    Address = strLoc + "," + strSubLocality + "," + strPostalCode + "," + strState + "," + strCountry;

                }
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
        });

       /* locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        //getting current location lat and long


                       *//* CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

                        map.moveCamera(center);
                        map.animateCamera(zoom);*//*
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

                        map.moveCamera(center);
                        map.animateCamera(zoom);

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });*/
    }


    private void initializeUI() {
        mStartButton = (Button) findViewById(R.id.startbutton);
        mChildrentoBoard = (TextView) findViewById(R.id.total_childern_boarding_number);
        mPickupNumbers = (TextView) findViewById(R.id.total_pickup_point_numbers);
        mBusName = (TextView) findViewById(R.id.busname);
        mImageViewpointer = (ImageView) findViewById(R.id.pickup_crownImage);
        mLayoutDetails = (RelativeLayout) findViewById(R.id.detailsRelative);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolBar_Title = (TextView) findViewById(R.id.toolbar_title);
        mToolBar_Title.setText("DashBoard");
        mBusName.setText(BusNumber);
        mChildrentoBoard.setText("" + TotalStudent);


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



    public void routemap() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Fetching route information.", true);

       /* Double latValue = Double.parseDouble(dropPointsModelArrayList.get(0).getLatitude());
        Double longValue = Double.parseDouble(dropPointsModelArrayList.get(0).getLongitude());
        LatLng start = new LatLng(latValue, longValue);

        Double latValue1 = Double.parseDouble(dropPointsModelArrayList.get(1).getLatitude());
        Double longValue1 = Double.parseDouble(dropPointsModelArrayList.get(1).getLongitude());
       LatLng start1 = new LatLng(latValue1, longValue1);

        Double latValue2 = Double.parseDouble(dropPointsModelArrayList.get(2).getLatitude());
        Double longValue2= Double.parseDouble(dropPointsModelArrayList.get(2).getLongitude());
       LatLng start2 = new LatLng(latValue2, longValue2);

        Double latValue3 = Double.parseDouble(dropPointsModelArrayList.get(3).getLatitude());
        Double longValue3 = Double.parseDouble(dropPointsModelArrayList.get(3).getLongitude());
        LatLng start3 = new LatLng(latValue3, longValue3);

        Double latValue4 = Double.parseDouble(dropPointsModelArrayList.get(4).getLatitude());
        Double longValue4 = Double.parseDouble(dropPointsModelArrayList.get(4).getLongitude());
        LatLng start4 = new LatLng(latValue4, longValue4);

//        int routecounter=0;

        Double latValue5 = Double.parseDouble(dropPointsModelArrayList.get(5).getLatitude());
        Double longValue5 = Double.parseDouble(dropPointsModelArrayList.get(5).getLongitude());
        start = new LatLng(latValue5, longValue5);

        Double latValue6 = Double.parseDouble(dropPointsModelArrayList.get(6).getLatitude());
        Double longValue6 = Double.parseDouble(dropPointsModelArrayList.get(6).getLongitude());
        LatLng start6= new LatLng(latValue6, longValue6);


            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(start, start1)
                    .build();
            routing.execute();
        Routing routing1= new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(start1, start2)
                .build();
        routing1.execute();*/

        Routing routing2= new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(start,end)
                .build();
        routing2.execute();

        /*Routing routing3= new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(start3, start4)
                .build();
        routing3.execute();*/






    }
    /*.waypoints(start, end)*/

    @Override
    public void onRoutingFailure() {
        // The Routing request failed
        progressDialog.dismiss();
        Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        progressDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        map.moveCamera(center);


        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % colors.length;

            PolylineOptions polyOptions = new PolylineOptions();
            /*polyOptions.color(getResources().getColor(colors[colorIndex]));*/
            polyOptions.color(getResources().getColor(R.color.marker_line));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

        // Start marker

        ArrayList<MarkerOptions> optionPoint = new ArrayList<MarkerOptions>();
        MarkerOptions options = new MarkerOptions();

        if(isPickUp) {
            options.position(start).title(dropPointsModelArrayList.get(0).getDropPointName());
            // Read your drawable from somewhere
            Drawable dr = getResources().getDrawable(R.drawable.map_source_point);
            Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
// Scale it to 50 x 50
            Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
// Set your new, scaled drawable "d"

            d.setBounds(0, 0, 50, 50);

            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_source_point1));
            map.addMarker(options);

            // End marker
            options = new MarkerOptions();

            options.position(end).title(dropPointsModelArrayList.get(dropPointsModelArrayList.size() - 1).getDropPointName());

            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_destination_point1));
            map.addMarker(options);
            //pickup drop point marker
        }else{

            options.position(end).title(dropPointsModelArrayList.get(0).getDropPointName());
            // Read your drawable from somewhere
            Drawable dr = getResources().getDrawable(R.drawable.map_source_point);
            Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
// Scale it to 50 x 50
            Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
// Set your new, scaled drawable "d"

            d.setBounds(0, 0, 50, 50);

            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_source_point1));
            map.addMarker(options);

            // End marker
            options = new MarkerOptions();

            options.position(start).title(dropPointsModelArrayList.get(dropPointsModelArrayList.size() - 1).getDropPointName());

            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_destination_point1));
            map.addMarker(options);
        }
        options = new MarkerOptions();
        for (int i = 0; i < mList.size(); i++) {
            options.position(new LatLng(mList.get(i).getLatValue(), mList.get(i).getLongValue())).title(mList.get(i).getTitle());
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pickup_points1));
            map.addMarker(options);
        }
        center = CameraUpdateFactory.newLatLng(new LatLng(mList.get(mList.size() - 1).getLatValue(), mList.get(mList.size() - 1).getLongValue()));
        zoom = CameraUpdateFactory.zoomTo(12);

        map.moveCamera(center);
        map.animateCamera(zoom);
    }

    @Override
    public void onRoutingCancelled() {
        Log.i(LOG_TAG, "Routing was cancelled.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.v(LOG_TAG, connectionResult.toString());
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.startbutton:
                //ImageL
                //call the service

                startService();
                Intent mServiceintent = new Intent(DashBoardActivity.this,
                        DashboardService.class);
                startService(mServiceintent);
                // GPSTrackerService gpsTrackerService = new GPSTrackerService(DashBoardActivity.this);

               /* mLayoutDetails.setVisibility(View.GONE);
                mImageViewpointer.setVisibility(View.VISIBLE);
                mStartButton.setText("Pick Up Points");*/

                if (mStartButton.getText().toString().contentEquals(getResources().getString(R.string.start_button))) {
                    mLayoutDetails.setVisibility(View.GONE);
                    mImageViewpointer.setVisibility(View.VISIBLE);
                    mStartButton.setText(getResources().getString(R.string.pickup_point_button));

                } else if (mStartButton.getText().toString().contentEquals(getResources().getString(R.string.pickup_point_button))) {

                    listOfStudents();

                   /* Intent pickupPoint = new Intent(DashBoardActivity.this, PickupPoint.class);
                    startActivity(pickupPoint);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);*/


                  /* overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);*/
                } else {
                    finish();
                    stopService(new Intent(DashBoardActivity.this,
                            DashboardService.class));
                   /* finish();*/

                    //send the lat and long for stop the service
                    stopService();

                    Toast.makeText(DashBoardActivity.this, "stop", Toast.LENGTH_SHORT).show();

                }

                /*Intent intent = new Intent(DashBoardActivity.this,PickupPoint.class);
                startActivity(intent);*/
                break;

            case R.id.menu_drawer:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;


        }
    }

    private void listOfStudents() {

        try {
            final JSONObject jsonObject = new JSONObject();
            /*jsonObject.put("RoleCode", Constant.STUDENT_ROLE);*/
            /*editor.putInt(ModelClass.PickupPointCount, 0);*/
           /* for(int i=0;i<dropPointsModelArrayList.size();i++){*/
            if (!sharedPreferences.contains(ModelClass.PickupPointCount))
                sharedPreferences.edit().putInt(ModelClass.PickupPointCount, 0);

            count = sharedPreferences.getInt(ModelClass.PickupPointCount, 0);

            String mDropPoints = dropPointsModelArrayList.get(count).getDropPointCode();
            jsonObject.put("DropPointCode", mDropPoints);
           /* }*/

            jsonObject.put(ModelClass.EntityCode, EntityCode);
           /* jsonObject.put("DropPointCode",mDropPoints);*/
            HttpPost http_post = new HttpPost() {
                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    if (s != null && !s.isEmpty()) {
                        try {
                            JSONObject students_detailsObject = new JSONObject(s);
                            String Success = students_detailsObject.getString("Success");

                            if (students_detailsObject.getBoolean("Success")) {
                                studentsArray = new ArrayList<PickupPointModel>();
                                JSONArray student_Array_obj = students_detailsObject.getJSONArray("ViewModels");

                              /* if( student_Array_obj.length() != 0 && student_Array_obj != null){

                               }else{


                               }*/

                                if (student_Array_obj.length() > 0 && student_Array_obj != null) {
                                    for (int i = 0; i < student_Array_obj.length(); i++) {
                                        PickupPointModel students_detailsModel = new PickupPointModel();
                                        JSONObject parse_array_obj = student_Array_obj.getJSONObject(i);
                                        students_detailsModel.setCode(parse_array_obj.getString("Code"));
                                        students_detailsModel.setRegistrationId(parse_array_obj.getString("RegistrationId"));
                                        students_detailsModel.setFirstName(parse_array_obj.getString("FirstName"));
                                        students_detailsModel.setLastName(parse_array_obj.getString("LastName"));
                                        students_detailsModel.setDropPointCode(parse_array_obj.getString("DropPointCode"));
                                       /* String mobileno=(parse_array_obj.getString("Mobile"));
                                        if(mobileno!=null &&!mobileno.isEmpty()){
                                            students_detailsModel.setMobile(Integer.parseInt(mobileno));

                                        }
                                        else{
                                            students_detailsModel.setMobile(0);
                                           *//* students_detailsModel.setMobile(Integer.parseInt(mobileno));*//*
                                        }*/
                                        //students_detailsModel.setMobile(Integer.parseInt(parse_array_obj.getString("Mobile")));
                                        students_detailsModel.setStatus(parse_array_obj.getString("Status"));
                                        students_detailsModel.setUserName(parse_array_obj.getString("UserName"));
                                        String Json = parse_array_obj.getString("JSON");
                                        JSONObject jsonObject1 = new JSONObject(Json);
                                        students_detailsModel.setStudentClass(jsonObject1.getString("ClassName"));
                                        students_detailsModel.setAddress(jsonObject1.getString("Address"));
                                        students_detailsModel.setParentCode(jsonObject1.getString("ParentCode"));

                                        String ImageUrl = parse_array_obj.getString("ImageJSON");


                                        if (!parse_array_obj.getString("ImageJSON").contentEquals("null") && !parse_array_obj.getString("ImageJSON").contentEquals("")) {
                                            JSONObject imageObject = new JSONObject(ImageUrl);
                                            students_detailsModel.setStudentpic(imageObject.getString("AWSFileUrl"));
                                        } else {
                                            students_detailsModel.setStudentpic("null");
                                        }

                                       /* if(ImageUrl!=null && ImageUrl.contentEquals("null")){
                                            students_detailsModel.setStudentpic("null");

                                        }else{
                                            JSONObject imageObject=new JSONObject(ImageUrl);
                                            students_detailsModel.setStudentpic(jsonObject1.getString("AWSFileUrl"));
                                        }*/
                                        /*JSONObject imageObject=new JSONObject(ImageUrl);
                                        if(imageObject!=null) {
                                            students_detailsModel.setStudentpic(jsonObject1.getString("AWSFileUrl"));
                                        }else{
                                            students_detailsModel.setStudentpic("null");
                                        }*/

                                        students_detailsModel.setIsBoarding(parse_array_obj.getBoolean("IsBoarding"));
                                        studentsArray.add(students_detailsModel);
                                    }
                                    Intent pickupPoint = new Intent(DashBoardActivity.this, PickupPoint.class);
                                    startActivity(pickupPoint);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                                } else {

                                    Toast.makeText(getApplicationContext(), "Array size is null" /*resultJsonObject.getString("Message")*/, Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                Toast.makeText(getApplicationContext(), "" /*resultJsonObject.getString("Message")*/, Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            Log.i("error message", e.getMessage());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), " " + getResources().getString(R.string.login_server_error), Toast.LENGTH_SHORT).show();
                    }
                }
            };
            http_post.execute(ModelClass.SEARCH_STUDENTS_API, jsonObject.toString());


        } catch (Exception e) {

        }
    }

    private void stopService() {

        if (Connectivity.isConnected(getApplicationContext())) {

            try {

                JSONObject serviceObject = new JSONObject();
                serviceObject.put("Name", Address);
                serviceObject.put("BusCode", EntityCode);
                serviceObject.put("RouteCode", RouteCode);
                serviceObject.put("DriverCode", Constant.DRIVER_ROLE);
                serviceObject.put("CurrentLatitude", latitude);
                serviceObject.put("CurrentLongitude", longitude);
                //serviceObject.put("TravelStartTime",getCurrentTime() );
                serviceObject.put("TravelEndTime", getCurrentTime());
                serviceObject.put("TravelStatusCode", Constant.JOURNEY_COMPLETED);
                serviceObject.put("Status", "1");
                serviceObject.put("UserID", "1");
                serviceObject.put("MethodType", "POST");
                serviceObject.put("TravelDate", getCurrentDate());
                serviceObject.put("DeviceCode", android_id);


                HTTP_PUT http_put = new HTTP_PUT() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();


                    }

                    // get login response from rest API
                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);


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
                                        /*Log.e(TAG, "Login"+e.getMessage().toString());
                                        Toast.makeText(getActivity(), "Login Failed, Please try again.", Toast.LENGTH_SHORT).show();*/
                            }
                        } else {
                                   /* Toast.makeText(getActivity(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show()*/
                            ;
                        }
                    }
                };
                http_put.execute(ModelClass.USER_CURRENT_TRAVEL_INFO, serviceObject.toString());
            } catch (Exception e) {
                        /*Log.e(TAG, "Login"+e.getMessage().toString());*/
            }
        } else {
                    /*Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();*/
        }

    }

    //call the method to send the lat/long details in cloud
    private void startService(){

        if (Connectivity.isConnected(getApplicationContext())) {

            try {

                JSONObject serviceObject = new JSONObject();
                serviceObject.put("Name", Address);
                serviceObject.put("BusCode", EntityCode);
                serviceObject.put("RouteCode", RouteCode);
                serviceObject.put("DriverCode", Constant.DRIVER_ROLE);
                serviceObject.put("CurrentLatitude", latitude);
                serviceObject.put("CurrentLongitude", longitude);
                //serviceObject.put("TravelStartTime",getCurrentTime() );
                serviceObject.put("TravelEndTime", getCurrentTime());
                serviceObject.put("TravelStatusCode", Constant.JOURNEY_COMPLETED);
                serviceObject.put("Status", "1");
                serviceObject.put("UserID", "1");
                serviceObject.put("MethodType", "POST");
                serviceObject.put("TravelDate", getCurrentDate());
                serviceObject.put("DeviceCode", android_id);


                HttpPost httpPost = new HttpPost() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();


                    }

                    // get login response from rest API
                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);


                        if (result != null) {
                            try {

                                JSONObject response = new JSONObject(result);
                                String success = response.getString("Success");
                                if (success.contentEquals("true")) {
                                    JSONObject Message = response.getJSONObject("Message");


                                } else {

                                }
                            } catch (Exception e) {

                            }
                        } else {

                        }
                    }
                };
                httpPost.execute(ModelClass.USER_CURRENT_TRAVEL_INFO, serviceObject.toString());
            } catch (Exception e) {
                        /*Log.e(TAG, "Login"+e.getMessage().toString());*/
            }
        } else {
                    /*Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();*/
        }



    }


    private String getCurrentDate() {
        String dateInString = null;


        try {

            java.util.Calendar cal = java.util.Calendar.getInstance();
            dateInString = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());


        } catch (Exception e) {

        }
        return dateInString;


    }

    private String getCurrentTime() {
        String timeInString = null;


        try {

            java.util.Calendar cal = java.util.Calendar.getInstance();

            timeInString = new java.text.SimpleDateFormat(" HH:mm").format(cal.getTime());


        } catch (Exception e) {

        }
        return timeInString;


    }
}
