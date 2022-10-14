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
import java.util.ArrayList;
import java.util.List;

public class RatedDiningSpotAdapter extends RecyclerView.Adapter<RatedDiningSpotAdapter.RatedDiningSpotHolder> {

    private List<RestaurantRating> mRestaurantRating;
    List<DiningSpot> mDiningSpot;
    private Context context;

    public RatedDiningSpotAdapter(List<RestaurantRating>restaurantRating, List<DiningSpot> diningSpots,  Context c){
        mRestaurantRating = restaurantRating;
        mDiningSpot = diningSpots;
        context = c;
    }

    @NonNull
    @Override
    public RatedDiningSpotHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater
                .inflate(/*android.R.layout.simple_list_item_1*/
                        R.layout.list_item_recommended_restaurant, parent, false);
        return new RatedDiningSpotAdapter.RatedDiningSpotHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatedDiningSpotHolder holder, int position) {
        RestaurantRating restaurantRating = mRestaurantRating.get(position);
        holder.bindRatedDiningSpot(restaurantRating);
    }

    @Override
    public int getItemCount() { return mRestaurantRating.size();}

    public class RatedDiningSpotHolder extends RecyclerView.ViewHolder{

        public TextView mTvRatedRestaurantName, mTvRatedRestaurantRating;
        private RestaurantRating mRestaurantRating;

        public RatedDiningSpotHolder(@NonNull View itemView) {
            super(itemView);

            mTvRatedRestaurantName = itemView.findViewById(R.id.list_item_recommended_restaurant_name);
            mTvRatedRestaurantRating = itemView.findViewById(R.id.list_item_recommended_restaurant_rating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ViewRestaurantProfile.class);
                    i.putExtra("restaurant_id", mRestaurantRating.getDiningSpotId());
                    context.startActivity(i);
                }
            });
        }

        public void bindRatedDiningSpot(RestaurantRating restaurantRating){
            mRestaurantRating = restaurantRating;
            for (DiningSpot diningSpot : mDiningSpot){
                if (diningSpot.getmId().equals(restaurantRating.getDiningSpotId())){
                    mTvRatedRestaurantName.setText(diningSpot.getmName());
                }
            }
            mTvRatedRestaurantRating.setText("" + restaurantRating.getRating());
        }
    }
}
