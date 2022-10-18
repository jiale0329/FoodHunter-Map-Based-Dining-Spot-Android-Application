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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class ViewRatedRestaurant extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    RecyclerView mRvRatedRestaurant;
    RatedDiningSpotAdapter adapter;
    String userId;
    UserRating mOwnerRating;
    private List<DiningSpot> mDiningSpot = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static SharedPreferences mPreferences;
    private final String SHARED_PREF = "myPreferences";
    private final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rated_restaurant);

        mPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        userId = mPreferences.getString(KEY_USER_ID, "");

        drawerLayout = findViewById(R.id.viewRatedRestaurant_drawer_layout);
        navigationView = findViewById(R.id.viewRatedRestaurant_nav_view);
        toolbar = findViewById(R.id.viewRatedRestaurant_toolbar);
        mRvRatedRestaurant = findViewById(R.id.rvRatedRestaurant);

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
        navigationView.setCheckedItem(R.id.nav_restaurant_rated);

        mOwnerRating = new UserRating(userId);
        mOwnerRating.initiateRestaurantRatingGiven();

        DiningSpotLab diningSpotLab = DiningSpotLab.get(ViewRatedRestaurant.this);
        mDiningSpot = diningSpotLab.getDiningSpots();

        ProgressDialog dialog = ProgressDialog.show(ViewRatedRestaurant.this, "",
                "Loading......", true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mRvRatedRestaurant.setLayoutManager(new LinearLayoutManager(ViewRatedRestaurant.this));
                adapter = new RatedDiningSpotAdapter(mOwnerRating.getmRestaurantRatingGiven(),mDiningSpot, ViewRatedRestaurant.this);
                mRvRatedRestaurant.setAdapter(adapter);
                dialog.dismiss();
            }
        }, 3000);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_map:
                Intent intentMap = new Intent(ViewRatedRestaurant.this, MapsActivity.class);
                startActivity(intentMap);
                finish();
                break;
            case R.id.nav_restaurant_rated:
                break;
            case R.id.nav_bill:
                Intent intentBill = new Intent(ViewRatedRestaurant.this, BillActivity.class);
                startActivity(intentBill);
                finish();
                break;
            case R.id.nav_individual_debt_record:
                Intent intentDebt = new Intent(ViewRatedRestaurant.this, DebtRecordByIndividual.class);
                startActivity(intentDebt);
                finish();
                break;
            case R.id.nav_recommend_dining_spot:
                Intent intentRecommend = new Intent(ViewRatedRestaurant.this, RecommendDiningSpot.class);
                startActivity(intentRecommend);
                finish();
                break;
            case R.id.nav_logout:
                SharedPreferences myPreferences = getSharedPreferences("myPreferences", MODE_PRIVATE);
                SharedPreferences.Editor spEditor = UserLogin.mPreferences.edit();
                spEditor.clear();
                spEditor.apply();
                Intent q = new Intent(ViewRatedRestaurant.this, UserLogin.class);
                startActivity(q);
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}