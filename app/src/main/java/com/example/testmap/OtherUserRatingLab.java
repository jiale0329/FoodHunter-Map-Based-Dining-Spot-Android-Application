package com.example.testmap;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OtherUserRatingLab {
    private static OtherUserRatingLab sOtherUserRatingLab;
    private List<UserRating> mOtherUserRating;
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    public static OtherUserRatingLab get(Context context, String userId){

        sOtherUserRatingLab = new OtherUserRatingLab(context, userId);
        return sOtherUserRatingLab;
    }

    private OtherUserRatingLab(Context context, String userId) {
        mOtherUserRating = new ArrayList<>();
        mOtherUserRating.clear();

        database.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();

                            if(!result.isEmpty()){
                                for (QueryDocumentSnapshot document : result) {
                                    if (!document.getId().equals(userId)){
                                        UserRating otherUserRating = new UserRating(document.getId());
                                        otherUserRating.initiateRestaurantRatingGiven();
                                        mOtherUserRating.add(otherUserRating);
                                    }
                                }
                            }
                        }
                    }
                });
    }

    public List<UserRating> getOtherUserRatings(){
        return mOtherUserRating;
    }

    public UserRating getOtherUserRating(UUID id){
        for (UserRating userRating : mOtherUserRating){
            if (userRating.getUserId().equals(id)){
                return userRating;
            }
        }
        return null;
    }
}
