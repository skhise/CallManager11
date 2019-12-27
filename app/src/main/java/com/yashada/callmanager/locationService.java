package com.yashada.callmanager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Timer;
import java.util.TimerTask;

public class locationService extends Service  {

    boolean checkGPS = false;
    boolean checkNetwork = false;
    boolean canGetLocation = false;
    Location loc;
    double latitude;
    double longitude;
    public int counter=0;
    UrlClass urlClass;

    String location = "";
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;


    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    protected LocationManager locationManager;

    public locationService(){

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        final String PREFS_NAME = "user_details";
        urlClass = new UrlClass(getApplicationContext());
         String USERNAME= "loginEmail";
         String UserId="userId";
         String CompanyId="companyId";
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String UserName     = sharedPreferences.getString(USERNAME,"");
        String UserID       = ""+sharedPreferences.getInt(UserId,0);
        String CompanyID    = ""+ sharedPreferences.getInt(CompanyId,0);
        startTimer(UserName,UserID,CompanyID);
        return START_STICKY;

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(getApplicationContext(), ServiceRestarter.class);
        sendBroadcast(broadcastIntent);
        stoptimertask();

    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {

        // Your job when the service stops.
        Log.i("onTaskRemoved", "onTaskRemoved!");
        stoptimertask();
        Intent broadcastIntent = new Intent(getApplicationContext(), ServiceRestarter.class);
        sendBroadcast(broadcastIntent);
        super.onTaskRemoved(rootIntent);
    }
    public void initializeTimerTask(final String userName,final String userId,final String companyId) {

        LocationManager locationManager = (LocationManager) getBaseContext()
                .getSystemService(LOCATION_SERVICE);
        checkGPS = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // get network provider status
        checkNetwork = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        String provider="";
        if(checkGPS){
            provider = LocationManager.GPS_PROVIDER;
        } else if(checkNetwork){
            provider = LocationManager.NETWORK_PROVIDER;
        }
        checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

        if(provider.equals("")){
            Log.e("Unable to get provider", "check setting");
        } else {
            locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

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

            if(locationManager!=null){

                loc = locationManager
                        .getLastKnownLocation(provider);
                if(loc!=null){
                    latitude = loc.getLatitude();
                    longitude = loc.getLongitude();
                    location = latitude+","+longitude;
                } else {
                    Log.e("Unable to get", "Location");
                }


            } else {
                Log.e("Unable to get", "Location");
            }
        }

        timerTask = new TimerTask() {
            public void run() {
                String locc = latitude+","+longitude;
                new updateLocation().execute(userName,userId,companyId,locc);
            }
        };
    }
    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer(String userName,String userId,String companyId) {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask(userName,userId,companyId);

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 5000, 5000); //
    }
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public double getLongitude() {
        if (loc != null) {
            longitude = loc.getLongitude();
        }
        return longitude;
    }

    public double getLatitude() {
        if (loc != null) {
            latitude = loc.getLatitude();
        }
        return latitude;
    }
    public class updateLocation extends AsyncTask<String,String,String>{

        String url = urlClass.getUrl();
        String NameSpace = urlClass.NameSpace();


        @Override
        protected String doInBackground(String... doubles) {


            String userId = doubles[1];
            String location = doubles[3];

            String result = "";

            String SOAP_ACTION = NameSpace+"updateEnginnerLocation";
            SoapObject request = new SoapObject(NameSpace, "updateEnginnerLocation");
            request.addProperty("userId",userId);
            request.addProperty("location",location);
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
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String setting) {
            super.onPostExecute(setting);
            //Toast.makeText(ge, "Location:"+setting, Toast.LENGTH_SHORT).show();
            Log.i("IN","Location Service");
            if(setting!="" && setting!=null && !setting.isEmpty()){
                try{
                    JSONObject jsonObject = new JSONObject(setting);
                    Integer code = jsonObject.getInt("code");
                }catch (Exception ee){
                    Log.e("location update:", ee.getLocalizedMessage());
                }

            } else {
                Log.e("Location Update Esle",setting+""+latitude+","+longitude);
            }

        }
    }

}
