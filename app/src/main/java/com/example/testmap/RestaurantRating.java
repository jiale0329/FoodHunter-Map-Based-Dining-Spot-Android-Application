package com.example.testmap;

public class RestaurantRating {
    private String ratingId;
    private String userId;
    private String diningSpotId;
    private int rating;

    public RestaurantRating(String ratingId, String userId, String diningSpotId, int rating) {
        this.ratingId = ratingId;
        this.userId = userId;
        this.diningSpotId = diningSpotId;
        this.rating = rating;
    }

    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDiningSpotId() {
        return diningSpotId;
    }

    public void setDiningSpotId(String diningSpotId) {
        this.diningSpotId = diningSpotId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
