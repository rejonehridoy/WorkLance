package com.example.worklance;

public class RequestInfo {
    String Rid,Subject,Description,UserName,RequestedTime,StartTime,EndTime,WorkerType,Price,AllocatedServiceMan,Status,Rating,Comment,NotificationStatus;

    public RequestInfo(String rid, String subject, String description, String userName, String requestedTime, String startTime, String endTime, String workerType, String price, String allocatedServiceMan, String status, String rating, String comment, String notificationStatus) {
        Rid = rid;
        Subject = subject;
        Description = description;
        UserName = userName;
        RequestedTime = requestedTime;
        StartTime = startTime;
        EndTime = endTime;
        WorkerType = workerType;
        Price = price;
        AllocatedServiceMan = allocatedServiceMan;
        Status = status;
        Rating = rating;
        Comment = comment;
        NotificationStatus = notificationStatus;
    }

    public String getRid() {
        return Rid;
    }

    public void setRid(String rid) {
        Rid = rid;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getRequestedTime() {
        return RequestedTime;
    }

    public void setRequestedTime(String requestedTime) {
        RequestedTime = requestedTime;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getWorkerType() {
        return WorkerType;
    }

    public void setWorkerType(String workerType) {
        WorkerType = workerType;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getAllocatedServiceMan() {
        return AllocatedServiceMan;
    }

    public void setAllocatedServiceMan(String allocatedServiceMan) {
        AllocatedServiceMan = allocatedServiceMan;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getNotificationStatus() {
        return NotificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        NotificationStatus = notificationStatus;
    }
}
