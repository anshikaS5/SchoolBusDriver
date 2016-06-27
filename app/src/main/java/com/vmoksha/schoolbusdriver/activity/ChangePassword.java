package com.vmoksha.schoolbusdriver.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vmoksha.schoolbusdriver.R;
import com.vmoksha.schoolbusdriver.fragment.DrawerFragment;
import com.vmoksha.schoolbusdriver.httprequest.HTTP_PUT;
import com.vmoksha.schoolbusdriver.model.Constant;
import com.vmoksha.schoolbusdriver.model.ModelClass;
import com.vmoksha.schoolbusdriver.util.Connectivity;
import com.vmoksha.schoolbusdriver.util.ProgressHUD;

import org.json.JSONObject;

public class ChangePassword extends AppCompatActivity implements DialogInterface.OnCancelListener {
    Button changePassword_button;
    EditText editOldpwd, editNewpwd;
    private TextView mToolBar_Title;
    public ImageView imgMenuDrawer, changeOldPwd, changeNewPwd;
    DrawerLayout drawerLayout;
    DrawerFragment mDrawerFragment;
    private ProgressHUD progressHUD;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    String Code;
    int UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_change_password);
        sharedPreferences = getApplicationContext().getSharedPreferences(ModelClass.DriverLoginPreference, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolBar_Title = (TextView) findViewById(R.id.toolbar_title);
        mToolBar_Title.setText("Change Password");
        setSupportActionBar(toolbar);


        imgMenuDrawer = (ImageView) findViewById(R.id.menu_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerFragment = (DrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragement);
        mDrawerFragment.setUp(R.id.fragement, drawerLayout);
        changePassword_button = (Button) findViewById(R.id.changepwd_button);
        editOldpwd = (EditText) findViewById(R.id.edit_oldpwd);
        editNewpwd = (EditText) findViewById(R.id.edit_newpwd);
        changeOldPwd = (ImageView) findViewById(R.id.forgot_oldpassword_visibility);
        changeNewPwd = (ImageView) findViewById(R.id.forgot_newpassword_visibility);
       /* Typeface tf = Typeface.createFromAsset(
                getBaseContext().getAssets(), "Lato-Regular.ttf");
        editOldpwd.setTypeface(tf);
        editNewpwd.setTypeface(tf);*/

        changeOldPwd.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                   /* Typeface tf = Typeface.createFromAsset(
                            getBaseContext().getAssets(), "Lato-Regular.ttf");*/
                    editOldpwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                    //  editNewpwd.setTypeface(tf);
                    return true;
                    //      on release hide password back
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                   /* Typeface tf = Typeface.createFromAsset(
                            getBaseContext().getAssets(), "Lato-Regular.ttf");*/
                    editOldpwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                    //editNewpwd.setTypeface(tf);
                    return true;
                }
                return false;
            }
        });


        changeNewPwd.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                   /* Typeface tf = Typeface.createFromAsset(
                            getBaseContext().getAssets(), "Lato-Regular.ttf");*/
                    editNewpwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                  //  editNewpwd.setTypeface(tf);
                    return true;
                    //      on release hide password back
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                   /* Typeface tf = Typeface.createFromAsset(
                            getBaseContext().getAssets(), "Lato-Regular.ttf");*/
                    editNewpwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                    //editNewpwd.setTypeface(tf);
                    return true;
                }
                return false;
            }
        });

        /*changeNewPwd.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    editNewpwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    editNewpwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                    return true;
                }
                return false;
            }
        });*/

        editNewpwd.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:


                        String strOldPassword = editOldpwd.getText().toString().trim();
                        String strNewPassword = editNewpwd.getText().toString().trim();
                        UserId = sharedPreferences.getInt(ModelClass.UserId, 0);
                        Code = sharedPreferences.getString(ModelClass.Code, "");


                        if (strOldPassword.trim().equals("")) {

                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.old_pass_message), Toast.LENGTH_SHORT).show();
                        } else if (strNewPassword.trim().equals("")) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.new_pass_message), Toast.LENGTH_SHORT).show();
                        } else {
                            if (strOldPassword.length() >= 4 && strNewPassword.length() >= 4) {
                                PasswordisEntered(strOldPassword, strNewPassword, Code, UserId);
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.password_min_length_msg), Toast.LENGTH_LONG).show();
                            }
                        }


                        break;
                    case EditorInfo.IME_ACTION_NEXT:
                        // next stuff
                        break;
                }
                return true;
            }

        });


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
        editOldpwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        editNewpwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        changePassword_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strOldPassword = editOldpwd.getText().toString().trim();
                String strNewPassword = editNewpwd.getText().toString().trim();
                UserId = sharedPreferences.getInt(ModelClass.UserId, 0);
                Code = sharedPreferences.getString(ModelClass.Code, "");


                if (strOldPassword.trim().equals("")) {

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.old_pass_message), Toast.LENGTH_SHORT).show();
                } else if (strNewPassword.trim().equals("")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.new_pass_message), Toast.LENGTH_SHORT).show();
                } else {
                    if (strOldPassword.length() >= 4 && strNewPassword.length() >= 4) {
                        PasswordisEntered(strOldPassword, strNewPassword, Code, UserId);
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.password_min_length_msg), Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
    }


    private void PasswordisEntered(String strOldPassword, String strNewPassword, String Code, int Id) {

        if (Connectivity.isConnected(getApplicationContext())) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("OldPassword", strOldPassword);
                jsonObject.put("NewPassword", strNewPassword);
                jsonObject.put("Code", Code);
                jsonObject.put("UserID", Id);
                jsonObject.put("RoleCode", Constant.DRIVER_ROLE);
                jsonObject.put("MethodType", "PUT");


                HTTP_PUT http_put = new HTTP_PUT() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        if (progressHUD == null || !progressHUD.isShowing())
                            progressHUD = ProgressHUD.show(ChangePassword.this, "", true, true, ChangePassword.this);

                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (progressHUD.isShowing()) {
                            progressHUD.dismiss();
                        }


                        if (s != null && !s.isEmpty()) {
                            try {
                                JSONObject resultJsonObject = new JSONObject(s);
                                if (resultJsonObject.getBoolean("Success")) {

                                    Toast.makeText(getApplicationContext(), resultJsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                                    editor=sharedPreferences.edit();
                                    editor.putString(ModelClass.UserName,"");
                                    editor.putString(ModelClass.PickupPointCount,"");
                                    editor.putInt(ModelClass.isLogin, 0);
                                    editor.clear();
                                    editor.commit();
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), resultJsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Error " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error ", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                http_put.execute(ModelClass.CHANGE_PASS_URL + Id, jsonObject.toString());
            } catch (Exception e) {

            }
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

 /*   public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ChangePassword.this, DashBoardActivity.class));
        finish();
    }*/
}
