package com.example.testmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button mBtnSaveChanges;
    String userId, userEmail, currentPassword, newPassword, confirmPassword;
    TextInputLayout mEtCurrentPassword, mEtNewPassword, mEtConfirmPassword;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private final String KEY_USER_EMAIL = "userEmail";
    public static SharedPreferences mPreferences;
    private final String SHARED_PREF = "myPreferences";
    private final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        userId = mPreferences.getString(KEY_USER_ID, "");
        userEmail = mPreferences.getString(KEY_USER_EMAIL, "");

        drawerLayout = findViewById(R.id.change_password_drawer_layout);
        navigationView = findViewById(R.id.change_password_nav_view);
        toolbar = findViewById(R.id.change_password_toolbar);
        mEtCurrentPassword = findViewById(R.id.etCurrentPassword);
        mEtNewPassword = findViewById(R.id.etNewPassword);
        mEtConfirmPassword = findViewById(R.id.etConfirmPassword);
        mBtnSaveChanges = findViewById(R.id.btnSaveChanges);

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

        navigationView.setCheckedItem(R.id.nav_change_password);

        mBtnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPassword = mEtCurrentPassword.getEditText().getText().toString().trim();
                newPassword = mEtNewPassword.getEditText().getText().toString().trim();
                confirmPassword = mEtConfirmPassword.getEditText().getText().toString().trim();

                if(TextUtils.isEmpty(currentPassword)){

                    Toast.makeText(ChangePassword.this, "Please input current password!", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "onCreate: " + userEmail);

                }else if((TextUtils.isEmpty(newPassword) || newPassword.length()<6)){
                    Toast.makeText(ChangePassword.this, "Password must be more than 6 characters!", Toast.LENGTH_SHORT).show();

                }else if(!newPassword.equals(confirmPassword)) {

                    Toast.makeText(ChangePassword.this, "Please input the new password correctly", Toast.LENGTH_SHORT).show();
                    mEtConfirmPassword.requestFocus();

                }else{
                    FirebaseUser user = mAuth.getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(userEmail, currentPassword);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                            Toast.makeText(ChangePassword.this, "Password" +
                                                    " updated successfully!", Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(ChangePassword.this, "Failed to update password!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChangePassword.this, "Failed to authenticate user " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_map:
                Intent intentMap = new Intent(ChangePassword.this, MapsActivity.class);
                startActivity(intentMap);
                finish();
                break;
            case R.id.nav_restaurant_rated:
                Intent intentRestaurantRated = new Intent(ChangePassword.this, ViewRatedRestaurant.class);
                startActivity(intentRestaurantRated);
                finish();
                break;
            case R.id.nav_bill:
                Intent intentBill = new Intent(ChangePassword.this, BillActivity.class);
                startActivity(intentBill);
                finish();
                break;
            case R.id.nav_individual_debt_record:
                Intent intentDebt = new Intent(ChangePassword.this, DebtRecordByIndividual.class);
                startActivity(intentDebt);
                finish();
                break;
            case R.id.nav_recommend_dining_spot:
                Intent intentRecommend = new Intent(ChangePassword.this, RecommendDiningSpot.class);
                startActivity(intentRecommend);
                finish();
                break;
            case R.id.nav_change_password:
                break;
            case R.id.nav_logout:
                SharedPreferences myPreferences = getSharedPreferences("myPreferences", MODE_PRIVATE);
                SharedPreferences.Editor spEditor = UserLogin.mPreferences.edit();
                spEditor.clear();
                spEditor.apply();
                Intent q = new Intent(ChangePassword.this, UserLogin.class);
                startActivity(q);
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}