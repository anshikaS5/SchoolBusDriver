package com.vmoksha.schoolbusdriver.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vmoksha.schoolbusdriver.R;
import com.vmoksha.schoolbusdriver.adapters.PickupPointAdapter;
import com.vmoksha.schoolbusdriver.fragment.DrawerFragment;
import com.vmoksha.schoolbusdriver.httprequest.HTTP_PUT;
import com.vmoksha.schoolbusdriver.httprequest.HttpPost;
import com.vmoksha.schoolbusdriver.model.Constant;
import com.vmoksha.schoolbusdriver.model.DropPointsModel;
import com.vmoksha.schoolbusdriver.model.MarkerModelClass;
import com.vmoksha.schoolbusdriver.model.ModelClass;
import com.vmoksha.schoolbusdriver.model.PickupPointModel;
import com.vmoksha.schoolbusdriver.util.Connectivity;
import com.vmoksha.schoolbusdriver.util.ProgressHUD;
import com.vmoksha.schoolbusdriver.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class DashBoardActivityNew extends AppCompatActivity implements LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener, DialogInterface.OnCancelListener {
    public static GoogleMap map;
    //  protected GoogleMap map;

    protected LatLng start;
    protected LatLng end;
    protected LatLng distancelatlong;
    protected LatLng currentLatLong;
    protected float distance = 0;
    private String LOG_TAG = "MyActivity";
    protected GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;
    private ArrayList<Polyline> polylines;
    private int[] colors = new int[]{R.color.route1, R.color.route2, R.color.route3, R.color.accent, R.color.primary_dark_material_light};
    ArrayList<MarkerModelClass> mList;

    private double latitude;
    private double longitude;
    private String android_id;
    Marker mPositionMarker;
    Handler h = new Handler();
    ThreadDemo td = null;
    /*  AlertDialog alert;*/
    AlertDialog alert;
    AlertDialog.Builder builder;
    Dialog mStartDialog, mstopApplicationDialog, onBackpressDialog;
    boolean isStart = false;
    private double latValue;
    private double longValue;
    private double meterdistance;
   public static Boolean canStartJourney;
    boolean permissiongranted=false;

    /**
     * This activity loads a map and then displays the route and pushpins on it.
     */
    private Toolbar toolbar;
    private TextView mToolBar_Title, mBusName, mPickupNumbers,
            mChildrentoBoard, mChild_board_text, mTotal_pickup_text, mDialogTitle, mstopDialogtitle, mOnbackDialogtitle;
    public static Button mStartButton, mShowCurrentLocation, mDialog_cancel, mDialog_Start, mStopDialog_yesButton, mStopDialog_NoButton, mOnbackpress_yes, mOnbackpress_no;
    private ImageView mImageViewpointer;
    private RelativeLayout mLayoutDetails, mStudent_listLayout, mBackToMapLayout;
    ListView listview;
    LinearLayout mSelectChildrenButton, mPickupNotifyNextButton, mPickuppointButon, mPickup_point_notifynext;
    DrawerLayout drawerLayout;
    DrawerFragment mDrawerFragment;
    public ImageView imgMenuDrawer;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ArrayList<DropPointsModel> dropPointsModelArrayList;
    String BusNumber;
    private Boolean isPickUp;
    private String JourneyType;
    String EntityCode, RouteCode;
    Integer TotalStudent;
    LocationManager locationManager;
    int PickupPointCount;
    public int count_for_pickup;

    private int countvalue = 0;
    public int count_for_drop;
    String Address;
    public static ArrayList<PickupPointModel> studentsArray;
    private ArrayList<PickupPointModel> pickupPointArrayList;
    PickupPointAdapter mpickup_point_listadapter;
    private ArrayList<PickupPointModel> modelArrayList = new ArrayList<PickupPointModel>();
    public static boolean buttonClicked = false; //select children button
    String CompanyCode;
    private ProgressHUD progressHUD;
    int clickCount;
    Location location;
    boolean isGPSEnabled, isNetworkEnabled;
    public static boolean clickMapButton = false;
    String valid_until = "1/1/1990";
    private int catalog_outdated;
    private Boolean isNotifyClicked = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(ModelClass.DriverLoginPreference, Context.MODE_PRIVATE);
       /* editor = sharedPreferences.edit();*/
        setContentView(R.layout.activity_dash_board);
        BusNumber = sharedPreferences.getString(ModelClass.BusNumber, "").trim();

        Log.e("isLogin ", "" + sharedPreferences.getInt(ModelClass.isLogin, 0));


        CompanyCode = sharedPreferences.getString(ModelClass.CompanyCode, "");        //geting the value through the intent
        Intent intent = getIntent();
        isPickUp = getIntent().getExtras().getBoolean("isPickUP");
        JourneyType = getIntent().getExtras().getString("JourneyType");


        builder = new AlertDialog.Builder(this);
        builder.create();
        TotalStudent = sharedPreferences.getInt(ModelClass.Count, 0);
        initializeUI();


        if (!isPickUp) {
            mChild_board_text.setText("Childrens to Deboard");
            mTotal_pickup_text.setText("Total Drop Points");
        }


       /* sharedPreferences = getSharedPreferences(ModelClass.LoginPreference, Context.MODE_PRIVATE);*/
        EntityCode = sharedPreferences.getString(ModelClass.EntityCode, "");

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
        mShowCurrentLocation.setOnClickListener(this);
        mSelectChildrenButton.setOnClickListener(this);
       /* mBackToMapLayout.setOnClickListener(this);*/
        mPickuppointButon.setOnClickListener(this);
        mPickupNotifyNextButton.setOnClickListener(this);

        //TODO :

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //TODO
        //taking run time permission
        getPermissionToReadUserContacts();
        //checking the gps is enable or not

        /*if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (alert == null)
                buildAlertMessageNoGps();

        } else {
            if (locationManager == null) {
                getLocation();
                callLocation();
            } else {
                callLocation();
            }
        }*/

        //compare date,if current date is greater that server date
        Date serverDate = null;
        Date todayDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String str = sharedPreferences.getString(ModelClass.TravelDate, "");
            if(!str.contentEquals("")) {
                String str1 = str.substring(0,str.indexOf("T"));
                serverDate = sdf.parse(str1);

                todayDate = sdf.parse(getCurrentDate());
                int res = serverDate.compareTo(todayDate);
                Log.e("Result ", "" + res);
                if (res == -1) {
                   /* mDialogTitle.setText(getResources().getString(R.string.start_alert_message));
                    mStartDialog.show();*/
                    canStartJourney=true;
                    editor = sharedPreferences.edit();
                    editor.putBoolean(ModelClass.canStartPickupJourney,true);
                    editor.putBoolean(ModelClass.canStartDropJourney,true);
                    editor.commit();

                } else {
                   /* Toast.makeText(DashBoardActivityNew.this,"Journey already completed",Toast.LENGTH_SHORT).show();*/
                    canStartJourney=false;
                }
            } else {
                canStartJourney=true;
                editor = sharedPreferences.edit();
                editor.putBoolean(ModelClass.canStartPickupJourney, true);
                editor.putBoolean(ModelClass.canStartDropJourney, true);
                editor.commit();

               /* canStartJourney=false;*/
            }

        } catch (Exception e) {
            Log.e("isPickUp", e.getMessage().toString());
        }

    }

    private static final int ACCESS_COARSE_LOCATION_PERMISSION = 1;
    private static final int ACCESS_FINE_LOCATION_PERMISSION = 2;
    private void initializeUI() {
        mStartButton = (Button) findViewById(R.id.startbutton);
        mChildrentoBoard = (TextView) findViewById(R.id.total_childern_boarding_number);
        mPickupNumbers = (TextView) findViewById(R.id.total_pickup_point_numbers);
        mBusName = (TextView) findViewById(R.id.busname);
        mImageViewpointer = (ImageView) findViewById(R.id.pickup_crownImage);
        mLayoutDetails = (RelativeLayout) findViewById(R.id.detailsRelative);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        mChild_board_text = (TextView) findViewById(R.id.child_board_text);
        mTotal_pickup_text = (TextView) findViewById(R.id.total_pickup_text);
        mToolBar_Title = (TextView) findViewById(R.id.toolbar_title);
        mShowCurrentLocation = (Button) findViewById(R.id.btn_draw_State);
        //TODO:for animantion

        mStudent_listLayout = (RelativeLayout) findViewById(R.id.student_listLayout);
        listview = (ListView) findViewById(R.id.pickup_point_listview);
        mSelectChildrenButton = (LinearLayout) findViewById(R.id.pickup_selectbutton);
        mPickupNotifyNextButton = (LinearLayout) findViewById(R.id.pickup_notifynext_button);
        mPickuppointButon = (LinearLayout) findViewById(R.id.pickup_point_linearmapbutton);
        // mPickup_point_notifynext = (LinearLayout) findViewById(R.id.pickup_point_linearbutton1);
        mBackToMapLayout = (RelativeLayout) findViewById(R.id.pickup_point_relativemapbutton);

        activityStartDialog();
        activityStopDialog();
        OnBacktoTrip();


        mToolBar_Title.setText("Dashboard");
        mBusName.setText(BusNumber);
        mChildrentoBoard.setText("" + TotalStudent);
    }


/* mchildren_board_text= (TextView) findViewById(R.id.children_board_text);
        mChildren_pickup_point=(TextView)findViewById(R.id.children_pickup_points);*/


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

                if (isPickUp) {
                    if (mStartButton.getText().toString().contentEquals(getResources().getString(R.string.start_button))) {

                        //comparing server time and system time
                      /*  Date serverDate = null;
                        Date todayDate = null;
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String str = sharedPreferences.getString(ModelClass.TravelDate, "");
                            if(!str.contentEquals("")) {
                                String str1 = str.substring(0,str.indexOf("T"));
                                serverDate = sdf.parse(str1);

                                todayDate = sdf.parse(getCurrentDate());
                                int res = serverDate.compareTo(todayDate);
                                Log.e("Result ", "" + res);
                                if (res == -1) {
                                    mDialogTitle.setText(getResources().getString(R.string.start_alert_message));
                                    mStartDialog.show();

                                } else {
                                    Toast.makeText(DashBoardActivityNew.this,"Journey already completed",Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mDialogTitle.setText(getResources().getString(R.string.start_alert_message));
                                mStartDialog.show();
                            }

                        } catch (Exception e) {
                            Log.e("isPickUp", e.getMessage().toString());
                        }*/
                       Boolean canStartPickupJourney= sharedPreferences.getBoolean(ModelClass.canStartPickupJourney, false);
                        if(canStartJourney||canStartPickupJourney){
                            Log.e("Pickup Journey ", "Pickup Journey started");
                            mDialogTitle.setText(getResources().getString(R.string.start_alert_message));
                            mStartDialog.show();
                        }else{
                            Log.e("Pickup Journey ", "pickup journey completed");
                                    Toast.makeText(DashBoardActivityNew.this, "Journey already completed", Toast.LENGTH_SHORT).show();
                        }


                    } else if (mStartButton.getText().toString().contentEquals(getResources().getString(R.string.pickup_point_button))) {

                        //taking lat/long value for pickup point and find the distance
                        if (progressHUD == null || !progressHUD.isShowing())
                            progressHUD = ProgressHUD.show(DashBoardActivityNew.this, "", true, true, DashBoardActivityNew.this);
                               /* countvalue=0;*/
                                         /*pickupcount=count_for_pickup+1;*/
                        Toast.makeText(getApplicationContext(), "Pickup Point: " + (countvalue+1), Toast.LENGTH_LONG).show();
                        listOfStudents(isPickUp);
                        /*clickCount++;*/


                      /*  Location crntLocation = new Location("crntlocation");
                        crntLocation.setLatitude(latitude);
                        crntLocation.setLongitude(longitude);

                        Location newLocation = new Location("newlocation");
                        newLocation.setLatitude((Double.parseDouble(dropPointsModelArrayList.get(countvalue).getLatitude())));
                        newLocation.setLongitude((Double.parseDouble(dropPointsModelArrayList.get(countvalue).getLongitude())));


                        //finding the distance in meters
                        distance = crntLocation.distanceTo(newLocation) / 1000; //
                        if (distance < 20) {

                                   *//* if (!sharedPreferences.contains(ModelClass.PickupPointCount))
                                        sharedPreferences.edit().putInt(ModelClass.PickupPointCount, 0);

                                    count_for_pickup = sharedPreferences.getInt(ModelClass.PickupPointCount, 0);*//*


                            //TODO taking countvalue variable for

                            listOfStudents(isPickUp);

                        } else {
                            Toast.makeText(getApplicationContext(), "Bus is not in near by pickup point", Toast.LENGTH_SHORT).show();


                        }*/
                        /*listOfStudents(isPickUp);*/


                    } else {
                        mstopApplicationDialog.show();
                    }
                } else {

                    if (mStartButton.getText().toString().contentEquals(getResources().getString(R.string.start_button))) {

                        //comparing server time and system time
                       /* Date serverDate = null;
                        Date todayDate = null;
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String str = sharedPreferences.getString(ModelClass.TravelDate, "");
                            if(!str.contentEquals("")) {
                                String str1 = str.substring(0,str.indexOf("T")); //date from 0 to before T
                                serverDate = sdf.parse(str1);

                                todayDate = sdf.parse(getCurrentDate());
                                int res = serverDate.compareTo(todayDate);
                                Log.e("Result ", "" + res);

                                //-1 previous date
                                //0 todays date
                                //1 tomorrow date
                                if (res == -1) {
                                    mDialogTitle.setText(getResources().getString(R.string.drop_start_alert_message));
                                    mStartDialog.show();
                                    //mLayoutDetails.setVisibility(View.GONE);
                                  //  mImageViewpointer.setVisibility(View.VISIBLE);

                                } else {
                                    Toast.makeText(DashBoardActivityNew.this,"Journey already completed",Toast.LENGTH_SHORT).show();
                                }
                            } else {


                            }

                        } catch (Exception e) {
                            Log.e("isPickUp", e.getMessage().toString());
                        }*/

                       //TODO:
                       Boolean canStartDropJourney= sharedPreferences.getBoolean(ModelClass.canStartDropJourney, false);
                        if(canStartJourney||canStartDropJourney){
                            mDialogTitle.setText(getResources().getString(R.string.drop_start_alert_message));
                            mStartDialog.show();
                        }else{
                            Toast.makeText(DashBoardActivityNew.this,"Journey already completed",Toast.LENGTH_SHORT).show();
                        }

                    } else if (mStartButton.getText().toString().contentEquals(getResources().getString(R.string.drop_point_button))) {

                                Toast.makeText(getApplicationContext(),"Drop"+"Point:"+(countvalue+1),Toast.LENGTH_SHORT).show();

                        listOfStudents(isPickUp);

                      /*  clickCount++;                                                 */
                      /*  */
                      /*  Toast.makeText(getApplicationContext(), "Drop" +              */
                       // distanceBetween(,latValue);
                      /*          " Point: " + clickCount, Toast.LENGTH_LONG).show();   */


                        //Location crntLocation = new Location("crntlocation");
                        //crntLocation.setLatitude(latitude);
                       // crntLocation.setLongitude(longitude);

                        //Location newLocation = new Location("newlocation");
                        //newLocation.setLatitude((Double.parseDouble(dropPointsModelArrayList.get(countvalue).getLatitude())));
                       // newLocation.setLongitude((Double.parseDouble(dropPointsModelArrayList.get(countvalue).getLongitude())));


                        //finding the distance in meters
                       // distance = crntLocation.distanceTo(newLocation) / 1000; //
                       /* if (distance < 20) {*/
/*
*/

/*
*/

                       /*     listOfStudents(isPickUp);*/
                       /*     clickCount++;*/
                       /*     Toast.makeText(getApplicationContext(), "Drop" +*/
                       /*             " Point: " + clickCount, Toast.LENGTH_LONG).show();*/
                       /* } else {*/
                       /*     Toast.makeText(getApplicationContext(), "Bus is not in near by pickup point", Toast.LENGTH_SHORT).show();*/
                       /* }*/


                    } else {

                        mstopApplicationDialog.show();
                    }
                }


                break;

            case R.id.btn_draw_State:
                //Zoommig in our current location
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 11));


                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude))     // Sets the center of the map to location user
                        .zoom(12)                   // Sets the zoom
                        .build();                   // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                break;

            case R.id.menu_drawer:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;

            case DialogInterface.BUTTON_NEGATIVE:

                break;

            case R.id.dialog_send:
                mStartDialog.dismiss();
                isStart = true;
               /* mDialogTitle.setText(getResources().getString(R.string.Alert_message));*/
                startServiceMethod();
               /* if (isPickUp) {
                    mStartButton.setText(getResources().getString(R.string.pickup_point_button));
                } else {
                    mStartButton.setText(getResources().getString(R.string.drop_point_button));
                }
                mLayoutDetails.setVisibility(View.GONE);
                mImageViewpointer.setVisibility(View.VISIBLE);*/

                break;
            case R.id.dialog_cancel:
                mStartDialog.dismiss();
                finish();
                break;

            case R.id.stop_application_yes:
               /* mstopApplicationDialog.dismiss();*/
                stopServiceMethod();
                /*finish();*/
                break;

            case R.id.stop_application_no:

                mstopApplicationDialog.dismiss();
               /* if (mstopApplicationDialog.isShowing())
                    mstopApplicationDialog.dismiss();*/
                //  stopServiceMethod();
                break;
            case R.id.onbackpress_yes:

                    onBackpressDialog.dismiss();


                /*editor.putInt(ModelClass.PickupPointCount, 0);*/
                //i added count 0 for onclick
                countvalue = 0;
               /* editor.commit();*/
                finish();

                break;
            case R.id.onbackpress_no:
                onBackpressDialog.dismiss();
                break;

            case R.id.pickup_point_relativemapbutton:

                //TODO:

                /*TranslateAnimation animation = new TranslateAnimation(0f, 0.0f, +800.0f, 0.0f);
                animation.setDuration(500);*/

                /*overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                mStudent_listLayout.setVisibility(View.GONE);
                mStartButton.getText().toString().contentEquals(getResources().getString(R.string.pickup_point_button));*/


                break;
            case R.id.pickup_notifynext_button:
                    if(isNotifyClicked) {
                        if (isPickUp) {

                                       /* if (sharedPreferences.getInt(ModelClass.PickupPointCount, 0) < TransportActivity.mArrayList.size() - 1) {
                                            editor.putInt(ModelClass.PickupPointCount, sharedPreferences.getInt(ModelClass.PickupPointCount, 0) + 1);
                                            editor.commit();*/
                            Log.e("pickup_notifynext_button","pickup_notifynext_button clicked");

                            //todo
                            if (countvalue < TransportActivity.mArrayList.size() - 1) {
                                countvalue = countvalue + 1;
                                           /* overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                                            mStudent_listLayout.setVisibility(View.GONE);
                                            mToolBar_Title.setText("DashBoard");
                                            DashBoardActivityNew.mStartButton.setText("Pick Up Point");*/
                    //                        finish();

                                TranslateAnimation animation = new TranslateAnimation(0f, 0.0f, 0.0f, +1800f);
                                mStudent_listLayout.setVisibility(View.GONE);
                                buttonClicked = false;
                                mToolBar_Title.setText("Dashboard");
                                animation.setDuration(500);
                                mStudent_listLayout.startAnimation(animation);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                             isNotifyClicked = false;
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });


                                           /* buttonClicked = false;*/

                            } else {
                                            /*overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                                            mStudent_listLayout.setVisibility(View.GONE);
                                            mToolBar_Title.setText("DashBoard");*/
                                DashBoardActivityNew.mStartButton.setText("Stop");
                                mpickup_point_listadapter.notifyDataSetChanged();
                                //  finish();
                                buttonClicked = false;


                                TranslateAnimation animation = new TranslateAnimation(0f, 0.0f, 0.0f, +1800f);
                                mStudent_listLayout.setVisibility(View.GONE);
                                mToolBar_Title.setText("Dashboard");
                                animation.setDuration(500);
                                mStudent_listLayout.startAnimation(animation);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                           isNotifyClicked = false;
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });

                                           /* buttonClicked = false;*/


                            }


                        } else {
                            if (isNotifyClicked) {


                                if (countvalue < TransportActivity.mArrayList.size() - 1) {
                                    countvalue = countvalue + 1;
                                    buttonClicked = false;
                                    DashBoardActivityNew.mStartButton.setText("Drop Point");
                                    mToolBar_Title.setText("Dashboard");

                                    TranslateAnimation animation = new TranslateAnimation(0f, 0.0f, 0.0f, +1800f);
                                    mStudent_listLayout.setVisibility(View.GONE);
                                    animation.setDuration(500);
                                    mStudent_listLayout.startAnimation(animation);
                                    animation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {
                                                 isNotifyClicked = false;
                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });
                                    // finish();

                                } else {
                                    DashBoardActivityNew.mStartButton.setText("Stop");
                                    mpickup_point_listadapter.notifyDataSetChanged();
                                    //  finish();
                                    buttonClicked = false;
                                    TranslateAnimation animation = new TranslateAnimation(0f, 0.0f, 0.0f, +1800f);
                                    mStudent_listLayout.setVisibility(View.GONE);
                                    mToolBar_Title.setText("Dashboard");
                                    animation.setDuration(500);
                                    mStudent_listLayout.startAnimation(animation);
                                    animation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {
                                                       isNotifyClicked = false;
                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });


                                }
                            }
                        }

                        clickMapButton = false;
                    }



             /* overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);*/


                break;

            case R.id.pickup_point_linearmapbutton:
                TranslateAnimation animation = new TranslateAnimation(0f, 0.0f, 0.0f, +1800f);
                mStudent_listLayout.setVisibility(View.GONE);
                mToolBar_Title.setText("Dashboard");
                animation.setDuration(500);
                mStudent_listLayout.startAnimation(animation);

                clickMapButton = true;
                isNotifyClicked=true;

                //mStudent_listLayout.setVisibility(View.GONE);
               /* try{
                    if(buttonClicked){
                        buttonClicked = false;
                    }

                }catch (Exception e){
                    Log.e("pickup_point_linearmapbutton",e.getMessage());
                }*/

                break;

            case R.id.pickup_selectbutton:
                buttonClicked = true;
                isNotifyClicked = true;

                for (int i = 0; i < modelArrayList.size(); i++) {
                    mSelectChildrenButton.setVisibility(View.INVISIBLE);
                    mPickupNotifyNextButton.setVisibility(View.VISIBLE);
                }

                mpickup_point_listadapter.notifyDataSetChanged();
                break;


        }
    }

    //TODO


    //call the api for getting the list of students for that droppoints

    private void listOfStudents(final boolean isPickUp) {

        try {
            JSONObject jsonObject = new JSONObject();
            if (isPickUp) {

                String mDropPoints = dropPointsModelArrayList.get(countvalue).getDropPointCode();
                jsonObject.put("DropPointCode", mDropPoints);
                jsonObject.put("BusCode", EntityCode);
                jsonObject.put("JourneyType", JourneyType);
            } else {


               /* if (!sharedPreferences.contains(ModelClass.DropPointCount))
                    sharedPreferences.edit().putInt(ModelClass.DropPointCount, 0);

                count_for_drop = sharedPreferences.getInt(ModelClass.DropPointCount, 0);*/


                String mDropPoints = dropPointsModelArrayList.get(countvalue).getDropPointCode();
                jsonObject.put("DropPointCode", mDropPoints);
                jsonObject.put("BusCode", EntityCode);
                jsonObject.put("JourneyType", JourneyType);
            }


                HttpPost http_post = new HttpPost() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                   /* if (progressHUD == null || !progressHUD.isShowing())

                        progressHUD = ProgressHUD.show(DashBoardActivityNew.this, "", true, true, DashBoardActivityNew.this);*/

                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    if (progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }


                    if (s != null && !s.isEmpty()) {
                        try {
                            JSONObject students_detailsObject = new JSONObject(s);
                            String Success = students_detailsObject.getString("Success");

                            if (students_detailsObject.getBoolean("Success")) {
                                studentsArray = new ArrayList<PickupPointModel>();
                                JSONArray student_Array_obj = students_detailsObject.getJSONArray("ViewModels");


                                if (student_Array_obj.length() > 0 && student_Array_obj != null) {
                                    for (int i = 0; i < student_Array_obj.length(); i++) {
                                        PickupPointModel students_detailsModel = new PickupPointModel();
                                        JSONObject parse_array_obj = student_Array_obj.getJSONObject(i);
                                        students_detailsModel.setCode(parse_array_obj.getString("Code"));
                                        students_detailsModel.setRegistrationId(parse_array_obj.getString("RegistrationId"));
                                        students_detailsModel.setFirstName(parse_array_obj.getString("FirstName"));
                                        students_detailsModel.setLastName(parse_array_obj.getString("LastName"));
                                        students_detailsModel.setDropPointCode(parse_array_obj.getString("DropPointCode"));

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


                                        students_detailsModel.setIsBoarding(parse_array_obj.getBoolean("IsBoarding"));
                                        studentsArray.add(students_detailsModel);
                                    }


                                    //Doing changes for checking the
                                   /* Intent pickupPoint = new Intent(DashBoardActivityNew.this, PickupPoint.class);
                                    pickupPoint.putExtra("isPickUp", isPickUp);
                                    startActivity(pickupPoint);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);*/

                                    mStudent_listLayout.setVisibility(View.VISIBLE);
                                    if (isPickUp) {
                                        mToolBar_Title.setText("Pickup Point");
                                    } else {
                                        mToolBar_Title.setText("Drop Point");
                                    }

                                   /* if(isPickupClicked){
                                        TranslateAnimation animation = new TranslateAnimation(0f, 0.0f, +1800.0f, 0.0f);
                                        animation.setDuration(500);
                                        mStudent_listLayout.startAnimation(animation);
                                        childAnimationListView();
                                    }*/
                                    TranslateAnimation animation = new TranslateAnimation(0f, 0.0f, +1800.0f, 0.0f);
                                    animation.setDuration(500);
                                    mStudent_listLayout.startAnimation(animation);

                                    childAnimationListView();


                                } else {


                                    Toast.makeText(getApplicationContext(), "No student boarding in this stop" /*resultJsonObject.getString("Message")*/, Toast.LENGTH_SHORT).show();

                                    if (isPickUp) {

                                        //TODO:i commented for refreshing the pickup point count value
                                        /*if (sharedPreferences.getInt(ModelClass.PickupPointCount, 0) < TransportActivity.mArrayList.size() - 1) {
                                            editor.putInt(ModelClass.PickupPointCount, sharedPreferences.getInt(ModelClass.PickupPointCount, 0) + 1);
                                            editor.commit();*/

                                        if (countvalue < TransportActivity.mArrayList.size()) {
                                            countvalue = countvalue + 1;
                                            listOfStudents(isPickUp);
                                        } else {
                                            mStartButton.setText("Stop");
                                            //                                        mstopApplicationDialog.show();
                                        }

                                    } else {

                                       /* if (sharedPreferences.getInt(ModelClass.DropPointCount, 0) < TransportActivity.mArrayList.size() - 1) {
                                            editor.putInt(ModelClass.DropPointCount, sharedPreferences.getInt(ModelClass.DropPointCount, 0) + 1);
                                            editor.commit();
                                            listOfStudents(isPickUp);*/
                                        if (countvalue < TransportActivity.mArrayList.size()) {
                                            countvalue = countvalue + 1;
                                            listOfStudents(isPickUp);

                                        } else {
                                            mStartButton.setText("Stop");
                                            //                                           mstopApplicationDialog.show();
                                        }

                                    }

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

    //call the method to send the lat/long details in cloud
    private void startServiceMethod() {

        if (Connectivity.isConnected(getApplicationContext())) {

            try {

                JSONObject serviceObject = new JSONObject();
                serviceObject.put("LastLocation", Address);
                serviceObject.put("BusCode", EntityCode);
                serviceObject.put("RouteCode", RouteCode);
                serviceObject.put("DriverCode", Constant.DRIVER_ROLE);
                serviceObject.put("CurrentLatitude", latitude);
                serviceObject.put("CurrentLongitude", longitude);
                serviceObject.put("TravelStartTime", getCurrentTime());
                //serviceObject.put("TravelEndTime", getCurrentTime());
                serviceObject.put("TravelStatusCode", Constant.JOURNEY_IN_PROGRESS_CODE);
                serviceObject.put("Status", "1");
                serviceObject.put("UserID", "1");
                serviceObject.put("MethodType", "POST");
                serviceObject.put("TravelDate", getCurrentDate());
                serviceObject.put("DeviceCode", android_id);
                serviceObject.put("CompanyCode", CompanyCode);


                HttpPost httpPost = new HttpPost() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        if (progressHUD == null || !progressHUD.isShowing())

                            progressHUD = ProgressHUD.show(DashBoardActivityNew.this, "", true, true, DashBoardActivityNew.this);

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

                                    String Message = response.getString("Message");
                                    String CurrentTravelInfo = response.getString("CurrentTravelInfo");
                                    JSONObject CurrentTravelInfoObject = new JSONObject(CurrentTravelInfo);
                                    String CurrentTravelInfoCode = CurrentTravelInfoObject.getString("Code");
                                    String travelDate = CurrentTravelInfoObject.getString("TravelDate");

                                    editor = sharedPreferences.edit();
                                    editor.putString(ModelClass.CurrentTravelInfoCode, CurrentTravelInfoCode);
                                    editor.putString(ModelClass.TravelDate, travelDate);
                                    editor.commit();

                                    if (isPickUp) {
                                        mStartButton.setText(getResources().getString(R.string.pickup_point_button));
                                    } else {
                                        mStartButton.setText(getResources().getString(R.string.drop_point_button));
                                    }
                                    mLayoutDetails.setVisibility(View.GONE);
                                    mImageViewpointer.setVisibility(View.VISIBLE);

                                    h.postDelayed(new Runnable() {


                                        @Override
                                        public void run() {

                                            Log.e("Handler", "Calling Thread");
                                            if (mStartButton.getText().toString().contentEquals(getResources().getString(R.string.stop_button))) {

                                                h.removeCallbacks(this);
                                                Log.e("Handler", "Stop Thread");

                                            } else {
                                                h.postDelayed(this, 15000);
                                                td = new ThreadDemo(longitude, latitude, Address);
                                                td.start();
                                            }
                                        }
                                    }, 15000);


                                } else {

                                }
                            } catch (Exception e) {
                                Toast.makeText(DashBoardActivityNew.this,"Some error occurred, Try again.",Toast.LENGTH_SHORT).show();
                                Log.e("error message", e.getMessage());
                            }
                        } else {

                        }
                    }
                };
                httpPost.execute(ModelClass.USER_CURRENT_TRAVEL_INFO, serviceObject.toString());
            } catch (Exception e) {
                Log.e("error message", e.getMessage());
            }
        } else {
                    /*Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();*/
        }


    }


    private void stopServiceMethod() {

        if (Connectivity.isConnected(getApplicationContext())) {

            try {

                String TravelInfoCode = sharedPreferences.getString(ModelClass.CurrentTravelInfoCode, "");
                JSONObject serviceObject = new JSONObject();

                serviceObject.put("Code", TravelInfoCode);
                serviceObject.put("LastLocation", Address);
                serviceObject.put("RouteCode", RouteCode);

               /* serviceObject.put("BusCode", EntityCode);
                serviceObject.put("RouteCode", RouteCode);
                serviceObject.put("DriverCode", Constant.DRIVER_ROLE);*/
                serviceObject.put("CurrentLatitude", latitude);
                serviceObject.put("CurrentLongitude", longitude);
                //serviceObject.put("TravelStartTime",getCurrentTime() );
                serviceObject.put("TravelEndTime", getCurrentTime());
                serviceObject.put("TravelStatusCode", Constant.JOURNEY_COMPLETED);
                serviceObject.put(ModelClass.CompanyCode, CompanyCode);
               /* serviceObject.put("Status", "1");*/
                serviceObject.put("UserID", "1");
                serviceObject.put("MethodType", "PUT");
                serviceObject.put("TravelDate", getCurrentDate());
               /* serviceObject.put("DeviceCode", android_id);*/


                HttpPost httpPost = new HttpPost() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();

                        if (progressHUD == null || !progressHUD.isShowing())

                            progressHUD = ProgressHUD.show(DashBoardActivityNew.this, "", true, true, DashBoardActivityNew.this);

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
                                Log.e("stopServiceMethod", result);
                                JSONObject response = new JSONObject(result);
                                String success = response.getString("Success");
                                if (success.contentEquals("true")) {
                                    String Message = response.getString("Message");
                                    String CurrentTravelInfo = response.getString("CurrentTravelInfo");
                                    JSONObject CurrentTravelInfoObject = new JSONObject(CurrentTravelInfo);
                                    String travelDate = CurrentTravelInfoObject.getString("TravelDate");

                                    editor = sharedPreferences.edit();
                                    editor.putString(ModelClass.TravelDate, travelDate);
                                    if(isPickUp){
                                    editor.putBoolean(ModelClass.canStartPickupJourney,false);
                                    }else{
                                        editor.putBoolean(ModelClass.canStartDropJourney,false);
                                    }
                                    editor.commit();



                                   /* mstopApplicationDialog.show();*/
                                    // Toast.makeText(DashBoardActivityNew.this, "stop", Toast.LENGTH_SHORT).show();
                                    h.removeCallbacks(td);
                                   /* sharedPreferences.edit().clear();
                                    sharedPreferences.edit().commit();*/

                                    mstopApplicationDialog.dismiss();

                                    /*sharedPreferences.edit().clear();
                                    sharedPreferences.edit().commit();*/
                                    // count_for_pickup=0;
                                    countvalue = 0;
                                    finish();

                                    /*finish();
                                    stopService(new Intent(DashBoardActivityNew.this,
                                            DashboardService.class));
*/

                                } else {
                                           /* Toast.makeText((), "Login Failed! Please check your Username and Password", Toast.LENGTH_SHORT)
                                                    .show();*/
                                }
                            } catch (Exception e) {
                                Log.e("stopServiceMethod error message", e.getMessage());  /*Log.e(TAG, "Login"+e.getMessage().toString());
                                        Toast.makeText(getActivity(), "Login Failed, Please try again.", Toast.LENGTH_SHORT).show();*/
                            }
                        } else {
                                   /* Toast.makeText(getActivity(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show()*/
                            ;
                        }
                    }
                };
                httpPost.execute(ModelClass.USER_CURRENT_TRAVEL_INFO_UPDATE, serviceObject.toString());
            } catch (Exception e) {
                Log.e("stopServiceMethod error message", e.getMessage());   /*Log.e(TAG, "Login"+e.getMessage().toString());*/
            }
        } else {
                    /*Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();*/
        }

    }

    private String getCurrentDate() {
        String dateInString = null;
       /* if (dateInString.isEmpty() || dateInString.trim().equals("")) {
            return String.valueOf(false);
        } else {*/
            try {

                Calendar cal = Calendar.getInstance();
                   dateInString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
               /* SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");*/
               /* Date d = null;
                Date d1 = null;
                String today = getToday("yyyy-MM-dd");
                try {
                    //System.out.println("expdate>> "+date);
                    //System.out.println("today>> "+today+"\n\n");
                    d = sdf.parse(dateInString);
                    d1 = sdf.parse(today);
                    if (d1.compareTo(d) < 0) {// not expired
                        return String.valueOf(false);
                    } else if (d.compareTo(d1) == 0) {// both date are same
                        if (d.getTime() < d1.getTime()) {// not expired
                            return String.valueOf(false);
                        } else if (d.getTime() == d1.getTime()) {//expired
                            return String.valueOf(true);
                        } else {//expired
                            return String.valueOf(true);
                        }
                    } else {//expired
                        return String.valueOf(true);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return String.valueOf(false);
                }*/

             /*   Date strDate = sdf.parse(valid_until);

                if (strDate.after(new Date())) {
                    catalog_outdated = 1;
                }*/
            } catch (Exception e) {

            }


        return dateInString;


    }

   /* private static String getToday(String format) {
        Date date = new Date();
        return new SimpleDateFormat(format).format(date);

    }*/

    private String getCurrentTime() {
        String timeInString = null;


        try {

            Calendar cal = Calendar.getInstance();

            timeInString = new SimpleDateFormat(" HH:mm").format(cal.getTime());


        } catch (Exception e) {

        }
        return timeInString;


    }


    private void drawRouteMap() {
       /* progressDialog = ProgressDialog.show(this, "Please wait.",
                "Fetching route information.", true);*/
        if (progressHUD == null || !progressHUD.isShowing())
            progressHUD = ProgressHUD.show(DashBoardActivityNew.this, "", true, true, DashBoardActivityNew.this);


        for (int i = 0; i < mList.size(); i++) {
            MarkerOptions markerOptions;
            if (i == 0)
                markerOptions = new MarkerOptions().position(start).draggable(false).title(mList.get(i).getTitle()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.start_pin_big));
            else if (i == mList.size() - 1)
                markerOptions = new MarkerOptions().position(end).draggable(false).title(mList.get(i).getTitle()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.end_pin_big));
            else {
                LatLng latLng = new LatLng(mList.get(i).getLatValue(), mList.get(i).getLongValue());
                markerOptions = new MarkerOptions().position(latLng).draggable(false).title(mList.get(i).getTitle()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.inbetween_stops_pin_big));
            }

            map.addMarker(markerOptions);
        }

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                View v = getLayoutInflater().inflate(R.layout.info_window, null);
                TextView tvLat = (TextView) v.findViewById(R.id.text);
                tvLat.setText(arg0.getTitle());
                return v;

            }
        });

        int size = mList.size();
        if (size >= 2) {

            int start = 0;
            int end = 9;

            if (size >= 10) {
                float h = mList.size() / (float) 10;
                double a = Math.ceil(h);
                for (int i = 0; i < a; i++) {
                    LatLng origin = new LatLng(mList.get(start).getLatValue(), mList.get(start).getLongValue());
                    LatLng dest = new LatLng(mList.get(end).getLatValue(), mList.get(end).getLongValue());

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest, start, end);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                    size = size - 10;
                    if (size > 0) {
                        if (size >= 10) {
                            start = end;
                            end = end + 9;

                        } else {
                            start = end;
                            end = mList.size() - 1;
                        }
                    }
                }
            } else {
                LatLng origin = new LatLng(mList.get(start).getLatValue(), mList.get(start).getLongValue());
                LatLng dest = new LatLng(mList.get(mList.size() - 1).getLatValue(), mList.get(mList.size() - 1).getLongValue());

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest, start, mList.size());

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);

            }

        }
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest, int start, int end) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        String waypoints = "";
        for (int i = start; i < end; i++) {
            LatLng point = new LatLng(mList.get(i).getLatValue(), mList.get(i).getLongValue());
            if (i == start + 1)
                waypoints = "waypoints=";
            if (dest.latitude != point.latitude)
                waypoints += point.latitude + "," + point.longitude + "|";
        }


        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.e("URL", url);


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            // Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onLocationChanged(Location location) {

        //added the marker to showing the current location

        Log.e("onLocationChanged", "latitude " + latitude + " longitude " + longitude);
        /*mPositionMarker = map.addMarker(new MarkerOptions()
                .flat(true)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.inbetween_stops_pin))
                .position(
                        new LatLng(latitude, longitude)).snippet("SNIPET"));*/

        if (location != null) {
//getting the current lat long

//            if (mPositionMarker!= null) {
//
//                mPositionMarker = map.addMarker(new MarkerOptions()
//                        .flat(true)
//                        .icon(BitmapDescriptorFactory
//                                .fromResource(R.drawable.inbetween_stops_pin))
//                        .position(
//                                new LatLng(location.getLatitude(), location
//                                        .getLongitude())).snippet("SNIPET"));
//            }
        }


        android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        List<Address> listAdd = getGeocoderAddress(getBaseContext(), location);
//        Log.i("", listAdd.toString());
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

    @Override
    public void onCancel(DialogInterface dialog) {

    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
           /* if (progressHUD.isShowing()) {
                progressHUD.dismiss();
            }*/


            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            // progressDialog.dismiss();
            if (progressHUD.isShowing()) {
                progressHUD.dismiss();
            }


            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(6);
                // polyOptions.color(getResources().getColor(R.color.marker_line));
                lineOptions.color(getResources().getColor(R.color.marker_line));
                // lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        h.removeCallbacks(td);
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
        locationManager.removeUpdates(this);
        finish();
        //  Thread.currentThread().interrupt();

                   /* finish();*/

        //send the lat and long for stop the service

    }

    @Override
    public void onPause() {
       /* if(locationManager != null)
        {
            locationManager.removeUpdates(this);
        }
*/
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO :
       /* locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //checking the gps is enable or not

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (alert == null)
                buildAlertMessageNoGps();

        } else {
            if (locationManager == null) {
                getLocation();
                callLocation();
            } else {
                callLocation();
            }
        }*/
        /*setUpMapIfNeeded();*/

       /* if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }else {

          *//* alert.dismiss();*//*


            if(alert != null && alert.isShowing()){
                alert.dismiss();
            }

            if (locationManager == null) {
                getLocation();

            } else {

                callLocation();
            }
        }*/
    }

     /* public void onBackPressed() {
        super.onBackPressed();
          if(isStart){
              onBackpressDialog.show();
              Toast.makeText(getApplicationContext(),"Transit in progressdo u want to cancel it",Toast.LENGTH_SHORT).show();
          }else{

          }

    }*/

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(isStart){
                onBackpressDialog.show();
            }else{
                finish();
            }
           /* finish();*/

            //moveTaskToBack(false);

            return true;
        }
        return super.onKeyDown(keyCode, event);
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

        @Override
        public void run() {
            super.run();
            try {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(" runOnUiThread ", "latitude " + latitude + " longitude " + longitude);
                        Log.i("DashboardActivityNew", "Thread Active");

                        try {
                            if (mPositionMarker != null)
                                mPositionMarker.remove();

                            mPositionMarker = map.addMarker(new MarkerOptions()
                                    .flat(true)
                                    .icon(BitmapDescriptorFactory
                                            .fromResource(R.drawable.bus_icon_small_1))
                                    .position(
                                            new LatLng(latitude, longitude)).snippet("SNIPET"));


                            mPositionMarker.setTitle(Address);
                            /*Address*/

                        } catch (Exception e) {
                            Log.e(" Exception ", e.getMessage());
                        }
                    }


                });

              /*  mPositionMarker = map.addMarker(new MarkerOptions()
                        .flat(true)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.inbetween_stops_pin))
                        .position(
                                new LatLng(latitude, longitude)).snippet("SNIPET"));*/
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
                        serviceObject.put("MethodType", "PUT");
                       /* serviceObject.put("TravelDate",getCurrentDate());
                        serviceObject.put("DeviceCode",android_id );*/


                        HTTP_PUT http_put = new HTTP_PUT() {

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                               /* if (progressHUD == null || !progressHUD.isShowing())
                                    progressHUD = ProgressHUD.show(DashBoardActivityNew.this, "", true, true, DashBoardActivityNew.this);*/
                            }

                            // get login response from rest API
                            @Override
                            protected void onPostExecute(String result) {
                                super.onPostExecute(result);
                               /* if (progressHUD.isShowing()) {
                                    progressHUD.dismiss();
                                }*/

                                Log.e("ThreadDemo", result);
                                if (result != null) {
                                    try {
                                        JSONObject response = new JSONObject(result);
                                        String success = response.getString("Success");
                                        if (success.contentEquals("true")) {
                                            String Message = response.getString("Message");

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
                Log.e("ThreadDemo", e.getMessage().toString());
            }
        }
    }

    private void buildAlertMessageNoGps() {
        builder = new AlertDialog.Builder(this);

        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Toast.makeText(DashBoardActivityNew.this, "GPS not enabled, this application won't work properly.", Toast.LENGTH_SHORT).show();
                        alert.dismiss();
                        finish();

                    }
                });
        alert = builder.create();
        alert.show();
    }


    //
    private Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
//                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                    Log.d("Network", "Network Enabled");
                    if (locationManager != null) {
//                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            // return TODO;
//                        }
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                        Log.d("GPS", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public void callLocation() {


       /* locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //checking the gps is enable or not

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }*/





  /*  private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();*/

//todo
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

           /* mImageViewpointer.setVisibility(View.GONE);
            imgMenuDrawer.setOnClickListener(this);
            mStartButton.setOnClickListener(this);*/
        //TODO:taken lat and long value from drop points model class


        mList = new ArrayList<MarkerModelClass>();
        dropPointsModelArrayList = TransportActivity.mArrayList;


        //getting the lat and lang value for markup point
        if ((dropPointsModelArrayList != null) && (dropPointsModelArrayList.size() != 0)) {
            for (int i = 0; i < dropPointsModelArrayList.size(); i++) {
                String name = dropPointsModelArrayList.get(i).getDropPointName();
                latValue = Double.parseDouble(dropPointsModelArrayList.get(i).getLatitude());
                longValue = Double.parseDouble(dropPointsModelArrayList.get(i).getLongitude());
                mList.add(new MarkerModelClass(latValue, longValue, name));
            }
//for pick up point array at position is start point and for drop point array of 0th position will be end
            if (isPickUp) {

                RouteCode = sharedPreferences.getString(ModelClass.PickUpCode, "");
                //for pickup point start and end point
                Double latValue = Double.parseDouble(dropPointsModelArrayList.get(0).getLatitude());
                Double longValue = Double.parseDouble(dropPointsModelArrayList.get(0).getLongitude());
                start = new LatLng(latValue, longValue);

                Double deslatValue = Double.parseDouble(dropPointsModelArrayList.get(dropPointsModelArrayList.size() - 1).getLatitude());
                Double deslongValue = Double.parseDouble(dropPointsModelArrayList.get(dropPointsModelArrayList.size() - 1).getLongitude());
                end = new LatLng(deslatValue, deslongValue);
            } else {
                //for drop start and end point
                RouteCode = sharedPreferences.getString(ModelClass.DropCode, "");
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


        /*if (Util.Operations.isOnline(this)) {
//            routemap();
            drawRouteMap();
        } else {
            Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
        }*/
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

        //TODO:
             /* this.map.getUiSettings().setMyLocationButtonEnabled(true);
        this.map.setMyLocationEnabled(true);*/

        this.map.getUiSettings().setMyLocationButtonEnabled(false);
        this.map.setMyLocationEnabled(false);


        //  map.setMyLocationEnabled(true);
       /* map.setMyLocationEnabled(true);*/

//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location == null) {
            getLocation();
        }

        if (location != null) {
//getting the current lat long

           /* if (mPositionMarker == null) {
                //TODO:


                mPositionMarker = map.addMarker(new MarkerOptions()
                        .flat(true)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.bus_icon_small_1))
                        .position(
                                new LatLng(location.getLatitude(), location
                                        .getLongitude())));



               *//* .anchor(0.5f, 0.5f)*//*
            }*/


            latitude = location.getLatitude();
            longitude = location.getLongitude();

            currentLatLong = new LatLng(latitude, longValue);

            List<Address> listAdd = getGeocoderAddress(getBaseContext(), location);
            //Log.i("", listAdd.toString());
            if (listAdd != null && listAdd.size() > 0) {
                Address currentAddress = listAdd.get(0);

                String strLoc = currentAddress.getThoroughfare();
                String strSubLocality = currentAddress.getSubLocality();
                String strPostalCode = currentAddress.getLocality();
                String strState = currentAddress.getAdminArea();
                String strCountry = currentAddress.getCountryCode();

                Address = strLoc + "," + strSubLocality + "," + strPostalCode + "," + strState + "," + strCountry;

                if (mPositionMarker == null) {
                    //TODO:


                    mPositionMarker = map.addMarker(new MarkerOptions()
                            .flat(true)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.bus_icon_small_1))
                            .position(
                                    new LatLng(location.getLatitude(), location
                                            .getLongitude())));
                    mPositionMarker.setTitle(Address);;



               /* .anchor(0.5f, 0.5f)*/
                }


               /* map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(15)                   // Sets the zoom
                        .build();                   // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/

                //zoomming in middle position of droppoint

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(


                        new LatLng(Double.parseDouble(dropPointsModelArrayList.get(dropPointsModelArrayList.size() / 2).getLatitude()), Double.parseDouble(dropPointsModelArrayList.get(dropPointsModelArrayList.size() / 2).getLongitude())), 11));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(Double.parseDouble(dropPointsModelArrayList.get(dropPointsModelArrayList.size() / 2).getLatitude()), Double.parseDouble(dropPointsModelArrayList.get(dropPointsModelArrayList.size() / 2).getLongitude())))      // Sets the center of the map to location user
                        .zoom(12)                   // Sets the zoom
                        .build();                   // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                //  map.animateCamera(CameraUpdateFactory.zoomIn());


            }
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


       /* CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(12.954647, 77.493334));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);

//        map.moveCamera(center);
        map.animateCamera(zoom);*/

            // LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
            /*locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
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
*/

            if (Util.Operations.isOnline(this)) {
//            routemap();
                drawRouteMap();
            } else {
                Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
            }


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 0) {
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                Log.v("onActivityResult", " Location providers: " + provider);
                //Start searching for location and update the location text when update available.
// Do whatever you want
                callLocation();
            } else {
                buildAlertMessageNoGps();
            }
        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            }
        }

    }

    /*protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CODE && resultCode == 0){
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if(provider != null){
                Log.v(TAG, " Location providers: "+provider);
                //Start searching for location and update the location text when update available.
// Do whatever you want
                startFetchingLocation();
            }else{
                //Users did not switch on the GPS
            }
        }*/

    private void activityStartDialog() {
        mStartDialog = new Dialog(this);
        mStartDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mStartDialog.setContentView(R.layout.start_trip_dialog);
        mStartDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mStartDialog.setCancelable(false);

        mDialogTitle = (TextView) mStartDialog.findViewById(R.id.dialog_title);
        mDialog_cancel = (Button) mStartDialog.findViewById(R.id.dialog_cancel);
        mDialog_Start = (Button) mStartDialog.findViewById(R.id.dialog_send);

        mDialog_cancel.setOnClickListener(this);
        mDialog_Start.setOnClickListener(this);

    }

    private void activityStopDialog() {

        mstopApplicationDialog = new Dialog(this);
        mstopApplicationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mstopApplicationDialog.setContentView(R.layout.stop_trip_dialog);
        mstopApplicationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mstopApplicationDialog.setCancelable(false);

        mstopDialogtitle = (TextView) mstopApplicationDialog.findViewById(R.id.dialog_title);
        mStopDialog_yesButton = (Button) mstopApplicationDialog.findViewById(R.id.stop_application_yes);
        mStopDialog_NoButton = (Button) mstopApplicationDialog.findViewById(R.id.stop_application_no);

        if (isPickUp) {
            mstopDialogtitle.setText(getResources().getString(R.string.stop_alert_message));
        } else {
            mstopDialogtitle.setText(getResources().getString(R.string.drop_stop_alert_message));
        }

        mStopDialog_yesButton.setOnClickListener(this);
        mStopDialog_NoButton.setOnClickListener(this);

    }

    //on back press calling the dialog to


    private void OnBacktoTrip() {

        onBackpressDialog = new Dialog(this);
        onBackpressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        onBackpressDialog.setContentView(R.layout.onbackpress_layout);
        onBackpressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        onBackpressDialog.setCancelable(false);

        mOnbackDialogtitle = (TextView) onBackpressDialog.findViewById(R.id.dialog_title);
        mOnbackpress_yes = (Button) onBackpressDialog.findViewById(R.id.onbackpress_yes);
        mOnbackpress_no = (Button) onBackpressDialog.findViewById(R.id.onbackpress_no);

        mOnbackpress_yes.setOnClickListener(this);
        mOnbackpress_no.setOnClickListener(this);

    }

    //find the near by distance and enable the pickup point button for that pickup point

   /* if ((dropPointsModelArrayList != null) && (dropPointsModelArrayList.size() != 0)) {
        for (int i = 0; i < dropPointsModelArrayList.size(); i++) {
            String name = dropPointsModelArrayList.get(i).getDropPointName();
            latValue = Double.parseDouble(dropPointsModelArrayList.get(i).getLatitude());
            longValue = Double.parseDouble(dropPointsModelArrayList.get(i).getLongitude());
            mList.add(new MarkerModelClass(latValue, longValue, name));
        }*/

    //populat the animatrion of list view
    private void childAnimationListView() {
        modelArrayList = new ArrayList<PickupPointModel>();
        for (int i = 0; i < studentsArray.size(); i++) {
            modelArrayList.add(new PickupPointModel(studentsArray.get(i).getStudentClass(), studentsArray.get(i).getStudentpic(), studentsArray.get(i).getRegistrationId(), studentsArray.get(i).getFirstName(), studentsArray.get(i).getLastName(), studentsArray.get(i).getIsBoarding()));
        }

        if (studentsArray.size() != 0 && studentsArray != null) {
            mpickup_point_listadapter = new PickupPointAdapter(this, modelArrayList);

            listview.setAdapter(mpickup_point_listadapter);

        }


        mSelectChildrenButton.setVisibility(View.VISIBLE);
        mPickupNotifyNextButton.setVisibility(View.INVISIBLE);
        //TODO:

        if (clickMapButton) {

            mSelectChildrenButton.setVisibility(View.INVISIBLE);
            mPickupNotifyNextButton.setVisibility(View.VISIBLE);
        }

        //Todo

        if (buttonClicked) {
            mSelectChildrenButton.setVisibility(View.INVISIBLE);
            mPickupNotifyNextButton.setVisibility(View.VISIBLE);
        }


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {




            }
        });


    }

    public void getPermissionToReadUserContacts() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
       /* if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            Boolean requestpermission=false;
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    ACCESS_COARSE_LOCATION_PERMISSION);
        }*/

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_PERMISSION);
        }else{
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (alert == null)
                    buildAlertMessageNoGps();

            } else {
                if (locationManager == null) {
                    getLocation();
                    callLocation();
                } else {
                    callLocation();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {



        if (requestCode == ACCESS_FINE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();

                permissiongranted = true;
                //mPickUpOrDrop.show();

            } else {
                permissiongranted = false;
                Toast.makeText(this, " Location permission denied", Toast.LENGTH_SHORT).show();
                finish();

            }
            if (permissiongranted){
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (alert == null)
                        buildAlertMessageNoGps();

                } else {
                    if (locationManager == null) {
                        getLocation();
                        callLocation();
                    } else {
                        callLocation();
                    }
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}