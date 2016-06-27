package com.vmoksha.schoolbusdriver.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.vmoksha.schoolbusdriver.R;
import com.vmoksha.schoolbusdriver.model.ModelClass;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIME_OUT = 3000;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                sharedPreferences = getApplicationContext().getSharedPreferences(ModelClass.DriverLoginPreference, Context.MODE_PRIVATE);
                Log.e("isLogin ", "" + sharedPreferences.getInt(ModelClass.isLogin, 0));
                //if (sharedPreferences.contains(ModelClass.isLogin)) {
                    if (sharedPreferences.getInt(ModelClass.isLogin, 0)==1) {
                        SplashScreenActivity.this.finish();
                        startActivity(new Intent(SplashScreenActivity.this, TransportActivity.class));
                    /*overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);*/
                    } else {
                        SplashScreenActivity.this.finish();
                        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    /*overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);*/
                    }
                }
           // }
        }, SPLASH_SCREEN_TIME_OUT);

    }
    }



