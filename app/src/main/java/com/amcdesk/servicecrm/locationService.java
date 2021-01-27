package com.amcdesk.servicecrm;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    public LocationManager locationManager;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    String provider = "";

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

    public void initializeTimerTaskNew(final String userName, final String userId, final String companyId) {

        MyLocation myLocation = new MyLocation();
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {

            @Override
            public void gotLocation(Location location) {
                // TODO Auto-generated method stub
                double Longitude = location.getLongitude();
                double Latitude = location.getLatitude();
                latitude = Latitude;
                longitude = Longitude;
                try {
                    SharedPreferences locationpref = getApplication()
                            .getSharedPreferences("user_details", MODE_WORLD_READABLE);
                    SharedPreferences.Editor prefsEditor = locationpref.edit();
                    prefsEditor.putString("Longitude", Longitude + "");
                    prefsEditor.putString("Latitude", Latitude + "");
                    prefsEditor.commit();

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        myLocation.getLocation(getApplicationContext(), locationResult);

        boolean r = myLocation.getLocation(getApplicationContext(),
                locationResult);

        timerTask = new TimerTask() {
            public void run() {

                String locc = latitude + "," + longitude;
                Log.e("locc", locc);
                String address = getCompleteAddressString(latitude, longitude);
                new updateLocation().execute(userName, userId, companyId, locc, address);
            }
        };
    }

    public void initializeTimerTaskOld(final String userName, final String userId, final String companyId) {

        locationManager = (LocationManager) getBaseContext()
                .getSystemService(LOCATION_SERVICE);
        checkGPS = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // get network provider status
        checkNetwork = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(checkGPS){
            provider = LocationManager.GPS_PROVIDER;
        } else if(checkNetwork){
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            provider = "";
        }
        checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

        if(provider.equals("")){
            //Toast.makeText(this, "Check Provider Setting", Toast.LENGTH_SHORT).show();
            Log.e("Unable to get provider", "check setting");
        } else {
            locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    loc = location;
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


        }

        timerTask = new TimerTask() {
            public void run() {
                if (locationManager != null) {
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

                    loc = locationManager
                            .getLastKnownLocation(provider);
                    if (loc != null) {
                        latitude = loc.getLatitude();
                        longitude = loc.getLongitude();
                        location = latitude + "," + longitude;
                    } else {
                        //  Toast.makeText(this, "Check Location Setting", Toast.LENGTH_SHORT).show();
                        Log.e("Unable to get", "Location");
                    }
                } else {
                    Log.e("Unable to get", "Location");
                    //Toast.makeText(this, "Check Location Setting", Toast.LENGTH_SHORT).show();
                }
                String locc = latitude+","+longitude;
                Log.e("locc", locc);
                String address = getCompleteAddressString(latitude, longitude);
                new updateLocation().execute(userName, userId, companyId, locc, address);
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
        initializeTimerTaskNew(userName, userId, companyId);

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 60000, 60000); //
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

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    public class updateLocation extends AsyncTask<String, String, String> {

        String url = urlClass.getUrl();
        String NameSpace = urlClass.NameSpace();
        String exp_string = "";
        String err_string = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                String userId = params[1];
                String location = params[3];
                String address = params[4];
                String urlString = urlClass.getFileUrl();
                String api = urlString + "updateLocation.php";
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(api);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("userId", userId));
                nameValuePairs.add(new BasicNameValuePair("companyId", params[2]));
                nameValuePairs.add(new BasicNameValuePair("location", location));
                nameValuePairs.add(new BasicNameValuePair("address", address));


                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {

                    String responseStr = EntityUtils
                            .toString(resEntity).trim();
                    err_string = responseStr;
                    exp_string = responseStr;

                }

            } catch (Exception e) {
                exp_string = e.getLocalizedMessage();
            }
            result = err_string;
            return result;
        }

        protected void onCancelled() {
            /*show_local_db();*/
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("err_string", "==>" + result);
            Log.i("IN", "Location Service");
            Log.i("IN", result);
            if (result != "" && result != null && !result.isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Integer code = jsonObject.getInt("code");
                    //Toast.makeText(getApplicationContext(), "Location updated, code:"+code, Toast.LENGTH_SHORT).show();
                } catch (Exception ee) {
                    Log.e("location update:", ee.getLocalizedMessage());
                }

            } else {
                Log.e("Location Update Esle", result + "" + latitude + "," + longitude);
            }
        }
    }

    public class updateLocation1 extends AsyncTask<String, String, String> {

        String url = urlClass.getUrl();
        String NameSpace = urlClass.NameSpace();


        @Override
        protected String doInBackground(String... doubles) {


            String userId = doubles[1];
            String location = doubles[3];
            String address = doubles[4];

            String result = "";

            String SOAP_ACTION = NameSpace+"updateEnginnerLocation";
            SoapObject request = new SoapObject(NameSpace, "updateEnginnerLocation");
            request.addProperty("userId",userId);
            request.addProperty("location",location);
            request.addProperty("address", address);
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
            Log.i("IN",setting);
            if(setting!="" && setting!=null && !setting.isEmpty()){
                try{
                    JSONObject jsonObject = new JSONObject(setting);
                    Integer code = jsonObject.getInt("code");
                    //Toast.makeText(getApplicationContext(), "Location updated, code:"+code, Toast.LENGTH_SHORT).show();
                }catch (Exception ee){
                    Log.e("location update:", ee.getLocalizedMessage());
                }

            } else {
                Log.e("Location Update Esle",setting+""+latitude+","+longitude);
            }

        }
    }

}
