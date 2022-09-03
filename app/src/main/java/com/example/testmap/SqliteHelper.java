package com.example.testmap;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class SqliteHelper extends SQLiteOpenHelper {
    public SqliteHelper(Context context) {
        super(context, "Spinningwheel.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table Foodchoice(id TEXT primary key, name TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists Foodchoice");
    }

    public void insertFoodChoice(Context context, String id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("name", name);
        long result = db.insert("Foodchoice", null, contentValues);
        if (result==-1){
            Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
        }
    }
}
