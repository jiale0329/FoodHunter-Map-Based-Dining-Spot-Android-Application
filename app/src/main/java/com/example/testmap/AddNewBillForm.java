package com.example.testmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ThrowOnExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNewBillForm extends AppCompatActivity {

    Button mBtnSubmit, mBtnSelectContact;
    private List<MealMate> mMealMate = new ArrayList<>();
    MealMate owner;
    RecyclerView mRvMealMateAdded;
    MealMateSelectedAdapter adapter;
    TextView mTvPaidByName;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    String userId;
    TextInputLayout mEtMealBillAmount, mEtBillRecordTitle;
    int peopleCount = 1;
    DatePicker mDpMealDate;
    private String payerId;
    String mealParticipants;
    Toolbar toolbar;

    public static SharedPreferences mPreferences;
    private final String SHARED_PREF = "myPreferences";
    private final String KEY_USER = "user";
    private final String KEY_USER_NAME = "userName";
    private final String KEY_PASSWORD = "password";
    private final String KEY_USER_EMAIL = "userEmail";
    private final String KEY_USER_ID = "userId";

    private static final int CONTACT_PERMISSION_CODE = 1;
    private static final int CONTACT_PICK_CODE = 2;
    private static final int OWNER_PICK_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_bill_form);

        mBtnSelectContact = findViewById(R.id.btnSelectContact);
        mBtnSubmit = findViewById(R.id.btnSubmitNewRecord);
        mRvMealMateAdded = findViewById(R.id.rvMealMateSelected);
        mTvPaidByName = findViewById(R.id.tvPaidByName);
        mEtMealBillAmount = findViewById(R.id.etMealBillAmount);
        mEtBillRecordTitle = findViewById(R.id.etBillRecordTitle);
        mDpMealDate = findViewById(R.id.dpMealDate);

        toolbar = findViewById(R.id.newBillForm_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

        userId = mPreferences.getString(KEY_USER_ID, "");

        mRvMealMateAdded.setLayoutManager(new LinearLayoutManager(AddNewBillForm.this));
        adapter = new MealMateSelectedAdapter(mMealMate, AddNewBillForm.this);
        mRvMealMateAdded.setAdapter(adapter);

        mTvPaidByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOwner();
            }
        });

        mBtnSelectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContact();
            }
        });

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadBillRecord();
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

    private void uploadBillRecord() {
        if(mTvPaidByName.getText().equals("OWNER")){
            for(MealMate mealMate : mMealMate){
                database.collection("mealMate")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot result = task.getResult();

                                    if(!result.isEmpty()){
                                        Double amount = Double.parseDouble(mEtMealBillAmount.getEditText().getText().toString());
                                        amount = amount / peopleCount;

                                        Boolean exist = false;

                                        for (QueryDocumentSnapshot document : result) {
                                            if (mealMate.getContactPhoneNumber().equals(document.get("contactPhoneNumber")) && userId.equals(document.get("ownerId"))){
                                                double toReceive = document.getDouble("amountToReceive");
                                                double toPay = document.getDouble("amountToPay");

                                                toReceive = toReceive + amount;

                                                if (toReceive > toPay){
                                                    toReceive = toReceive - toPay;
                                                    toPay = 0;
                                                }else{
                                                    toPay = toPay - toReceive;
                                                    toReceive = 0;
                                                }

                                                database.collection("mealMate").document(document.getId()).update("amountToReceive", toReceive);
                                                database.collection("mealMate").document(document.getId()).update("amountToPay", toPay);
                                                exist = true;
                                            }
                                        }

                                        if (exist==false){
                                            Map<String, Object> user = new HashMap<>();
                                            user.put("contactName", mealMate.getContactName());
                                            user.put("contactPhoneNumber", mealMate.getContactPhoneNumber());
                                            user.put("amountToReceive", amount);
                                            user.put("amountToPay", 0);
                                            user.put("ownerId", userId);

                                            database.collection("mealMate")
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
                                        }
                                    }
                                }
                            }
                        });
            }
        }else{
            database.collection("mealMate")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot result = task.getResult();

                                if(!result.isEmpty()){
                                    Double amount = Double.parseDouble(mEtMealBillAmount.getEditText().getText().toString());
                                    amount = amount / (peopleCount+1);

                                    Boolean exist = false;

                                    for (QueryDocumentSnapshot document : result) {
                                        if (owner.getContactPhoneNumber().equals(document.get("contactPhoneNumber")) && userId.equals(document.get("ownerId"))){

                                            double toReceive = document.getDouble("amountToReceive");
                                            double toPay = document.getDouble("amountToPay");

                                            toPay = toPay + amount;

                                            if (toReceive > toPay){
                                                toReceive = toReceive - toPay;
                                                toPay = 0;
                                            }else{
                                                toPay = toPay - toReceive;
                                                toReceive = 0;
                                            }

                                            payerId = document.getId();

                                            database.collection("mealMate").document(document.getId()).update("amountToReceive", toReceive);
                                            database.collection("mealMate").document(document.getId()).update("amountToPay", toPay);
                                            exist = true;
                                        }
                                    }

                                    if (exist==false){

                                        Map<String, Object> user = new HashMap<>();
                                        user.put("contactName", owner.getContactName());
                                        user.put("contactPhoneNumber", owner.getContactPhoneNumber());
                                        user.put("amountToReceive", 0);
                                        user.put("amountToPay", amount);
                                        user.put("ownerId", userId);

                                        database.collection("mealMate")
                                                .add(user)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                        payerId = documentReference.getId();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("TAG", "Error adding document", e);
                                                    }
                                                });
                                    }
                                }
                            }
                        }
                    });
        }

        ProgressDialog dialog = ProgressDialog.show(AddNewBillForm.this, "",
                "Loading......", true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                //inputBillRecord
                Map<String, Object> user = new HashMap<>();
                user.put("billRecordTitle", mEtBillRecordTitle.getEditText().getText().toString());
                user.put("totalAmount", Double.parseDouble(mEtMealBillAmount.getEditText().getText().toString()));

                if(mTvPaidByName.getText().equals("OWNER")){

                    user.put("paidById", "OWNER");

                    mealParticipants = "";
                    for(MealMate mealMate : mMealMate){
                        mealParticipants = mealParticipants + ", " + mealMate.getContactName();
                    }
                }else{

                    user.put("paidById", payerId);

                    mealParticipants = "Me";
                    for(MealMate mealMate : mMealMate){
                        mealParticipants = mealParticipants + ", " + mealMate.getContactName();
                    }
                }



                int day = mDpMealDate.getDayOfMonth();
                int month = mDpMealDate.getMonth();
                int year = mDpMealDate.getYear();

                String mealDate = day + "/" + month + "/" + year;

                user.put("mealDate", mealDate);
                user.put("mealParticipants", mealParticipants);
                user.put("ownerId", userId);

                database.collection("billRecord")
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

                finish();
                dialog.dismiss();
            }
        }, 3000);

    }

    private void selectOwner() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, OWNER_PICK_CODE);
    }

    private void selectContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, CONTACT_PICK_CODE);
        peopleCount++;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case CONTACT_PICK_CODE:
                    Cursor cursor;
                    try {
                        Uri uri = data.getData();
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        int  nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        int  phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
                        String name = cursor.getString(nameIndex);
                        String phoneNumber = cursor.getString(phoneIndex);
                        String contactId = cursor.getString(idIndex);

                        MealMate mealMate = new MealMate(contactId, name, phoneNumber);
                        mMealMate.add(mealMate);
                        adapter.notifyItemInserted(mMealMate.size()-1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case OWNER_PICK_CODE:
                    try {
                        Uri uri = data.getData();
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        int  nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        int  phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
                        String name = cursor.getString(nameIndex);
                        String phoneNumber = cursor.getString(phoneIndex);
                        String contactId = cursor.getString(idIndex);

                        owner = new MealMate(contactId, name, phoneNumber);
                        mTvPaidByName.setText(owner.getContactName());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }
}