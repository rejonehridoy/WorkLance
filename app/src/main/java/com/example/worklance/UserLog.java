package com.example.worklance;

public class UserLog {
    private String id,lastVisit,noOfWorkCompleted,totalAmount,username;

    public UserLog(String id, String lastVisit, String noOfWorkCompleted, String totalAmount, String username) {
        this.id = id;
        this.lastVisit = lastVisit;
        this.noOfWorkCompleted = noOfWorkCompleted;
        this.totalAmount = totalAmount;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getLastVisit() {
        return lastVisit;
    }

    public String getNoOfWorkCompleted() {
        return noOfWorkCompleted;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getUsername() {
        return username;
    }
}
