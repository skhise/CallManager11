package com.yashada.callmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Admin on 2/1/2018.
 */

public class UrlClass {
    Context context;
    public UrlClass(Context context){
        this.context = context;
    }
    public  String getUrl(){

        String URL = "http://service.newpro.in/api/app/service.php";
        return URL;
    }
    public String NameSpace(){
        String NAMESPACE = "urn:service";
        return NAMESPACE;
    }



    public boolean checkInternet(){

        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);

            if (netInfo != null
                    && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null
                        && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;

    }
}
