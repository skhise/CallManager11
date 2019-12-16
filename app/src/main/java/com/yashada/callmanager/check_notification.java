package com.yashada.callmanager;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.games.internal.constants.NotificationChannel;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class check_notification extends Service {
    public int counter=0;
    public  int count=0;

    public check_notification(){
        Log.i("HERE", "here I am!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
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
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
                new check_for_notification().execute();
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
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 2000, 2000); //
    }

    public class check_for_notification extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            sendNotification(10);
            count=1;

            Log.e("Notification","Check");
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
            intent = getPackageManager().getLaunchIntentForPackage("com.yashada.callmanager");

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
        mBuilder.setContentTitle(callCount+" New Call");
        mBuilder.setContentText("Admin assigned you new call");
     //   mBuilder.setVibrate(new long[]{0, 500, 500, 500});
       // mBuilder.setLights(Color.MAGENTA, 2000, 500);
        //mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), AudioManager.STREAM_NOTIFICATION);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE);



        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(001,mBuilder.getNotification());
    }
}
