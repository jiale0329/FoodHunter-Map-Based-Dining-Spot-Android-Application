package com.example.testmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SettleBill extends AppCompatActivity {

    TextView mTvSettleBillMealMateName, mTvSettleBillReceiveAmount, mTvSettleBillPayAmount;
    private List<MealMate> mMealMate = new ArrayList<>();
    String mealMate_id, userId;
    Button mBtnSettleBillSubmit;
    Toolbar toolbar;
    TextInputLayout mEtSettleBillPay, mEtSettleBillReceive;
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    public static SharedPreferences mPreferences;
    private final String SHARED_PREF = "myPreferences";
    private final String KEY_USER_ID = "userId";
    public static final String EXTRA_MEAL_MATE_ID = "mealMate_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_bill);

        mTvSettleBillMealMateName = findViewById(R.id.tvSettleBillMealMateName);
        mTvSettleBillReceiveAmount = findViewById(R.id.tvSettleBillReceiveAmount);
        mTvSettleBillPayAmount = findViewById(R.id.tvSettleBillPayAmount);
        mEtSettleBillPay = findViewById(R.id.etSettleBillPay);
        mEtSettleBillReceive = findViewById(R.id.etSettleBillReceive);
        mBtnSettleBillSubmit = findViewById(R.id.btnSettleBillSubmit);
        toolbar = findViewById(R.id.settleBill_toolbar);

        mPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        userId = mPreferences.getString(KEY_USER_ID, "");

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        mealMate_id = bundle.getString(EXTRA_MEAL_MATE_ID);

        MealMateLab mealMateLab = MealMateLab.get(SettleBill.this, userId);
        mMealMate = mealMateLab.getMealMates();

        ProgressDialog dialog = ProgressDialog.show(SettleBill.this, "",
                "Loading......", true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("ResourceAsColor")
            public void run() {
                for (MealMate mealMate : mMealMate){
                    if (mealMate.getContactId().equals(mealMate_id)){
                        mTvSettleBillMealMateName.setText(mealMate.getContactName());

                        DecimalFormat df = new DecimalFormat("0.00");

                        mTvSettleBillReceiveAmount.setText("RM " + df.format(mealMate.getAmountToReceive()));
                        mTvSettleBillPayAmount.setText("RM " + df.format(mealMate.getAmountToPay()));

                        if (mealMate.getAmountToReceive() == 0 && mealMate.getAmountToPay() == 0){
                            mBtnSettleBillSubmit.setEnabled(false);
                        }

                        if (mealMate.getAmountToReceive() == 0){
                            mEtSettleBillReceive.setHint("No Bill To Receive");
                            mEtSettleBillReceive.setEnabled(false);
                        }else{
                            mEtSettleBillReceive.setHintTextColor(ColorStateList.valueOf(R.color.black));
                        }

                        if (mealMate.getAmountToPay() == 0){
                            mEtSettleBillPay.setHint("No Bill To Receive");
                            mEtSettleBillPay.setEnabled(false);
                        }else{
                            mEtSettleBillPay.setHintTextColor(ColorStateList.valueOf(R.color.black));
                        }
                    }
                }
                dialog.dismiss();
            }
        }, 2000);

        mBtnSettleBillSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database.collection("mealMate")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot result = task.getResult();

                                    if(!result.isEmpty()){
                                        for (QueryDocumentSnapshot document : result) {
                                            if (document.getId().equals(mealMate_id)){
                                                double toReceive = document.getDouble("amountToReceive");
                                                double toPay = document.getDouble("amountToPay");

                                                if (!(toReceive == 0)){
                                                    toReceive = toReceive - Double.parseDouble(mEtSettleBillReceive.getEditText().getText().toString().trim());
                                                    database.collection("mealMate").document(mealMate_id).update("amountToReceive", toReceive);
                                                }

                                                if (!(toPay == 0)){
                                                    toPay = toPay - Double.parseDouble(mEtSettleBillPay.getEditText().getText().toString().trim());
                                                    database.collection("mealMate").document(mealMate_id).update("amountToPay", toPay);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });

                ProgressDialog dialog = ProgressDialog.show(SettleBill.this, "",
                        "Submitting......", true);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @SuppressLint("ResourceAsColor")
                    public void run() {
                        Intent intentDebt = new Intent(SettleBill.this, DebtRecordByIndividual.class);
                        startActivity(intentDebt);
                        finish();
                    }
                }, 2000);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intentDebt = new Intent(SettleBill.this, DebtRecordByIndividual.class);
                startActivity(intentDebt);

                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}