package org.androidtown.foodmanger2.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import org.androidtown.foodmanger2.BasicInfo;
import java.io.File;

public class MemoDatabase {


    public static final String TAG = "MemoDatabase";

    public static String TABLE_MEMO = "FOOD";

    private Context context;

    private static MemoDatabase database;

    public static int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    private DatabaseHelper dbHelper;

    private MemoDatabase(Context context) {
        this.context = context;
    }

    public static MemoDatabase getInstance(Context context) {
        if(database == null) {
            database = new MemoDatabase(context);
        }
        return database;
    }

    public boolean open() {
        println("opening database [" + BasicInfo.DATABASE_NAME + "].");
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();

        return true;

    }

    public void close() {
        println("closing database [" + BasicInfo.DATABASE_NAME + "].");
        db.close();

        database = null;
    }

    public Cursor rawQuery(String SQL) {

        Cursor cl = null;

        try {
            cl = db.rawQuery(SQL, null);
            println("cursor count: " + cl.getCount());
        }catch (Exception e) {
            Log.e(TAG, "Exception in executeQuery", e);
        }

        return cl;
    }

    public boolean execSQL(String SQL) {
        try {
            Log.d(TAG, "SQL: " + SQL);
            db.execSQL(SQL);
        } catch (Exception e) {
            Log.e(TAG, "Exception in executeQuery", e);
            return false;
        }
        return true;
    }


    // database helper inner class
    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, BasicInfo.DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            println("creating database [" + BasicInfo.DATABASE_NAME + "]");

            // create a memo table
            println("creating table [" + TABLE_MEMO + "]");
            // drop existing table
            String DROP_SQL = "drop table if exists " + TABLE_MEMO;
            execSQL(db, DROP_SQL);
            String CREATE_SQL = "create table " + TABLE_MEMO + "("
                    + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + "INPUT_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "FOOD_NAME TEXT DEFAULT '', "
                    + "ID_RES  INTEGER, "
                    + "FOOD_DURATION INTEGER, "
                    + "CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
                    +")";
            execSQL(db, CREATE_SQL);
        }

        public void onOpen(SQLiteDatabase db) {
            println("open database [" + BasicInfo.DATABASE_NAME + "]");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            println("Upgrading database from version" + oldVersion + "to" + newVersion + ".");
        }

        // SQL 쿼리 실행
        private void execSQL(SQLiteDatabase db, String SQL) {
            try {
                db.execSQL(SQL);
            }catch (Exception e) {
                Log.e(TAG, "Exception in " + SQL, e);
            }
        }


    }

    private void println(String msg) {
        Log.d(TAG, msg);
    }



}
