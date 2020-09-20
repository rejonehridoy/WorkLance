package com.example.worklance;

public class ServiceRequest {
    //private String sid;
    private String rid;
    private String serviceman;
    private String dateTime;
    private String requestPrice;
    private String notificationStatus;
    private String subject;
    private String description;
    private String sid;

    public ServiceRequest(String dateTime, String requestPrice, String notificationStatus) {

        this.dateTime = dateTime;
        this.requestPrice = requestPrice;
        this.notificationStatus = notificationStatus;
    }

    public ServiceRequest(String rid,String dateTime,String requestPrice,String notificationStatus,String subject,String description){
        this.rid = rid;
        this.dateTime = dateTime;
        this.requestPrice = requestPrice;
        this.notificationStatus = notificationStatus;
        this.subject = subject;
        this.description = description;
    }
    // this constuctor is used in servicemanSelection.java
    public ServiceRequest(String rid,String sid,String dateTime,String requestPrice,String notificationStatus,String subject,String description){
        this.rid = rid;
        this.sid = sid;
        this.subject = subject;
        this.description = description;
        this.requestPrice = requestPrice;
        this.dateTime = dateTime;
        this.notificationStatus = notificationStatus;
    }

    public String getSid() {
        return sid;
    }

    public String getServiceman() {
        return serviceman;
    }
    public String getRid(){ return rid;}

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public void setServiceman(String serviceman) {
        this.serviceman = serviceman;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getRequestPrice() {
        return requestPrice;
    }

    public void setRequestPrice(String requestPrice) {
        this.requestPrice = requestPrice;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;

    }
}
