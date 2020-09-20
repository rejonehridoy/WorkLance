package com.example.worklance;

public class UserNotificaiton {
    private String rid,startNotification,cancelNotification,userName,servicemanName,sid,uid;
    public UserNotificaiton(String cancelNotification, String rid, String servicemanName, String sid, String startNotification, String userName, String uid){
        this.cancelNotification = cancelNotification;
        this.rid = rid;
        this.servicemanName = servicemanName;
        this.sid = sid;
        this.startNotification = startNotification;
        this.userName = userName;
        this.uid = uid;
    }

    public String getRid() {
        return rid;
    }

    public String getStartNotification() {
        return startNotification;
    }

    public String getCancelNotification() {
        return cancelNotification;
    }

    public String getUserName() {
        return userName;
    }

    public String getServicemanName() {
        return servicemanName;
    }

    public String getSid() {
        return sid;
    }

    public String getUid() {
        return uid;
    }
}
