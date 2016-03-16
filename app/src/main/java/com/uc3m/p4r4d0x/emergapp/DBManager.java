package com.uc3m.p4r4d0x.emergapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by p4r4d0x on 19/02/16.
 */

public class DBManager {

        //Table Name
        public static final String TABLE_NAME="Users";
        //Fields Name
        public static final String FN_ID = "_id";
        public static final String FN_NAME= "name";
        public static final String FN_PASSWORD="password";
        public static final String FN_EMAIL="email";
        public static final String FN_DATE="date";
        //Sql sentence for building the table Users
        public static final String CREATE_TABLE = "create table "+TABLE_NAME+" ("
                + FN_ID +" integer primary key autoincrement,"
                + FN_NAME + " text not null,"
                + FN_PASSWORD + " text not null,"
                + FN_EMAIL + " text not null,"
                + FN_DATE + " text not null);";

        //DBHelper & SQLIteDatabase objects
        private DBHelper helper;
        private SQLiteDatabase db;

        //Constructor
        public DBManager(Context context) {
            helper = new DBHelper(context);
            db = helper.getWritableDatabase();
        }

        /*
        * Desc: Fills an ContentValue object with data
        * Param: Strings with user data
        * Ret: A filled ContentValues object
        * */
        public ContentValues generateCV(String name, String password, String email, String date){
            ContentValues contentV = new ContentValues();
            contentV.put(FN_NAME,name);
            contentV.put(FN_PASSWORD,password);
            contentV.put(FN_EMAIL,email);
            contentV.put(FN_DATE,date);
            return contentV;
        }

        /*
        * Desc: Insert on database a new user
        * Param: Strings with user data
        * */
        public void insertUser(String name, String password, String email, String date){

            db.insert(TABLE_NAME, null, generateCV(name, password, email, date));
            // Toast.makeText(this,"Escribiendo", Toast.LENGTH_SHORT).show();
        }

        /*
        * Desc: Select from the database an user
        * Param: the nickname of the user
        * Ret: Cursor object with the information
        * */
        public Cursor selectUser(String userName){
            return db.rawQuery("SELECT * from "+TABLE_NAME+" where "+FN_NAME+"=\""+userName+"\";", null);
        }

        /*
        * Desc: Check if the user exist on the database
        * Param: the nickname of the user
        * Ret: true if user exist, false  if not
        * */
        public boolean userExist(String userName){
            //Execute select and return if there is an element
            return selectUser(userName).moveToFirst();
        }




    }

