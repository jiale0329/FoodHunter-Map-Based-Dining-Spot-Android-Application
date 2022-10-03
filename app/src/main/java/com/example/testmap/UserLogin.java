package com.example.testmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class UserLogin extends AppCompatActivity {

    EditText etEmail;
    EditText etPassword;
    Button btnLogin;
    Button btnRegister;
    TextView tvAdminLogin;
    TextView tvHospitalLogin;
    String userName, userEmail, userPassword;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    FirebaseUser user;

    public static SharedPreferences mPreferences;
    private final String SHARED_PREF = "myPreferences";
    private final String KEY_USER = "user";
    private final String KEY_USER_NAME = "userName";
    private final String KEY_PASSWORD = "password";
    private final String KEY_USER_EMAIL = "userEmail";
    private final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        mPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

        if (mPreferences.contains(KEY_USER_EMAIL) && mPreferences.contains(KEY_PASSWORD)) {
            Intent i = new Intent(UserLogin.this, MapsActivity.class);
            startActivity(i);
            finish();
        }

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserLogin.this, UserRegister.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void loginUser() {
        userEmail = etEmail.getText().toString();
        userPassword = etPassword.getText().toString();

        if (TextUtils.isEmpty(userEmail)) {
            etEmail.setError("Please fill in Email!");
            etEmail.requestFocus();
        } else if (TextUtils.isEmpty(userPassword)) {
            etPassword.setError("Please fill in Password!");
            etPassword.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        db.collection("users")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot result = task.getResult();

                                            if(!result.isEmpty()){
                                                for (QueryDocumentSnapshot document : result) {
                                                    if (userEmail.equals(document.get("email").toString()))
                                                    {
                                                        Intent i = new Intent(UserLogin.this, MapsActivity.class);
                                                        i.putExtra(KEY_USER_NAME, userName);
                                                        i.putExtra(KEY_USER_EMAIL, etEmail.getText().toString());
                                                        i.putExtra(KEY_PASSWORD, etPassword.getText().toString());
                                                        startActivity(i);
                                                        finish();

                                                        SharedPreferences.Editor editor = mPreferences.edit();
                                                        editor.putString(KEY_USER_EMAIL, etEmail.getText().toString());
                                                        editor.putString(KEY_PASSWORD, etPassword.getText().toString());
                                                        editor.putString(KEY_USER_NAME, userName);
                                                        editor.putString(KEY_USER_ID, document.getId().toString());
                                                        editor.apply();
                                                    }
                                                }
                                            }
                                        } else {
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(UserLogin.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}