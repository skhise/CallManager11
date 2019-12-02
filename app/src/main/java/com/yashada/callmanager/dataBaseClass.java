package com.yashada.callmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Admin on 1/23/2018.
 */

public class dataBaseClass extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "call_manager";
    private static final String TABLE_ALL_CALL = "ALL_CALLS";
    private static final String TABLE_LOGIN = "Login_Details";

    private static final String KEY_ID = "id";
    private static final String KEY_CALLID = "call_id";
    private static final String KEY_CUST_NAME = "cust_name";
    private static final String KEY_SERVICE_TYPE = "service_type";
    private static final String KEY_CALL_STATUS = "call_status";
    private static final String KEY_CALL_ISSUE = "call_issue";
    private static final String KEY_CALL_ASSIGNED_ID = "engineer_id";

    private static final String KEY_ID_L = "id";
    private static final String KEY_ID_E = "engineerId";
    private static final String KEY_Name_L = "loginName";
    private static final String KEY_ID_Password = "loginPassword";
    private static final String KEY_ID_ISActive = "isActive";
    private static final String Login_email = "Login_email";

    public dataBaseClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_ALL_CALL + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CALLID + "INTEGER,"
                + KEY_CUST_NAME + " TEXT,"+ KEY_SERVICE_TYPE + " TEXT," +KEY_CALL_STATUS + " INTEGER,"+ KEY_CALL_ISSUE + " TEXT,"+ KEY_CALL_ASSIGNED_ID + " INTEGER)";

        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID_L + " INTEGER ," + KEY_ID_E + " INTEGER,"+ KEY_Name_L + " TEXT ,"
                + KEY_ID_Password + " TEXT,"+ KEY_ID_ISActive + " TEXT,"+Login_email+"TEXT)";
        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALL_CALL);
    }

    @Override
    public String getDatabaseName() {
        return super.getDatabaseName();
    }
    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ALL_CALL;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setSystem_call_id(cursor.getString(1));
                contact.setService_type(cursor.getString(2));
              //  contact.setCall_id(cursor.getString(3));
                contact.setCall_status(cursor.getString(4));
                contact.setCustomer_name(cursor.getString(5));
                // Adding contact to list

                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        db.close();
        return contactList;
    }
    public void AddCalls(Contact contact){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CALLID, contact.getCall_id()); // Contact Name
        values.put(KEY_CUST_NAME, contact.getCustomer_name()); // Contact Phone Number
        values.put(KEY_SERVICE_TYPE, contact.getService_type()); // Contact Phone Number
        values.put(KEY_CALL_STATUS, contact.getCall_status()); // Contact Phone Number
        values.put(KEY_CALL_ISSUE, contact.getCall_issue()); // Contact Phone Number
        values.put(KEY_CALL_ASSIGNED_ID, contact.call_assigned_id); // Contact Phone Number
        // Inserting Row
        db.insert(TABLE_ALL_CALL, null, values);
        db.close(); // Closing database connection
    }

}
