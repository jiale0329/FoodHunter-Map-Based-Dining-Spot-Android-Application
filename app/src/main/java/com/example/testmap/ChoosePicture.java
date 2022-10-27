package com.example.testmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

public class ChoosePicture extends AppCompatActivity {

    Button mBtnChoosePicture, mBtnConfirmPicture;
    ImageView mIvImageSelected;
    public Uri imageSelectedUri;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_picture);

        if (!OpenCVLoader.initDebug()){
            Toast.makeText(this, "OPENCV NOT INSTALL", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "OPENCV INSTALLED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
        }

        toolbar = findViewById(R.id.choosePicture_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mBtnChoosePicture = findViewById(R.id.btnChoosePicture);
        mBtnConfirmPicture = findViewById(R.id.btnConfirmImageSelected);
        mIvImageSelected = findViewById(R.id.ivImageSelected);

        mBtnChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        mBtnConfirmPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoosePicture.this, SimilarityCalculation.class);
                intent.putExtra("imagePath", imageSelectedUri.toString());
                startActivity(intent);
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

    private void choosePicture() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {

                imageSelectedUri = data.getData();
                mIvImageSelected.setImageURI(imageSelectedUri);
        }
    }
}