package com.example.testmap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;

public class MealMateDebtRecordAdapter extends RecyclerView.Adapter<MealMateDebtRecordAdapter.MealMateDebtRecordHolder> {

    private List<MealMate> mMealMate;
    private Context context;
    int width, height;

    public MealMateDebtRecordAdapter(List<MealMate>mealMate, Context c, int width, int height){
        mMealMate = mealMate;
        context = c;
        this.width = width;
        this.height = height;
    }

    @NonNull
    @Override
    public MealMateDebtRecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater
                .inflate(/*android.R.layout.simple_list_item_1*/
                        R.layout.list_item_individual_debt_record, parent, false);
        return new MealMateDebtRecordAdapter.MealMateDebtRecordHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealMateDebtRecordHolder holder, int position) {
        MealMate mealMate = mMealMate.get(position);
        holder.bindMealMate(mealMate, width, height);
    }

    @Override
    public int getItemCount() { return mMealMate.size();}

    public class MealMateDebtRecordHolder extends RecyclerView.ViewHolder{

        public TextView mTvContactName, mTvAmountToPay, mTvAmountToReceive;
        private MealMate mMealMate;
        int width, height;

        public MealMateDebtRecordHolder(@NonNull View itemView) {
            super(itemView);

            mTvContactName = itemView.findViewById(R.id.list_item_debt_record_contact_name);
            mTvAmountToPay = itemView.findViewById(R.id.list_item_debt_record_amountToPay);
            mTvAmountToReceive = itemView.findViewById(R.id.list_item_debt_record_amountToReceive);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressDialog dialog3 = ProgressDialog.show(context, "",
                            "Loading. Please wait...", true);   //show loading dialog
                    Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        public void run() {
                            dialog3.dismiss();   //remove loading Dialog
                            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                            View popupView = layoutInflater.inflate(R.layout.individual_debt_popup_layout, null);

                            PopupWindow popupWindow = new PopupWindow(popupView);
                            popupWindow.setWidth(width-40);
                            popupWindow.setHeight(height);
                            popupWindow.setFocusable(true);

                            popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 150);

                            TextView mTvDebtPopupMealMateName = (TextView) popupView.findViewById(R.id.tvDebtPopupMealMateName);
                            TextView mTvDebtPopupReceiveContent = (TextView) popupView.findViewById(R.id.tvDebtPopupReceiveContent);
                            TextView mTvDebtPopupPayContent = (TextView) popupView.findViewById(R.id.tvDebtPopupPayContent);
                            Button mBtnDebtPopupSendReminder = (Button) popupView.findViewById(R.id.btnDebtPopupSendReminder);
                            Button mBtnDebtPopupSettleBill = (Button) popupView.findViewById(R.id.btnDebtPopupSettleBill);

                            mTvDebtPopupMealMateName.setText(mMealMate.getContactName());

                            DecimalFormat df = new DecimalFormat("0.00");

                            mTvDebtPopupReceiveContent.setText("RM " + df.format(mMealMate.getAmountToReceive()));
                            mTvDebtPopupPayContent.setText("RM " + df.format(mMealMate.getAmountToPay()));

                            if (mMealMate.getAmountToReceive() == 0 ){
                                mBtnDebtPopupSendReminder.setEnabled(false);
                            }

                            popupView.setOnTouchListener(new View.OnTouchListener()
                            {
                                @Override
                                public boolean onTouch(View v, MotionEvent event)
                                {
                                    popupWindow.dismiss();
                                    return true;
                                }
                            });

                            mBtnDebtPopupSettleBill.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(context, SettleBill.class);
                                    i.putExtra("mealMate_id", mMealMate.getContactId());
                                    context.startActivity(i);
                                    popupWindow.dismiss();
                                    ((Activity)context).finish();
                                }
                            });

                            mBtnDebtPopupSendReminder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PackageManager packageManager = context.getPackageManager();
                                    Intent i = new Intent(Intent.ACTION_VIEW);

                                    try {
                                        String url = "https://api.whatsapp.com/send?phone="+ mMealMate.getContactPhoneNumber() +"&text="
                                                + URLEncoder.encode("Greetings. \n\nAuto reminder from FoodHunter Bill Record System." +
                                                "\n\nDon't forget to pay RM " + df.format(mMealMate.getAmountToReceive()) + " to me" +
                                                "\n\nDo let me know if you're having any troubles", "UTF-8");
                                        i.setPackage("com.whatsapp");
                                        i.setData(Uri.parse(url));
                                        if (i.resolveActivity(packageManager) != null) {
                                            context.startActivity(i);
                                        }
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                    }, 2000);
                }
            });
        }

        public void bindMealMate(MealMate mealMate, int width, int height){
            this.width = width;
            this.height = height;

            DecimalFormat df = new DecimalFormat("0.00");
            mMealMate = mealMate;
            mTvContactName.setText(mMealMate.getContactName());
            mTvAmountToPay.setText("RM" + df.format(mMealMate.getAmountToPay()));
            mTvAmountToReceive.setText("RM" + df.format(mMealMate.getAmountToReceive()));
        }
    }
}
