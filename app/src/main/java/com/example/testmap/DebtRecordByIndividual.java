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
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DebtRecordByIndividual extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    String userId;
    RecyclerView mRvDebtRecord;
    MealMateDebtRecordAdapter adapter;
    private List<MealMate> mMealMate = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static SharedPreferences mPreferences;
    private final String SHARED_PREF = "myPreferences";
    private final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt_record_by_individual);

        mPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        userId = mPreferences.getString(KEY_USER_ID, "");

        drawerLayout = findViewById(R.id.debtRecordByIndividual_drawer_layout);
        navigationView = findViewById(R.id.debtRecordByIndividual_nav_view);
        toolbar = findViewById(R.id.debtRecordByIndividual_toolbar);
        mRvDebtRecord = findViewById(R.id.rvIndividualDebtRecord);

        setSupportActionBar(toolbar);

        View headerView = navigationView.getHeaderView(0);
        TextView mTvHeaderProfileName = (TextView) headerView.findViewById(R.id.tvHeaderProfileName);

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();

                            if(!result.isEmpty()){
                                for (QueryDocumentSnapshot document : result) {

                                    if (document.getId().equals(userId)){
                                        mTvHeaderProfileName.setText(document.get("fullName").toString());
                                    }
                                }
                            }
                        }
                    }
                });

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        navigationView.setCheckedItem(R.id.nav_individual_debt_record);

        MealMateLab mealMateLab = MealMateLab.get(DebtRecordByIndividual.this, userId);
        mMealMate = mealMateLab.getMealMates();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        ProgressDialog dialog = ProgressDialog.show(DebtRecordByIndividual.this, "",
                "Loading......", true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mRvDebtRecord.setLayoutManager(new LinearLayoutManager(DebtRecordByIndividual.this));
                adapter = new MealMateDebtRecordAdapter(mMealMate, DebtRecordByIndividual.this, width, height);
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
            case R.id.nav_restaurant_rated:
                Intent intentRestaurantRated = new Intent(DebtRecordByIndividual.this, ViewRatedRestaurant.class);
                startActivity(intentRestaurantRated);
                finish();
                break;
            case R.id.nav_bill:
                Intent intentBill = new Intent(DebtRecordByIndividual.this, BillActivity.class);
                startActivity(intentBill);
                finish();
                break;
            case R.id.nav_individual_debt_record:
                break;
            case R.id.nav_recommend_dining_spot:
                Intent intentRecommend = new Intent(DebtRecordByIndividual.this, RecommendDiningSpot.class);
                startActivity(intentRecommend);
                finish();
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