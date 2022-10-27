package com.example.testmap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;

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

    public void insertFoodChoice(Context context, String id, String name, int width, int height){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("name", name);
        long result = db.insert("Foodchoice", null, contentValues);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.add_to_spinning_wheel_popup_layout, null);

        PopupWindow popupWindow = new PopupWindow(popupView);
        popupWindow.setWidth(width-40);
        popupWindow.setHeight(height/2-400);
        popupWindow.setFocusable(true);

        popupWindow.showAtLocation(popupView, Gravity.TOP, 0, 10);

        TextView mTvPopupNotificationContent = (TextView) popupView.findViewById(R.id.tvNotiPopupContent);
        CardView mCvSpinningWheel = (CardView) popupView.findViewById(R.id.cvSpinningWheel);

        if (result==-1){

            mTvPopupNotificationContent.setText("You have FAILED to add " + name + " into the Spinning Wheel!");

            mCvSpinningWheel.setCardBackgroundColor(Color.parseColor("#FFACA3"));
        }else{

            mTvPopupNotificationContent.setText("You have added " + name + " into Spinning Wheel SUCCESSFULLY!");
        }

        new CountDownTimer(3000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
            }
        }.start();
    }

    public ArrayList<DiningChoice> readDiningChoice(){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursorDiningChoice = db.rawQuery("SELECT * FROM Foodchoice", null);

        ArrayList<DiningChoice> diningChoiceArrayList = new ArrayList<>();

        if (cursorDiningChoice.moveToFirst()) {
            do {
                diningChoiceArrayList.add(new DiningChoice(cursorDiningChoice.getString(0),
                        cursorDiningChoice.getString(1)));
            } while (cursorDiningChoice.moveToNext());
        }

        cursorDiningChoice.close();
        return diningChoiceArrayList;
    }

    public void removeDiningChoice(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Foodchoice", "id ='" + id + "'", null);
    }
}
