package com.amcdesk.servicecrm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity{

    Button Login_BTN;
    EditText Login_Email;
    EditText Login_Password;


    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;



    public String USERROLE="Role";
    public String USERCNAME="companyName";
    public String IsCompanyActive="isCompanyActive";
    public String roleName="roleName";
    public String IsUserActive="IsActive";
    public String USERNAME= "loginEmail";
    public String PASSWORD="loginPassword";
    public static final String PREFS_NAME = "user_details";
    public String UserId="userId";
    public String CompanyId="companyId";
    public String deviceId="";


    SharedPreferences sharedpreferences;

    UrlClass urlClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Login_BTN = (Button) findViewById(R.id.btn_login);
        Login_Email = (EditText) findViewById(R.id.input_email);
        Login_Password = (EditText)findViewById(R.id.input_password);
        urlClass = new UrlClass(this);
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Login_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = Login_Email.getText().toString();
                String password = Login_Password.getText().toString();
                deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                Log.e("deviceId-->",deviceId);
                if(email.equals("")){
                    Login_Email.setError("Required");
                } else if(password.equals("")){
                    Login_Password.setError("Required");
                } else if(!email.equals("") && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Login_Email.setError("Required valid email ");
                } else if(deviceId.equals("") || deviceId.equals(null)){
                    Toast.makeText(Login.this, "Unable to get device Id", Toast.LENGTH_SHORT).show();
                } else {
                    checkLogin(email,password,deviceId);
                }

            }
        });

    }
    public void checkLogin(final String email, final String password, final String deviceId){
        boolean checkInternet = urlClass.checkInternet();
        if(checkInternet){
            //  new check_login().execute(email,password,deviceId);
            //new RetrieveFeedTask().execute(email, password, deviceId);

            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();
            try{

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("loginName", email);
                jsonObject.put("password", password);
                jsonObject.put("deviceId", deviceId);

                String url = urlClass.getUrl()+"login?" +"loginName=" + email +"&password=" + password +"&deviceId=" + deviceId;

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,

                        url,jsonObject,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.d("Login", jsonObject.toString());
                                pDialog.hide();
                                try {
                                    Integer code = jsonObject.getInt("code");
                                    if (code == 1) {
                                        Integer uid = jsonObject.getInt("UserID");
                                        Integer Companyid = jsonObject.getInt("CompanyId");
                                        String UserName = jsonObject.getString("UserName");
                                        Integer Role = jsonObject.getInt("Role");
                                        //  String RoleName = jsonObject.getString("RoleName");
                                        String RoleName = "User";
                                        Boolean IsActive = jsonObject.getBoolean("IsActive");
                                        Boolean IsActiveCompany = jsonObject.getBoolean("IsActiveCompany");
                                        String CompanyName = jsonObject.getString("CompanyName");
                                        if (!uid.equals("") && IsActive.equals(true) && !Companyid.equals("") && IsActiveCompany.equals(true) && (Role.equals("5") || RoleName.equals("User"))) {
                                            try {

                                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                                editor.putString(USERNAME, UserName);
                                                editor.putString(PASSWORD, password);
                                                editor.putInt(UserId, uid);
                                                editor.putInt(CompanyId, Companyid);
                                                editor.putString(roleName, RoleName);
                                                editor.putInt(USERROLE, Role);
                                                editor.putString(USERCNAME, CompanyName);
                                                editor.putBoolean(IsCompanyActive, IsActiveCompany);
                                                editor.putBoolean(IsUserActive, IsActive);


                                                Intent userHome = new Intent(Login.this, userHome.class);
                                                startActivity(userHome);
                                                editor.putString("online", "1");
                                                editor.apply();
                                                editor.commit();
                                                startService(new Intent(Login.this, onlineService.class));
                                                startService(new Intent(Login.this, locationService.class));
                                            } catch (Exception ee) {
                                                Toast.makeText(getApplicationContext(), "Error in read user input" + ee.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Invalid user details, please check login details", Toast.LENGTH_LONG).show();
                                        }
                                    } else if (code == 3) {
                                        Toast.makeText(getApplicationContext(), "User active on other device", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Invalid user details, please check login details", Toast.LENGTH_LONG).show();
                                    }


                                } catch (Exception ee) {

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
                pDialog.hide();
                e.printStackTrace();
                Toast.makeText(Login.this,"Login:"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(Login.this,"Internet connection failed, please check",Toast.LENGTH_LONG).show();
        }

    }


    class RetrieveFeedTask extends AsyncTask<String, String, String> {


        public ProgressDialog dialog =
                new ProgressDialog(Login.this);
        String UserPassword = "";

        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... settings) {
            try {
                UserPassword = settings[1];
                URL url = new URL(urlClass.getUrl()+"login?" + "loginName=" + settings[0] + "&password=" + settings[1] + "&deviceId=" + settings[2]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("content-length", "0");
                urlConnection.setConnectTimeout(3000);
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String result) {
            dialog.dismiss();
            if (result != "" && !result.equals(null)) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Integer code = jsonObject.getInt("code");
                    if (code == 1) {
                        Integer uid = jsonObject.getInt("UserID");
                        Integer Companyid = jsonObject.getInt("CompanyId");
                        String UserName = jsonObject.getString("UserName");
                        Integer Role = jsonObject.getInt("Role");
                        //  String RoleName = jsonObject.getString("RoleName");
                        String RoleName = "User";
                        Boolean IsActive = jsonObject.getBoolean("IsActive");
                        Boolean IsActiveCompany = jsonObject.getBoolean("IsActiveCompany");
                        String CompanyName = jsonObject.getString("CompanyName");
                        if (!uid.equals("") && IsActive.equals(true) && !Companyid.equals("") && IsActiveCompany.equals(true) && (Role.equals("5") || RoleName.equals("User"))) {
                            try {

                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(USERNAME, UserName);
                                editor.putString(PASSWORD, UserPassword);
                                editor.putInt(UserId, uid);
                                editor.putInt(CompanyId, Companyid);
                                editor.putString(roleName, RoleName);
                                editor.putInt(USERROLE, Role);
                                editor.putString(USERCNAME, CompanyName);
                                editor.putBoolean(IsCompanyActive, IsActiveCompany);
                                editor.putBoolean(IsUserActive, IsActive);


                                Intent userHome = new Intent(Login.this, userHome.class);
                                startActivity(userHome);
                                editor.putString("online", "1");
                                editor.apply();
                                editor.commit();
                                startService(new Intent(Login.this, onlineService.class));
                                startService(new Intent(Login.this, locationService.class));
                            } catch (Exception ee) {
                                Toast.makeText(getApplicationContext(), "Error in read user input" + ee.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid user details, please check login details", Toast.LENGTH_LONG).show();
                        }
                    } else if (code == 3) {
                        Toast.makeText(getApplicationContext(), "User active on other device", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid user details, please check login details", Toast.LENGTH_LONG).show();
                    }


                } catch (Exception ee) {

                    Toast.makeText(getApplicationContext(), ee.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Invalid user details, please check login details", Toast.LENGTH_LONG).show();
            }

        }
    }

    public class check_login extends AsyncTask<String,String, String> {

        String url = urlClass.getUrl();
        String NameSpace = urlClass.NameSpace();
        String SOAP_ACTION = NameSpace+"UserLogin";
        String UserPassword="";
        String METHOD_NAME = "UserLogin";
        public ProgressDialog dialog =
                new ProgressDialog(Login.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        @Override
        protected String doInBackground(String... params) {

            String UserName = params[0];
            UserPassword = params[1];
            String result = "";
            SoapObject request = new SoapObject(NameSpace, METHOD_NAME);
            request.addProperty("UserName",UserName);
            request.addProperty("Password",UserPassword);
            request.addProperty("deviceId",params[2]);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);
                result = ((SoapObject)envelope.bodyIn).getProperty(0).toString();
                if(request.equals("")){
                    Object re= null;
                    result = envelope.getResponse().toString();
                }

            } catch (Exception e) {
                System.out.println("Error"+e);
            }
            return result;
        }
        protected void onCancelled() {
            dialog.dismiss();
            Toast toast = Toast.makeText(Login.this,
                    "Error connecting to Server", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();
            /*show_local_db();*/
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("result","==>"+result);
            dialog.dismiss();
            if(result!="" && !result.equals(null)){
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Integer code = jsonObject.getInt("code");
                    if(code  == 1){
                        Integer uid = jsonObject.getInt("UserID");
                        Integer Companyid = jsonObject.getInt("CompanyId");
                        String UserName = jsonObject.getString("UserName");
                        Integer Role = jsonObject.getInt("Role");
                        //  String RoleName = jsonObject.getString("RoleName");
                        String RoleName="User";
                        Boolean IsActive = jsonObject.getBoolean("IsActive");
                        Boolean IsActiveCompany = jsonObject.getBoolean("IsActiveCompany");
                        String CompanyName = jsonObject.getString("CompanyName");
                        if(!uid.equals("") && IsActive.equals(true) && !Companyid.equals("") && IsActiveCompany.equals(true) &&(Role.equals("5") || RoleName.equals("User")) ){
                            try {

                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(USERNAME,UserName);
                                editor.putString(PASSWORD,UserPassword);
                                editor.putInt(UserId,uid);
                                editor.putInt(CompanyId,Companyid);
                                editor.putString(roleName,RoleName);
                                editor.putInt(USERROLE,Role);
                                editor.putString(USERCNAME,CompanyName);
                                editor.putBoolean(IsCompanyActive,IsActiveCompany);
                                editor.putBoolean(IsUserActive,IsActive);


                                Intent userHome = new Intent(Login.this, userHome.class);
                                startActivity(userHome);
                                editor.putString("online","1");
                                editor.apply();
                                editor.commit();
                                startService(new Intent(Login.this,onlineService.class));
                                startService(new Intent(Login.this,locationService.class));
                            } catch (Exception ee){
                                Toast.makeText(getApplicationContext(),"Error in read user input"+ee.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"Invalid user details, please check login details",Toast.LENGTH_LONG).show();
                        }
                    } else if(code == 3){
                        Toast.makeText(getApplicationContext(),"User active on other device",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Invalid user details, please check login details",Toast.LENGTH_LONG).show();
                    }


                }catch (Exception ee){

                    Toast.makeText(getApplicationContext(),ee.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(),"Invalid user details, please check login details",Toast.LENGTH_LONG).show();
            }
        }
    }
}
