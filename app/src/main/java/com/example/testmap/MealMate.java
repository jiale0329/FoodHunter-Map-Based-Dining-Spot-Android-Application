package com.example.testmap;

public class MealMate {
    private String contactId;
    private String contactName;
    private String contactPhoneNumber;
    private double amountToReceive;
    private double amountToPay;

    public MealMate(String contactId, String name, String phoneNumber) {
        this.contactId = contactId;
        this.contactName = name;
        this.contactPhoneNumber = phoneNumber;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public double getAmountToReceive() {
        return amountToReceive;
    }

    public void setAmountToReceive(double amountToReceive) {
        this.amountToReceive = amountToReceive;
    }

    public double getAmountToPay() {
        return amountToPay;
    }

    public void setAmountToPay(double amountToPay) {
        this.amountToPay = amountToPay;
    }
}
