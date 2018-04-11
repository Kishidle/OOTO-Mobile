package com.example.user.otherproject.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.user.otherproject.Model.Child;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ramon on 2/11/2018.
 */

public class DBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;



    public DBHelper(Context context){
        super(context, "test", null, DATABASE_VERSION);
    }

    //creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {

    }
    //upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //drop table if it exists

        //create table again
        onCreate(db);
    }


    public SQLiteDatabase getReadableDatabase(){
        return this.getReadableDatabase();
    }


}
