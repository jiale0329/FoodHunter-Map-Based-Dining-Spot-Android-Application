package com.example.testmap;

import java.util.List;

public class DiningSpot {
    private String mId;
    private String mName;
    private String  mLatitude;
    private String mLongitude;
    private String mAddress;
    private String mPictureUrl;
    private List<RestaurantRating> mRestaurantRating;
    private double mRating = 0;

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(String mLatitude) {
        this.mLatitude = mLatitude;
    }

    public String getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(String mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getmPictureUrl() {
        return mPictureUrl;
    }

    public void setmPictureUrl(String mPictureUrl) {
        this.mPictureUrl = mPictureUrl;
    }

    public double getmRating() {
        return mRating;
    }

    public List<RestaurantRating> getmRestaurantRating() {
        return mRestaurantRating;
    }

    public void setmRestaurantRating(List<RestaurantRating> mRestaurantRating) {
        this.mRestaurantRating = mRestaurantRating;
    }

    public void calculateRating(){
        for (RestaurantRating restaurantRating : mRestaurantRating){
            mRating += (double)restaurantRating.getRating();
        }
        mRating /= mRestaurantRating.size();
    }
}
