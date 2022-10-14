package com.example.testmap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserRating {
    private String userId;
    private List<RestaurantRating> mRestaurantRatingGiven;
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    public UserRating(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<RestaurantRating> getmRestaurantRatingGiven() {
        return mRestaurantRatingGiven;
    }

    public void initiateRestaurantRatingGiven() {
        List<RestaurantRating> mRating = new ArrayList<>();;

        database.collection("restaurantRating")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();

                            if(!result.isEmpty()){
                                for (QueryDocumentSnapshot document : result) {

                                    if (document.get("userId").toString().equals(userId)){
                                        double rating = document.getDouble("rating");
                                        RestaurantRating restaurantRating = new RestaurantRating(document.getId()
                                                , document.get("userId").toString()
                                                , document.get("diningSpotId").toString()
                                                , (int)rating);
                                        mRating.add(restaurantRating);
                                    }
                                }
                            }
                        }
                    }
                });
        this.mRestaurantRatingGiven = mRating;
    }
}
