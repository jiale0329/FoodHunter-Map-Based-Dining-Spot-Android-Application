package com.example.testmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class MealMateDebtRecordAdapter extends RecyclerView.Adapter<MealMateDebtRecordAdapter.MealMateDebtRecordHolder> {

    private List<MealMate> mMealMate;
    private Context context;

    public MealMateDebtRecordAdapter(List<MealMate>mealMate, Context c){
        mMealMate = mealMate;
        context = c;
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
        holder.bindMealMate(mealMate);
    }

    @Override
    public int getItemCount() { return mMealMate.size();}

    public class MealMateDebtRecordHolder extends RecyclerView.ViewHolder{

        public TextView mTvContactName, mTvAmountToPay, mTvAmountToReceive;
        private MealMate mMealMate;

        public MealMateDebtRecordHolder(@NonNull View itemView) {
            super(itemView);

            mTvContactName = itemView.findViewById(R.id.list_item_debt_record_contact_name);
            mTvAmountToPay = itemView.findViewById(R.id.list_item_debt_record_amountToPay);
            mTvAmountToReceive = itemView.findViewById(R.id.list_item_debt_record_amountToReceive);
        }

        public void bindMealMate(MealMate mealMate){
            DecimalFormat df = new DecimalFormat("0.00");
            mMealMate = mealMate;
            mTvContactName.setText(mMealMate.getContactName());
            mTvAmountToPay.setText("RM" + df.format(mMealMate.getAmountToPay()));
            mTvAmountToReceive.setText("RM" + df.format(mMealMate.getAmountToReceive()));
        }
    }
}
