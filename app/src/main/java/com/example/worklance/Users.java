package com.example.worklance;

public class Users {
    String Id;
    String UserName;
    String FullName;
    String Email;
    String Password;
    String Phone;
    String Gender;
    String UserType;
    String Address;
    String Latitude;
    String Longitude;
    String CreatedDate;
    String Rating;

    public Users(String id, String userName, String fullName, String email, String password, String phone, String gender, String userType, String address, String latitude, String longitude,String createdDate,String rating) {
        Id = id;
        UserName = userName;
        FullName = fullName;
        Email = email;
        Password = password;
        Phone = phone;
        Gender = gender;
        UserType = userType;
        Address = address;
        Latitude = latitude;
        Longitude = longitude;
        CreatedDate = createdDate;
        Rating = rating;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }
}
