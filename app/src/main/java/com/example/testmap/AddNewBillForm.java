package com.example.testmap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddNewBillForm extends AppCompatActivity {

    Button mBtnSubmit, mBtnSelectContact;
    private List<MealMate> mMealMate = new ArrayList<>();
    RecyclerView mRvMealMateAdded;
    MealMateSelectedAdapter adapter;

    private static final int CONTACT_PERMISSION_CODE = 1;
    private static final int CONTACT_PICK_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_bill_form);

        mBtnSelectContact = findViewById(R.id.btnSelectContact);
        mBtnSubmit = findViewById(R.id.btnSubmitNewRecord);
        mRvMealMateAdded = findViewById(R.id.rvMealMateSelected);

        mRvMealMateAdded.setLayoutManager(new LinearLayoutManager(AddNewBillForm.this));
        adapter = new MealMateSelectedAdapter(mMealMate, AddNewBillForm.this);
        mRvMealMateAdded.setAdapter(adapter);

        mBtnSelectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContact();
            }
        });

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void selectContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, CONTACT_PICK_CODE);
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
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }
}