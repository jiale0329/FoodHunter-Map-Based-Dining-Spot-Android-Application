package com.example.testmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

public class BillActivity extends AppCompatActivity {

    Button mBtnAddNewBillRecord;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    RecyclerView mRvBillRecord;
    private List<BillRecord> mBillRecord = new ArrayList<>();
    BillRecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        mRvBillRecord = findViewById(R.id.rvBillRecord);
        mBtnAddNewBillRecord = findViewById(R.id.btnAddNewBillRecord);
        drawerLayout = findViewById(R.id.bill_activity_drawer_layout);
        navigationView = findViewById(R.id.bill_activity_nav_view);
        toolbar = findViewById(R.id.bill_activity_toolbar);

        BillRecordLab billRecordLab = BillRecordLab.get(BillActivity.this);
        mBillRecord = billRecordLab.getBillRecords();

        ProgressDialog dialog = ProgressDialog.show(BillActivity.this, "",
                "Loading......", true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mRvBillRecord.setLayoutManager(new LinearLayoutManager(BillActivity.this));
                adapter = new BillRecordAdapter(mBillRecord, BillActivity.this);
                mRvBillRecord.setAdapter(adapter);
                dialog.dismiss();
            }
        }, 3000);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        navigationView.setCheckedItem(R.id.nav_bill);

        checkMyPermission();

        mBtnAddNewBillRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BillActivity.this, AddNewBillForm.class);
                startActivity(intent);
            }
        });

    }

    private void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_CONTACTS).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(BillActivity.this, "Contact Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(BillActivity.this, "Contact Permission Denied", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_map:
                Intent intentMap = new Intent(BillActivity.this, MapsActivity.class);
                startActivity(intentMap);
                finish();
                break;
            case R.id.nav_bill:
                break;
            case R.id.nav_individual_debt_record:
                Intent intentDebt = new Intent(BillActivity.this, DebtRecordByIndividual.class);
                startActivity(intentDebt);
                finish();
                break;
            case R.id.nav_logout:
                SharedPreferences myPreferences = getSharedPreferences("myPreferences", MODE_PRIVATE);
                SharedPreferences.Editor spEditor = UserLogin.mPreferences.edit();
                spEditor.clear();
                spEditor.apply();
                Intent q = new Intent(BillActivity.this, UserLogin.class);
                startActivity(q);
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

}