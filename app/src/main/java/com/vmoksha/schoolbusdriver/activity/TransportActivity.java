package com.vmoksha.schoolbusdriver.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.vmoksha.schoolbusdriver.R;
import com.vmoksha.schoolbusdriver.httprequest.HTTP_GET;
import com.vmoksha.schoolbusdriver.httprequest.HTTP_POST;
import com.vmoksha.schoolbusdriver.model.DropPointsModel;
import com.vmoksha.schoolbusdriver.model.ModelClass;
import com.vmoksha.schoolbusdriver.util.Connectivity;
import com.vmoksha.schoolbusdriver.util.ProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TransportActivity extends AppCompatActivity implements DialogInterface.OnCancelListener {
    private Button mPickUpButton, mDropButon;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static ArrayList<DropPointsModel> mArrayList;
    ArrayList mDropPointsModelArrayList = new ArrayList();
    public final boolean isPickUP = false;
    private String PickUpCode;
    private String JourneyType;
    private String DropCode;
    private ProgressHUD progressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);
        getSupportActionBar().hide();
        initializeUi();
        sharedPreferences = getSharedPreferences(ModelClass.DriverLoginPreference, Context.MODE_PRIVATE);
        mPickUpButton.setOnClickListener(listener);
        mDropButon.setOnClickListener(listener);
        getBusDetails();
        mPickUpButton.setOnClickListener(listener);
        mDropButon.setOnClickListener(listener);

    }

    private void initializeUi() {
        mPickUpButton = (Button) findViewById(R.id.pickup_button);
        mDropButon = (Button) findViewById(R.id.drop_button);

    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                // on click of login button call login API
                case R.id.pickup_button:

                    String PickUpCode = sharedPreferences.getString(ModelClass.PickUpCode, "");
                    JourneyType = "Pickup";
                    //isPickUP=true;
                    GetRoute(PickUpCode, true, JourneyType);


                    break;
                case R.id.drop_button:

                    // custom dialog
                    DropCode = sharedPreferences.getString(ModelClass.DropCode, "");
                    // isPickUP=false;
                    JourneyType = "Drop";
                    GetRoute(DropCode, false, JourneyType);
                    /*Intent dropIntent = new Intent(getApplicationContext(), DashBoardActivityNew.class);

                    dropIntent.putExtra("isPickUP",isPickUP);
                    startActivity(dropIntent);*/
                    break;
            }
        }
    };

    private void getBusDetails() {
        if (Connectivity.isConnected(getApplicationContext())) {

            try {
                //  final ProgressDialog mProgressDialoge = new ProgressDialog(TransportActivity.this);
                HTTP_GET httpGet = new HTTP_GET() {



                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        if (progressHUD == null || !progressHUD.isShowing())
                            progressHUD = ProgressHUD.show(TransportActivity.this, "", true, true, TransportActivity.this);

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
                                String Bus = response.getString("Bus");
                                if (success.contentEquals("true")) {
                                    JSONObject jsonData = response.getJSONObject("Bus");
                                    String RouteCode = jsonData.getString("RouteCode");
                                    String pickUpCode = RouteCode.substring(0, RouteCode.indexOf(","));
                                    String dropCode = RouteCode.substring(RouteCode.indexOf(",") + 1, RouteCode.length());
                                    String Name = jsonData.getString("Name");
                                    String Status = jsonData.getString("Status");
                                    Integer Count = jsonData.getInt("StudentCount");
                                    editor = sharedPreferences.edit();
                                    editor.putInt(ModelClass.Count, Count);
                                    editor.putString(ModelClass.BusNumber, Name);
                                    editor.putString(ModelClass.RouteCode, RouteCode);
                                    editor.putString(ModelClass.PickUpCode, pickUpCode);
                                    editor.putString(ModelClass.DropCode, dropCode);
                                    editor.commit();

                                    //   GetRoute();
                                } else {
                                    Toast.makeText(getApplicationContext(), response.getString("Success"), Toast.LENGTH_SHORT)
                                            .show();
                                }
                            } catch (Exception e) {
                               /* Log.e(TAG, "Login" + e.getMessage().toString());*/
                                Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                };
                String EntityCode = sharedPreferences.getString(ModelClass.EntityCode, "");
                httpGet.execute(ModelClass.BUS_API_URL + EntityCode);
              /*  busCodeObject.toString()*/
            } catch (Exception e) {

            }
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }


    }

    //call the method for getting the list of droppoints
    private void GetRoute(String DropCodeOrPickup, final boolean isPickUP, final String JourneyType) {
        if (Connectivity.isConnected(getApplicationContext())) {

            try {
                HTTP_GET httpGet = new HTTP_GET() {

                    @Override
                    protected void onPreExecute() {

                        super.onPreExecute();
                        if (progressHUD == null || !progressHUD.isShowing())
                            progressHUD = ProgressHUD.show(TransportActivity.this, "", true, true, TransportActivity.this);

                    }

                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        if (progressHUD.isShowing()) {
                            progressHUD.dismiss();
                        }

                        if (result != null) {

                            try {

                                JSONObject routeresponse = new JSONObject(result);
                                String success = routeresponse.getString("Success");
                                JSONObject routeObject = routeresponse.getJSONObject("Route");
                                if (success.contentEquals("true")) {


                                    String RouteCode = routeObject.getString("RouteCode");
                                    String RouteName = routeObject.getString("RouteName");
                                    String StartPoint = routeObject.getString("StartPoint");
                                    String EndPoint = routeObject.getString("EndPoint");
                                    String CompanyCode = routeObject.getString("CompanyCode");

                                    //route json array list
                                    mArrayList = new ArrayList<DropPointsModel>();
                                    JSONArray dropJsonArray = routeresponse.getJSONArray("DropPoints");

                                    if ((dropJsonArray != null) && (dropJsonArray.length() != 0)) {
                                        for (int j = 0; j < dropJsonArray.length(); j++) {
                                            DropPointsModel mDropPointsModel = new DropPointsModel();
                                            JSONObject dropdetailsObject = dropJsonArray.getJSONObject(j);
                                            mDropPointsModel.setArrivalTime(dropdetailsObject.getString("ArrivalTime"));
                                            mDropPointsModel.setDropPointCode(dropdetailsObject.getString("DropPointCode"));
                                            mDropPointsModel.setDropPointName(dropdetailsObject.getString("DropPointName"));
                                            mDropPointsModel.setLatitude(dropdetailsObject.getString("Latitude"));
                                            mDropPointsModel.setLongitude(dropdetailsObject.getString("Longitude"));
                                            mDropPointsModel.setSequenceId(dropdetailsObject.getString("SequenceId"));
                                            mArrayList.add(mDropPointsModel);

                                            //Boolean isPickUP=isPickUP;


                                        }

                                        Intent i = new Intent(getApplicationContext(), DashBoardActivityNew.class);
                                        i.putExtra("isPickUP", isPickUP);
                                        i.putExtra("JourneyType", JourneyType);
                                        startActivity(i);
                                    }


                                } else {
                                    Toast.makeText(getApplicationContext(), "Login Failed! Please check your Username and Password", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            } catch (Exception e) {
                                Log.e("exception message", e.getMessage().toString());
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    }


                };
                DropCodeOrPickup = DropCodeOrPickup;
                //DropCodeOrPickup
                httpGet.execute(ModelClass.BUS_ROUTE_API + DropCodeOrPickup);


            } catch (Exception e) {

            }
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onCancel(DialogInterface dialog) {

    }
}

