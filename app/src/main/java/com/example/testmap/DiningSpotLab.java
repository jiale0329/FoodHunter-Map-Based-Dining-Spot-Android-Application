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
                                    mDiningSpot.add(diningSpot);
                                    //Toast.makeText(MainActivity.this, document.get("name").toString(), Toast.LENGTH_SHORT).show();
//                            set.add(document.getId().toString());
                                }
                            }
                        } else {

                        }
                    }
                });
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
