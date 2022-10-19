package com.example.testmap;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DiningSpotLab {
    private static DiningSpotLab sDiningSpotLab;
    private List<DiningSpot> mDiningSpot;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static DiningSpotLab get(Context context){

        sDiningSpotLab = new DiningSpotLab(context);
        return sDiningSpotLab;
    }

    private DiningSpotLab(Context context) {
        mDiningSpot = new ArrayList<>();
        mDiningSpot.clear();

        db.collection("diningspot")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();

                            if(!result.isEmpty()){
                                for (QueryDocumentSnapshot document : result) {
                                    DiningSpot diningSpot = new DiningSpot();
                                    diningSpot.setmId(document.getId());
                                    diningSpot.setmName(document.get("name").toString());
                                    diningSpot.setmLatitude(document.get("latitude").toString());
                                    diningSpot.setmLongitude(document.get("longitude").toString());
                                    diningSpot.setmAddress(document.get("address").toString());
                                    diningSpot.setmPictureUrl(document.get("pictureUrl").toString());
                                    diningSpot.setmTypeOfCuisine(document.get("typeOfCuisine").toString());
                                    diningSpot.setmRestaurantRating(searchRating(document.getId(), context));

                                    ProgressDialog dialog = ProgressDialog.show(context, "",
                                            "Loading......", true);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            diningSpot.calculateRating();
                                            dialog.dismiss();
                                        }
                                    }, 3000);

                                    mDiningSpot.add(diningSpot);
                                }
                            }
                        }
                    }
                });
    }

    private List<RestaurantRating> searchRating(String id, Context context) {
        List<RestaurantRating> mRestaurantRatings = new ArrayList<>();
        db.collection("restaurantRating")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();

                            if(!result.isEmpty()){
                                for (QueryDocumentSnapshot document : result) {
                                    if (document.get("diningSpotId").toString().equals(id)){
                                        double rating = document.getDouble("rating");
                                        RestaurantRating restaurantRating = new RestaurantRating(document.getId()
                                                , document.get("userId").toString()
                                                , document.get("diningSpotId").toString()
                                                , (int)rating);
                                        mRestaurantRatings.add(restaurantRating);
                                    }
                                }
                            }

                        }
                    }
                });
        return mRestaurantRatings;
    }

    public List<DiningSpot> getDiningSpots(){
        return mDiningSpot;
    }

    public DiningSpot getDiningSpot(UUID id){
        for (DiningSpot diningSpot : mDiningSpot){
            if (diningSpot.getmId().equals(id)){
                return diningSpot;
            }
        }
        return null;
    }
}
