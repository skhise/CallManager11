package com.yashada.callmanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class userProfile extends AppCompatActivity implements ontaskComplet {

    ImageView btn_edit;
    CircleImageView eng_profile;
    Button btn_update_eng;
    UrlClass urlClass;
    ScrollView profile_scroll;
    TextView nameENG,emailENG;
    EditText eng_name,eng_email,eng_contact,eng_address;
    ConstraintLayout user_profile_details;
    SharedPreferences sharedpreferences;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    Boolean checkInternet;
    public static final String PREFS_NAME = "user_details";
    public String UserId="userId";
    public String CompanyId="companyId";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Profile");
        urlClass = new UrlClass(this);
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        checkInternet = urlClass.checkInternet();
        try  {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            // InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getRootView().getWindowToken(), 0);
        } catch (Exception e) {
            Log.e("eee",""+e.getMessage());
            //onTaskCompleted(e.getMessage());
        }
        btn_edit = (ImageView)findViewById(R.id.btn_edit_eng);
        profile_scroll = (ScrollView) findViewById(R.id.profile_scroll);
        nameENG = (TextView) findViewById(R.id.name);
        emailENG = (TextView) findViewById(R.id.email);
        eng_name = (EditText)findViewById(R.id.eng_name);
        eng_email = (EditText)findViewById(R.id.eng_email);
        eng_contact = (EditText)findViewById(R.id.eng_contact);
        eng_address = (EditText)findViewById(R.id.eng_address);
        eng_profile = (CircleImageView) findViewById(R.id.eng_profile);
        btn_update_eng = (Button) findViewById(R.id.btn_update_eng);
        user_profile_details = (ConstraintLayout) findViewById(R.id.user_profile_details);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_update_eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    try  {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        //          onTaskCompleted(e.getMessage());
                    }
                    Integer userId = sharedpreferences.getInt(UserId,0);
                    Integer companyId = sharedpreferences.getInt(CompanyId,0);
                    if (!userId.equals(0) && !companyId.equals(0)){
                        Log.e("userId",""+userId);
                        Log.e("companyId",""+companyId);

                        if(checkInternet){
                            new update_eng_info().execute(userId.toString(),companyId.toString());
                        } else {
                            onTaskCompleted("Internet connection failed, please check");
                        }
                    }
                } catch (Exception ee){
                    onTaskCompleted(ee.getMessage());
                }
            }
        });
        try {
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
    }
    @Override
    public void onTaskCompleted(String response) {
        Toast.makeText(userProfile.this,"call responce:"+response,Toast.LENGTH_LONG).show();
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
        startActivity(new Intent(userProfile.this,userHome.class));
    }
    public class update_eng_info extends AsyncTask<String,String,String>{

        String url = urlClass.getUrl();
        String NameSpace = urlClass.NameSpace();
        String SOAP_ACTION = NameSpace+"EditEngineerProfile";
        String METHOD_NAME = "EditEngineerProfile";
        public ProgressDialog dialog =
                new ProgressDialog(userProfile.this);
        String emp_name,emp_email,emp_contact,emp_address;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            emp_name = eng_name.getText().toString();
            emp_email = eng_email.getText().toString();
            emp_contact = eng_contact.getText().toString();
            emp_address = eng_address.getText().toString();
            try {
                dialog.setMessage("Loading...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            } catch (Exception ee){
                onTaskCompleted(ee.getLocalizedMessage());
                Log.e("eee"," dd"+ee.getLocalizedMessage());

            }
        }
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                String userId = params[0];
                String companyId = params[1];
                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String formattedDate = df.format(c.getTime());



                SoapObject request = new SoapObject(NameSpace, METHOD_NAME);
                request.addProperty("UserId",userId);
                request.addProperty("Name",emp_name);
                request.addProperty("Address",emp_address);
                request.addProperty("ContactNo",emp_contact);
                request.addProperty("EmailId",emp_email);
                request.addProperty("companyId", companyId);
                request.addProperty("CurrentDateTime",formattedDate);
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
            Toast toast = Toast.makeText(userProfile.this,
                    "Error connecting to Server", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                dialog.dismiss();
                profile_scroll.scrollTo(0,0);
                //String sub = s.substring(1,s.length()-1);
                Log.e("sub A s","s=>"+s);
                if(s.equals("1")){
                    onTaskCompleted("Details updated");
                } else{
                    onTaskCompleted("Failed to update, try again");
                }
            } catch (Exception e){
                onTaskCompleted(e.getMessage());
            }

        }
    }
    public class get_eng_info extends AsyncTask<String,String, String> {

        String url = urlClass.getUrl();
        String NameSpace = urlClass.NameSpace();
        String SOAP_ACTION = NameSpace+"EngineerProfile";
        String METHOD_NAME = "EngineerProfile";
        public ProgressDialog dialog =
                new ProgressDialog(userProfile.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                dialog.setMessage("Loading...");
                dialog.setCanceledOnTouchOutside(false);
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
            Toast toast = Toast.makeText(userProfile.this,
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

                    eng_email.setText(email);
                    nameENG.setText(name);
                    emailENG.setText(email);
                    eng_contact.setText(phoneNo);
                    eng_address.setText(Location);
                    eng_name.setText(name);
                } catch (Exception ee){
                    Toast.makeText(getApplicationContext(),ee.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        Intent intentN = new Intent(userProfile.this,check_notification.class);
        Intent intentL = new Intent(userProfile.this,locationService.class);
        Log.i("MAINACT", "onDestroy!");
        stopService(intentL);
        stopService(intentN);
        super.onDestroy();
    }
}
