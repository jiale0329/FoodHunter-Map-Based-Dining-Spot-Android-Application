package com.example.testmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MealMateSelectedAdapter extends RecyclerView.Adapter<MealMateSelectedAdapter.MealMateSelectedHolder> {

    private List<MealMate> mMealMate;
    private Context context;

    public MealMateSelectedAdapter(List<MealMate>mealMate, Context c){
        mMealMate = mealMate;
        context = c;
    }

    @NonNull
    @Override
    public MealMateSelectedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater
                .inflate(/*android.R.layout.simple_list_item_1*/
                        R.layout.list_item_meal_mate_selected, parent, false);
        return new MealMateSelectedAdapter.MealMateSelectedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealMateSelectedHolder holder, int position) {
        MealMate mealMate = mMealMate.get(position);
        holder.bindMealMate(mealMate);
    }

    @Override
    public int getItemCount() { return mMealMate.size();}

    public class MealMateSelectedHolder extends RecyclerView.ViewHolder{

        public TextView mTvMealMateSelected;
        private MealMate mMate;

        public MealMateSelectedHolder(@NonNull View itemView) {
            super(itemView);

            mTvMealMateSelected = itemView.findViewById(R.id.list_item_meal_mate_selected);
        }

        public void bindMealMate(MealMate mealMate){
            mMate = mealMate;
            mTvMealMateSelected.setText(mMate.getContactName());
        }
    }
}
