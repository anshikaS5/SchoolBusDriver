package com.vmoksha.schoolbusdriver.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;

import android.view.KeyEvent;

import android.view.Gravity;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;

import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vmoksha.schoolbusdriver.R;
import com.vmoksha.schoolbusdriver.httprequest.HTTP_POST;
import com.vmoksha.schoolbusdriver.model.Constant;
import com.vmoksha.schoolbusdriver.model.ModelClass;
import com.vmoksha.schoolbusdriver.util.Connectivity;
import com.vmoksha.schoolbusdriver.util.FontsOverride;
import com.vmoksha.schoolbusdriver.util.ProgressHUD;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity implements DialogInterface.OnCancelListener, View.OnClickListener {

    private TextView mForgotPassword;
    private Button mLoginButton, mSendButton;
    private EditText mUser_name, mPassword, mforgotdialogPwd, mForgotEdittext;
    private Dialog mDialogForgotPwd;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Dialog dialog;
    String DriveProfileUrl;
    private ProgressHUD progressHUD;
    ImageView mShowPwdicon;
    String strUserName;
    String strPassword;
    Typeface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        getSupportActionBar().hide();
        initializeUi();
        sharedPreferences = getApplicationContext().getSharedPreferences(ModelClass.DriverLoginPreference, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mForgotPassword.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);


        mPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:


                        strUserName = mUser_name.getText().toString().trim();
                        strPassword = mPassword.getText().toString().trim();

                        if (strUserName.equals("")) {
                            Toast.makeText(getApplicationContext(), " " + getResources().getString(R.string.login_username_msg), Toast.LENGTH_SHORT).show();
                        } else if (strPassword.equals("")) {
                            Toast.makeText(getApplicationContext(), " " + getResources().getString(R.string.login_passwordfield_msg), Toast.LENGTH_SHORT).show();
                        } else {

                            authenticateUser(strUserName, strPassword);

                        }


                        break;
                    case EditorInfo.IME_ACTION_NEXT:
                        // next stuff
                        break;
                }
                return true;
            }

        });

        mUser_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });


    }

    private void initializeUi() {
        mForgotPassword = (TextView) findViewById(R.id.forgot_password);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mUser_name = (EditText) findViewById(R.id.user_name);
        mPassword = (EditText) findViewById(R.id.password);
        mPassword.setTypeface(Typeface.DEFAULT);
        mPassword.setTransformationMethod(new PasswordTransformationMethod());
       /* tf = Typeface.createFromAsset(
                getBaseContext().getAssets(), "Lato-Regular.ttf");
        mPassword.setTypeface(tf);
        setLayoutFont();*/

        forgotPasswordDialog();
        //mforgotdialogPwd = (EditText) findViewById(R.id.forgotpwd_edittext);
        mShowPwdicon = (ImageView) findViewById(R.id.login_password_visibilty);
        mShowPwdicon.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    /*Typeface tf = Typeface.createFromAsset(
                            getBaseContext().getAssets(), "Lato-Regular.ttf");*/

                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                    mPassword.setTypeface(tf);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                   /* Typeface tf = Typeface.createFromAsset(
                            getBaseContext().getAssets(), "Lato-Regular.ttf");*/
                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                    mPassword.setTypeface(tf);
                    return true;
                }
                return false;
            }
        });
       /* mShowPwdicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPassword.getInputType() ==(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD))
                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                else
                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });*/

        //forgotPwdDialog();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // on click of login button call login API
            case R.id.login_button:
                strUserName = mUser_name.getText().toString().trim();
                strPassword = mPassword.getText().toString().trim();

                if (strUserName.equals("")) {
                    Toast.makeText(getApplicationContext(), " " + getResources().getString(R.string.login_username_msg), Toast.LENGTH_SHORT).show();
                } else if (strPassword.equals("")) {
                    Toast.makeText(getApplicationContext(), " " + getResources().getString(R.string.login_passwordfield_msg), Toast.LENGTH_SHORT).show();
                } else {

                    authenticateUser(strUserName, strPassword);
                       /* if (authenticateUser(strUserName, strPassword)) {
                            *//*editor = sharedPreferences.edit();
                            editor.putBoolean(ModelClass.isLogin, true);
                            editor.commit();
                            finish();
                            Intent i = new Intent(getApplicationContext(), TransportActivity.class);
                            startActivity(i);*//*
                        } else {

                            Toast.makeText(getApplicationContext(), " " + getResources().getString(R.string.login_passwordfield_msg), Toast.LENGTH_SHORT).show();
                        }
*/

                }
                break;
            case R.id.forgot_password:

                mDialogForgotPwd.show();

                   /* // custom dialog
                    Dialog dialog = new Dialog(LoginActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                  *//*  dialog.setContentView(R.layout.content_forgot_password);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();
                    break;*//*
                    final View dialogView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.content_forgot_password, null);
                    dialog.setContentView(dialogView);
                    ((Button) dialogView.findViewById(R.id.forgotpwd_button)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String strMobileOrEmail = ((EditText) dialogView.findViewById(R.id.forgotpwd_edittext)).getText().toString().trim();

                            //To hide softkey board in dialog box when user click other than edit text box
                            final Dialog settingdialog = new Dialog(LoginActivity.this);
                            settingdialog.setContentView(R.layout.content_forgot_password);
                            //  settingdialog.setTitle(theMessagesList.getMessage(MessageDefine.STR_SETTINGS));
                            settingdialog.getWindow().setGravity(Gravity.TOP);
                            // to hide a keyboard when a setting menu display
                            settingdialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            //Dialog no disappear when click outside
                            settingdialog.setCanceledOnTouchOutside(false);
                            final InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                            settingdialog.show();

                            //hide a keyboard on click outside dialog
                            View focus = getCurrentFocus();
                            if (focus != null) {
                                im.hideSoftInputFromWindow(focus.getWindowToken(), 0);
                            }*/


                break;
            case R.id.forgotpwd_edittext:
                break;
            case R.id.forgotpwd_button:
                String strMobileOrEmail = mForgotEdittext.getText().toString().trim();
                if (strMobileOrEmail != null && !strMobileOrEmail.isEmpty()) {
                    String regex = "[0-9]+";
                    boolean isMobile = false;
                    if (strMobileOrEmail.matches(regex)) {
                        isMobile = true;
                    } else {
                        isMobile = false;
                    }
                    if (isMobile) {
                        //TODO

                        if (strMobileOrEmail.length() == 10) {
                            mForgotEdittext.setText("");
                            if (mDialogForgotPwd.isShowing())
                                mDialogForgotPwd.dismiss();
                            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                            callForgotPasswordApi(strMobileOrEmail, isMobile);
                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter 10 digit mobile number", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (isValidEmail(strMobileOrEmail)) {
                            mForgotEdittext.setText("");
                            if (mDialogForgotPwd.isShowing())
                                mDialogForgotPwd.dismiss();
                            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                            callForgotPasswordApi(strMobileOrEmail, isMobile);
                        } else {

                            Toast.makeText(getApplicationContext(), "Please enter a valid Mobile No./Email Id", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter the mobile number", Toast.LENGTH_SHORT).show();
                }

        }

    }


    private void callForgotPasswordApi(String strData, boolean isMobile) {

        if (Connectivity.isConnected(getApplicationContext())) {

            try {
                JSONObject jsonObject = new JSONObject();
                if (isMobile) {
                    jsonObject.put("UserName", strData);

                } else {
                    jsonObject.put("UserName", strData);
                }

                HTTP_POST http_post = new HTTP_POST() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        if (progressHUD == null)
                    progressHUD = ProgressHUD.show(LoginActivity.this, "", true, true, LoginActivity.this);

                }

                    @Override
                    protected void onPostExecute(String s) {

                        super.onPostExecute(s);
                        if (progressHUD.isShowing()) {
                            progressHUD.dismiss();
                        }

                        if (s != null && !s.isEmpty()) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                if (jsonObject.getBoolean("Success")) {
                                    //If message from the server need to display
                                    Toast.makeText(LoginActivity.this, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                                    //Or
                                    // Toast.makeText(LoginActivity.this, "Password has been sent to the registered email", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(LoginActivity.this, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {

                            }
                        }
                        //check the response and validate and show confirmation toast and dismiss the dialog

                    }
                };
                //uncomment this line to call api
                http_post.execute(ModelClass.FORGOT_PASSWORD_URL, jsonObject.toString());
               /* Toast.makeText(getApplicationContext(), "Password has been sent to the registered email", Toast.LENGTH_SHORT).show();
                dialog.dismiss();*/
            } catch (Exception e) {

            }
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean authenticateUser(String userName, String passWord) {
        boolean status = false;
//        {"UserName":"admin@mydomain.com","Password":"admin123"}

        if (Connectivity.isConnected(getApplicationContext())) {

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(ModelClass.UserName, userName);
                jsonObject.put(ModelClass.Password, passWord);
                jsonObject.put(ModelClass.DriverRole, Constant.DRIVER_ROLE.toString());

                HTTP_POST http_post = new HTTP_POST() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        if (progressHUD == null)
                            progressHUD = ProgressHUD.show(LoginActivity.this, "", true, true, LoginActivity.this);
                        else if (!progressHUD.isShowing()) {
                            progressHUD = ProgressHUD.show(LoginActivity.this, "", true, true, LoginActivity.this);
                        }

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

                                    String TOKEN = resultJsonObject.getString("token");
                                    JSONObject userJsonObject = resultJsonObject.getJSONObject("User");

                                    Integer UserId = userJsonObject.getInt("Id");
                                    String Code = userJsonObject.getString("Code");
                                    String RegistrationId = userJsonObject.getString("RegistrationId");
                                    String FirstName = userJsonObject.getString("FirstName");
                                    String MiddleName = userJsonObject.getString("MiddleName");
                                    String LastName = userJsonObject.getString("LastName");
                                    String Email = userJsonObject.getString("Email");
                                    Long Mobile = Long.parseLong(userJsonObject.getString("Mobile"));
                                    String UserName = userJsonObject.getString("UserName");
                                    String Password = userJsonObject.getString("Password");
                                    String RoleCode = userJsonObject.getString("RoleCode");
                                    String GenderCode = userJsonObject.getString("GenderCode");
                                    String EntityCode = userJsonObject.getString("EntityCode");
                                    String CompanyCode = userJsonObject.getString("CompanyCode");
                                    String ImageJson = userJsonObject.getString("ImageJSON");
                                  /*  JSONObject imageObject = new JSONObject(ImageJson);
                                    String DriveProfileUrl = imageObject.getString("AWSFileUrl");*/


                                    if (!ImageJson.contentEquals("null") && !ImageJson.contentEquals("")) {
                                        JSONObject imageObject = new JSONObject(ImageJson);
                                        DriveProfileUrl = imageObject.getString("AWSFileUrl");
                                    } else {
                                        DriveProfileUrl = null;
                                    }

                                    editor = sharedPreferences.edit();
                                    editor.putInt(ModelClass.isLogin, 1);
                                    editor.putInt(ModelClass.UserId, UserId);
                                    editor.putString(ModelClass.Code, Code);
                                    editor.putString(ModelClass.RegistrationId, RegistrationId);
                                    editor.putString(ModelClass.FirstName, FirstName);
                                    editor.putString(ModelClass.MiddleName, MiddleName);
                                    editor.putString(ModelClass.LastName, LastName);
                                    editor.putString(ModelClass.Email, Email);
                                    editor.putLong(ModelClass.Mobile, Mobile);
                                    editor.putString(ModelClass.UserName, UserName);
                                    editor.putString(ModelClass.Password, Password);
                                    editor.putString(ModelClass.RoleCode, RoleCode);
                                    editor.putString(ModelClass.GenderCode, GenderCode);
                                    editor.putString(ModelClass.CompanyCode, CompanyCode);
                                    editor.putString(ModelClass.EntityCode, EntityCode);
                                    editor.putString(ModelClass.TOKEN, TOKEN);
                                    editor.putString(ModelClass.DriveProfileUrl, DriveProfileUrl);

                                    editor.commit();

                                    if (Constant.DRIVER_ROLE.contentEquals(RoleCode)) {
                                        finish();
                                        Intent i = new Intent(getApplicationContext(), TransportActivity.class);
                                        startActivity(i);
                                    } else {

                                    }


                                } else {
                                    //resultJsonObject.getString("Message")
                                    Toast.makeText(getApplicationContext(), "Username and password mismatch", Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                Log.i("error message", e.getMessage());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), " " + getResources().getString(R.string.login_server_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                http_post.execute(ModelClass.LOGIN_URL, jsonObject.toString());
            } catch (Exception e) {

            }
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

        return status;
    }


    public boolean isValidEmail(String email) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void forgotPasswordDialog() {
        mDialogForgotPwd = new Dialog(this);
        mDialogForgotPwd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogForgotPwd.setContentView(R.layout.forgot_password);
        mDialogForgotPwd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialogForgotPwd.setCancelable(true);

        TextView title = (TextView) mDialogForgotPwd.findViewById(R.id.forgotpwd_textview);
        mForgotEdittext = (EditText) mDialogForgotPwd.findViewById(R.id.forgotpwd_edittext);
        mSendButton = (Button) mDialogForgotPwd.findViewById(R.id.forgotpwd_button);
        final LinearLayout mdialogeRoot = (LinearLayout) mDialogForgotPwd.findViewById(R.id.forgot_root_layout);
        mdialogeRoot.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    hideKeyboard(view);
                }
            }
        });


        mdialogeRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mForgotEdittext.isFocusable())
                    hideKeyboard(view);
            }
        });

        mSendButton.setOnClickListener(this);
        mDialogForgotPwd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mForgotEdittext.setText("");
            }
        });

    }

   /* public void setLayoutFont() {
        // FontsOverride.setDefaultFont();
        Typeface tf = Typeface.createFromAsset(
                getBaseContext().getAssets(), "Lato-Regular.ttf");
        mPassword.setTypeface(tf);

*//*
        try {
            //create the font to use. Specify the size!
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("Fonts\\lato-Regular.ttf")).deriveFont(12f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            //register the font
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("Fonts\\lato-Bold.ttf.ttf")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch(FontFormatException e)
        {
            e.printStackTrace();
        }

        //use the font
        yourSwingComponent.setFont(customFont);*//*

    }*/

}



