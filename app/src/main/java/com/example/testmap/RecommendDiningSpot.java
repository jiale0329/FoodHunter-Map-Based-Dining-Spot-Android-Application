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

import java.util.ArrayList;
import java.util.List;

public class RecommendDiningSpot extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    String userId;
    UserRating mOwnerRating;
    private List<UserRating> mOtherUserRating = new ArrayList<>();
    List<RestaurantRating> mRestaurantRatingGiven;
    int highestCount = 0;
    private List<UserRating> highestCountUsers = new ArrayList<>();
    private List<DiningSpot> mDiningSpot = new ArrayList<>();
    private List<DiningSpot> mRecommendedDiningSpot = new ArrayList<>();
    RecyclerView mRvRecommendedRestaurant;
    RecommendedDiningSpotAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static SharedPreferences mPreferences;
    private final String SHARED_PREF = "myPreferences";
    private final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_dining_spot);

        mPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        userId = mPreferences.getString(KEY_USER_ID, "");

        drawerLayout = findViewById(R.id.recommend_restaurant_drawer_layout);
        navigationView = findViewById(R.id.recommend_restaurant_nav_view);
        toolbar = findViewById(R.id.recommend_restaurant_toolbar);
        mRvRecommendedRestaurant = findViewById(R.id.rvRecommendedRestaurant);

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

        navigationView.setCheckedItem(R.id.nav_recommend_dining_spot);

        DiningSpotLab diningSpotLab = DiningSpotLab.get(RecommendDiningSpot.this);
        mDiningSpot = diningSpotLab.getDiningSpots();

        mOwnerRating = new UserRating(userId);
        mOwnerRating.initiateRestaurantRatingGiven();

        OtherUserRatingLab otherUserRatingLab = OtherUserRatingLab.get(RecommendDiningSpot.this, userId);
        mOtherUserRating = otherUserRatingLab.getOtherUserRatings();

        ProgressDialog dialog = ProgressDialog.show(RecommendDiningSpot.this, "",
                "Loading......", true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                for (UserRating mOtherUser : mOtherUserRating){
                    int count = compareRatings(mOwnerRating, mOtherUser);

                    if (count > highestCount){
                        highestCount = count;
                        highestCountUsers.clear();
                        highestCountUsers.add(mOtherUser);
                    }
                    else if (count == highestCount){
                        highestCountUsers.add(mOtherUser);
                    }
                }
                for (UserRating highestCountUser : highestCountUsers){
                    for (RestaurantRating restaurant : highestCountUser.getmRestaurantRatingGiven()){
                        boolean exist = false;
                        for (RestaurantRating userRestaurant : mOwnerRating.getmRestaurantRatingGiven()){
                            if (restaurant.getDiningSpotId().equals(userRestaurant.getDiningSpotId())){
                                exist = true;
                            }
                        }
                        if ((exist == false) && (restaurant.getRating() > 3)){
                            for (DiningSpot diningSpot : mDiningSpot){
                                if (diningSpot.getmId().equals(restaurant.getDiningSpotId())){
                                    mRecommendedDiningSpot.add(diningSpot);
                                }
                            }
                        }
                    }
                }

                mRvRecommendedRestaurant.setLayoutManager(new LinearLayoutManager(RecommendDiningSpot.this));
                adapter = new RecommendedDiningSpotAdapter(mRecommendedDiningSpot, RecommendDiningSpot.this);
                mRvRecommendedRestaurant.setAdapter(adapter);

                dialog.dismiss();
            }
        }, 5000);
    }

    private int compareRatings(UserRating mOwner, UserRating mOtherUser) {
        int count = 0;

        for (RestaurantRating owner : mOwner.getmRestaurantRatingGiven()){
            for (RestaurantRating otherUser : mOtherUser.getmRestaurantRatingGiven()){
                if ((owner.getDiningSpotId().equals(otherUser.getDiningSpotId())) && (owner.getRating() == otherUser.getRating())){
                    count++;
                }
            }
        }

        return count;
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_map:
                Intent intentMap = new Intent(RecommendDiningSpot.this, MapsActivity.class);
                startActivity(intentMap);
                finish();
                break;
            case R.id.nav_restaurant_rated:
                Intent intentRestaurantRated = new Intent(RecommendDiningSpot.this, ViewRatedRestaurant.class);
                startActivity(intentRestaurantRated);
                finish();
                break;
            case R.id.nav_bill:
                Intent intentBill = new Intent(RecommendDiningSpot.this, BillActivity.class);
                startActivity(intentBill);
                finish();
                break;
            case R.id.nav_individual_debt_record:
                Intent intentDebt = new Intent(RecommendDiningSpot.this, DebtRecordByIndividual.class);
                startActivity(intentDebt);
                finish();
                break;
            case R.id.nav_recommend_dining_spot:
                break;
            case R.id.nav_logout:
                SharedPreferences myPreferences = getSharedPreferences("myPreferences", MODE_PRIVATE);
                SharedPreferences.Editor spEditor = UserLogin.mPreferences.edit();
                spEditor.clear();
                spEditor.apply();
                Intent q = new Intent(RecommendDiningSpot.this, UserLogin.class);
                startActivity(q);
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}