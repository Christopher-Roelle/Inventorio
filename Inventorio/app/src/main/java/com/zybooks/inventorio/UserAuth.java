package com.zybooks.inventorio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserAuth extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Users.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "users";
    private static final String COL_ID = "userID";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    public UserAuth(Context context)
    {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL " +
                ")";

        db.execSQL(sql);
    }

    //TODO: Add logic to backup old table to transfer to new Version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Adds the user to the DB. Returns false if error or user exists already.
    //TODO: Handle errors with numerical code to differentiate reason for failure.
    public boolean AddUser(String username, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //Check if the user already exists
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_USERNAME + " = ?", new String[]{username});
        if(cursor.getCount() > 0)
        {
            //User exists
            cursor.close();
            return false;
        }

        //Begin insert transaction
        ContentValues cv = new ContentValues();
        cv.put(COL_USERNAME, username);
        cv.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_NAME, null, cv);

        //Check the insert worked
        return true;
    }

    //Checks if the user and password are in the DB and returns true if valid user.
    public boolean AuthenticateUser(String username, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_USERNAME + " = ? AND " +
                COL_PASSWORD + " = ?";

        //Check if the user exists?
        Cursor cursor = db.rawQuery(sql, new String[]{username, password});
        boolean result = cursor.getCount() > 0;

        cursor.close();
        return result;
    }
}
