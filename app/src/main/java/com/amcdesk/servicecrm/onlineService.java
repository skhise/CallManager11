package com.amcdesk.servicecrm;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class onlineService extends Service {
    public int counter=0;
    public  int count=0;
    UrlClass urlClass;
    String isOnline="0";

    public onlineService(){
        Log.i("HERE", "here I am!");

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
        isOnline = sharedPreferences.getString("online","0");
        startTimer(UserName,UserID,CompanyID);
        return START_STICKY;

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        stoptimertask();

    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // Your job when the service stops.
        Log.i("onTaskRemoved N", "onTaskRemoved!");
        stoptimertask();
        super.onTaskRemoved(rootIntent);
    }
    public void initializeTimerTask(final String UserName,final String  UserID,final String  CompanyID) {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
                new updateOnline().execute(UserName,UserID,CompanyID,isOnline);
            }
        };
    }
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer(String UserName,String  UserID,String  CompanyID) {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask(UserName,UserID,CompanyID);

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 5000, 5000); //
    }

    public class updateOnline extends AsyncTask<String,String,String> {
        String url = urlClass.getUrl();
        String NameSpace = urlClass.NameSpace();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            String userId = strings[1];
            String companyId = strings[2];

            String result = "";

            String SOAP_ACTION = NameSpace+"updateUserOnline";
            SoapObject request = new SoapObject(NameSpace, "updateUserOnline");
            request.addProperty("userId",userId);
            request.addProperty("companyId",companyId);
            request.addProperty("isOnline",strings[3]);
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
            Log.i("IN","Online Service");
            super.onPostExecute(setting);
            if(setting!="" && setting!=null && !setting.isEmpty()){
                try{
                    JSONObject jsonObject = new JSONObject(setting);
                    Integer code = jsonObject.getInt("code");
                    if(code == 1){
                       // Log.e("online update",""+setting);
                    } else if(code == 0){
                        //Log.e("online update",""+setting);
                    }else {
                        //Log.e("online update","error check api");
                    }
                } catch (Exception ee){
                    Log.e("online update:", ee.getLocalizedMessage());
                }
            } else {
                Log.e("online Update Esle",setting+"");
            }

        }
    }
    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

}