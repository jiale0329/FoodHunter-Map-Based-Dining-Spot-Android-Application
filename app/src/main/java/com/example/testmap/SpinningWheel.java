package com.example.testmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
import com.bluehomestudio.luckywheel.WheelItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.sql.SQLPermission;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpinningWheel extends AppCompatActivity {

    LuckyWheel mLwFoodChoice;
    private List<WheelItem> wheelItemList = new ArrayList<>();
    int points;
    Button mBtnSpinLuckyWheel;
    ArrayList<DiningChoice> mDiningChoice = new ArrayList<>();
    private List<DiningSpot> mDiningSpot = new ArrayList<>();
    Toolbar toolbar;
    StorageReference storageReference;
    private FusedLocationProviderClient mClient;
    String imageId;
    public static final String EXTRA_RESTAURANT_ID = "restaurant_id";
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinning_wheel);

        mLwFoodChoice = findViewById(R.id.lwFoodChoice);
        mBtnSpinLuckyWheel = findViewById(R.id.btnSpinLuckyWheel);

        mClient = new FusedLocationProviderClient(this);

        DiningSpotLab diningSpotLab = DiningSpotLab.get(SpinningWheel.this);
        mDiningSpot = diningSpotLab.getDiningSpots();

        toolbar = findViewById(R.id.spinningWheel_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDiningChoice = (ArrayList<DiningChoice>) getIntent().getSerializableExtra("DiningChoice");

        for (DiningChoice diningChoice : mDiningChoice){
            if (count % 3 == 0){
                WheelItem wheelItem1 = new WheelItem(ResourcesCompat.getColor(getResources(),
                        R.color.shimmeringBlush, null), BitmapFactory.decodeResource(getResources(), R.drawable.empty_icon),
                        diningChoice.getmId());
                wheelItemList.add(wheelItem1);
            }else if (count % 3 == 1){
                WheelItem wheelItem2 = new WheelItem(ResourcesCompat.getColor(getResources(),
                        R.color.turquoiseGreen, null), BitmapFactory.decodeResource(getResources(), R.drawable.empty_icon),
                        diningChoice.getmId());
                wheelItemList.add(wheelItem2);
            }else if (count % 3 == 2){
                WheelItem wheelItem3 = new WheelItem(ResourcesCompat.getColor(getResources(),
                        R.color.lemonMeringue, null), BitmapFactory.decodeResource(getResources(), R.drawable.empty_icon),
                        diningChoice.getmId());
                wheelItemList.add(wheelItem3);
            }
            count++;
        }

        mLwFoodChoice.addWheelItems(wheelItemList);

        mLwFoodChoice.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
            @SuppressLint("MissingPermission")
            @Override
            public void onReachTarget() {
                WheelItem itemSelected = wheelItemList.get(points - 1);
                String selectedItemTitle = itemSelected.text;

                ProgressDialog dialog3 = ProgressDialog.show(SpinningWheel.this, "",
                        "Loading. Please wait...", true);   //show loading dialog
                Handler handler3 = new Handler();
                handler3.postDelayed(new Runnable() {
                    public void run() {
                        dialog3.dismiss();   //remove loading Dialog
                        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = layoutInflater.inflate(R.layout.spinning_wheel_result_popup_layout, null);

                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int width = size.x;
                        int height = size.y;

                        PopupWindow popupWindow = new PopupWindow(popupView);
                        popupWindow.setWidth(width-40);
                        popupWindow.setHeight(height);
                        popupWindow.setFocusable(true);

                        popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 150);

                        TextView mtvPopupTitle = (TextView) popupView.findViewById(R.id.tvPopupTitle_spinningWheelPopup);
                        TextView mtvPopupAddress = (TextView) popupView.findViewById(R.id.tvPopUpAddress_spinningWheelPopup);
                        ImageView mIvPopupPicture = (ImageView) popupView.findViewById(R.id.ivPopupPicture_spinningWheelPopup);
                        Button mBtnPopUpViewRestaurantProfile = popupView.findViewById(R.id.btnPopUpViewDetails_spinningWheelPopup);
                        TextView mTvDistance = (TextView) popupView.findViewById(R.id.tvDistance_spinningWheelPopup);

                        for (DiningSpot diningSpot : mDiningSpot)
                        {
                            if (diningSpot.getmId().equals(selectedItemTitle))
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
                                    Toast.makeText(SpinningWheel.this, "FUCKING FAIL TO RETRIEVE", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        mBtnPopUpViewRestaurantProfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(SpinningWheel.this, ViewRestaurantProfile.class);
                                i.putExtra(EXTRA_RESTAURANT_ID, selectedItemTitle);
                                startActivity(i);
                            }
                        });

                    }
                }, 2000);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}