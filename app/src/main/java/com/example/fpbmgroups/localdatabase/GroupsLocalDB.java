package com.example.fpbmgroups.localdatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class GroupsLocalDB extends SQLiteOpenHelper {

    public static final int DB_VERSION = 2;
    public static String DB_NAME = "UserData.db";

    public GroupsLocalDB(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE user(username TEXT UNIQUE, userpassword TEXT) ";
        sqLiteDatabase.execSQL(query);

        query = "CREATE TABLE app_item(name TEXT, value TEXT) ";
        sqLiteDatabase.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i >= i1)
            return;
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS user");
        onCreate(sqLiteDatabase);
    }

    public boolean editItem(String name, String newValue)
    {
        SQLiteDatabase db =getWritableDatabase();
        boolean test = true;
        try
        {
            db.execSQL("UPDATE user_item SET value=\"" + newValue + "\" WHERE name=\"" + name + "\"");

        }
        catch (Exception e)
        {
            test = false;
        }

        return test;

    }

    public boolean addNewItem(String name, String value)
    {
        SQLiteDatabase db =getWritableDatabase();
        boolean test = true;
        if(existanceOfItem(name))
        {
            return false;
        }
        try
        {
            db.execSQL("INSERT INTO user_item VALUES(\"" + name + "\",  value=\"" + value + "\"");

        }
        catch (Exception e)
        {
            test = false;
        }

        return test;
    }

    public boolean existanceOfItem(String name)
    {
        SQLiteDatabase db =getReadableDatabase();
        boolean test = true;
        try
        {
            Cursor cursor = db.rawQuery("SELECT * FROM user_item WHERE name=\"" + name + "\"", null);
            cursor.moveToFirst();
            String result = cursor.getString(1);
            if (result.isEmpty())
            {
                test = false;
            }
        }
        catch (Exception e)
        {
            test = false;
        }

        return test;
    }

    public String getItem(String name)
    {
        SQLiteDatabase db =getReadableDatabase();
        String result = null;
        if(!existanceOfItem(name))
        {
            return null;
        }
        try
        {
            Cursor cursor = db.rawQuery("SELECT value FROM user_item WHERE name=\"" + name + "\"" , null);
            cursor.moveToFirst();
            result = cursor.getString(0);
        }
        catch (Exception e)
        {
            return null;
        }

        return result;
    }
}
