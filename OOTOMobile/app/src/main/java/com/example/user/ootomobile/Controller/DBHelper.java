package com.example.user.ootomobile.Controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
