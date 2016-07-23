package com.uc3m.p4r4d0x.emergapp.helpers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by Alvaro Loarte Rodríguez on 19/02/16.
 *
 */

public class DBUserManager {

    //Table Name
    public static final String TABLE_NAME="Users";
    //Fields Name
    public static final String TU_ID = "_id";
    public static final String TU_NAME= "name";
    public static final String TU_PASSWORD="password";
    public static final String TU_EMAIL="email";
    public static final String TU_DATE="date";
    public static final String TU_LEVEL="level";
    public static final String TU_AP_POINTS="ap_points";
    public static final String TU_XP_POINTS="xp_points";
    public static final String TU_TITLE="title";
    public static final String TU_COLOR="color";
    public static final String TU_MODIFY_TITLE="modify_title";
    public static final String TU_MODIFY_AVATAR="modify_avatar";
    public static final String TU_MODIFY_COLOR="modify_color";





    //Sql sentence for building the table Users
    public static final String CREATE_TABLE = "create table "+TABLE_NAME+" ("
            //+ TU_ID             + " integer primary key autoincrement,"
            + TU_NAME           + " text primary key not null,"
            + TU_PASSWORD       + " text not null,"
            + TU_EMAIL          + " text not null,"
            + TU_DATE           + " text not null,"
            + TU_LEVEL          + " text not null,"
            + TU_AP_POINTS      + " integer not null,"
            + TU_XP_POINTS      + " integer not null,"
            + TU_TITLE          + " text,"
            + TU_COLOR          + " integer not null,"
            + TU_MODIFY_TITLE   + " integer not null,"
            + TU_MODIFY_AVATAR  + " integer not null,"
            + TU_MODIFY_COLOR   + " integer not null);";

    //DBHelper & SQLIteDatabase objects
    private DBHelper helper;
    private SQLiteDatabase db;

    //Constructor
    public DBUserManager(Context context) {
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    /*
    * Desc: Fills an ContentValue object with data
    * Param: Strings with user data
    * Ret: A filled ContentValues object
    * */
    public ContentValues generateCVUser( String name, String password, String email, String date,
                                     String level,int ap_points,int xp_points, String title, int color,
                                     int modifTitle,int modifImage, int modifColor ){

        ContentValues contentV = new ContentValues();
        contentV.put(TU_NAME,name);
        contentV.put(TU_PASSWORD,password);
        contentV.put(TU_EMAIL,email);
        contentV.put(TU_DATE,date);
        contentV.put(TU_LEVEL,level);
        contentV.put(TU_AP_POINTS,ap_points);
        contentV.put(TU_XP_POINTS,xp_points);
        contentV.put(TU_TITLE,title);
        contentV.put(TU_COLOR,color);
        contentV.put(TU_MODIFY_TITLE,modifTitle);
        contentV.put(TU_MODIFY_AVATAR,modifImage);
        contentV.put(TU_MODIFY_COLOR,modifColor);
        return contentV;
    }

    /*
    * Desc: Insert on database a new user
    * Param: Strings with user data
    * */
    public boolean insertUser(String name, String password, String email, String date){
        String level="Traveler",title=null;
        int APpoints=0,XPpoints=0;
        int color=0;
        int modifTitle=0, modifAvatar=0,modifColor=0;
        long retValue=0;
        retValue=db.insert(TABLE_NAME, null, generateCVUser(name, password, email, date,level,
                                                        APpoints,XPpoints,title,color,
                                                        modifTitle,modifAvatar,modifColor));
        if(retValue==-1 || retValue==0){
            return false;
        }
        else{
            return true;
        }

    }

    /*
    * Desc: Select from the database an user
    * Param: the nickname of the user
    * Ret: Cursor object with the information
    * */
    public Cursor selectUser(String userName){
        return db.rawQuery("SELECT * from " + TABLE_NAME + " where " + TU_NAME + "=\"" + userName + "\";", null);
    }

    /*
     * Desc: Upgrade one user in the database
     * Param: Strings with user data, and integers with the points and the level
     * Ret: Long with the amount of elements affected
    * */
    public long upgradeUser(String name, String password, String email, String date,
                        String level,int ap_points,int xp_points, String title, int color,
                        int modifTitle,int modifImage, int modifColor){

        return db.update(
                TABLE_NAME,
                generateCVUser(name, password, email, date, level,
                        ap_points, xp_points, title, color,
                        modifTitle, modifImage, modifColor)
                , TU_NAME + " LIKE ? ", new String[]{name});
    }


    public long upgradeUserColor(String username,int color){
        long retValue=-1;
        Cursor resultQuery= selectUser(username);

        //If the user exists
        if(resultQuery.moveToFirst()==true) {
            retValue=upgradeUser(
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_NAME)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_PASSWORD)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_EMAIL)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_DATE)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_LEVEL)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_AP_POINTS)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_XP_POINTS)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_TITLE)),
                    color,
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_MODIFY_TITLE)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_MODIFY_AVATAR)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_MODIFY_COLOR))
            );
        }
        return retValue;


    }

    public long upgradeUserLevelAndTitle(String username,String rank,String title){
        long retValue=-1;
        Cursor resultQuery= selectUser(username);

        //If the user exists
        if(resultQuery.moveToFirst()==true) {
            retValue=upgradeUser(
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_NAME)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_PASSWORD)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_EMAIL)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_DATE)),
                    rank,
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_AP_POINTS)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_XP_POINTS)),
                    title,
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_COLOR)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_MODIFY_TITLE)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_MODIFY_AVATAR)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_MODIFY_COLOR))
            );
        }
        return retValue;


    }

    public long upgradeUserAPXPpoints(String username,int appoints,int xppoints){
        long retValue=-1;
        Cursor resultQuery= selectUser(username);

        //If the user exists
        if(resultQuery.moveToFirst()==true) {
            retValue=upgradeUser(
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_NAME)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_PASSWORD)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_EMAIL)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_DATE)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_LEVEL)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_AP_POINTS)) + appoints,
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_XP_POINTS)) + xppoints,
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_TITLE)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_COLOR)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_MODIFY_TITLE)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_MODIFY_AVATAR)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_MODIFY_COLOR))
            );
        }
        return retValue;


    }

    public long upgradeUserUnlockTitleAvatarColor(String username,int unlockT, int unlockA, int unlockC){
        long retValue=-1;
        Cursor resultQuery= selectUser(username);

        //If the user exists
        if(resultQuery.moveToFirst()==true) {
            retValue=upgradeUser(
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_NAME)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_PASSWORD)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_EMAIL)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_DATE)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_LEVEL)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_AP_POINTS)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_XP_POINTS)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_TITLE)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_COLOR)),
                    unlockT,
                    unlockA,
                    unlockC
            );
        }
        return retValue;


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
