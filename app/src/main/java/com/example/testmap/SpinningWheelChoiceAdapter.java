package com.example.testmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SpinningWheelChoiceAdapter extends RecyclerView.Adapter<SpinningWheelChoiceAdapter.SpinningWheelChoiceHolder> {

    private List<DiningChoice> mDiningChoice;
    private Context context;

    public SpinningWheelChoiceAdapter(List<DiningChoice>diningChoices, Context c){
        mDiningChoice = diningChoices;
        context = c;
    }

    @NonNull
    @Override
    public SpinningWheelChoiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater
                .inflate(/*android.R.layout.simple_list_item_1*/
                        R.layout.list_item_spinning_choice, parent, false);
        return new SpinningWheelChoiceAdapter.SpinningWheelChoiceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpinningWheelChoiceHolder holder, int position) {
        DiningChoice diningChoice = mDiningChoice.get(position);
        holder.bindDiningChoice(diningChoice);

        holder.itemView.setTag(diningChoice.getmId());
    }

    @Override
    public int getItemCount() { return mDiningChoice.size();}

    public class SpinningWheelChoiceHolder extends RecyclerView.ViewHolder{

        public TextView mTvDiningChoiceName;
        private DiningChoice mDiningChoice;

        public SpinningWheelChoiceHolder(@NonNull View itemView) {
            super(itemView);

            mTvDiningChoiceName = itemView.findViewById(R.id.list_item_choice_title);
        }

        public void bindDiningChoice(DiningChoice diningChoice){
            mDiningChoice = diningChoice;
            mTvDiningChoiceName.setText(mDiningChoice.getmName());
        }
    }
}
