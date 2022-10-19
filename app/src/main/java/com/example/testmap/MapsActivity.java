package com.example.testmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.location.Location.distanceBetween;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    boolean isPermissionGranted;
    SupportMapFragment mapFragment;
    FloatingActionButton fab;
    ExtendedFloatingActionButton mFabSpinningWheel, mFabSearchWithPicture;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    private FusedLocationProviderClient mClient;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<DiningSpot> mDiningSpot = new ArrayList<>();
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    StorageReference storageReference;
    String imageId;
    ImageView mIvPopupPicture;
    SqliteHelper sql;
    String userId;
    Boolean isOpen = false;

    public static final String EXTRA_RESTAURANT_ID = "restaurant_id";
    public static SharedPreferences mPreferences;
    private final String SHARED_PREF = "myPreferences";
    private final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        DiningSpotLab diningSpotLab = DiningSpotLab.get(MapsActivity.this);
        mDiningSpot = diningSpotLab.getDiningSpots();

        mPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        userId = mPreferences.getString(KEY_USER_ID, "");

        drawerLayout = findViewById(R.id.map_activity_drawer_layout);
        navigationView = findViewById(R.id.map_activity_nav_view);
        toolbar = findViewById(R.id.map_activity_toolbar);

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


        sql = new SqliteHelper(MapsActivity.this);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_map);

        fab = findViewById(R.id.fab);
        mFabSpinningWheel = findViewById(R.id.fabSpinningWheel);
        mFabSearchWithPicture = findViewById(R.id.fabSearchWithPicture);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        checkMyPermission();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        initMap();

        mClient = new FusedLocationProviderClient(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });

        mFabSpinningWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Intent intent = new Intent(MapsActivity.this, PreviewSpinningWheelChoice.class);
                startActivity(intent);
            }
        });

        mFabSearchWithPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Intent intent = new Intent(MapsActivity.this, ChoosePicture.class);
                startActivity(intent);
            }
        });
    }

    private void animateFab() {
        if (isOpen){
            fab.startAnimation(rotateForward);
            mFabSpinningWheel.startAnimation(fabClose);
            mFabSearchWithPicture.startAnimation(fabClose);
            mFabSpinningWheel.setClickable(false);
            mFabSearchWithPicture.setClickable(false);
            isOpen = false;
        }
        else{
            fab.startAnimation(rotateBackward);
            mFabSpinningWheel.startAnimation(fabOpen);
            mFabSearchWithPicture.startAnimation(fabOpen);
            mFabSpinningWheel.setClickable(true);
            mFabSearchWithPicture.setClickable(true);
            isOpen = true;
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    private void initMap() {
        if (isPermissionGranted) {
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_map);
            mapFragment.getMapAsync(this);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrLoc() {
        mClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Location location = task.getResult();
                goToLocation(location.getLatitude(), location.getLongitude());
            }
        });
    }

    private void goToLocation(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
        mMap.moveCamera(cameraUpdate);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    private void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(MapsActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Go to current location
        mMap.setMyLocationEnabled(true);

        getCurrLoc();

        ProgressDialog dialog = ProgressDialog.show(MapsActivity.this, "",
                "Loading......", true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                for (DiningSpot diningSpot : mDiningSpot)
                {
                    LatLng newLatLng = new LatLng(Double.parseDouble(diningSpot.getmLatitude()), Double.parseDouble(diningSpot.getmLongitude()));
                    mMap.addMarker(new MarkerOptions().position(newLatLng).title(diningSpot.getmId()));
                }
                dialog.dismiss();
            }
        }, 3000);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.popup_layout, null);

                // create the popup window
//                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
//                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
//                boolean focusable = true; // lets taps outside the popup also dismiss it
//                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
//
//                // show the popup window
//                // which view you pass in doesn't matter, it is only used for the window token
//                popupWindow.showAtLocation(popupView, Gravity.CLIP_HORIZONTAL, 0, 200);

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                PopupWindow popupWindow = new PopupWindow(popupView);
                popupWindow.setWidth(width-40);
                popupWindow.setHeight(height/2-10);
                popupWindow.setFocusable(true);

                popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 150);


                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        popupWindow.dismiss();
                        return true;
                    }

                });

                TextView mtvPopupTitle = (TextView) popupView.findViewById(R.id.tvPopupTitle);
                TextView mtvPopupAddress = (TextView) popupView.findViewById(R.id.tvPopUpAddress);
                mIvPopupPicture = (ImageView) popupView.findViewById(R.id.ivPopupPicture);
                Button mBtnPopUpAddToSpinningWheel = popupView.findViewById(R.id.btnPopUpAddToSpinningWheel);
                Button mBtnPopUpViewRestaurantProfile = popupView.findViewById(R.id.btnPopUpViewDetails);
                TextView mTvDistance = (TextView) popupView.findViewById(R.id.tvDistance);
                float[] results = {};

                for (DiningSpot diningSpot : mDiningSpot)
                {
                    if (diningSpot.getmId().equals(marker.getTitle()))
                    {
                        mtvPopupTitle.setText(diningSpot.getmName());
                        mtvPopupAddress.setText("Address: " + diningSpot.getmAddress());
                        imageId = diningSpot.getmPictureUrl();

                        mClient.getLastLocation().addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                Location location = task.getResult();
                                Location endPoint=new Location("locationA");
                                endPoint.setLatitude(Double.parseDouble(diningSpot.getmLatitude()));
                                endPoint.setLongitude(Double.parseDouble(diningSpot.getmLongitude()));
                                float distanceCalculated = location.distanceTo(endPoint)/1000;

                                int scale = (int) Math.pow(10, 1);
                                double distance = (double) Math.round(distanceCalculated * scale) / scale;

                                mTvDistance.setText("" + distance + " KM");
                            }
                        });

                    }
                }

                storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference ref
                        = storageReference
                        .child(
                                "images/"
                                        + imageId);
                try {
                    File localfile = File.createTempFile("tempfile", ".jpg");
                    ref.getFile(localfile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                    mIvPopupPicture.setImageBitmap(bitmap);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MapsActivity.this, "FUCKING FAIL TO RETRIEVE", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
                mMap.moveCamera(cameraUpdate);

                mBtnPopUpAddToSpinningWheel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (DiningSpot diningSpot : mDiningSpot)
                        {
                            if (diningSpot.getmId().equals(marker.getTitle()))
                            {
                                sql.insertFoodChoice(MapsActivity.this, diningSpot.getmId(), diningSpot.getmName());
                            }
                        }
                    }
                });

                mBtnPopUpViewRestaurantProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MapsActivity.this, ViewRestaurantProfile.class);
                        i.putExtra(EXTRA_RESTAURANT_ID, marker.getTitle());
                        startActivity(i);
                    }
                });

                return true;
            }
        });
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_map:
                break;
            case R.id.nav_restaurant_rated:
                Intent intentRestaurantRated = new Intent(MapsActivity.this, ViewRatedRestaurant.class);
                startActivity(intentRestaurantRated);
                finish();
                break;
            case R.id.nav_bill:
                Intent intentBill = new Intent(MapsActivity.this, BillActivity.class);
                startActivity(intentBill);
                finish();
                break;
            case R.id.nav_individual_debt_record:
                Intent intentDebt = new Intent(MapsActivity.this, DebtRecordByIndividual.class);
                startActivity(intentDebt);
                finish();
                break;
            case R.id.nav_recommend_dining_spot:
                Intent intentRecommend = new Intent(MapsActivity.this, RecommendDiningSpot.class);
                startActivity(intentRecommend);
                finish();
                break;
            case R.id.nav_logout:
                SharedPreferences myPreferences = getSharedPreferences("myPreferences", MODE_PRIVATE);
                SharedPreferences.Editor spEditor = UserLogin.mPreferences.edit();
                spEditor.clear();
                spEditor.apply();
                Intent q = new Intent(MapsActivity.this, UserLogin.class);
                startActivity(q);
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}