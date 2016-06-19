package com.uc3m.p4r4d0x.emergapp.helpers.database;

/**
 *
 * Created by Alvaro Loarte Rodríguez on 19/02/16.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

        private static final String DB_NAME ="users.sqlite";
        private static final int DB_SCHEME_VERSION =2;

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_SCHEME_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DBManager.CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS users; ");
            db.execSQL(DBManager.CREATE_TABLE);
        }
    }





