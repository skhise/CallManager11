package com.amcdesk.servicecrm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreen extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public String USERNAME= "loginEmail";
    public String PASSWORD="loginPassword";
    public static final String PREFS_NAME = "user_details";
    public String UserId="userId";
    public String CompanyId="companyId";
    public String USERROLE="Role";
    public String roleName="roleName";
    public String USERCNAME="companyName";
    public String IsCompanyActive="isCompanyActive";
    public String IsUserActive="IsActive";
    SharedPreferences sharedpreferences;
    UrlClass urlClass;
    Boolean checkInternet;
    private static final boolean AUTO_HIDE = true;
    public String deviceId="";

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        sharedpreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        Handler handler = new Handler();
        urlClass = new UrlClass(this);
        checkInternet = urlClass.checkInternet();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                   /* if(!Settings.canDrawOverlays(SplashScreen.this)){
                        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        startActivity(myIntent);
                    }*/
                    checkPermission();
                } else {
                    startApp();
                }
            }
        },1000);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }
    private void startApp(){

        final String loginName = sharedpreferences.getString(USERNAME,"");
        final String loginPassword = sharedpreferences.getString(PASSWORD,"");
        Integer loginUserId = sharedpreferences.getInt(UserId,0);
        Integer company_id = sharedpreferences.getInt(CompanyId,0);
        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.e("deviceId-->",deviceId);
        if(loginName!="" && loginPassword!="" && loginUserId!=0 && company_id !=0){
            try{
                checkInternet = urlClass.checkInternet();
                if(checkInternet){
                    if(deviceId!="" && deviceId!=null){
                        //new check_login().execute(loginName,loginPassword,deviceId);

                        final ProgressDialog pDialog = new ProgressDialog(this);
                        pDialog.setMessage("Loading...");
                        pDialog.show();
                        try{

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("loginName", loginName);
                            jsonObject.put("password", loginPassword);
                            jsonObject.put("deviceId", deviceId);

                            String url = "http://service.newpro.in/app_slim/v1/login?" +"loginName=" + loginName +"&password=" + loginPassword +"&deviceId=" + deviceId;

                            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                    url,jsonObject,
                                    new Response.Listener<JSONObject>() {

                                        @Override
                                        public void onResponse(JSONObject jsonObject) {
                                            Log.d("Login", jsonObject.toString());
                                            pDialog.hide();
                                            try {
                                                Integer code = jsonObject.getInt("code");
                                                if (code == 1) {
                                                    Integer uid = jsonObject.getInt("UserID");
                                                    Integer Companyid = jsonObject.getInt("CompanyId");
                                                    String UserName = jsonObject.getString("UserName");
                                                    Integer Role = jsonObject.getInt("Role");
                                                    //  String RoleName = jsonObject.getString("RoleName");
                                                    String RoleName = "User";
                                                    Boolean IsActive = jsonObject.getBoolean("IsActive");
                                                    Boolean IsActiveCompany = jsonObject.getBoolean("IsActiveCompany");
                                                    String CompanyName = jsonObject.getString("CompanyName");
                                                    if (!uid.equals("") && IsActive.equals(true) && !Companyid.equals("") && IsActiveCompany.equals(true) && (Role.equals("5") || RoleName.equals("User"))) {
                                                        try {

                                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                                            editor.putString(USERNAME, UserName);
                                                            editor.putString(PASSWORD, loginPassword);
                                                            editor.putInt(UserId, uid);
                                                            editor.putInt(CompanyId, Companyid);
                                                            editor.putString(roleName, RoleName);
                                                            editor.putInt(USERROLE, Role);
                                                            editor.putString(USERCNAME, CompanyName);
                                                            editor.putBoolean(IsCompanyActive, IsActiveCompany);
                                                            editor.putBoolean(IsUserActive, IsActive);


                                                            Intent userHome = new Intent(SplashScreen.this, userHome.class);
                                                            startActivity(userHome);
                                                            editor.putString("online", "1");
                                                            editor.apply();
                                                            editor.commit();
                                                            startService(new Intent(SplashScreen.this, onlineService.class));
                                                            startService(new Intent(SplashScreen.this, locationService.class));
                                                        } catch (Exception ee) {
                                                            Toast.makeText(getApplicationContext(), "Error in read user input" + ee.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                            Intent intent = new Intent(SplashScreen.this, Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Invalid user details, please check login details", Toast.LENGTH_LONG).show();
                                                        Intent intent = new Intent(SplashScreen.this, Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                } else if (code == 3) {
                                                    Toast.makeText(getApplicationContext(), "User active on other device", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(SplashScreen.this, Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Invalid user details, please check login details", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(SplashScreen.this, Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    finish();
                                                }


                                            } catch (Exception ee) {

                                            }

                                        }
                                    }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    VolleyLog.d("Login", "Error: " + error.getMessage());
                                    Toast.makeText(getApplicationContext(),"Login failed, Try again",Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(SplashScreen.this, Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                    pDialog.hide();
                                }
                            }){
                                @Override
                                public Request.Priority getPriority() {
                                    return Priority.IMMEDIATE;
                                }

                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    HashMap<String, String> headers = new HashMap<String, String>();
                                    headers.put("Content-Type", "application/json; charset=utf-8");

                                    return headers;
                                }
                            };


// Adding request to request queue

                            //   AppController.getInstance().addToRequestQueue(jsonObjReq);
                            RequestQueue queue = AppController.getInstance(getApplicationContext()).getRequestQueue();
                            queue.add(jsonObjReq);
                        }catch (Exception e){
                            pDialog.hide();
                            e.printStackTrace();
                            Toast.makeText(SplashScreen.this,"Login:"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SplashScreen.this, Login.class);
                            startActivity(intent);
                            finish();

                        }



                    } else {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Device Id");
                        builder.setMessage("Unable to get device Id");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Close App", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                builder.create().dismiss();

                            }
                        });
                        builder.setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startApp();
                                builder.create().dismiss();
                            }
                        });
                        builder.show();


                    }


                } else{
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Connection Failed");
                    builder.setMessage("Internet connection failed, Check connection and try again");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Close App", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            builder.create().dismiss();

                        }
                    });
                    builder.setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startApp();
                            builder.create().dismiss();
                        }
                    });
                    builder.show();


                }

            }catch (Exception ee){
                Toast.makeText(this,""+ee.getMessage(),Toast.LENGTH_LONG).show();
                //ontaskComplet(ee.getMessage());
            }
            /*
            Intent intent = new Intent(SplashScreen.this,userHome.class);
            startActivity(intent);*/
        } else {
            Intent intent = new Intent(SplashScreen.this, Login.class);
            startActivity(intent);
            finish();
        }
    }
    public class check_login extends AsyncTask<String,String, String> {

        String url = urlClass.getUrl();
        String NameSpace = urlClass.NameSpace();
        String SOAP_ACTION = NameSpace+"UserLogin";
        String UserPassword="";
        String METHOD_NAME = "UserLogin";
        public ProgressDialog dialog =
                new ProgressDialog(SplashScreen.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        @Override
        protected String doInBackground(String... params) {

            String UserName = params[0];
            UserPassword = params[1];
            String result = "";
            SoapObject request = new SoapObject(NameSpace, METHOD_NAME);
            request.addProperty("UserName",UserName);
            request.addProperty("Password",UserPassword);
            request.addProperty("deviceId",params[2]);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);
                result = ((SoapObject)envelope.bodyIn).getProperty(0).toString();

                if(request.equals("")){
                    Object re= null;
                    result = envelope.getResponse().toString();
                }

            } catch (Exception e) {
                System.out.println("Error"+e);
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.i("on progress",values.toString());
        }

        protected void onCancelled() {
            dialog.dismiss();
            Toast toast = Toast.makeText(SplashScreen.this,
                    "Error connecting to Server", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();
            /*show_local_db();*/
            startActivity(new Intent(SplashScreen.this,Login.class));
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("result","==>"+result);
            dialog.dismiss();
            if(result!="" && !result.equals(null)){
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Integer UserID = jsonObject.getInt("UserID");
                    Integer Companyid = jsonObject.getInt("CompanyId");
                    String UserName = jsonObject.getString("UserName");
                    Integer Role = jsonObject.getInt("Role");
                    Integer code = jsonObject.getInt("code");
                    //  String RoleName = jsonObject.getString("RoleName");
                    String RoleName="User";
                    Boolean IsActive = jsonObject.getBoolean("IsActive");
                    Boolean IsActiveCompany = jsonObject.getBoolean("IsActiveCompany");
                    String CompanyName = jsonObject.getString("CompanyName");
                    if(code==1 && !UserID.equals("") && IsActive.equals(true) && !Companyid.equals("") && IsActiveCompany.equals(true) &&(Role.equals("5") || RoleName.equals("User")) ){
                        try {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(USERNAME,UserName);
                            editor.putString(PASSWORD,UserPassword);
                            editor.putInt(UserId,UserID);
                            editor.putInt(CompanyId,Companyid);
                            editor.putString(roleName,RoleName);
                            editor.putInt(USERROLE,Role);
                            editor.putString(USERCNAME,CompanyName);
                            editor.putBoolean(IsCompanyActive,IsActiveCompany);
                            editor.putBoolean(IsUserActive,IsActive);
                            editor.putString("online","1");
                            editor.apply();
                            editor.commit();
                            Intent userHome = new Intent(SplashScreen.this, userHome.class);
                            startActivity(userHome);
                            startService(new Intent(SplashScreen.this,onlineService.class));
                            startService(new Intent(SplashScreen.this,locationService.class));
                        } catch (Exception ee){
                            Log.e("Ex:",ee.getLocalizedMessage());
                            Toast.makeText(getApplicationContext(),"Error in read user input"+ee.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SplashScreen.this,Login.class));
                        }
                    } else if(code == 3){

                        final AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                        builder.setTitle("User active");
                        builder.setMessage("User active on other device");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Close App", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                builder.create().dismiss();

                            }
                        });
                        builder.setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(SplashScreen.this,Login.class));
                                builder.create().dismiss();
                            }
                        });
                        builder.show();


                    } else {
                        Toast.makeText(getApplicationContext(),"Invalid user details, please check login details",Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(new Intent(SplashScreen.this,Login.class));

                    }

                }catch (Exception ee){

                    Toast.makeText(getApplicationContext(),ee.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SplashScreen.this,Login.class));
                }

            } else {
                Toast.makeText(getApplicationContext(),"Login Failed, Login Manually.",Toast.LENGTH_LONG).show();
                startActivity(new Intent(SplashScreen.this,Login.class));
            }
        }
    }
    private void checkPermission() {
        if (
                ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.READ_PHONE_STATE)+
                        ContextCompat.checkSelfPermission(SplashScreen.this,Manifest.permission.INTERNET)+
                        ContextCompat.checkSelfPermission(SplashScreen.this,Manifest.permission.CAMERA)+
                        ContextCompat.checkSelfPermission(SplashScreen.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)+
                        ContextCompat.checkSelfPermission(SplashScreen.this,Manifest.permission.READ_EXTERNAL_STORAGE)+
                        ContextCompat.checkSelfPermission(SplashScreen.this,Manifest.permission.ACCESS_FINE_LOCATION)

                        != PackageManager.PERMISSION_GRANTED) {
            if (
                    ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this, Manifest.permission.READ_PHONE_STATE) ||
                            ActivityCompat.shouldShowRequestPermissionRationale
                                    (SplashScreen.this, Manifest.permission.INTERNET)){
                Snackbar.make(SplashScreen.this.findViewById(android.R.id.content),
                        "Please Grant Permissions",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{
                                                Manifest.permission.INTERNET,
                                                Manifest.permission.READ_PHONE_STATE,
                                                Manifest.permission.CAMERA,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                        },
                                        1);
                            }
                        }).show();
            } else {
                requestPermissions(
                        new String[]{
                                Manifest.permission.INTERNET,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                        },
                        1);
            }
        } else {
            startApp();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    boolean READ_PHONE_STATE = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean INTERNET = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(READ_PHONE_STATE && INTERNET ){
                        Intent intent = new Intent(SplashScreen.this, Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Snackbar.make(SplashScreen.this.findViewById(android.R.id.content),
                                "Please Grant Permissions",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        requestPermissions(
                                                new String[]{
                                                        Manifest.permission.INTERNET,
                                                        Manifest.permission.READ_PHONE_STATE,
                                                        Manifest.permission.CAMERA,
                                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                                        Manifest.permission.ACCESS_FINE_LOCATION,


                                                },
                                                1);
                                    }
                                }).show();
                    }
                }
                break;
        }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bardeviceId
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
