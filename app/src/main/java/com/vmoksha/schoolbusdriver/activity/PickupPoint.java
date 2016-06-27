package com.vmoksha.schoolbusdriver.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vmoksha.schoolbusdriver.R;
import com.vmoksha.schoolbusdriver.adapters.PickupPointAdapter;
import com.vmoksha.schoolbusdriver.fragment.DrawerFragment;
import com.vmoksha.schoolbusdriver.model.ModelClass;
import com.vmoksha.schoolbusdriver.model.PickupPointModel;

import java.util.ArrayList;

public class PickupPoint extends AppCompatActivity {

    private TextView mToolBar_Title;
    private RelativeLayout mBackToMapLayout;
    private LinearLayout mPickuppointButon, mPickup_point_notifynext;
    /*public static int count=1;*/
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    ListView listView;
    /*ListView mBoardedStudentList;*/
    Toolbar toolbar;
    TextView mTitle;
    private LinearLayout mlinearlayout;
    public ImageView imgMenuDrawer;
    DrawerLayout drawerLayout;
    private LinearLayout mlinearlayout1;
    DrawerFragment mDrawerFragment;
    PickupPointAdapter mpickup_point_listadapter;
    private TextView mNotboarding;
    private boolean isPickUp;
    /* BoardedChildrenAdapter mBoardedChildrenAdapter;*/
    //  public static int[] childpic = {R.drawable.childpic, R.drawable.childpic, R.drawable.childpic, R.drawable.childpic};
    public static String[] childName = {"Mickie Biros", "Sage Tuggle", "Maryanne Valdes", "Jerri Morning"};
    public static String[] childClass = {"class 5", "class 2", "UKG", "class 1"};
    public static String[] childRoll = {"Roll no 15", "Roll no 22", "Roll no 18", "Roll no 5"};
   /* private static int[] childrenSelect = {R.drawable.children_icon_selected, R.drawable.children_icon_selected,
            R.drawable.children_icon_selected, R.drawable.children_icon_selected};*/
   /* private boolean[] select = {false, true, false, false}; //
    private boolean[] childSelection = {true, false, true, true}; //selection images
    private boolean[] showText = {false, true, false, false}; //boarding text*/

    private ArrayList<PickupPointModel> pickupPointArrayList;
    private ArrayList<PickupPointModel> modelArrayList = new ArrayList<PickupPointModel>();
    public static boolean buttonClicked = false; //select children button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_pickup_point);
        sharedPreferences = getSharedPreferences(ModelClass.DriverLoginPreference, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Intent intent = getIntent();
        isPickUp = getIntent().getExtras().getBoolean("isPickUp");
       /* JourneyType = getIntent().getExtras().getString("JourneyType");*/

        initializeUi();

        setSupportActionBar(toolbar);

        mTitle.setText("Pickup Point");

        imgMenuDrawer = (ImageView) findViewById(R.id.menu_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerFragment = (DrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragement);
        mDrawerFragment.setUp(R.id.fragement, drawerLayout);

        pickupPointArrayList = DashBoardActivityNew.studentsArray;
        mBackToMapLayout.setOnClickListener(listener);
        mPickuppointButon.setOnClickListener(listener);
        mPickup_point_notifynext.setOnClickListener(listener);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        imgMenuDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);

            }
        });

        for (int i = 0; i < pickupPointArrayList.size(); i++) {


            //modelArrayList.add(new PickupPointModel("Class 5", childpic[i], childrenSelect[i], pickupPointArrayList.get(i).getRegistrationId(), pickupPointArrayList.get(i).getFirstName(), select[i], childSelection[i], showText[i]));
            // modelArrayList.add(new PickupPointModel(pickupPointArrayList.get(i).getStudentClass(),childpic[i],pickupPointArrayList.get(i).getRegistrationId(),pickupPointArrayList.get(i).getFirstName(),pickupPointArrayList.get(i).getIsBoarding(),childSelection[i],showText[i]));

            modelArrayList.add(new PickupPointModel(pickupPointArrayList.get(i).getStudentClass(),pickupPointArrayList.get(i).getStudentpic(),pickupPointArrayList.get(i).getRegistrationId(),pickupPointArrayList.get(i).getFirstName(),pickupPointArrayList.get(i).getLastName(),pickupPointArrayList.get(i).getIsBoarding()));

        }

        if (pickupPointArrayList.size() != 0 && pickupPointArrayList != null) {
            mpickup_point_listadapter = new PickupPointAdapter(this, modelArrayList);

            listView.setAdapter(mpickup_point_listadapter);

        }

        mlinearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked = true;


                for (int i = 0; i < modelArrayList.size(); i++) {
                    mlinearlayout.setVisibility(View.INVISIBLE);
                    mlinearlayout1.setVisibility(View.VISIBLE);
                    //   pickupPointArrayList.get(i).setSelect(true);
                    //   pickupPointArrayList.get(i).setChildSelection(false);
                  /*  modelArrayList.get(i).setShowText(false);
                    if (i == 1) {
                        modelArrayList.get(i).setSelect(true);
                        modelArrayList.get(i).setChildSelection(true);


                    } else {
                        modelArrayList.get(i).setChildSelection(false);
                    }*/

                }

          /*    for (int i = 0; i < modelArrayList.size(); i++) {
                    mlinearlayout.setVisibility(View.INVISIBLE);
                    mlinearlayout1.setVisibility(View.VISIBLE);

                   // pickupPointArrayList.get(i).setSelect(true);
                    //   pickupPointArrayList.get(i).setChildSelection(false);

                 *//*   if (showText[i] == true) {
                        modelArrayList.get(i).setSelect(false);
                        modelArrayList.get(i).setChildSelection(false);

                    } else {
                        modelArrayList.get(i).setChildSelection(false);
                           *//**//* modelArrayList.get(i).setSelect(true);
                            modelArrayList.get(i).setChildSelection(true);*//**//*
                    }
*//*
                        if (showText[i] == true) {
                            modelArrayList.get(i).setSelect(true);
                            modelArrayList.get(i).setChildSelection(true);

                        } else {
                            modelArrayList.get(i).setChildSelection(false);
                           *//* modelArrayList.get(i).setSelect(true);
                            modelArrayList.get(i).setChildSelection(true);*//*
                        }
                    modelArrayList.get(i).setShowText(false);

                    //I commented this below code
                   *//* if (i == 1) {
                        modelArrayList.get(i).setSelect(true);
                        modelArrayList.get(i).setChildSelection(true);


                    } else {
                        modelArrayList.get(i).setChildSelection(false);
                    }*//*



                }*/
               /* buttonClicked=false;*/

                mpickup_point_listadapter.notifyDataSetChanged();
              /*  buttonClicked = false;*/
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               /* if(buttonClicked){
                   if( modelArrayList.get(position).getIsBoarding()){

                   }else{

                   }

                }else{

                }*/

              /*  if (buttonClicked) {


                    modelArrayList.get(position).setShowText(false);


                    if (modelArrayList.get(position).isSelect()) {
                        modelArrayList.get(position).setChildSelection(false);
                        modelArrayList.get(position).setSelect(false);
                    } else {
                        modelArrayList.get(position).setChildSelection(true);
                        modelArrayList.get(position).setSelect(true);

                    }

                    mpickup_point_listadapter.notifyDataSetChanged();
                   *//* buttonClicked = false;*//*
                }*/

               /* if(buttonClicked){
                    if(!modelArrayList.get(position).getIsBoarding()){


                    }
                }*/
            }
        });


    }

    private void initializeUi() {

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolBar_Title = (TextView) findViewById(R.id.toolbar_title);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mToolBar_Title.setText("Pickup Point");
        mlinearlayout = (LinearLayout) findViewById(R.id.pickup_point_linearbutton);
        mlinearlayout1 = (LinearLayout) findViewById(R.id.pickup_point_linearbutton1);
        mPickuppointButon = (LinearLayout) findViewById(R.id.pickup_point_linearmapbutton);
    //    mPickup_point_notifynext = (LinearLayout) findViewById(R.id.pickup_point_linearbutton1);
        mBackToMapLayout = (RelativeLayout) findViewById(R.id.pickup_point_relativemapbutton);
        listView = (ListView) findViewById(R.id.pickup_point_listview);

    }

    public void onBackPressed() {
        super.onBackPressed();
        /*startActivity(new Intent(PickupPoint.this, DashBoardActivity.class));*/
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*ApplicationSchoolbusDriver.PwdactivityPaused();*/

       /* overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);*/
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override

        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.pickup_point_relativemapbutton:


                   /* Intent intent = new Intent(PickupPoint.this, DashBoardActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);*/

                /*Intent intent = new Intent(DashBoardActivity.this,PickupPoint.class);
                startActivity(intent);*/
                    break;
                case R.id.pickup_point_linearbutton1:
                    /*for (int i = 0; i < TransportActivity.mArrayList.size(); i++) {*/
                    if (isPickUp) {

                        if (sharedPreferences.getInt(ModelClass.PickupPointCount, 0) < TransportActivity.mArrayList.size() - 1) {
                            editor.putInt(ModelClass.PickupPointCount, sharedPreferences.getInt(ModelClass.PickupPointCount, 0) + 1);
                            editor.commit();
                            buttonClicked = false;
                            DashBoardActivityNew.mStartButton.setText("Pickup Point");
                            //  listOfStudents(isPickUp);
                            finish();

                        } else {
                            DashBoardActivityNew.mStartButton.setText("Stop");
                            mpickup_point_listadapter.notifyDataSetChanged();
                            buttonClicked = false;
                            finish();

                       /* finish();*/


                        }
                    } else {

                        if (sharedPreferences.getInt(ModelClass.DropPointCount, 0) < TransportActivity.mArrayList.size() - 1) {
                            editor.putInt(ModelClass.DropPointCount, sharedPreferences.getInt(ModelClass.DropPointCount, 0) + 1);
                            editor.commit();
                            buttonClicked = false;
                            DashBoardActivityNew.mStartButton.setText("Drop Point");
                            finish();

                        } else {
                            DashBoardActivityNew.mStartButton.setText("Stop");
                            mpickup_point_listadapter.notifyDataSetChanged();
                            finish();

                            buttonClicked = false;
                     /*   finish();
*/

                        }
                    }


                    overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                 /*   }*/

                    /*DashBoardActivity.mStartButton.setText("Stop");
                    finish();*/

                    break;

                case R.id.pickup_point_linearmapbutton:
                    finish();
                    overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);

                    toolbar.collapseActionView();

                    if(buttonClicked){
                        buttonClicked = false;
                    }


                    break;
                case R.id.menu_drawer:
                    drawerLayout.openDrawer(Gravity.LEFT);
                    break;
            }
        }
    };

}
