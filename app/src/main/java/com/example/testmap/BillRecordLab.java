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

public class BillRecordLab {
    private static BillRecordLab sBillRecordLab;
    private List<BillRecord> mBillRecord;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static BillRecordLab get(Context context){

        sBillRecordLab = new BillRecordLab(context);
        return sBillRecordLab;
    }

    private BillRecordLab(Context context) {
        mBillRecord = new ArrayList<>();
        mBillRecord.clear();

        db.collection("billRecord")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();

                            if(!result.isEmpty()){
                                for (QueryDocumentSnapshot document : result) {
                                    BillRecord billRecord = new BillRecord(document.getId()
                                            , document.get("billRecordTitle").toString()
                                            , document.getDouble("totalAmount")
                                            , document.get("mealDate").toString()
                                            , document.get("paidById").toString()
                                            , document.get("mealParticipants").toString()
                                            , document.get("ownerId").toString());
                                    mBillRecord.add(billRecord);
                                }
                            }
                        }
                    }
                });
    }

    public List<BillRecord> getBillRecords(){
        return mBillRecord;
    }

    public BillRecord getBillRecord(UUID id){
        for (BillRecord BillRecord : mBillRecord){
            if (BillRecord.getBillRecordId().equals(id)){
                return BillRecord;
            }
        }
        return null;
    }
}
