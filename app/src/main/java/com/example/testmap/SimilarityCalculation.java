package com.example.testmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SimilarityCalculation extends AppCompatActivity {

    RecyclerView mRv;
    SimilarityCalculationAdapter adapter;
    List<DiningSpot> mDiningSpot = new ArrayList<>();
    Uri imagePassed;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similarity_calculation);

        toolbar = findViewById(R.id.similarityCalculation_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRv = (RecyclerView) findViewById(R.id.rvSimilarityCalculation);
        imagePassed = Uri.parse(getIntent().getStringExtra("imagePath"));

        DiningSpotLab diningSpotLab = DiningSpotLab.get(SimilarityCalculation.this);
        mDiningSpot = diningSpotLab.getDiningSpots();

        ProgressDialog dialog = ProgressDialog.show(SimilarityCalculation.this, "",
                "Loading. Please wait...", true);   //show loading dialog
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();   //remove loading Dialog
                mRv.setLayoutManager(new LinearLayoutManager(SimilarityCalculation.this));
                adapter = new SimilarityCalculationAdapter(mDiningSpot, imagePassed, SimilarityCalculation.this);
                mRv.setAdapter(adapter);
            }
        }, 1000);
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
}