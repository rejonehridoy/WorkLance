package com.example.worklance;

public class FeedbackInfo {

    private String dateTime,id,message,subject,username;

    public FeedbackInfo(String dateTime, String id, String message, String subject, String username) {
        this.dateTime = dateTime;
        this.id = id;
        this.message = message;
        this.subject = subject;
        this.username = username;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getSubject() {
        return subject;
    }

    public String getUsername() {
        return username;
    }
}
