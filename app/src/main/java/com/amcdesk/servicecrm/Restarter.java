package com.amcdesk.servicecrm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Restarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Broadcast Listened", "Service tried to stop");


        context.startService(new Intent(context, check_notification.class));
    }
}