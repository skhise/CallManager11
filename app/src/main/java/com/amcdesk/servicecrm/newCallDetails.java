package com.amcdesk.servicecrm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class newCallDetails extends AppCompatActivity implements ontaskComplet{

    SharedPreferences sharedpreferences;
    TextView call_veiw_id,call_veiw_date,call_veiw_productNumber,
            call_veiw_productType,
            call_view_issueDetails,call_details_customerName,
            call_details_mobileNumber,call_details_emailId,call_details_customerAddress,
            contract_number,contract_type,productName,product_details,serviceType,IssueType,IssuePriority,LocationCall;
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
    Integer logedUserID;
    Integer CompanyID;
    Integer UuserRole;
    Boolean IsUseractive;
    Button btn_accept_call,btn_reject_call;
    public static BufferedWriter out;
    UrlClass urlClass;
    boolean checkInternet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_call_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedpreferences   = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent acName = getIntent();
        String activity_name = acName.getStringExtra("activity_name");
        getSupportActionBar().setTitle(activity_name);

        //call_veiw_rnumber = (TextView) findViewById(R.id.call_veiw_rnumber);
        call_veiw_id = (TextView) findViewById(R.id.call_veiw_id);
        call_veiw_date = (TextView) findViewById(R.id.call_veiw_date);

        contract_number = (TextView) findViewById(R.id.contract_number);
        contract_type = (TextView) findViewById(R.id.contract_type);

        productName = (TextView) findViewById(R.id.call_veiw_productName);
        call_veiw_productNumber = (TextView) findViewById(R.id.call_veiw_productNumber);
        call_veiw_productType = (TextView) findViewById(R.id.call_veiw_productType);
        product_details = (TextView) findViewById(R.id.product_details);

        serviceType = (TextView) findViewById(R.id.serviceType);
        IssueType = (TextView) findViewById(R.id.IssueType);
        IssuePriority = (TextView) findViewById(R.id.IssuePriority);
        LocationCall = (TextView) findViewById(R.id.LocationCall);
        call_view_issueDetails = (TextView) findViewById(R.id.call_view_issueDetails);




        call_details_customerName = (TextView) findViewById(R.id.call_details_customerName);
        call_details_mobileNumber = (TextView) findViewById(R.id.call_details_mobileNumber);
        call_details_emailId = (TextView) findViewById(R.id.call_details_emailId);
        call_details_customerAddress = (TextView) findViewById(R.id.call_details_customerAddress);

        btn_accept_call = (Button) findViewById(R.id.btn_accept_call);
        btn_reject_call = (Button) findViewById(R.id.btn_reject_call);

        UserName     = sharedpreferences.getString(USERNAME,"");
        logedUserID       = sharedpreferences.getInt(UserId,0);
        CompanyID    = sharedpreferences.getInt(CompanyId,0);
        UuserRole    = sharedpreferences.getInt(USERROLE,0);
        IsUseractive = sharedpreferences.getBoolean(IsUserActive,false);
        final String clickedId = sharedpreferences.getString("clickedCallId","0");
        urlClass = new UrlClass(newCallDetails.this);
        checkInternet = urlClass.checkInternet();
        try{
            createFileOnDevice(true);
            if((clickedId!=null|| clickedId!="0") && (CompanyID!=null || CompanyID!=0)){
                try{
                    if(checkInternet){
                        Integer cId = Integer.parseInt(clickedId);
                       // new getCallDetails().execute(cId,CompanyID,logedUserID);
                        getCallDetails(logedUserID,CompanyID,cId);
                    } else {
                        onTaskCompleted("Internet connection failed, please check");
                    }
                }catch (Exception ee){
                    Log.e("eeee",ee.getLocalizedMessage());
                    onTaskCompleted(ee.getLocalizedMessage());
                }
            } else {
                Toast.makeText(newCallDetails.this,"Unable to call details, ",Toast.LENGTH_LONG).show();
            }

        }catch (Exception ee){
            Toast.makeText(newCallDetails.this,"Error, "+ee.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
        try {
            btn_accept_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        checkInternet = urlClass.checkInternet();
                        if(checkInternet){
                            //new acceptCall().execute(clickedId,CompanyID+"");
                            acceptCall(clickedId,CompanyID+"",logedUserID+"",UuserRole+"");
                        } else {
                            onTaskCompleted("Internet connection failed, please check");
                        }
                    }catch (Exception ee){
                        Log.e("eeee",ee.getLocalizedMessage());
                        onTaskCompleted(ee.getLocalizedMessage());
                    }
                }
            });
            btn_reject_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        checkInternet = urlClass.checkInternet();
                        if(checkInternet){
                            //new rejectCall().execute(clickedId);
                            rejectCall(clickedId,CompanyID+"",logedUserID+"",UuserRole+"");
                        } else {
                            onTaskCompleted("Internet connection failed, please check");
                        }
                    }catch (Exception ee){
                        Log.e("eeee",ee.getLocalizedMessage());
                        onTaskCompleted(ee.getLocalizedMessage());
                    }
                }
            });
        }catch (Exception  ee){
            onTaskCompleted(ee.getLocalizedMessage());
        }




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                try{
                    String clickedId = sharedpreferences.getString("clickedCallId","0");
                    if((clickedId!=null|| clickedId!="0") && (CompanyID!=null || CompanyID!=0)){
                        try{
                            checkInternet = urlClass.checkInternet();
                            if(checkInternet){
                                Integer cId = Integer.parseInt(clickedId);
                                //new getCallDetails().execute(cId,CompanyID,logedUserID);
                                getCallDetails(logedUserID,CompanyID,cId);
                            } else {
                                onTaskCompleted("Internet connection failed, please check");
                            }
                        } catch (Exception ee){
                            Log.e("eeee",ee.getLocalizedMessage());
                            onTaskCompleted(ee.getLocalizedMessage());
                        }
                    } else {
                        Toast.makeText(newCallDetails.this,"Unable to get user details, ",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e){
                    Log.e("Service Call error",e.getLocalizedMessage());
                }
            }
        });
    }
    public void getCallDetails(Integer UserID, Integer companyId,final Integer callId){
        //GetSelectedCallByID

        try{
            //Toast.makeText(this, "Get Details  "+callId, Toast.LENGTH_LONG ).show();
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", UserID);
            jsonObject.put("companyId", companyId);
            jsonObject.put("callId", callId);
            Log.d("companyId", companyId.toString());


            String url = "http://service.newpro.in/app_slim/v1/GetSelectedCallByID?callId="+callId +"&companyId=" + companyId;

            JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.POST,
                    url,null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            Log.d("Call Details", jsonArray.toString());

                            pDialog.hide();
                            try {
                                if(jsonArray.length()>0){
                                    for(int i=0;i<jsonArray.length();i++){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        //1
                                      //  String callId = sharedpreferences.getString("clickeCalldId","0");
                                        String Id = jsonObject.getString("Id");
                                        Log.e("Id",""+Id);
                                        Log.e("callId",""+callId);
                                        if(Id.equals(callId.toString())){
                                            String CallNo = jsonObject.getString("callNo");
                                            String Date = jsonObject.getString("callDatestr");
                                            String SerialNo = jsonObject.getString("contactNo");
                                            String ProductType = jsonObject.getString("productType");
                                            String ProductBrand = jsonObject.getString("productNo");
                                            String ProductNamev= jsonObject.getString("productName");
                                            String CallDesc = jsonObject.getString("callDetails");
                                            String CustomerName = jsonObject.getString("customerName");
                                            String ContactNo = jsonObject.getString("customerContact");
                                            String CustEmail = jsonObject.getString("CustEmail");
                                            String Address = jsonObject.getString("customer_address");

                                            String contract_numberV = jsonObject.getString("contactNo");
                                            String contract_typeV = jsonObject.getString("contactType");
                                            String product_detailsV = jsonObject.getString("prodcutDetails");
                                            String serviceTypeV = jsonObject.getString("serviceType");
                                            String IssueTypeV = jsonObject.getString("issueType");
                                            String IssuePriorityV = jsonObject.getString("servicePrority");
                                            String LocationCallV = jsonObject.getString("callLocation");

                                            if(contract_numberV.equals("") || contract_numberV.equals("0") || contract_numberV.equals(null)){
                                                contract_numberV="NA";
                                            }
                                            if(contract_typeV.equals("") || contract_typeV.equals(null) || contract_typeV.equals("0")){
                                                contract_typeV = "NA";
                                            }
                                            contract_number.setText(contract_numberV);
                                            contract_type.setText(contract_typeV);
                                            serviceType.setText(serviceTypeV);
                                            IssueType.setText(IssueTypeV);
                                            IssuePriority.setText(IssuePriorityV);
                                            LocationCall.setText(LocationCallV);
                                            call_view_issueDetails.setText(CallDesc);
                                            if(ProductNamev.equals("") || ProductNamev.equals(null)){
                                                ProductNamev = "NA";
                                            }
                                            if(ProductBrand.equals("0") || ProductBrand.equals(null) || ProductBrand.equals("")){
                                                ProductBrand ="NA";
                                            }
                                            if(ProductType.equals("") || ProductType.equals(null) || ProductType.equals("0")){
                                                ProductType = "NA";
                                            }
                                            if(product_detailsV.equals("") || product_detailsV.equals(null)){
                                                product_detailsV = "NA";
                                            }
                                            productName.setText(ProductNamev);
                                            call_veiw_productNumber.setText(ProductBrand);
                                            call_veiw_productType.setText(ProductType);
                                            product_details.setText(product_detailsV);


                                            call_veiw_id.setText(CallNo);
                                            call_veiw_date.setText(Date);
//                                call_veiw_rnumber.setText(SerialNo);



//2


                                            //3



                                            call_details_customerName.setText(CustomerName);
                                            call_details_mobileNumber.setText(ContactNo);
                                            call_details_emailId.setText(CustEmail);
                                            call_details_customerAddress.setText(Address);
                                        } else {
                                            Log.e("Id",""+Id);
                                            Log.e("callId",""+callId);
                                            onTaskCompleted("No Data found Id:"+Id+",  callId"+callId);
                                        }
                                    }
                                } else {
                                    Log.e("eee","No data found1111");
                                    onTaskCompleted("No Data found");
                                }
                            } catch (Exception ee){
                                Log.e("eee"," dd"+ee.getLocalizedMessage());
                                onTaskCompleted(ee.getLocalizedMessage());
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Login", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),"Error:"+error.getMessage(),Toast.LENGTH_LONG).show();
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
            e.printStackTrace();
            Toast.makeText(newCallDetails.this,"User Home:"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
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
        startActivity(new Intent(newCallDetails.this,newCall.class));

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
            startActivity(new Intent(newCallDetails.this,userHome.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void createFileOnDevice(Boolean append) throws IOException {
        /*
         * Function to initially create the log file and it also writes the time of creation to file.
         */
        File Root = Environment.getExternalStorageDirectory();
        if(Root.canWrite()){
            File  LogFile = new File(Root, "Log.txt");
            FileWriter LogWriter = new FileWriter(LogFile, append);
            out = new BufferedWriter(LogWriter);
            Date date = new Date();
            out.write("Logged at" + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "\n");
            out.close();

        }
    }


    @Override
    public void onTaskCompleted(String response) {
        Toast.makeText(getApplicationContext(), "Response"+response, Toast.LENGTH_LONG).show();
    }
    void rejectCall(String clickedId,String companyId,String logedUserID,String userRole){

        try{
            //Toast.makeText(this, "clickedId:"+clickedId, Toast.LENGTH_SHORT).show();
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", logedUserID);
            jsonObject.put("companyId", companyId);
            jsonObject.put("callId", clickedId);
            jsonObject.put("userRole", userRole);
            Log.d("companyId", companyId.toString());
            String url = "http://service.newpro.in/app_slim/v1/RejectCall?userRole="+userRole+"&callId="+clickedId +"&companyId="+companyId+"&userId="+logedUserID+"";
            JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.POST,
                    url,null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            Log.d("Call Details", jsonArray.toString());

                            pDialog.hide();
                            try{
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                //String sub = s.substring(1,s.length()-1);
                                String code = jsonObject.getString("code");
                                String message = jsonObject.getString("message");
                                if(code.equals("1")){
                                    onTaskCompleted("Call rejected");
                                    startActivity(new Intent(newCallDetails.this, userHome.class));
                                } else {
                                    onTaskCompleted("Error:"+message);
                                }
                            }catch (Exception e){
                                onTaskCompleted(e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Login", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),"Error:"+error.getMessage(),Toast.LENGTH_LONG).show();
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
            e.printStackTrace();
            Toast.makeText(newCallDetails.this,"User Home:"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }
    void acceptCall(String clickedId,String companyId,String logedUserID,String userRole){
        try{
            //Toast.makeText(this, "clickedId:"+clickedId, Toast.LENGTH_SHORT).show();
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", logedUserID);
            jsonObject.put("companyId", companyId);
            jsonObject.put("callId", clickedId);
            jsonObject.put("userRole", userRole);
            Log.d("companyId", companyId.toString());

            String url = "http://service.newpro.in/app_slim/v1/CallStart?userRole="+userRole+"&UserId="+logedUserID+"&callId="+clickedId +"&companyId=" + companyId;

            JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.POST,
                    url,null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            Log.d("Call Details", jsonArray.toString());

                            pDialog.hide();
                            try {

                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                //String sub = s.substring(1,s.length()-1);
                                String code = jsonObject.getString("code");
                                String message = jsonObject.getString("message");
                                //Log.e("sub A s","s"+sub);
                                if (code.equals("1")) {
                                    onTaskCompleted("Call accept and moved in started call");
                                    startActivity(new Intent(newCallDetails.this, userHome.class));
                                } else {
                                    onTaskCompleted("Error:"+message);
                                }
                            } catch (Exception e) {
                                onTaskCompleted(e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Login", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),"Error:"+error.getMessage(),Toast.LENGTH_LONG).show();
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
            e.printStackTrace();
            Toast.makeText(newCallDetails.this,"User Home:"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }

   /* class rejectCall extends AsyncTask<String,String,String>{


        String url = urlClass.getUrl();
        String NameSpace = urlClass.NameSpace();
        public ProgressDialog dialog =
                new ProgressDialog(newCallDetails.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try{
                dialog.setMessage("Loading...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }catch (Exception ee){
                Log.e("eee"," dd"+ee.getLocalizedMessage());

            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                Integer callId = Integer.parseInt(strings[0]);
                String SOAP_ACTION = NameSpace+"RejectCall";
                SoapObject request = new SoapObject(NameSpace, "RejectCall");
                request.addProperty("callId",callId);
                request.addProperty("UserId",logedUserID);
                request.addProperty("companyId",CompanyID);
                request.addProperty("note","");


                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                envelope.dotNet = true;
                HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
                androidHttpTransport.call(SOAP_ACTION, envelope);
                if(envelope.bodyIn instanceof SoapFault){
                    String str= ((SoapFault) envelope.bodyIn).faultstring;
                    Log.e("eeeee", str);
                } else {
                    result = ((SoapObject)envelope.bodyIn).getProperty(0).toString();
                }

                if(result.equals("")){
                    Object re= null;
                    re = envelope.getResponse();

                    return re.toString();
                }

            } catch(SoapFault fault){
                Log.v("TAG", "soapfault = "+fault.getMessage());

            }catch (Exception e) {
                Log.v("TAG", "e = "+e.getMessage());
                return result;
            }
            return result;
        }

        @Override
        protected void onCancelled(String s) {
            dialog.dismiss();
            super.onCancelled(s);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();

            if(!s.equals("")){
                try{
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    //String sub = s.substring(1,s.length()-1);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if(code.equals("1")){
                        onTaskCompleted("Call rejected");
                        startActivity(new Intent(newCallDetails.this, userHome.class));
                    } else {
                        onTaskCompleted("Error:"+message);
                    }
                }catch (Exception e){
                    onTaskCompleted(e.getMessage());
                }
            } else {
                onTaskCompleted("Unknown error try again");
            }
            Log.e("reject result", s);
        }
    }
    class acceptCall extends AsyncTask<String,String,String>{

        String url = urlClass.getUrl();
        String NameSpace = urlClass.NameSpace();
        public ProgressDialog dialog =
                new ProgressDialog(newCallDetails.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try{
                dialog.setMessage("Loading...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }catch (Exception ee){
                Log.e("eee"," dd"+ee.getLocalizedMessage());
            }
        }
        @Override
        protected String doInBackground(String... strings) {
            String result = "";

            try {
                Integer callId = Integer.parseInt(strings[0]);
                String SOAP_ACTION = NameSpace+"CallStart";
                SoapObject request = new SoapObject(NameSpace, "CallStart");
                request.addProperty("callId",callId);
                request.addProperty("UserId",logedUserID);
                request.addProperty("companyId",CompanyID);
                Date date = new Date();
                //    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //    request.addProperty("modifiedDate",df.format(date));

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                envelope.dotNet = true;
                HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
                androidHttpTransport.call(SOAP_ACTION, envelope);
                if(envelope.bodyIn instanceof SoapFault){
                    String str= ((SoapFault) envelope.bodyIn).faultstring;
                    Log.e("eeeee", str);
                } else {
                    result = ((SoapObject)envelope.bodyIn).getProperty(0).toString();
                }
                if(request.equals("")){
                    Object re= null;
                    re = envelope.getResponse();
                    return re.toString();
                }
            } catch(SoapFault fault){
                Log.v("TAG", "soapfault = "+fault.getMessage());

            } catch (Exception e) {
                Log.v("TAG", "Exception = "+e.getMessage());
                return result;
            }
            return result;
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            dialog.dismiss();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();

            if(!s.equals("")) {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    //String sub = s.substring(1,s.length()-1);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    //Log.e("sub A s","s"+sub);
                    if (code.equals("1")) {
                        onTaskCompleted("Call accept and moved in started call");
                        startActivity(new Intent(newCallDetails.this, userHome.class));
                    } else {
                        onTaskCompleted("Error:"+message);
                    }
                } catch (Exception e) {
                    onTaskCompleted(e.getMessage());
                }
            } else{
                onTaskCompleted("Unknown error try again");
            }
            Log.e("accept result", s);
        }
    }

    class getCallDetails extends AsyncTask<Integer,String,String> {

        String url = urlClass.getUrl();
        String NameSpace = urlClass.NameSpace();
        public ProgressDialog dialog =
                new ProgressDialog(newCallDetails.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try{

                dialog.setMessage("Loading...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }catch (Exception ee){
                onTaskCompleted(ee.getLocalizedMessage());
                Log.e("eee"," dd"+ee.getLocalizedMessage());

            }
        }

        @Override
        protected String doInBackground(Integer... params) {

            String result = "";
            Integer callId = params[0];
            Integer companyId = params[1];
            Integer UserID = params[2];
            String SOAP_ACTION = NameSpace+"GetSelectedCallByID";
            SoapObject request = new SoapObject(NameSpace, "GetSelectedCallByID");
            request.addProperty("userId",UserID);
            request.addProperty("companyId",companyId);
            request.addProperty("callId",callId);
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
                    //    return re.toString();
                }
            } catch (Exception e) {
                System.out.println("Error"+e);
                onTaskCompleted(e.getLocalizedMessage());
            }
            return result;
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            try{
                dialog.dismiss();
            }catch (Exception ee){
                Log.e("eee"," dd"+ee.getLocalizedMessage());
                onTaskCompleted(ee.getLocalizedMessage());
            }

        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            dialog.dismiss();
            Log.e("SSS-->ss", s);
            if(!s.equals("") && !s.equals(null)){
                try {
                    dialog.dismiss();
                    JSONArray jsonArray = new JSONArray(s);
                    if(jsonArray.length()>0){
                        for(int i=0;i<jsonArray.length();i++){

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            //1
                            String callId = sharedpreferences.getString("clickedId","0");
                            String Id = jsonObject.getString("callNo");
                            Log.e("Id",""+callId);

                            if(Id.equals(callId)){
                                String CallNo = jsonObject.getString("callNo");
                                String Date = jsonObject.getString("callDatestr");
                                String SerialNo = jsonObject.getString("contactNo");
                                String ProductType = jsonObject.getString("productType");
                                String ProductBrand = jsonObject.getString("productNo");
                                String ProductNamev= jsonObject.getString("productName");
                                String CallDesc = jsonObject.getString("callDetails");
                                String CustomerName = jsonObject.getString("customerName");
                                String ContactNo = jsonObject.getString("customerContact");
                                String CustEmail = jsonObject.getString("CustEmail");
                                String Address = jsonObject.getString("customer_address");

                                String contract_numberV = jsonObject.getString("contactNo");
                                String contract_typeV = jsonObject.getString("contactType");
                                String product_detailsV = jsonObject.getString("prodcutDetails");
                                String serviceTypeV = jsonObject.getString("serviceType");
                                String IssueTypeV = jsonObject.getString("issueType");
                                String IssuePriorityV = jsonObject.getString("servicePrority");
                                String LocationCallV = jsonObject.getString("callLocation");

                                if(contract_numberV.equals("") || contract_numberV.equals("0") || contract_numberV.equals(null)){
                                    contract_numberV="NA";
                                }
                                if(contract_typeV.equals("") || contract_typeV.equals(null) || contract_typeV.equals("0")){
                                    contract_typeV = "NA";
                                }
                                contract_number.setText(contract_numberV);
                                contract_type.setText(contract_typeV);

                                serviceType.setText(serviceTypeV);
                                IssueType.setText(IssueTypeV);
                                IssuePriority.setText(IssuePriorityV);
                                LocationCall.setText(LocationCallV);
                                call_view_issueDetails.setText(CallDesc);


                                if(ProductNamev.equals("") || ProductNamev.equals(null)){
                                    ProductNamev = "NA";
                                }
                                if(ProductBrand.equals("0") || ProductBrand.equals(null) || ProductBrand.equals("")){
                                    ProductBrand ="NA";
                                }
                                if(ProductType.equals("") || ProductType.equals(null) || ProductType.equals("0")){
                                    ProductType = "NA";
                                }
                                if(product_detailsV.equals("") || product_detailsV.equals(null)){
                                    product_detailsV = "NA";
                                }
                                productName.setText(ProductNamev);
                                call_veiw_productNumber.setText(ProductBrand);
                                call_veiw_productType.setText(ProductType);
                                product_details.setText(product_detailsV);


                                call_veiw_id.setText(CallNo);
                                call_veiw_date.setText(Date);
//                                call_veiw_rnumber.setText(SerialNo);



//2


                                //3



                                call_details_customerName.setText(CustomerName);
                                call_details_mobileNumber.setText(ContactNo);
                                call_details_emailId.setText(CustEmail);
                                call_details_customerAddress.setText(Address);
                            }

                        }
                    } else {
                        dialog.dismiss();
                        Log.e("eee","No data found1111");
                        onTaskCompleted("No Data found");
                    }
                } catch (Exception ee){
                    dialog.dismiss();
                    Log.e("eee"," dd"+ee.getLocalizedMessage());
                    onTaskCompleted(ee.getLocalizedMessage());
                }
            } else{
                dialog.dismiss();
                Log.e("eee","No data found111"+s);
                onTaskCompleted("Unable to get details, try gain");
            }
        }
    }
    @Override
    protected void onDestroy() {
        Intent intentN = new Intent(newCallDetails.this,check_notification.class);
        Intent intentL = new Intent(newCallDetails.this,locationService.class);
        Log.i("MAINACT", "onDestroy!");
        stopService(intentL);
        stopService(intentN);
        super.onDestroy();
    }*/
}
