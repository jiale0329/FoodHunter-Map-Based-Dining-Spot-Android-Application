package com.example.testmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class DebtRecordByIndividual extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    RecyclerView mRvDebtRecord;
    MealMateDebtRecordAdapter adapter;
    private List<MealMate> mMealMate = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt_record_by_individual);

        drawerLayout = findViewById(R.id.debtRecordByIndividual_drawer_layout);
        navigationView = findViewById(R.id.debtRecordByIndividual_nav_view);
        toolbar = findViewById(R.id.debtRecordByIndividual_toolbar);
        mRvDebtRecord = findViewById(R.id.rvIndividualDebtRecord);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        navigationView.setCheckedItem(R.id.nav_individual_debt_record);

        MealMateLab mealMateLab = MealMateLab.get(DebtRecordByIndividual.this);
        mMealMate = mealMateLab.getMealMates();

        ProgressDialog dialog = ProgressDialog.show(DebtRecordByIndividual.this, "",
                "Loading......", true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mRvDebtRecord.setLayoutManager(new LinearLayoutManager(DebtRecordByIndividual.this));
                adapter = new MealMateDebtRecordAdapter(mMealMate, DebtRecordByIndividual.this);
                mRvDebtRecord.setAdapter(adapter);
                dialog.dismiss();
            }
        }, 3000);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_map:
                Intent intentMap = new Intent(DebtRecordByIndividual.this, MapsActivity.class);
                startActivity(intentMap);
                finish();
                break;
            case R.id.nav_bill:
                Intent intentBill = new Intent(DebtRecordByIndividual.this, BillActivity.class);
                startActivity(intentBill);
                finish();
                break;
            case R.id.nav_individual_debt_record:
                break;
            case R.id.nav_logout:
                SharedPreferences myPreferences = getSharedPreferences("myPreferences", MODE_PRIVATE);
                SharedPreferences.Editor spEditor = UserLogin.mPreferences.edit();
                spEditor.clear();
                spEditor.apply();
                Intent q = new Intent(DebtRecordByIndividual.this, UserLogin.class);
                startActivity(q);
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}