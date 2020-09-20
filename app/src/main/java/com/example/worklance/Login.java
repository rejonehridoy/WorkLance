package com.example.worklance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private Button loginButton;
    private EditText userEt,passwordEt;
    List<Users> usersList;
    DatabaseReference databaseReference;
    SharedPreferences.Editor editor;
    String Uid,Uname,Utype,Ufullname,Uphone,Uemail,UcreatedDate,Urating,Uaddress,Ugender,Upassword,Ulongtitude,Ulatitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //check_previous_login();

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbarLogin);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setTitle("Login");
        myChildToolbar.setTitleTextColor(0xFFFFFFFF);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        loginButton = findViewById(R.id.LoginIntoButton);
        loginButton.setOnClickListener(this);

        //finding Textview
        userEt = findViewById(R.id.userEditTextview);
        passwordEt = findViewById(R.id.passwordEditTextview);
        usersList = new ArrayList<>();

        // get database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");
        fetch_user_data();




    }

    public void fetch_user_data(){
        if (check_internet_connection()){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    usersList.clear();
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        String id = postSnapshot.child("id").getValue().toString();
                        String userName = postSnapshot.child("userName").getValue().toString();
                        String password = postSnapshot.child("password").getValue().toString();
                        String userType = postSnapshot.child("userType").getValue().toString();
                        String fullName = postSnapshot.child("fullName").getValue().toString();
                        String gender = postSnapshot.child("gender").getValue().toString();
                        String email = postSnapshot.child("email").getValue().toString();
                        String address = postSnapshot.child("address").getValue().toString();
                        String phone = postSnapshot.child("phone").getValue().toString();
                        String latitude = postSnapshot.child("latitude").getValue().toString();
                        String longitude = postSnapshot.child("longitude").getValue().toString();
                        String createdDate = postSnapshot.child("createdDate").getValue().toString();
                        String rating = postSnapshot.child("rating").getValue().toString();
                        //adding artist to the list
                        Users users = new Users(id,userName,fullName,email,password,phone,gender,userType,address,
                                latitude,longitude,createdDate,rating);
                        usersList.add(users);
                        //Toast.makeText(getApplicationContext(),"Fetching Data",Toast.LENGTH_LONG).show();


                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();
        }
    }

    /*public void check_previous_login(){
        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Boolean isloggedin = sharedPreferences.getBoolean("isLoggedin", false);
        if (isloggedin && sharedPreferences.contains("userName") && sharedPreferences.contains("userPassword")) {

            String name = sharedPreferences.getString("userName", "");
            String pass = sharedPreferences.getString("userPassword", "");
            String type = sharedPreferences.getString("userType", "");

            if (type.equals("User")) {
                Intent intentUserHome = new Intent(getApplicationContext(), userHome.class);
                finish();
                startActivity(intentUserHome);

            } else {
                Intent intentserviceManHome = new Intent(getApplicationContext(), servicemanHome.class);
                finish();
                startActivity(intentserviceManHome);

            }

        }
    }*/

    public void check_offline_login() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("userName") && sharedPreferences.contains("userPassword")) {
            Boolean isloggedin = sharedPreferences.getBoolean("isLoggedin", false);
            String name = sharedPreferences.getString("userName", "");
            String pass = sharedPreferences.getString("userPassword", "");
            String type = sharedPreferences.getString("userType", "");
            if (name.equals(userEt.getText().toString()) && pass.equals(passwordEt.getText().toString())) {
                if (type.equals("User")) {
                    Intent intentUserHome = new Intent(getApplicationContext(), userHome.class);
                    finish();
                    startActivity(intentUserHome);

                } else {
                    Intent intentserviceManHome = new Intent(getApplicationContext(), servicemanHome.class);
                    finish();
                    startActivity(intentserviceManHome);

                }
            }
        }
    }

    public String check_idPassword(){
        // checking the username and password

        if (usersList.isEmpty()){
            //Toast.makeText(getApplicationContext(),"List are empty",Toast.LENGTH_LONG).show();
            check_offline_login();
        }
        for (Users user: usersList){

            if (user.UserName.equals(userEt.getText().toString().trim()) && user.Password.equals(passwordEt.getText().toString().trim()))
            {
                Uid = user.Id;
                Uname = user.UserName;
                Utype = user.UserType;
                Ufullname = user.FullName;
                Uemail = user.Email;
                UcreatedDate = user.CreatedDate;
                Uphone = user.Phone;
                Uaddress = user.Address;
                Ugender = user.Gender;
                Urating = user.Rating;
                Upassword = user.Password;
                Ulongtitude = user.Longitude;
                Ulatitude = user.Latitude;
                return user.UserType;
                //Toast.makeText(this,"Login Successful",Toast.LENGTH_LONG).show();
            }
        }

        return "";

    }

    public boolean check_empty_textField(){
        if(userEt.getText().toString().isEmpty() || passwordEt.getText().toString().isEmpty()){
            return true;
        }
        return false;
    }

    public boolean check_internet_connection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                //Toast.makeText(this,"Wifi Connected",Toast.LENGTH_LONG).show();

            }else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                //Toast.makeText(this,"Mobile Network Connected",Toast.LENGTH_LONG).show();
            }
            return true;
        }else{
            //Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.LoginIntoButton:
                if (check_empty_textField()){
                    Toast.makeText(this,"Username and password should not empty",Toast.LENGTH_LONG).show();
                }else{
                    // login authentication
                    if (check_idPassword().isEmpty()){
                        Toast.makeText(this,"Username or password is incorrect",Toast.LENGTH_LONG).show();
                    }else{
                        if (check_idPassword().equals("User")){
                            // save data in sharedPreference
                            SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails",Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedin",true);
                            editor.putString("userName",Uname);
                            editor.putString("userId",Uid);
                            editor.putString("userGender",Ugender);
                            editor.putString("userFullName",Ufullname);
                            editor.putString("userPhone",Uphone);
                            editor.putString("userType",Utype);
                            editor.putString("userAddress",Uaddress);
                            editor.putString("userRating",Urating);
                            editor.putString("userCreatedDate",UcreatedDate);
                            editor.putString("userPassword",Upassword);
                            editor.putString("userEmail",Uemail);
                            editor.putString("userLongitude",Ulongtitude);
                            editor.putString("userLatitude",Ulatitude);
                            editor.commit();

                            Intent intentUserHome = new Intent(getApplicationContext(), userHome.class);
                            finish();
                            startActivity(intentUserHome);
                        }else{
                            // save data in sharedPreference
                            SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails",Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedin",true);
                            editor.putString("userName",Uname);
                            editor.putString("userId",Uid);
                            editor.putString("userGender",Ugender);
                            editor.putString("userFullName",Ufullname);
                            editor.putString("userPhone",Uphone);
                            editor.putString("userType",Utype);
                            editor.putString("userAddress",Uaddress);
                            editor.putString("userRating",Urating);
                            editor.putString("userCreatedDate",UcreatedDate);
                            editor.putString("userEmail",Uemail);
                            editor.putString("userPassword",Upassword);
                            editor.putString("userLongitude",Ulongtitude);
                            editor.putString("userLatitude",Ulatitude);
                            editor.commit();

                            Intent intentServicemanHome = new Intent(getApplicationContext(), servicemanHome.class);
                            finish();
                            startActivity(intentServicemanHome);
                        }
                    }
                }

                break;

        }
    }

    public String get_current_Date(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        Date date = new Date();
        return formatter.format(date);

    }
}
