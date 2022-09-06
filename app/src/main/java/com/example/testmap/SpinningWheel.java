package com.example.testmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
import com.bluehomestudio.luckywheel.WheelItem;

import java.sql.SQLPermission;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpinningWheel extends AppCompatActivity {

    LuckyWheel mLwFoodChoice;
    private List<WheelItem> wheelItemList = new ArrayList<>();
    int points;
    Button mBtnSpinLuckyWheel;
    SqliteHelper sql;
    ArrayList<DiningChoice> mDiningChoice = new ArrayList<>();
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinning_wheel);

        mLwFoodChoice = findViewById(R.id.lwFoodChoice);
        mBtnSpinLuckyWheel = findViewById(R.id.btnSpinLuckyWheel);

        sql = new SqliteHelper(SpinningWheel.this);

        mDiningChoice = sql.readDiningChoice();

        for (DiningChoice diningChoice : mDiningChoice){
            if (count % 3 == 0){
                WheelItem wheelItem1 = new WheelItem(ResourcesCompat.getColor(getResources(),
                        R.color.shimmeringBlush, null), BitmapFactory.decodeResource(getResources(), R.drawable.empty_icon),
                        diningChoice.getmName());
                wheelItemList.add(wheelItem1);
            }else if (count % 3 == 1){
                WheelItem wheelItem2 = new WheelItem(ResourcesCompat.getColor(getResources(),
                        R.color.turquoiseGreen, null), BitmapFactory.decodeResource(getResources(), R.drawable.empty_icon),
                        diningChoice.getmName());
                wheelItemList.add(wheelItem2);
            }else if (count % 3 == 2){
                WheelItem wheelItem3 = new WheelItem(ResourcesCompat.getColor(getResources(),
                        R.color.lemonMeringue, null), BitmapFactory.decodeResource(getResources(), R.drawable.empty_icon),
                        diningChoice.getmName());
                wheelItemList.add(wheelItem3);
            }
            count++;
        }

        mLwFoodChoice.addWheelItems(wheelItemList);

        mLwFoodChoice.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
            @Override
            public void onReachTarget() {
                WheelItem itemSelected = wheelItemList.get(points - 1);
                String selectedItemTitle = itemSelected.text;

                Toast.makeText(SpinningWheel.this, selectedItemTitle, Toast.LENGTH_SHORT).show();
            }
        });

        mBtnSpinLuckyWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                points = random.nextInt(wheelItemList.size()) + 1;

                mLwFoodChoice.rotateWheelTo(points);
            }
        });
    }
}