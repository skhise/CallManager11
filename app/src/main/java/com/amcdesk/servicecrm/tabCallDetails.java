package com.amcdesk.servicecrm;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class tabCallDetails extends AppCompatActivity implements ontaskComplet{

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
    chatAdaptor chatArrayAdapter;
    String UserName;
    Integer logedUserID;
    Integer CompanyID;
    GridView gridview;
    Integer UuserRole;
    final String gpsLocationProvider = LocationManager.GPS_PROVIDER;
    final String networkLocationProvider = LocationManager.NETWORK_PROVIDER;
    String wantPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    Boolean IsUseractive;
    List<String> spinnerLabelList;
    List<OtherImage> otherImagesList;
    List<Integer> spinnerIdList;
    List<String> spinnerLabelListReason;
    List<Integer> spinnerIdListReason;
    ListView chat_message_list;
    Spinner call_spinner;
    spinnerItem spinnerItem;
    List<ChatMessage> msg_List;
    SharedPreferences sharedpreferences;
    UrlClass urlClass;
    Integer statusId;
    Integer reasonId;
    Boolean checkInternet;
    TextView pd_txt;
    String userLocation = "";
    Button btn_add_description, btn_attend_call, btn_call_action;
    String userChoosenTask = "";
    String selectedImage = null;
    private final int REQUEST_CAMERA = 0;
    private final int SELECT_FILE = 1;
    ImageView imageHolder;
    LinearLayout actionLayout;
    TextView no_attachment;
    Button btn_upload;
    String callStatusG = "0";
    TextView call_veiw_id, call_veiw_date, call_veiw_rnumber, call_veiw_productNumber, call_time,
            call_veiw_productBrand, call_veiw_productType,
            call_view_issueDetails, call_details_customerName, call_details_subcustomerName,
            call_details_mobileNumber, call_details_emailId, call_details_customerAddress, mTextMessage,
            contract_number, contract_type, productName, product_details, serviceType, IssueType, IssuePriority, LocationCall;
    String selectedContract="0";
    LinearLayout layout_user;
    Uri fileUri;
    Spinner call_action_spinner, call_action_reason_spinner;
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            LinearLayout layout_user = (LinearLayout) findViewById(R.id.user_layout);
            AppBarLayout.LayoutParams lparams = new AppBarLayout.LayoutParams(
                    AppBarLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.WRAP_CONTENT);
            TextView tv = new TextView(tabCallDetails.this);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    layout_user.removeAllViews();
                    LayoutInflater inflater = LayoutInflater.from(tabCallDetails.this);
                    View view = inflater.inflate(R.layout.call_info, layout_user, false);
                    layout_user.addView(view);
                    initializeInfoData(view);
                    try {
                        UserName     = sharedpreferences.getString(USERNAME,"");
                        logedUserID       = sharedpreferences.getInt(UserId,0);
                        CompanyID    = sharedpreferences.getInt(CompanyId,0);
                        UuserRole    = sharedpreferences.getInt(USERROLE,0);
                        IsUseractive = sharedpreferences.getBoolean(IsUserActive,false);
                        String clickedId = sharedpreferences.getString("clickedId","0");
                        if((clickedId!=null|| clickedId!="0") && (CompanyID!=null || CompanyID!=0)){
                            try{
                                Integer cId = Integer.parseInt(clickedId);
                                checkInternet = urlClass.checkInternet();
                                if(checkInternet){
                                    //new getCallDetails().execute(logedUserID,cId,CompanyID);
                                    getCallDetails(logedUserID,CompanyID,cId);
                                }else{
                                    onTaskCompleted("Internet connection failed, please check");
                                }

                            }catch (Exception ee){
                                Log.e("eeee",ee.getLocalizedMessage());
                                onTaskCompleted(ee.getLocalizedMessage());
                            }
                        } else {
                            Toast.makeText(tabCallDetails.this,"Unable to get user details, ",Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception ee){
                        Toast.makeText(tabCallDetails.this,"Unable to get user details, "+ee.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                    return true;
                case R.id.navigation_dashboard:
                    layout_user.removeAllViews();
                    //  mTextMessage.setText(R.string.title_dashboard);
                    LayoutInflater inflater_action = LayoutInflater.from(tabCallDetails.this);
                    View view_action = inflater_action.inflate(R.layout.call_action, layout_user, false);
                    layout_user.addView(view_action);
                    initializeActionData(view_action);
                    if(callStatusG.equals("6") || callStatusG.equals("7")){
                        actionLayout.setVisibility(LinearLayout.GONE);
                    } else {
                        actionLayout.setVisibility(LinearLayout.VISIBLE);
                    }


                    try{
                        String callId = sharedpreferences.getString("clickedId","0");
                        CompanyID    = sharedpreferences.getInt(CompanyId,0);
                        if (!callId.equals(0) && CompanyID != 0 && CompanyID.toString() != "o0") {
                            try {
                                checkInternet = urlClass.checkInternet();
                                if(checkInternet){
                                    //new update_read_status().execute(callId,CompanyID);
                                    Log.e("CompanyID", CompanyID + "");
                                   // new get_user_chat_list().execute(callId,CompanyID+"");
                                    get_user_chat_list(Integer.parseInt(callId),CompanyID);
                                    getProblemDescription(Integer.parseInt(callId),CompanyID);
                                    //new getProblemDescription().execute(callId, CompanyID.toString());
                                }else{
                                    onTaskCompleted("Internet connection failed, please check");
                                }
                            }catch (Exception e){
                                onTaskCompleted(e.getLocalizedMessage());
                            }

                        }

                        btn_add_description.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                LayoutInflater factory = LayoutInflater.from(tabCallDetails.this);
                                final View addDialogView1 = factory.inflate(R.layout.problem_dailog, null);
                                final AlertDialog addDialog = new AlertDialog.Builder(tabCallDetails.this).create();
                                addDialog.setView(addDialogView1);
                                addDialogView1.findViewById(R.id.btn_add_call_problem).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //your business logic
                                        try{
                                            EditText description_edit = addDialogView1.findViewById(R.id.add_problem_description);
                                            String description = description_edit.getText().toString();
                                            if(description.equals("")){
                                                onTaskCompleted("Input required");
                                            } else {
                                                try{
                                                    checkInternet = urlClass.checkInternet();
                                                    if(checkInternet){
                                                        addCallDescription(description);
                                                      //  new addCallDescription().execute();
                                                    }else{
                                                        onTaskCompleted("Internet connection failed, please check");
                                                    }
                                                }catch (Exception e){
                                                    onTaskCompleted(e.getLocalizedMessage());
                                                }

                                                addDialog.dismiss();
                                            }

                                        }catch (Exception ee){
                                            onTaskCompleted(ee.getLocalizedMessage());
                                        }

                                    }
                                });
                                addDialogView1.findViewById(R.id.btn_close_call_problem).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addDialog.dismiss();
                                    }
                                });

                                addDialog.show();
                            }
                        });
                        btn_call_action.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                LayoutInflater factory = LayoutInflater.from(tabCallDetails.this);
                                final View actionDialogView = factory.inflate(R.layout.action_dailog, null);
                                final AlertDialog actionDialog = new AlertDialog.Builder(tabCallDetails.this).create();

                                call_action_spinner = actionDialogView.findViewById(R.id.call_status_list);
                                call_action_reason_spinner = actionDialogView.findViewById(R.id.call_actionTaken_list);
                                final EditText action_note_txt = actionDialogView.findViewById(R.id.action_note_txt);
                                spinnerLabelList = new ArrayList<String>();
                                spinnerLabelListReason = new ArrayList<String>();
                                spinnerIdList = new ArrayList<Integer>();
                                spinnerIdListReason = new ArrayList<Integer>();
                                spinnerLabelList.add("Select call status");
                                spinnerIdList.add(0);
                                spinnerLabelListReason.add("Select action");
                                spinnerIdListReason.add(0);
                                try {
                                    UserName = sharedpreferences.getString(USERNAME, "");
                                    logedUserID       = sharedpreferences.getInt(UserId,0);
                                    CompanyID    = sharedpreferences.getInt(CompanyId,0);
                                    UuserRole    = sharedpreferences.getInt(USERROLE,0);
                                    IsUseractive = sharedpreferences.getBoolean(IsUserActive,false);
                                    String clickedId = sharedpreferences.getString("clickedId","0");
                                    try {
                                        checkInternet = urlClass.checkInternet();
                                        if(checkInternet){
                                            getCallStatus(CompanyID);
                                            getCallReason(CompanyID);
                                        }else{
                                            onTaskCompleted("Internet connection failed, please check");
                                        }
                                    } catch (Exception ee){
                                        onTaskCompleted(ee.getLocalizedMessage());
                                    }

                                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(tabCallDetails.this, android.R.layout.simple_spinner_item, spinnerLabelList);
                                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    call_action_spinner.setAdapter(dataAdapter);

                                    call_action_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            statusId = spinnerIdList.get(position);
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
                                    ArrayAdapter<String> dataAdapterReason = new ArrayAdapter<String>(tabCallDetails.this, android.R.layout.simple_spinner_item, spinnerLabelListReason);
                                    dataAdapterReason.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    call_action_reason_spinner.setAdapter(dataAdapterReason);

                                    call_action_reason_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            reasonId = spinnerIdListReason.get(position);
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });

                                }catch (Exception ee){
                                    onTaskCompleted(ee.getLocalizedMessage());
                                }

                                actionDialog.setView(actionDialogView);

                                actionDialogView.findViewById(R.id.btn_apply_action).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        UserName     = sharedpreferences.getString(USERNAME,"");
                                        logedUserID       = sharedpreferences.getInt(UserId,0);
                                        CompanyID    = sharedpreferences.getInt(CompanyId,0);
                                        UuserRole    = sharedpreferences.getInt(USERROLE,0);
                                        IsUseractive = sharedpreferences.getBoolean(IsUserActive,false);
                                        Integer userId = logedUserID;//sharedpreferences.getInt(UserId,0);
                                        Integer callId = sharedpreferences.getInt("callId",0);
                                        String clickedId = sharedpreferences.getString("clickedId","0");

                                        String Note = action_note_txt.getText().toString();
                                        if(statusId.equals(null) || statusId.equals(0)){
                                            onTaskCompleted("Please select call status");
                                            call_spinner.setFocusable(true);

                                        } else if(reasonId.equals(null) || reasonId.equals(0)){
                                            onTaskCompleted("Please select call status reason");
                                            call_spinner.setFocusable(true);
                                        }else if(userId.equals(0) || callId.equals(0)){
                                            onTaskCompleted("Unable to get the call or user detail");
                                        } else if(Note.equals("")){
                                            onTaskCompleted("Mention status change reason");
                                            action_note_txt.setFocusable(true);
                                            action_note_txt.setError("Required");
                                        } else{
                                            try{
                                                checkInternet = urlClass.checkInternet();
                                                if(checkInternet){
                                                   // new applyActionOnCall().execute(Note);
                                                    applyActionOnCall(Note);
                                                    actionDialog.dismiss();
                                                }else{
                                                    onTaskCompleted("Internet connection failed, please check");
                                                }

                                            }catch (Exception ee){
                                                onTaskCompleted(ee.getMessage());
                                            }

                                        }
                                    }
                                });
                                actionDialogView.findViewById(R.id.btn_close_action).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        actionDialog.dismiss();
                                    }
                                });

                                actionDialog.show();
                            }
                        });
                        btn_attend_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try{
                                    checkInternet = urlClass.checkInternet();
                                    if(checkInternet){
                                        try{
                                            userLocation =  share_user_location();
                                        }catch (Exception e){
                                            onTaskCompleted(e.getLocalizedMessage()+" location share error");
                                        }
                                    } else {
                                        onTaskCompleted("Internet connection failed, please check");
                                    }
                                }catch (Exception e){
                                    onTaskCompleted(e.getLocalizedMessage());
                                }

                            }
                        });
                    }catch (Exception ee){
                        onTaskCompleted(ee.getLocalizedMessage());
                    }

                    return true;
                case R.id.navigation_notifications:
                    layout_user.removeAllViews();
                    // mTextMessage.setText(R.string.title_notifications);
                    LayoutInflater inflater_other = LayoutInflater.from(tabCallDetails.this);
                    View view_other = inflater_other.inflate(R.layout.call_other, layout_user, false);
                    layout_user.addView(view_other);
                    otherImagesList = new ArrayList<OtherImage>();
                    LinearLayout image_other_view = view_other.findViewById(R.id.image_other_view);
                    if (callStatusG.equals("6") ||callStatusG.equals("7")) {
                        image_other_view.setVisibility(LinearLayout.GONE);
                    } else {
                        image_other_view.setVisibility(LinearLayout.VISIBLE);
                    }

                    try {
                        Button b = view_other.findViewById(R.id.btn_camera_open);
                        imageHolder = view_other.findViewById(R.id.captured_image_other);
                        btn_upload = view_other.findViewById(R.id.btn_upload);
                        imageHolder.setImageDrawable(null);
                        selectedImage = null;
                        b.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                selectImage();
                            }
                        });
                        gridview = view_other.findViewById(R.id.gridview);
                        no_attachment = view_other.findViewById(R.id.no_attachment);
                        if (!logedUserID.equals(0) || !CompanyID.equals(0)) {
                            try {
                                String callId = sharedpreferences.getString("clickedId", "0");
                                if(checkInternet){
                                    loadCallAttachment(logedUserID,CompanyID,callId,gridview);
                                } else {
                                    onTaskCompleted("Internet connection failed, please check");
                                }
                            }catch (Exception e){
                                onTaskCompleted(e.getLocalizedMessage());
                            }
                        } else {
                            Toast.makeText(tabCallDetails.this,"Unable to get user details, ",Toast.LENGTH_LONG).show();
                        }

                        btn_upload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UserName     = sharedpreferences.getString(USERNAME,"");
                                logedUserID       = sharedpreferences.getInt(UserId,0);
                                CompanyID    = sharedpreferences.getInt(CompanyId,0);
                                UuserRole    = sharedpreferences.getInt(USERROLE,0);
                                IsUseractive = sharedpreferences.getBoolean(IsUserActive,false);
                                Integer userId = logedUserID;//sharedpreferences.getInt(UserId,0);
                                Integer callId = sharedpreferences.getInt("callId",0);
                                if(selectedImage!=null && selectedContract!="" && callId!=0 && logedUserID!=0){
                                    new upLoaData().execute(selectedImage,CompanyID+"",selectedContract,callId+"",logedUserID+"");
                                } else {
                                    Toast.makeText(tabCallDetails.this, "Unable to get call details. check log", Toast.LENGTH_SHORT).show();
                                    Log.e("call Details",selectedImage+","+CompanyID+","+selectedContract+","+callId+","+logedUserID+"");
                                }

                            }
                        });
                    }catch (Exception ee){
                        onTaskCompleted(ee.getLocalizedMessage());
                    }
                    return true;

            }
            return false;

        }

    };
    public void loadCallAttachment(Integer UserId, Integer companyId,String callId,final GridView gridView){
        //GetSelectedCallByID

        try {
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();
            Integer statusId = 7;
            final String url = urlClass.getUrl()+"GetCallsAttachment?callId=" + callId +"&UserId=" + UserId +"&companyId=" + companyId;
            JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.POST,
                    url,null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            Log.d("Closed Call", jsonArray.toString()+" url:"+url);
                            pDialog.hide();
                            try {
                                if(jsonArray.length()>0){
                                    otherImagesList.clear();
                                    for(int i=0;i<jsonArray.length();i++){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        String imagepath = jsonObject.getString("imagepath");
                                        try {
                                            OtherImage img = new OtherImage();
                                            img.setImageUrl(imagepath);
                                            otherImagesList.add(img);
                                        } catch (Exception e) {
                                            Log.e("displayCountryList: ", e.getLocalizedMessage());
                                        }
                                    }
                                    gridView.setVisibility(View.VISIBLE);
                                    no_attachment.setVisibility(View.GONE);
                                    gridView.setAdapter(new ImageAdapter(getApplicationContext(), otherImagesList, urlClass.getUrl()));
                                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            try {
                                                String img = otherImagesList.get(i).getImageUrl();
                                                String url = urlClass.getFileUrl() + img;
                                                loadPhoto(url);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                } else {
                                    gridView.setVisibility(View.GONE);
                                    no_attachment.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception ee){
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
            Toast.makeText(tabCallDetails.this,"closedCall:"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }
    private void loadPhoto(String url) {

        final Dialog dialog = new Dialog(tabCallDetails.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //dialog.setContentView(R.layout.custom_fullimage_dialog);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_fullimage_dialoge, null);
        ImageView image = layout.findViewById(R.id.fullimage);

        Picasso.with(this)
                .load(url).into(image);
        image.requestLayout();
        dialog.setContentView(layout);
        dialog.show();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_open_call);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        urlClass = new UrlClass(tabCallDetails.this);
        checkInternet = urlClass.checkInternet();
        try {
            msg_List = new ArrayList<ChatMessage>();

            sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            Intent acName = getIntent();
            String activity_name = acName.getStringExtra("activity_name");
            getSupportActionBar().setTitle(activity_name);
            layout_user = (LinearLayout) findViewById(R.id.user_layout);
            layout_user.removeAllViews();
            LayoutInflater inflater_default = LayoutInflater.from(tabCallDetails.this);
            View view_default = inflater_default.inflate(R.layout.call_info, layout_user, false);
            layout_user.addView(view_default);
            initializeInfoData(view_default);
            UserName = sharedpreferences.getString(USERNAME, "");
            logedUserID = sharedpreferences.getInt(UserId, 0);
            CompanyID = sharedpreferences.getInt(CompanyId, 0);
            UuserRole = sharedpreferences.getInt(USERROLE, 0);
            IsUseractive = sharedpreferences.getBoolean(IsUserActive, false);
            String clickedId = sharedpreferences.getString("clickedId", "0");
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            if ((clickedId != null || clickedId != "0") && (CompanyID != null || CompanyID != 0)) {
                try {
                    Integer cId = Integer.parseInt(clickedId);
                    if (checkInternet) {
                        //new getCallDetails().execute(logedUserID, cId, CompanyID);
                        getCallDetails(logedUserID,CompanyID,cId);
                        userLocation = share_user_location();
                    } else {
                        onTaskCompleted("Internet connection failed, please check");
                    }
                } catch (Exception ee) {
                    Log.e("eeee", ee.getLocalizedMessage());
                    onTaskCompleted(ee.getLocalizedMessage());
                }
            } else {
                Toast.makeText(tabCallDetails.this, "Unable to get user details, ", Toast.LENGTH_LONG).show();
            }

        } catch (Exception ee) {
            Toast.makeText(tabCallDetails.this, "Unable to get user details, " + ee.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

        // mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onTaskCompleted(String response) {
        Toast.makeText(this, "Call Responce: " + response, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("ActivityCode", 1);
        Integer ActivityCde = sharedpreferences.getInt("ActivityCode", 0);
        if (ActivityCde == 1) {
            startActivity(new Intent(tabCallDetails.this, newCall.class));
            finish();
        }
        if (ActivityCde == 2) {
            startActivity(new Intent(tabCallDetails.this, openCall.class));
            finish();
        }
        if (ActivityCde == 3) {
            startActivity(new Intent(tabCallDetails.this, pendingCall.class));
            finish();
        }
        if (ActivityCde == 4) {
            startActivity(new Intent(tabCallDetails.this, resolvedCall.class));
            finish();
        }
        if (ActivityCde == 5) {
            startActivity(new Intent(tabCallDetails.this, closedCall.class));
            finish();
        }

    }

    public void initializeInfoData(View initializeData) {
        try {
            call_veiw_id = initializeData.findViewById(R.id.call_veiw_id);
            call_veiw_date = initializeData.findViewById(R.id.call_veiw_date);
            // call_veiw_rnumber = (TextView)  initializeData.findViewById(R.id.call_veiw_rnumber);
            call_veiw_productNumber = initializeData.findViewById(R.id.call_veiw_productNumber);
            call_veiw_productType = initializeData.findViewById(R.id.call_veiw_productType);
            call_view_issueDetails = initializeData.findViewById(R.id.call_view_issueDetails);
            call_details_customerName = initializeData.findViewById(R.id.call_details_customerName);
            call_details_subcustomerName = initializeData.findViewById(R.id.call_details_subcustomerName);
            call_details_mobileNumber = initializeData.findViewById(R.id.call_details_mobileNumber);
            call_details_emailId = initializeData.findViewById(R.id.call_details_emailId);
            call_details_customerAddress = initializeData.findViewById(R.id.call_details_customerAddress);
            // call_time = (TextView)  initializeData.findViewById(R.id.call_time);
            serviceType = (TextView) findViewById(R.id.serviceType);
            IssueType = (TextView) findViewById(R.id.IssueType);
            IssuePriority = (TextView) findViewById(R.id.IssuePriority);
            LocationCall = (TextView) findViewById(R.id.LocationCall);
            productName = (TextView) findViewById(R.id.call_veiw_productName);
            contract_number = (TextView) findViewById(R.id.contract_number);
            contract_type = (TextView) findViewById(R.id.contract_type);
            product_details = (TextView) findViewById(R.id.product_details);

        } catch (Exception ee) {
            onTaskCompleted(ee.getMessage());
        }
    }

    private boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            return result == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(tabCallDetails.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = UrlClass.checkPermission(tabCallDetails.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case UrlClass.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    Toast.makeText(this, "Permission required to complete the action", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (data != null) {
            try {

                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] byteArray = bytes.toByteArray();
        selectedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        imageHolder.setImageBitmap(bm);
    }
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] byteArray = bytes.toByteArray();
        selectedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        imageHolder.setImageBitmap(thumbnail);
    }
    private boolean isDeviceSupportCamera() {
        // this device has a camera
        // no camera on this device
        return getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    /*
     * Here we restore the fileUri again
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        String IMAGE_DIRECTORY_NAME = "CM";
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == 2) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
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
            startActivity(new Intent(tabCallDetails.this,userHome.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void initializeActionData(View action_view) {

        btn_add_description = action_view.findViewById(R.id.btn_add_problem);
        btn_attend_call = action_view.findViewById(R.id.btn_attend_call);
        btn_call_action = action_view.findViewById(R.id.btn_call_action);
        actionLayout = action_view.findViewById(R.id.actionLayout);
        chat_message_list = action_view.findViewById(R.id.call_action_text_list);
        pd_txt = action_view.findViewById(R.id.pd_txt);
        View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_footer_view, null, false);
        //    chat_message_list.addFooterView(footerView);
    }
    public String share_user_location(){
        Location loc;
        double latitude;
        double longitude;
        LocationManager locationManager = (LocationManager) getBaseContext()
                .getSystemService(LOCATION_SERVICE);
        boolean checkGPS = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // get network provider status
        boolean checkNetwork = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        String provider = "";
        if (checkGPS) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (checkNetwork) {
            provider = LocationManager.NETWORK_PROVIDER;
        }
        checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (provider.equals("")) {
            Log.e("Unable to get provider", "check setting");
        } else {
            locationManager.requestLocationUpdates(provider, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });

            if (locationManager != null) {

                loc = locationManager
                        .getLastKnownLocation(provider);
                if (loc != null) {
                    latitude = loc.getLatitude();
                    longitude = loc.getLongitude();
                    userLocation = latitude + "," + longitude;
                    Log.e("userLocation",userLocation);
                } else {
                    Log.e("Unable to get", "Location");
                }


            } else {
                Log.e("Unable to get", "Location");
            }
        }
        return  userLocation;
        }

    public void applyActionOnCall(String note){
        //GetSelectedCallByID

        try{
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();

            Integer callId = sharedpreferences.getInt("callId",0);
            final String url = urlClass.getUrl()+"CallActions?userLocation="+userLocation+"&userRole="+UuserRole+
                    "&UserId="+logedUserID+"&action_note="+note+"&CallReasonId="+reasonId+
                    "&action="+statusId+"&callId="+callId+"&companyId=" + CompanyID;

            JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.POST,
                    url,null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            Log.e("CallActions", jsonArray.toString()+"url:"+url);

                            pDialog.hide();
                            try{
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String code = jsonObject.getString("code");
                                String message = jsonObject.getString("message");
                                if(code.equals("1")){
                                    onTaskCompleted("Action Applied !!!");
                                    String callId = sharedpreferences.getString("clickedId","0");
                                    CompanyID    = sharedpreferences.getInt(CompanyId,0);
                                    if(!callId.equals(0) && CompanyID!=0){
                                        try {
                                            checkInternet = urlClass.checkInternet();
                                            if(checkInternet){
                                                //new update_read_status().execute(callId,CompanyID);
                                                // new get_user_chat_list().execute(callId,CompanyID+"");
                                                get_user_chat_list(Integer.parseInt(callId),CompanyID);
                                                onBackPressed();
                                            } else {
                                                onTaskCompleted("Internet connection failed, please check");
                                            }
                                        } catch (Exception e) {
                                            onTaskCompleted(e.getLocalizedMessage());
                                        }
                                    }
                                } else {
                                    onTaskCompleted(message);
                                }
                            } catch (Exception e){
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
            Toast.makeText(tabCallDetails.this,"User Home:"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public void addCallDescription(String action_note){
        //GetSelectedCallByID

        try{
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();
            String callId       = sharedpreferences.getString("clickedId","0");
            logedUserID       = sharedpreferences.getInt(UserId,0);

            Integer c = Integer.parseInt(callId);


            String url = urlClass.getUrl()+"SendMessage?ActionNote="+action_note+"&CallId="+c+"&CompanyId=" + CompanyID;

            JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET,
                    url,null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            Log.d("Add CallD", jsonArray.toString());

                            pDialog.hide();
                            if (jsonArray.length()!=0) {
                                try{
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    Integer code = jsonObject.getInt("code");
                                    Integer callId = jsonObject.getInt("callId");
                                    Integer companyId = jsonObject.getInt("companyId");
                                    if(code == 1){
                                        getProblemDescription(callId,companyId);
                                    }else {
                                        getProblemDescription(callId,companyId);
                                        Toast.makeText(getApplicationContext(),"No Change or something went wrong",Toast.LENGTH_LONG).show();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),""+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
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
            Toast.makeText(tabCallDetails.this,"User Home:"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void getProblemDescription(Integer callId, Integer companyId){
        //GetSelectedCallByID

        try{
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("companyId", companyId);
            Log.d("companyId", companyId.toString());
            String url = urlClass.getUrl()+"getProblemDesc?callId="+ callId +"&companyId="+ companyId;

            JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.POST,
                    url,null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            Log.d("get p Description", jsonArray.toString());

                            pDialog.hide();
                            if(!jsonArray.equals(null)
                            ){
                                try {
                                    if(jsonArray.length()>0){
                                        for(int i = 0; i<jsonArray.length(); i++){
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            String pd = jsonObject.getString("userCallDescription");
                                            if(!pd.equals("")){
                                                pd_txt.setText(pd);
                                                btn_add_description.setEnabled(false);
                                            } else {
                                                btn_add_description.setEnabled(true);
                                            }
                                        }
                                    } else {
                                        Log.i("No Data"," found spinner");
                                        // onTaskCompleted("No Data found spinner");
                                    }
                                } catch (Exception ee){
                                    Log.i("No Data", " found spinner");
                                    //onTaskCompleted(ee.getLocalizedMessage());
                                }
                            } else {
                                Log.i("No Data", " found spinner");
                                //onTaskCompleted("Unable to get details, try gain");
                            }   }
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
            Toast.makeText(tabCallDetails.this,"User Home:"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public class upLoaData extends AsyncTask<String,String, String> {

        String url = urlClass.getUrl();
        String NameSpace = urlClass.NameSpace();
        String exp_string="";
        String err_string="";
        public ProgressDialog dialog =
                new ProgressDialog(tabCallDetails.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try{
                String urlString = urlClass.getFileUrl();
                String api = urlString+"fileUpload.php";
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost =new HttpPost(api);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("invoiceImage",params[0]));
                nameValuePairs.add(new BasicNameValuePair("companyId",params[1]));
                nameValuePairs.add(new BasicNameValuePair("contractId",params[2]));
                nameValuePairs.add(new BasicNameValuePair("callId",params[3]));
                nameValuePairs.add(new BasicNameValuePair("userId",params[4]));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {

                    String responseStr = EntityUtils
                            .toString(resEntity).trim();
                    err_string = responseStr;
                    exp_string = responseStr;

                }

            } catch (Exception e){
                exp_string = e.getLocalizedMessage();
            }
            result =err_string;
            return  result;
        }
        protected void onCancelled() {
            dialog.dismiss();
            Toast toast = Toast.makeText(tabCallDetails.this,
                    "Error connecting to Server", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();
            /*show_local_db();*/
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("err_string","==>"+result);

            if(!result.equals("") && !result.equals(null)){
                try {
                    dialog.dismiss();
                    JSONObject jsonObject = new JSONObject(result);
                    String code = jsonObject.getString("code");
                    if(code.equals("1")){
                        Toast.makeText(getApplicationContext(),"Upload Successfully", Toast.LENGTH_LONG).show();
                        if(!logedUserID.equals(0) || !CompanyID.equals(0)){
                            try {
                                String callId = sharedpreferences.getString("clickedId","0");
                                if(checkInternet){
                                    //new loadClosedCall().execute(UserID,CompanyID);
                                    imageHolder.setImageDrawable(null);
                                    selectedImage = null;
                                    loadCallAttachment(logedUserID, CompanyID, callId, gridview);
                                } else {
                                    onTaskCompleted("Internet connection failed, please check");
                                }
                            }catch (Exception e){
                                onTaskCompleted(e.getLocalizedMessage());
                            }
                        } else {
                            Toast.makeText(tabCallDetails.this, "Something went wrong, Try again.", Toast.LENGTH_LONG).show();
                        }
                      //  startActivity(new Intent(tabCallDetails.this,tabCallDetails.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } else if(code.equals("0")){
                        Toast.makeText(getApplicationContext(),"Error in upload", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Server connection Failed,Error:"+result, Toast.LENGTH_LONG).show();
                        Log.e("Ex",result);
                    }

                } catch (Exception ee){
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),ee.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(),"Server connection Failed", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void getCallStatus(Integer companyId){
        //GetSelectedCallByID

        try{
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("companyId", companyId);
            Log.d("companyId", companyId.toString());


            String url = urlClass.getUrl()+"GetCallStatus?companyId=" + companyId;

            JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET,
                    url,null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            Log.d("userhome", jsonArray.toString());

                            pDialog.hide();

                            if(jsonArray.length()!=0 ){
                                try {
                                    if(jsonArray.length()>0){
                                        for(int i=0;i<jsonArray.length();i++){
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            Integer CallstatusId = jsonObject.getInt("CallstatusId");
                                            String StatusName = jsonObject.getString("StatusName");
                                            spinnerLabelList.add(StatusName);
                                            spinnerIdList.add(CallstatusId);
                                        }
                                    } else {
                                        onTaskCompleted("No Data found spinner");
                                    }
                                } catch (Exception ee){
                                    onTaskCompleted(ee.getLocalizedMessage());
                                }
                            } else{
                                onTaskCompleted("Unable to get details, try gain");
                            }}
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
            Toast.makeText(tabCallDetails.this,"User Home:"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public void get_user_chat_list(Integer callId, Integer companyId){
        //GetSelectedCallByID

        try{
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("callId", callId);
            jsonObject.put("companyId", companyId);
            Log.d("companyId", companyId.toString());


            String url = urlClass.getUrl()+"GetMessage?CallId=" + callId +"&UserId=" + UserId +"&CompanyId=" + companyId;

            JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET,
                    url,null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            pDialog.hide();
                            if(jsonArray.length()!=0){
                                try {
                                    msg_List.clear();
                                    if(jsonArray.length()>0){
                                        for(int i=0;i<jsonArray.length();i++){
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            //1
                                            ChatMessage chatMessage = new ChatMessage();
                                            try {
                                                String UserId = jsonObject.getString("UserId");
                                                String TimeStampStr = jsonObject.getString("TimeStampStr");
                                                Integer db_user = Integer.parseInt(UserId);
                                                String Text = jsonObject.getString("Text");
                                                if(db_user.equals(logedUserID)){
                                                    chatMessage.setMessage(Text);
                                                    chatMessage.setDate_time(TimeStampStr);
                                                    chatMessage.setSide(1);
                                                } else {
                                                    chatMessage.setMessage(Text);
                                                    chatMessage.setDate_time(TimeStampStr);
                                                    chatMessage.setSide(2);
                                                }
                                                msg_List.add(chatMessage);

                                            } catch (Exception ee){
                                                onTaskCompleted(ee.getMessage());
                                            }

                                        }
                                        chatArrayAdapter = new chatAdaptor(msg_List,tabCallDetails.this);
                                        chat_message_list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                                        chat_message_list.setAdapter(chatArrayAdapter);

                                    } else {
                                        onTaskCompleted("No Data found");
                                    }
                                } catch (Exception ee){
                                    onTaskCompleted(ee.getLocalizedMessage());
                                }
                            } else{
                                onTaskCompleted("No call history to show");
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
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
            Toast.makeText(tabCallDetails.this,"User Home:"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public void getCallReason(Integer companyId){
        //GetSelectedCallByID

        try{
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("companyId", companyId);
            Log.d("companyId", companyId.toString());


            String url = urlClass.getUrl()+"GetCallActionReasons?companyId=" + companyId;

            JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.POST,
                    url,null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            Log.d("userhome", jsonArray.toString());

                            pDialog.hide();

                            if(jsonArray.length()!=0){
                                try {
                                    if(jsonArray.length()>0){
                                        for(int i=0;i<jsonArray.length();i++){
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            Integer CallstatusId = jsonObject.getInt("Id");
                                            String StatusName = jsonObject.getString("No");
                                            spinnerLabelListReason.add(StatusName);
                                            spinnerIdListReason.add(CallstatusId);
                                        }
                                        call_action_reason_spinner.setSelection(spinnerIdListReason.get(1));
                                    } else {
                                        onTaskCompleted("No Data found spinner");
                                    }
                                } catch (Exception ee){
                                    onTaskCompleted(ee.getLocalizedMessage());
                                }
                            } else{
                                onTaskCompleted("Unable to get details, try gain");
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
            Toast.makeText(tabCallDetails.this,"User Home:"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public void getCallDetails(Integer UserID, Integer companyId,Integer callId){
        //GetSelectedCallByID

        try{
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", UserID);
            jsonObject.put("companyId", companyId);
            jsonObject.put("callId", callId);
            Log.d("companyId", companyId.toString());


            String url = urlClass.getUrl()+"GetSelectedCallByID?callId="+callId +"&companyId=" + companyId;

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
                                        String callId = sharedpreferences.getString("clickedId","0");
                                        String Id = jsonObject.getString("Id");
                                        Log.e("Id",""+Id);
                                        Log.e("callId",""+callId);
                                        try{
                                            if(Id.equals(callId)){
                                                String CallNo = jsonObject.getString("callNo");
                                                String Date = jsonObject.getString("callDatestr");
                                                String ProductType = jsonObject.getString("productType");
                                                String ProductBrand = jsonObject.getString("productNo");
                                                String ProductNamev= jsonObject.getString("productName");
                                                String CallDesc = jsonObject.getString("callDetails");
                                                String CustomerName = jsonObject.getString("customerName");
                                                String SubCustomerName = jsonObject.getString("subCustomerName");
                                                String ContactNo = jsonObject.getString("customerContact");
                                                String CustEmail = jsonObject.getString("CustEmail");
                                                String Address = jsonObject.getString("Address");
                                                selectedContract = ContactNo;
                                                String contract_numberV = jsonObject.getString("contactNo");
                                                String contract_typeV = jsonObject.getString("contactType");
                                                String product_detailsV = jsonObject.getString("prodcutDetails");
                                                String serviceTypeV = jsonObject.getString("serviceType");
                                                String IssueTypeV = jsonObject.getString("issueType");
                                                String IssuePriorityV = jsonObject.getString("servicePrority");
                                                String LocationCallV = jsonObject.getString("callLocation");
                                                String callStatus = jsonObject.getString("CallStatus");
                                                callStatusG = callStatus;

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
                                                call_details_customerName.setText(CustomerName);
                                                call_details_subcustomerName.setText(SubCustomerName);
                                                call_details_mobileNumber.setText(ContactNo);
                                                call_details_emailId.setText(CustEmail);
                                                call_details_customerAddress.setText(Address);
                                            }
                                        }catch (Exception ee){
                                            onTaskCompleted(ee.getMessage());
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
            Toast.makeText(tabCallDetails.this,"User Home:"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        Intent intentN = new Intent(tabCallDetails.this,check_notification.class);
        Intent intentL = new Intent(tabCallDetails.this,locationService.class);
        Log.i("MAINACT", "onDestroy!");
        stopService(intentL);
        stopService(intentN);

        super.onDestroy();
    }
}
