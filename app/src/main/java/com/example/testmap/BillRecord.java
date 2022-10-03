package com.example.testmap;

public class BillRecord {
    private String billRecordId;
    private String billRecordTitle;
    private double totalAmount;
    private String billDate;
    private String paidById;
    private String mealMate;
    private String userId;

    public BillRecord(String billRecordId, String billRecordTitle, double totalAmount, String billDate, String paidById, String mealMate, String userId) {
        this.billRecordId = billRecordId;
        this.billRecordTitle = billRecordTitle;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
        this.paidById = paidById;
        this.mealMate = mealMate;
        this.userId = userId;
    }

    public String getBillRecordId() {
        return billRecordId;
    }

    public void setBillRecordId(String billRecordId) {
        this.billRecordId = billRecordId;
    }

    public String getBillRecordTitle() {
        return billRecordTitle;
    }

    public void setBillRecordTitle(String billRecordTitle) {
        this.billRecordTitle = billRecordTitle;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaidById() {
        return paidById;
    }

    public void setPaidById(String paidById) {
        this.paidById = paidById;
    }

    public String getMealMate() {
        return mealMate;
    }

    public void setMealMate(String mealMate) {
        this.mealMate = mealMate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }
}
