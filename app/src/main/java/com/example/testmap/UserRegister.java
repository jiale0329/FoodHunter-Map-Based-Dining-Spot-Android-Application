package com.example.testmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserRegister extends AppCompatActivity {

    Button mBtnRegisterAcc, mBtnNavigateToLogin;
    TextInputLayout etFullName, etUserPhone, etUserEmail, etUserPassword;
    String userName, userEmail, userPhone, userPassword;
    ImageView ivBack;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        mBtnRegisterAcc = (Button) findViewById(R.id.btnRegisterAcc);
        ivBack = findViewById(R.id.ivBack);
        etFullName = findViewById(R.id.etName);
        etUserEmail =  findViewById(R.id.etUserEmail);
        etUserPhone =  findViewById(R.id.etUserPhone);
        etUserPassword =findViewById(R.id.etUserPassword);
        mBtnNavigateToLogin = (Button) findViewById(R.id.btnNavigateToLogin);

        mAuth = FirebaseAuth.getInstance();

        mBtnRegisterAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        mBtnNavigateToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserRegister.this, UserLogin.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void registerUser() {
        userName = etFullName.getEditText().getText().toString().trim();
        userEmail = etUserEmail.getEditText().getText().toString().trim();
        userPassword = etUserPassword.getEditText().getText().toString().trim();
        userPhone = etUserPhone.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            etFullName.setError("Please fill in your name!");
            etFullName.requestFocus();
        } else if (TextUtils.isEmpty(userEmail)) {
            etUserEmail.setError("Please fill in Email!");
            etUserEmail.requestFocus();
        }else if (TextUtils.isEmpty(userPassword)) {
            etUserPassword.setError("Please fill in Password!");
            etUserPassword.requestFocus();
        }else if(TextUtils.isEmpty(userPhone)){
            etUserPhone.setError("Please fill in Phone Number!");
            etUserPhone.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("fullName", userName);
                        user.put("email", userEmail);
                        user.put("phone", userPhone);

                        db.collection("users")
                                .add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(UserRegister.this, "Insert into Firebase Auth Success", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(UserRegister.this, UserLogin.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserRegister.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(UserRegister.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}