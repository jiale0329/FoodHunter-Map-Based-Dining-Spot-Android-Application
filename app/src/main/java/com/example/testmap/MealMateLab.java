package com.example.testmap;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MealMateLab {
    private static MealMateLab sMealMateLab;
    private List<MealMate> mMealMate;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static MealMateLab get(Context context, String userId){

        sMealMateLab = new MealMateLab(context, userId);
        return sMealMateLab;
    }

    private MealMateLab(Context context, String userId) {
        mMealMate = new ArrayList<>();
        mMealMate.clear();

        db.collection("mealMate")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();

                            if(!result.isEmpty()){
                                for (QueryDocumentSnapshot document : result) {
                                    if (document.get("ownerId").toString().equals(userId)){
                                        MealMate mealMate = new MealMate(document.getId()
                                                , document.get("contactName").toString()
                                                , document.get("contactPhoneNumber").toString());
                                        mealMate.setAmountToPay(document.getDouble("amountToPay"));
                                        mealMate.setAmountToReceive(document.getDouble("amountToReceive"));
                                        mealMate.setOwnerId(document.get("ownerId").toString());
                                        mMealMate.add(mealMate);
                                    }
                                }
                            }
                        }
                    }
                });
    }

    public List<MealMate> getMealMates(){
        return mMealMate;
    }

    public MealMate getMealMate(UUID id){
        for (MealMate mealMate : mMealMate){
            if (mealMate.getContactId().equals(id)){
                return mealMate;
            }
        }
        return null;
    }
}
