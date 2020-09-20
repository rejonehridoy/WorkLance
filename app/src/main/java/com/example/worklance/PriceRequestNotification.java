package com.example.worklance;

public class PriceRequestNotification {
    private String description,notification,pid,price,rid,servicemanName,sid,subject,username;

    public PriceRequestNotification(String description, String notification,String pid, String price, String rid, String servicemanName, String sid, String subject, String username) {
        this.description = description;
        this.notification = notification;
        this.price = price;
        this.pid = pid;
        this.rid = rid;
        this.servicemanName = servicemanName;
        this.sid = sid;
        this.subject = subject;
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public String getPid() {
        return pid;
    }

    public String getNotification() {
        return notification;
    }

    public String getPrice() {
        return price;
    }

    public String getRid() {
        return rid;
    }

    public String getServicemanName() {
        return servicemanName;
    }

    public String getSid() {
        return sid;
    }

    public String getSubject() {
        return subject;
    }

    public String getUsername() {
        return username;
    }
}
