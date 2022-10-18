package com.example.testmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewRestaurantProfile extends AppCompatActivity {

    ImageView mIvRestaurantPicture;
    TextView mTvRestaurantName, mTvRestaurantAddress, mTvRestaurantRating, mTvRatingSize;
    String imageId, userId, restaurantId;
    StorageReference storageReference;
    Button mBtnRateRestaurantPopUp;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    private List<DiningSpot> mDiningSpot = new ArrayList<>();
    LinearProgressIndicator mLpiFiveStar, mLpiFourStar, mLpiThreeStar, mLpiTwoStar, mLpiOneStar;
    int fiveStar = 0, fourStar = 0, threeStar = 0, twoStar = 0, oneStar = 0;
    RatingBar mRbRating;
    Boolean rated = false;

    public static final String EXTRA_RESTAURANT_ID = "restaurant_id";
    public static SharedPreferences mPreferences;
    private final String SHARED_PREF = "myPreferences";
    private final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_restaurant_profile);

        DiningSpotLab diningSpotLab = DiningSpotLab.get(ViewRestaurantProfile.this);
        mDiningSpot = diningSpotLab.getDiningSpots();

        mIvRestaurantPicture = findViewById(R.id.ivRestaurantPicture);
        mTvRestaurantName = findViewById(R.id.tvRestaurantNameContent);
        mTvRestaurantAddress = findViewById(R.id.tvAddressContent);
        mBtnRateRestaurantPopUp = findViewById(R.id.btnRateRestaurantPopUp);
        mTvRestaurantRating = findViewById(R.id.tvRestaurantRatingContent);
        mLpiFiveStar = findViewById(R.id.lpiFiveStar);
        mLpiFourStar = findViewById(R.id.lpiFourStar);
        mLpiThreeStar = findViewById(R.id.lpiThreeStar);
        mLpiTwoStar = findViewById(R.id.lpiTwoStar);
        mLpiOneStar = findViewById(R.id.lpiOneStar);
        mRbRating = findViewById(R.id.rbRating);
        mTvRatingSize = findViewById(R.id.tvRatingSize);

        mPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        userId = mPreferences.getString(KEY_USER_ID, "");

        Bundle bundle = getIntent().getExtras();
        restaurantId = bundle.getString(EXTRA_RESTAURANT_ID);

        ProgressDialog dialog = ProgressDialog.show(ViewRestaurantProfile.this, "",
                "Loading......", true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                for (DiningSpot diningSpot : mDiningSpot){
                    if (restaurantId.equals(diningSpot.getmId())){
                        mTvRestaurantName.setText(diningSpot.getmName());
                        mTvRestaurantAddress.setText(diningSpot.getmAddress());

                        int scale = (int) Math.pow(10, 1);
                        double rating = (double) Math.round(diningSpot.getmRating() * scale) / scale;

                        mTvRestaurantRating.setText("" + rating);
                        mRbRating.setRating((float)diningSpot.getmRating());
                        imageId = diningSpot.getmPictureUrl();

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
                                            mIvRestaurantPicture.setImageBitmap(bitmap);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ViewRestaurantProfile.this, "FUCKING FAIL TO RETRIEVE", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        for (RestaurantRating restaurantRating : diningSpot.getmRestaurantRating()){
                            switch (restaurantRating.getRating()){
                                case 1:
                                    oneStar++;
                                    break;
                                case 2:
                                    twoStar++;
                                    break;
                                case 3:
                                    threeStar++;
                                    break;
                                case 4:
                                    fourStar++;
                                    break;
                                case 5:
                                    fiveStar++;
                                    break;
                            }

                            if (restaurantRating.getDiningSpotId().equals(restaurantId) && restaurantRating.getUserId().equals(userId)){
                                rated = true;
                            }
                        }

                        double size = (double) diningSpot.getmRestaurantRating().size();

                        mTvRatingSize.setText("" + (int)size);

                        double calculate;
                        int rounded;

                        calculate = (fiveStar/size)*100;
                        rounded = (int) calculate;
                        mLpiFiveStar.setProgressCompat(rounded, true);

                        calculate = (fourStar/size)*100;
                        rounded = (int) calculate;
                        mLpiFourStar.setProgressCompat(rounded, true);

                        calculate = (threeStar/size)*100;
                        rounded = (int) calculate;
                        mLpiThreeStar.setProgressCompat(rounded, true);

                        calculate = (twoStar/size)*100;
                        rounded = (int) calculate;
                        mLpiTwoStar.setProgressCompat(rounded, true);

                        calculate = (oneStar/size)*100;
                        rounded = (int) calculate;
                        mLpiOneStar.setProgressCompat(rounded, true);

                        if (rated){
                            mBtnRateRestaurantPopUp.setEnabled(false);
                            mBtnRateRestaurantPopUp.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                dialog.dismiss();
            }
        }, 5000);

        mBtnRateRestaurantPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ViewRestaurantProfile.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.setContentView(R.layout.rate_restaurant_popup_layout);

                dialog.show();

                RatingBar mRatingBarRestaurantRating = dialog.findViewById(R.id.ratingBarRestaurantRating);
                Button mBtnSubmitRestaurantRating = dialog.findViewById(R.id.btnSubmitRestaurantRating);

                mBtnSubmitRestaurantRating.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("userId", userId);
                        user.put("diningSpotId", restaurantId);
                        user.put("rating", (int)mRatingBarRestaurantRating.getRating());

                        database.collection("restaurantRating")
                                .add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("TAG", "Error adding document", e);
                                    }
                                });
                        dialog.dismiss();
                    }
                });
            }
        });
    }
}