package com.example.testmap;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class RecommendedDiningSpotAdapter extends RecyclerView.Adapter<RecommendedDiningSpotAdapter.RecommendedDiningSpotHolder> {

    private List<DiningSpot> mRecommendedDiningSpot;
    private Context context;

    public RecommendedDiningSpotAdapter(List<DiningSpot>diningSpot, Context c){
        mRecommendedDiningSpot = diningSpot;
        context = c;
    }

    @NonNull
    @Override
    public RecommendedDiningSpotHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater
                .inflate(/*android.R.layout.simple_list_item_1*/
                        R.layout.list_item_recommended_restaurant, parent, false);
        return new RecommendedDiningSpotAdapter.RecommendedDiningSpotHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedDiningSpotHolder holder, int position) {
        DiningSpot diningSpot = mRecommendedDiningSpot.get(position);
        holder.bindRecommendedDiningSpot(diningSpot);
    }

    @Override
    public int getItemCount() { return mRecommendedDiningSpot.size();}

    public class RecommendedDiningSpotHolder extends RecyclerView.ViewHolder{

        public TextView mTvRecommendedRestaurantName, mTvRecommendedRestaurantRating;
        private DiningSpot mRecommendedDiningSpot;

        public RecommendedDiningSpotHolder(@NonNull View itemView) {
            super(itemView);

            mTvRecommendedRestaurantName = itemView.findViewById(R.id.list_item_recommended_restaurant_name);
            mTvRecommendedRestaurantRating = itemView.findViewById(R.id.list_item_recommended_restaurant_rating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ViewRestaurantProfile.class);
                    i.putExtra("restaurant_id", mRecommendedDiningSpot.getmId());
                    context.startActivity(i);
                }
            });
        }

        public void bindRecommendedDiningSpot(DiningSpot diningSpot){
            mRecommendedDiningSpot = diningSpot;
            mTvRecommendedRestaurantName.setText(mRecommendedDiningSpot.getmName());
            mTvRecommendedRestaurantRating.setText("" + diningSpot.getmRating());
        }
    }
}
