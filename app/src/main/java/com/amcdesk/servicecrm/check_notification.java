package com.amcdesk.servicecrm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
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
import java.util.Timer;
import java.util.TimerTask;

public class check_notification extends Service {
    public int counter=0;
    public  int count=0;
    UrlClass urlClass;

    public check_notification(){

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
        Intent broadcastIntent = new Intent(getApplicationContext(), Restarter.class);
        sendBroadcast(broadcastIntent);
        stoptimertask();

    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {

        // Your job when the service stops.
        Log.i("onTaskRemoved N", "onTaskRemoved!");
        stoptimertask();
        Intent broadcastIntent = new Intent(getApplicationContext(), ServiceRestarter.class);
        sendBroadcast(broadcastIntent);
        super.onTaskRemoved(rootIntent);
    }
    public void initializeTimerTask(final String UserName,final String  UserID,final String  CompanyID) {
        timerTask = new TimerTask() {
            public void run() {
                new check_for_notification().execute(UserName,UserID,CompanyID);
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

    public class check_for_notification extends AsyncTask<String, String, String> {

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
                String companyId = params[2];
                String urlString = urlClass.getFileUrl();
                String api = urlString + "check_notification.php";
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(api);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("userId", userId));
                nameValuePairs.add(new BasicNameValuePair("companyId", companyId));

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
        protected void onPostExecute(String setting) {
            super.onPostExecute(setting);
            Log.i("IN", "Notification Service");
            if (setting != "" && setting != null && !setting.isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(setting);
                    Integer code = jsonObject.getInt("code");
                    if (code == 1) {
                        Integer callCount = jsonObject.getInt("callCount");
                        if (callCount > 0) {
                            sendNotification(callCount);
                        }

                    } else if (code == 0) {
                        Log.e("notification update", "" + setting);
                    } else {
                        Log.e("notification update", "error check api");
                    }
                } catch (Exception ee) {
                    Log.e("notification update:", ee.getLocalizedMessage());
                }

            } else {
                Log.e("Location Update Esle", setting + "");
            }
        }
    }

    public class check_for_notification1 extends AsyncTask<String, String, String> {
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

            String SOAP_ACTION = NameSpace+"getNotification";
            SoapObject request = new SoapObject(NameSpace, "getNotification");
            request.addProperty("userId",userId);
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
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String setting) {
            super.onPostExecute(setting);
            Log.i("IN","Notification Service");
            if(setting!="" && setting!=null && !setting.isEmpty()){
                try{
                    JSONObject jsonObject = new JSONObject(setting);
                    Integer code = jsonObject.getInt("code");
                    if(code == 1){
                        Integer callCount = jsonObject.getInt("callCount");
                        if(callCount>0){
                            sendNotification(callCount);
                        }

                    } else if(code == 0){
                        Log.e("notification update",""+setting);
                    }else {
                        Log.e("notification update","error check api");
                    }
                }catch (Exception ee){
                    Log.e("notification update:", ee.getLocalizedMessage());
                }

            } else {
                Log.e("Location Update Esle",setting+"");
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
    public void sendNotification(Integer callCount) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);

        //Create the intent that’ll fire when the user taps the notification//
        Notification notification = new Notification();
        Intent intent=null;
        if (isAppRunning(getApplicationContext(),getApplicationContext().getPackageName())){
            intent = getPackageManager().getLaunchIntentForPackage("com.amcdesk.servicecrm");

        } else {
            intent =new Intent(check_notification.this,userHome.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//        notification = mBuilder.setContentIntent(pendingIntent)
//                .setSmallIcon(R.drawable.ic_noti)
//                .setTicker(getApplicationContext().getString(R.string.app_name)).setWhen(System.currentTimeMillis())
//                .setAutoCancel(true).setContentTitle(getApplicationContext().getString(R.string.app_name))
//                .setContentTitle(callCount+" New Call").setContentText("Admin assigned you new call").build();
        //notification.sound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.eventually);
        mBuilder.setContentIntent(pendingIntent);

        mBuilder.setOnlyAlertOnce(true);
        mBuilder.setSmallIcon(R.drawable.ic_noti);
        mBuilder.setContentTitle("You have "+callCount+" New Call");
        mBuilder.setContentText("Click to check the call");
     //   mBuilder.setVibrate(new long[]{0, 500, 500, 500});
       // mBuilder.setLights(Color.MAGENTA, 2000, 500);
        //mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), AudioManager.STREAM_NOTIFICATION);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE);



        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(001,mBuilder.getNotification());
    }
}
