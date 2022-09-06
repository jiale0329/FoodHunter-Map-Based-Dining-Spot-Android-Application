package com.example.testmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class PreviewSpinningWheelChoice extends AppCompatActivity {

    RecyclerView mRv;
    Button mBtnConfirmSpinningWheelChoice;
    SpinningWheelChoiceAdapter adapter;
    SqliteHelper sql;
    ArrayList<DiningChoice> mDiningChoice = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_spinning_wheel_choice);

        mRv = (RecyclerView) findViewById(R.id.rvSpinningWheelChoice);
        mBtnConfirmSpinningWheelChoice = findViewById(R.id.btnConfirmSpinningWheelChoice);

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
                Intent intent = new Intent(PreviewSpinningWheelChoice.this, SpinningWheel.class);
                startActivity(intent);
            }
        });
    }
}