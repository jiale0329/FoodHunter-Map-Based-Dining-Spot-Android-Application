package com.example.testmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ViewBillRecordDetails extends AppCompatActivity {

    Toolbar toolbar;
    String billRecordId, userId;
    TextInputLayout mTilBillRecordTitle, mTilBillRecordDate, mTilBillRecordTotalAmountContent, mTilBillRecordPaidByContent, mTilBillRecordParticipantsContent;
    AppCompatButton mBtnDeleteRecord;
    private List<BillRecord> mBillRecord = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static SharedPreferences mPreferences;
    public static final String EXTRA_BILLRECORD_ID = "billRecord_id";
    private final String SHARED_PREF = "myPreferences";
    private final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bill_record_details);

        mTilBillRecordTitle = findViewById(R.id.tilBillRecordTitle);
        mTilBillRecordDate = findViewById(R.id.tilBillRecordDate);
        mTilBillRecordTotalAmountContent = findViewById(R.id.tilBillRecordTotalAmountContent);
        mTilBillRecordPaidByContent = findViewById(R.id.tilBillRecordPaidByContent);
        mTilBillRecordParticipantsContent = findViewById(R.id.tilBillRecordParticipantsContent);
        mBtnDeleteRecord = findViewById(R.id.btnDeleteBillrecord);

        toolbar = findViewById(R.id.viewRestaurant_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        userId = mPreferences.getString(KEY_USER_ID, "");

        Bundle bundle = getIntent().getExtras();
        billRecordId = bundle.getString(EXTRA_BILLRECORD_ID);

        BillRecordLab billRecordLab = BillRecordLab.get(ViewBillRecordDetails.this, userId);
        mBillRecord = billRecordLab.getBillRecords();

        mBtnDeleteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("billRecord")
                        .document(billRecordId)
                        .delete();

                Intent intentBill = new Intent(ViewBillRecordDetails.this, BillActivity.class);
                startActivity(intentBill);
                finish();
            }
        });

        ProgressDialog dialog = ProgressDialog.show(ViewBillRecordDetails.this, "",
                "Loading......", true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                for (BillRecord billRecord : mBillRecord) {
                    if (billRecordId.equals(billRecord.getBillRecordId())) {
                        mTilBillRecordTitle.getEditText().setText(billRecord.getBillRecordTitle());
                        mTilBillRecordDate.getEditText().setText(billRecord.getBillDate());

                        DecimalFormat df = new DecimalFormat("0.00");

                        mTilBillRecordTotalAmountContent.getEditText().setText("RM " + df.format(billRecord.getTotalAmount()));

                        if (!billRecord.getPaidById().equals("OWNER")){
                            db.collection("mealMate")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot result = task.getResult();

                                                if(!result.isEmpty()){
                                                    for (QueryDocumentSnapshot document : result) {
                                                        if (document.getId().equals(billRecord.getPaidById())){
                                                            mTilBillRecordPaidByContent.getEditText().setText(document.get("contactName").toString());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }else{
                            mTilBillRecordPaidByContent.getEditText().setText(billRecord.getPaidById());
                        }

                        mTilBillRecordParticipantsContent.getEditText().setText("Me");
                    }
                }

                mTilBillRecordTitle.getEditText().setTextColor(ContextCompat.getColor(ViewBillRecordDetails.this, R.color.black));
                mTilBillRecordDate.getEditText().setTextColor(ContextCompat.getColor(ViewBillRecordDetails.this, R.color.black));
                mTilBillRecordTotalAmountContent.getEditText().setTextColor(ContextCompat.getColor(ViewBillRecordDetails.this, R.color.black));
                mTilBillRecordPaidByContent.getEditText().setTextColor(ContextCompat.getColor(ViewBillRecordDetails.this, R.color.black));
                mTilBillRecordParticipantsContent.getEditText().setTextColor(ContextCompat.getColor(ViewBillRecordDetails.this, R.color.black));

                dialog.dismiss();
            }
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intentBill = new Intent(ViewBillRecordDetails.this, BillActivity.class);
                startActivity(intentBill);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}