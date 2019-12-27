package com.yashada.callmanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

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
                if(email.equals("")){
                    Login_Email.setError("Required");
                } else if(password.equals("")){
                    Login_Password.setError("Required");
                } else if(!email.equals("") && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Login_Email.setError("Required valid email ");
                } else {
                    checkLogin(email,password);
                }

            }
        });

    }


    public void checkLogin(String email, String password){
        boolean checkInternet = urlClass.checkInternet();
        if(checkInternet){
            new check_login().execute(email,password);
        } else {
            Toast.makeText(Login.this,"Internet connection failed, please check",Toast.LENGTH_LONG).show();
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
                    } else {
                        Toast.makeText(getApplicationContext(),"Invalid user details, please check login details",Toast.LENGTH_LONG).show();
                    }


                }catch (Exception ee){

                    Toast.makeText(getApplicationContext(),ee.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

            }
        }
    }
}
