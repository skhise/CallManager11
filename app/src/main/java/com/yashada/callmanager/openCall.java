package com.yashada.callmanager;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

public class openCall extends AppCompatActivity implements ontaskComplet,SearchView.OnQueryTextListener{
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public String USERNAME= "loginEmail";
    public String PASSWORD="loginPassword";
    public static final String PREFS_NAME = "user_details";
    public String UserId="userId";
    public String CompanyId="companyId";
    public String USERROLE="Role";
    public String USERCNAME="companyName";
    public String IsCompanyActive="isCompanyActive";
    public String IsUserActive="IsActive";
    String UserName;
    Integer UserID;
    Integer CompanyID;
    Integer UuserRole;
    Boolean IsUseractive;
    SharedPreferences sharedpreferences;
    ListView new_listView ;
    ListView open_listView ;
    List<Contact> contactList;
    SearchView search_call;
    ListAdaptor adapter;
    UrlClass urlClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_call);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Open Call");
        urlClass = new UrlClass(this);
        open_listView = (ListView) findViewById(R.id.open_call_list);
        contactList = new ArrayList<Contact>();
        adapter = new ListAdaptor(contactList, openCall.this);

        try {
            search_call = (SearchView) findViewById(R.id.search_call);
            search_call.setOnQueryTextListener(this);
            new_listView.setTextFilterEnabled(true);
            search_call.setActivated(true);
            //search_call.onActionViewExpanded();
            //search_call.setIconified(false);
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            View focusedView = this.getCurrentFocus();
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (Exception ee){

            Log.e("excp",ee.getLocalizedMessage());
            //  onTaskCompleted(ee.getLocalizedMessage());
        }
        try{

            sharedpreferences   = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            UserName     = sharedpreferences.getString(USERNAME,"");
            UserID       = sharedpreferences.getInt(UserId,0);
            CompanyID    = sharedpreferences.getInt(CompanyId,0);
            UuserRole    = sharedpreferences.getInt(USERROLE,0);
            IsUseractive = sharedpreferences.getBoolean(IsUserActive,false);
            if(!UserID.equals(0) || !CompanyID.equals(0)){
                try{
                    boolean checkInternet = urlClass.checkInternet();
                    if(checkInternet){
                        new loadOpenCall().execute(UserID,CompanyID);
                    } else {
                        onTaskCompleted("Internet connection failed, please check");
                    }
                }catch (Exception ee){
                    onTaskCompleted(ee.getLocalizedMessage());
                }
            } else {
                Toast.makeText(openCall.this,"Unable to get user details, ",Toast.LENGTH_LONG).show();
            }

        }catch (Exception ee){
            Toast.makeText(openCall.this,"Unable to get user details, "+ee.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(!UserID.equals(0) || !CompanyID.equals(0)){
                        try{
                            boolean checkInternet = urlClass.checkInternet();
                            if(checkInternet){
                                contactList.clear();
                                new loadOpenCall().execute(UserID,CompanyID);
                            } else {
                                onTaskCompleted("Internet connection failed, please check");
                            }
                        }catch (Exception ee){
                            onTaskCompleted(ee.getLocalizedMessage());
                        }

                    } else {
                        Toast.makeText(openCall.this,"Unable to get user details, ",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e("Service Call error",e.getLocalizedMessage());
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_home_back) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onTaskCompleted(String response) {

        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //android.widget.Filter  filter = adapter.getFilter();
        adapter.filter(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(openCall.this,userHome.class));
    }
    class loadOpenCall extends AsyncTask<Integer,String,String> {

        String url = urlClass.getUrl();
        String NameSpace = urlClass.NameSpace();
        public ProgressDialog dialog =
                new ProgressDialog(openCall.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Integer... params) {

            String result = "";
            Integer UserID = params[0];
            Integer companyId = params[1];
            Integer statusId = 4;
            String SOAP_ACTION = NameSpace+"GetCalls";
            SoapObject request = new SoapObject(NameSpace, "GetCalls");
            request.addProperty("UserID",UserID);
            request.addProperty("companyId",companyId);
            request.addProperty("statusId",statusId);
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
                    // return re.toString();
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
            onTaskCompleted("Unable to connect server");
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
                            String CallNo = jsonObject.getString("CallNo");
                            String Date = jsonObject.getString("CallDatestr");
                            String CustomerName = jsonObject.getString("CustomerName");
                            String CallStatusName = jsonObject.getString("CallStatusName");
                            String UnreadMessages = jsonObject.getString("UnreadMessages");
                            String system_call_id = jsonObject.getString("Id");
                            String callLife = jsonObject.getString("callAlive");
                            String time = jsonObject.getString("modifyAt");
                            if(CallStatusName.equals("Open")) {
                                try {
                                    Contact pendingContact = new Contact();
                                    pendingContact.setCustomer_name(CustomerName);
                                    pendingContact.setDate_time(Date);
                                    pendingContact.setCall_id(CallNo);
                                    pendingContact.setService_type("");
                                    pendingContact.setSystem_call_id(system_call_id);
                                    pendingContact.setUnreadMessages(Integer.parseInt(UnreadMessages));
                                    pendingContact.setCallAlive(callLife);
                                    pendingContact.setActionTime(Integer.parseInt(time));
                                    contactList.add(pendingContact);
                                } catch (Exception e) {
                                    Log.e("Call_List: ",e.getLocalizedMessage());
                                    onTaskCompleted(e.getMessage());
                                }
                            }
                        }
                        open_listView.setAdapter(adapter);
                        open_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String callId = contactList.get(position).getCall_id();
                                SharedPreferences.Editor editor =sharedpreferences.edit();
                                editor.putInt("ActivityCode",2);
                                editor.putString("clickedId",callId);
                                editor.putInt("callId",Integer.parseInt(callId));
                                editor.apply();
                                editor.commit();
                                try{
                                    boolean checkInternet = urlClass.checkInternet();
                                    if(checkInternet){

                                        Log.e("clickedId->",callId);
                                        Log.e("callId->",callId+"");
                                        Intent callDetails = new Intent(openCall.this,tabCallDetails.class);
                                        callDetails.putExtra("activity_name","Open Call View");
                                        startActivity(callDetails);
                                        finish();
                                    } else {
                                        onTaskCompleted("Internet connection failed, please check");
                                    }
                                }catch (Exception ee){
                                    onTaskCompleted(ee.getLocalizedMessage());
                                }


                            }
                        });
                    } else {
                        onTaskCompleted("No Data found");
                    }
                } catch (Exception ee){
                    onTaskCompleted(ee.getLocalizedMessage());
                }
            } else{
                onTaskCompleted("Unable to get details, try gain");
            }
        }
    }

    @Override
    protected void onDestroy() {
        Intent intentN = new Intent(openCall.this,check_notification.class);
        Intent intentL = new Intent(openCall.this,locationService.class);
        Log.i("MAINACT", "onDestroy!");
        stopService(intentL);
        stopService(intentN);
        super.onDestroy();
    }
}
