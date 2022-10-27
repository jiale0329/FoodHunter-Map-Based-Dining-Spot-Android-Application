package com.example.testmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;

import org.opencv.android.OpenCVLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PreviewSpinningWheelChoice extends AppCompatActivity {

    RecyclerView mRv;
    Button mBtnConfirmSpinningWheelChoice;
    SpinningWheelChoiceAdapter adapter;
    SqliteHelper sql;
    ArrayList<DiningChoice> mDiningChoice = new ArrayList<>();
    private FusedLocationProviderClient mClient;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_spinning_wheel_choice);

        toolbar = findViewById(R.id.preview_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRv = (RecyclerView) findViewById(R.id.rvSpinningWheelChoice);
        mBtnConfirmSpinningWheelChoice = findViewById(R.id.btnConfirmSpinningWheelChoice);

        mClient = new FusedLocationProviderClient(this);

        sql = new SqliteHelper(PreviewSpinningWheelChoice.this);
        mDiningChoice = sql.readDiningChoice();

        ProgressDialog dialog = ProgressDialog.show(PreviewSpinningWheelChoice.this, "",
                "Loading. Please wait...", true);   //show loading dialog
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();   //remove loading Dialog
                mRv.setLayoutManager(new LinearLayoutManager(PreviewSpinningWheelChoice.this));
                adapter = new SpinningWheelChoiceAdapter(mDiningChoice, PreviewSpinningWheelChoice.this);
                mRv.setAdapter(adapter);
            }
        }, 1000);

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                new AlertDialog.Builder(viewHolder.itemView.getContext())
                        .setMessage("Are you sure you want to delete this choice?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sql.removeDiningChoice(viewHolder.itemView.getTag().toString());
                                mDiningChoice.remove(viewHolder.getAdapterPosition());
                                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            }
                        })
                        .create()
                        .show();
            }
        });

        helper.attachToRecyclerView(mRv);

        mBtnConfirmSpinningWheelChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(PreviewSpinningWheelChoice.this)
                        .setMessage("Do you want to include nearby restaurants within 5km into your choice?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DiningSpotLab diningSpotLab = DiningSpotLab.get(PreviewSpinningWheelChoice.this);
                                List<DiningSpot> mDiningSpot = diningSpotLab.getDiningSpots();

                                ProgressDialog dialog2 = ProgressDialog.show(PreviewSpinningWheelChoice.this, "",
                                        "Loading. Please wait...", true);   //show loading dialog
                                Handler handler2 = new Handler();
                                handler2.postDelayed(new Runnable() {
                                    public void run() {
                                        dialog2.dismiss();   //remove loading Dialog
                                        for (DiningSpot diningSpot : mDiningSpot) {
                                            if (ActivityCompat.checkSelfPermission(PreviewSpinningWheelChoice.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PreviewSpinningWheelChoice.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    ActivityCompat#requestPermissions
                                                // here to request the missing permissions, and then overriding
                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                //                                          int[] grantResults)
                                                // to handle the case where the user grants the permission. See the documentation
                                                // for ActivityCompat#requestPermissions for more details.
                                                return;
                                            }
                                            mClient.getLastLocation().addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Location location = task.getResult();
                                                    Location endPoint = new Location("locationA");
                                                    endPoint.setLatitude(Double.parseDouble(diningSpot.getmLatitude()));
                                                    endPoint.setLongitude(Double.parseDouble(diningSpot.getmLongitude()));
                                                    float distanceCalculated = location.distanceTo(endPoint) / 1000;

                                                    int scale = (int) Math.pow(10, 1);
                                                    double distance = (double) Math.round(distanceCalculated * scale) / scale;

                                                    if (distance < 5){

                                                        Boolean existInsideChoice = false;

                                                        for (DiningChoice diningChoice : mDiningChoice){
                                                            if (diningChoice.getmId().equals(diningSpot.getmId())){
                                                                existInsideChoice = true;
                                                            }
                                                        }

                                                        if (existInsideChoice == false){
                                                            DiningChoice newChoice = new DiningChoice(diningSpot.getmId(), diningSpot.getmName());
                                                            mDiningChoice.add(newChoice);
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }, 2000);

                                ProgressDialog dialog3 = ProgressDialog.show(PreviewSpinningWheelChoice.this, "",
                                        "Loading. Please wait...", true);   //show loading dialog
                                Handler handler3 = new Handler();
                                handler3.postDelayed(new Runnable() {
                                    public void run() {
                                        dialog3.dismiss();   //remove loading Dialog
                                        Intent intent = new Intent(PreviewSpinningWheelChoice.this, SpinningWheel.class);
                                        intent.putExtra("DiningChoice",mDiningChoice );
                                        startActivity(intent);
                                    }
                                }, 5000);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ProgressDialog dialog2 = ProgressDialog.show(PreviewSpinningWheelChoice.this, "",
                                        "Loading. Please wait...", true);   //show loading dialog
                                Handler handler2 = new Handler();
                                handler2.postDelayed(new Runnable() {
                                    public void run() {
                                        dialog2.dismiss();   //remove loading Dialog
                                        Intent intent = new Intent(PreviewSpinningWheelChoice.this, SpinningWheel.class);
                                        intent.putExtra("DiningChoice",mDiningChoice );
                                        startActivity(intent);
                                    }
                                }, 5000);
                            }
                        })
                        .create()
                        .show();
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
}