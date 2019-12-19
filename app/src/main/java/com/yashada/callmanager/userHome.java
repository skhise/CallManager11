package com.yashada.callmanager;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kobjects.util.Util;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Calendar;
import java.util.Date;

public class userHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,ontaskComplet {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    public String PASSWORD="loginPassword";
    public static final String PREFS_NAME = "user_details";
    public String USERNAME= "loginEmail";
    public String UserId="userId";
    public String CompanyId="companyId";
    public String USERROLE="Role";
    public String USERCNAME="companyName";
    public String IsCompanyActive="isCompanyActive";
    public String IsUserActive="IsActive";
    SharedPreferences sharedpreferences;
    TextView login_engineer_name;
    Integer count=0;
    UrlClass urlClass;
    boolean checkInternet;
    Intent intentL,intentN;
    TextView cmp;
    CheckConnectivity checkConnectivity;
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }
    @Override
    protected void onDestroy() {
        Log.i("MAINACT", "onDestroy!");
        stopService(intentL);
        stopService(intentN);

        super.onDestroy();

    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());


        alertDialog.setTitle("GPS is not Enabled!");

        alertDialog.setMessage("Do you want to turn on GPS?");


        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });


        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertDialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        sharedpreferences   = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        login_engineer_name = (TextView) findViewById(R.id.login_engineer_name);
        urlClass = new UrlClass(userHome.this);
        checkInternet = urlClass.checkInternet();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        intentN = new Intent(userHome.this,check_notification.class);
        intentL = new Intent(userHome.this,locationService.class);
        if (!isMyServiceRunning(check_notification.class)) {

            startService(intentN);
        }

        if(!isMyServiceRunning(onlineService.class)){
            SharedPreferences.Editor editor =sharedpreferences.edit();
            editor.putString("online","1");
            editor.commit();
            startService(new Intent(userHome.this,onlineService.class));
        }
        if(!isMyServiceRunning(locationService.class)){
            try{

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    showSettingsAlert();
                } else {
                    startService(intentL);
                }

            } catch (Exception e){
                e.printStackTrace();
            }


        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                    String UserName     = sharedpreferences.getString(USERNAME,"");
                    Integer UserID       = sharedpreferences.getInt(UserId,0);
                    Integer CompanyID    = sharedpreferences.getInt(CompanyId,0);
                    Integer UuserRole    = sharedpreferences.getInt(USERROLE,0);
                    Boolean IsUseractive = sharedpreferences.getBoolean(IsUserActive,false);
                    if(!UserID.equals(0) || !CompanyID.equals(0)){
                        try{
                            checkInternet = urlClass.checkInternet();
                            if(checkInternet){
                                new loadUSerDashboard().execute(UserID,CompanyID);
                            } else{
                                onTaskCompleted("Internet connection failed, please check");
                            }
                        }catch (Exception ee){
                            onTaskCompleted(ee.getMessage());
                        }
                    } else {
                        Toast.makeText(userHome.this,"Unable to get user details, ",Toast.LENGTH_LONG).show();
                    }

                }catch (Exception ee){
                    Toast.makeText(userHome.this,"Unable to get user details, "+ee.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        try {
            sharedpreferences   = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            Integer userId = sharedpreferences.getInt(UserId,0);
            Integer companyId = sharedpreferences.getInt(CompanyId,0);
            if (!userId.equals("0") && !companyId.equals("0")){
                Log.e("userId",""+userId);
                Log.e("companyId",""+companyId);
                if(checkInternet){
                    new get_eng_info().execute(userId.toString(),companyId.toString());
                } else {
                    onTaskCompleted("Internet connection failed, please check");
                }

            }
        } catch (Exception ee){
            onTaskCompleted(ee.getMessage());
        }
        try{
            sharedpreferences   = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String UserName     = sharedpreferences.getString(USERNAME,"");
            Integer UserID       = sharedpreferences.getInt(UserId,0);
            Integer CompanyID    = sharedpreferences.getInt(CompanyId,0);
            Integer UuserRole    = sharedpreferences.getInt(USERROLE,0);
            Boolean IsUseractive = sharedpreferences.getBoolean(IsUserActive,false);
            login_engineer_name.setText(UserName);
            View header=navigationView.getHeaderView(0);
            TextView name = (TextView)header.findViewById(R.id.userName);
            TextView email = (TextView)header.findViewById(R.id.userEmail);
            cmp = (TextView)header.findViewById(R.id.userCompanyName);
            name.setText(UserName);
            email.setText(UserName);

            if(!UserID.equals(0) && !CompanyID.equals(0)){
                Log.e("UserID",""+UserID);
                try{
                    if(checkInternet){
                        new loadUSerDashboard().execute(UserID,CompanyID);
                        Date currentTime = Calendar.getInstance().getTime();
                        Intent ll24 = new Intent(getApplicationContext(), AlarmReceiverLifeLog.class);
                        PendingIntent recurringLl24 = PendingIntent.getBroadcast(getApplicationContext(), 0, ll24, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        alarms.setRepeating(AlarmManager.RTC_WAKEUP,currentTime.getTime(), AlarmManager.INTERVAL_HOUR, recurringLl24); // Log repetition

                    } else{
                        onTaskCompleted("Internet connection failed, please check");
                    }
                }catch (Exception ee){
                    onTaskCompleted(ee.getLocalizedMessage());
                }

            } else {
                Toast.makeText(this,"Unable to get user details, ",Toast.LENGTH_LONG).show();
            }

        }catch (Exception ee){
            Toast.makeText(this,"Unable to get user details, "+ee.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,  R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    public class AlarmReceiverLifeLog extends BroadcastReceiver {

        private static final String TAG = "LL24";
        Context context;

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.v(TAG, "Alarm for LifeLog...");

            Intent ll24Service = new Intent(context, check_notification.class);
            context.startService(ll24Service);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        try{

            this.count = count+1;
            if(this.count==2){
                finish();
            } else {
                Toast.makeText(getApplicationContext(),"Press one more time to exit",Toast.LENGTH_LONG).show();
            }
        }catch (Exception ee){
            Toast.makeText(getApplicationContext(),ee.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public void showNewCall(View veiw){
        try{
            checkInternet = urlClass.checkInternet();
            if(checkInternet){
                Intent intent = new Intent(userHome.this,newCall.class);
                startActivity(intent);
                finish();
            } else{
                onTaskCompleted("Internet connection failed, please check");
            }
        }catch (Exception ee){
            onTaskCompleted(ee.getMessage());
        }

    }
    public void showOpenCall(View veiw){
        try{
            checkInternet = urlClass.checkInternet();
            if(checkInternet){
                Intent intent = new Intent(userHome.this,openCall.class);
                startActivity(intent);
                finish();
            } else{
                onTaskCompleted("Internet connection failed, please check");
            }

        }catch (Exception ee){
            onTaskCompleted(ee.getLocalizedMessage());
        }

    }
    public void showPendingCall(View veiw){
        try{
            checkInternet = urlClass.checkInternet();
            if(checkInternet){
                Intent intent = new Intent(userHome.this,pendingCall.class);
                startActivity(intent);
                finish();
            } else{
                onTaskCompleted("Internet connection failed, please check");
            }
        }catch (Exception ee){
            onTaskCompleted(ee.getMessage());
        }


    }
    public void showResolvedCall(View veiw){
        try{
            checkInternet = urlClass.checkInternet();
            if(checkInternet){
                Intent intent = new Intent(userHome.this,closedCall.class);
                startActivity(intent);
                finish();
            } else{
                onTaskCompleted("Internet connection failed, please check");
            }
        }catch (Exception ee){
            onTaskCompleted(ee.getMessage());
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            try{
                checkInternet = urlClass.checkInternet();
                if(checkInternet){
                    finish();
                    startActivity(new Intent(userHome.this,Setting.class));
                } else{
                    onTaskCompleted("Internet connection failed, please check");
                }

            }catch (Exception e){
                onTaskCompleted(e.getMessage());
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskCompleted(String response) {
        Toast.makeText(getApplicationContext(),"Action Response: "+response,Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        try{
            checkInternet = urlClass.checkInternet();
            if (id == R.id.nav_new_call) {
                // Handle the camera action
                if(checkInternet){
                    finish();
                    showNewCall(this.getCurrentFocus().getRootView());
                } else{
                    onTaskCompleted("Internet connection failed, please check");
                }

            } else if (id == R.id.nav_open_call) {
                if(checkInternet){
                    finish();
                    showOpenCall(this.getCurrentFocus().getRootView());
                } else{
                    onTaskCompleted("Internet connection failed, please check");
                }

            } else if (id == R.id.nav_pending_call) {
                if(checkInternet){
                    finish();
                    showPendingCall(this.getCurrentFocus().getRootView());
                } else{
                    onTaskCompleted("Internet connection failed, please check");
                }

            } else if (id == R.id.nav_resolved_call) {
                try{
                    if(checkInternet){
                        finish();
                        showResolvedCall(this.getCurrentFocus().getRootView());
                    } else{
                        onTaskCompleted("Internet connection failed, please check");
                    }
                }catch (Exception e){
                    onTaskCompleted(e.getMessage());
                }

            } else if (id == R.id.nav_user_profile) {
                try{
                    if(checkInternet){
                        finish();
                        startActivity(new Intent(userHome.this,userProfile.class));
                    } else{
                        onTaskCompleted("Internet connection failed, please check");
                    }

                }catch (Exception e){
                    onTaskCompleted(e.getMessage());
                }
            } else if (id == R.id.nav_setting) {
                try{
                    if(checkInternet){
                        finish();
                        startActivity(new Intent(userHome.this,Setting.class));
                    } else{
                        onTaskCompleted("Internet connection failed, please check");
                    }

                }catch (Exception e){
                    onTaskCompleted(e.getMessage());
                }
            } else if (id == R.id.log_out) {
                try{
                    stopService(new Intent(userHome.this,onlineService.class));
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("online","0");
                    editor.apply();
                    editor.commit();
                    startService(new Intent(userHome.this,onlineService.class));
                    startActivity(new Intent(userHome.this,Login.class));
                    finish();
                }catch (Exception e){
                    onTaskCompleted(e.getMessage());
                }
            }
        }catch (Exception ee){
            onTaskCompleted(ee.getMessage());
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public class get_eng_info extends AsyncTask<String,String, String> {

        String url = urlClass.getUrl();
        String NameSpace = urlClass.NameSpace();
        String SOAP_ACTION = NameSpace+"EngineerProfile";
        String METHOD_NAME = "EngineerProfile";
        public ProgressDialog dialog =
                new ProgressDialog(userHome.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                dialog.setMessage("Loading...");
                dialog.show();
            } catch (Exception ee){
                onTaskCompleted(ee.getLocalizedMessage());
                Log.e("eee"," dd"+ee.getLocalizedMessage());

            }
        }
        @Override
        protected String doInBackground(String... params) {

            String userId = params[0];
            String companyId = params[1];
            String result = "";
            SoapObject request = new SoapObject(NameSpace, METHOD_NAME);
            request.addProperty("userId",userId);
            request.addProperty("companyId",companyId);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);
                if(envelope.bodyIn instanceof SoapFault){
                    String str= ((SoapFault) envelope.bodyIn).faultstring;
                    Log.e("eeeee", str);
                } else {
                    result = ((SoapObject)envelope.bodyIn).getProperty(0).toString();
                }
                if (request.equals("")) {
                    Object re = null;
                    re = envelope.getResponse();
                    return re.toString();
                }
            }catch (Exception ee){

                onTaskCompleted(ee.getLocalizedMessage());
            }
            return result;
        }
        protected void onCancelled() {
            dialog.dismiss();
            Toast toast = Toast.makeText(userHome.this,
                    "Error connecting to Server", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("result","==>"+result);
            dialog.dismiss();
            if(result!="" && !result.equals(null)){
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String name = jsonObject.getString("Name");
                    String email = jsonObject.getString("EmailId");
                    String phoneNo = jsonObject.getString("PhoneNo");
                    String Location = jsonObject.getString("Location");
                    TextView login_name_txt = (TextView) findViewById(R.id.login_name);
                    login_name_txt.setText(name);
                    cmp.setText(name);
                } catch (Exception ee){
                    Toast.makeText(getApplicationContext(),ee.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

            }
        }
    }
    class loadUSerDashboard extends AsyncTask<Integer,String,String>{
        String url = urlClass.getUrl();
        String NameSpace = urlClass.NameSpace();
        public ProgressDialog dialog =
                new ProgressDialog(userHome.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected String doInBackground(Integer... params) {

            String result = "";
            Integer UserID = params[0];
            Integer companyId = params[1];
            String SOAP_ACTION = NameSpace+"EngineerDashBoard";
            SoapObject request = new SoapObject(NameSpace, "EngineerDashBoard");
            request.addProperty("userId",UserID);
            request.addProperty("companyId",companyId);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);

                result = ((SoapObject)envelope.bodyIn).getProperty(0).toString();
                if(request.equals("")){
                    Object re= null;
                    re = envelope.getResponse();
                    return re.toString();
                }
                return  result;
            } catch (Exception e) {
                System.out.println("Error"+e);
                onTaskCompleted(e.getLocalizedMessage());
            }
            return result;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
            onTaskCompleted(" Unable to connect server");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if(!s.equals("") && !s.equals(null)){

                try {
                    JSONArray jsonArray = new JSONArray(s);
                    if(jsonArray.length()>0){
                        for(int i=0;i<jsonArray.length();i++){

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String Status = jsonObject.getString("Status");
                            String Calls = jsonObject.getString("Calls");
                            String Read = jsonObject.getString("Read");
                            TextView callAlert;
                            if(Status.equals("Forward")){
                                TextView newCall = (TextView) findViewById(R.id.new_call);
                                newCall.setText(Calls);
                                callAlert = (TextView) findViewById(R.id.new_call_count);
                                if(Read.equals("0")){
                                    //  callAlert.setVisibility(View.GONE);
                                } else {
                                    callAlert.setVisibility(View.VISIBLE);
                                    callAlert.setText(Read);
                                }
                            }
                            if(Status.equals("Open")){
                                TextView open_call = (TextView) findViewById(R.id.open_call);
                                open_call.setText(Calls);
                                callAlert = (TextView) findViewById(R.id.new_open_count);
                                if(Read.equals("0")){
                                    //callAlert.setVisibility(View.GONE);
                                } else {
                                    callAlert.setVisibility(View.VISIBLE);
                                    callAlert.setText(Read);
                                }
                            }
                            if(Status.equals("Partially Resolved")){

                                TextView closed_call = (TextView) findViewById(R.id.closed_call);
                                closed_call.setText(Calls);

                            }
                            if(Status.equals("Pending")){
                                TextView pending_call = (TextView) findViewById(R.id.pending_call);
                                pending_call.setText(Calls);
                                callAlert = (TextView) findViewById(R.id.new_pending_count);
                                if(Read.equals("0")){
                                    //callAlert.setVisibility(View.GONE);
                                } else {
                                    callAlert.setVisibility(View.VISIBLE);
                                    callAlert.setText(Read);
                                }
                            }
                        }

                    } else {
                        onTaskCompleted(" No Data found");
                    }
                } catch (Exception ee){
                    onTaskCompleted(ee.getLocalizedMessage());
                }

            } else{
                onTaskCompleted(" Unable to get details, try gain");
            }
        }
    }

}
