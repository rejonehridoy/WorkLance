package com.example.worklance;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class profile extends AppCompatActivity implements View.OnClickListener {

    private Button editProfileButton;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private TextView fullNameTv,userNameTv,userTypeTv,emailTv,genderTv,phoneTv,addressTv,ratingTv;
    String name,pass,type,id,fullName,gender,phone,address,rating,createdDate,password,email,longitude,latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbarProfile);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setTitle("Profile");
        myChildToolbar.setTitleTextColor(0xFFFFFFFF);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        editProfileButton = findViewById(R.id.editProfileButton);
        //finding textview by id
        fullNameTv = findViewById(R.id.fullnameProfile);
        userNameTv = findViewById(R.id.userNameProfile);
        userTypeTv = findViewById(R.id.userTypeProfile);
        emailTv = findViewById(R.id.userEmailProfile);
        genderTv = findViewById(R.id.genderProfile);
        phoneTv = findViewById(R.id.phoneProfile);
        addressTv = findViewById(R.id.addressProfile);
        ratingTv = findViewById(R.id.ratingProfile);

        load_offline_data();
        load_online_data();

        editProfileButton.setOnClickListener(this);

    }

    public void load_online_data(){
        FirebaseFirestore.getInstance().collection("Users").document(id)
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null){
                            return;
                        }
                        if (documentSnapshot.exists()){
                            String addres = documentSnapshot.getString("address");
                            String rating = documentSnapshot.getString("rating");
                            if (!addres.equals(address)){
                                addressTv.setText("Address : "+addres);
                            }
                            ratingTv.setText("Rating : "+rating);
                        }
                    }
                });

    }
    public void load_offline_data()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Boolean isloggedin = sharedPreferences.getBoolean("isLoggedin", false);
        if (isloggedin && sharedPreferences.contains("userName") && sharedPreferences.contains("userPassword")) {

             name = sharedPreferences.getString("userName", "");
             pass = sharedPreferences.getString("userPassword", "");
             type = sharedPreferences.getString("userType", "");
             id = sharedPreferences.getString("userId","");
             fullName = sharedPreferences.getString("userFullName","");
             gender = sharedPreferences.getString("userGender","");
             phone = sharedPreferences.getString("userPhone","");
             address = sharedPreferences.getString("userAddress","");
             rating = sharedPreferences.getString("userRating","");
             createdDate = sharedPreferences.getString("userCreatedDate","");
             latitude = sharedPreferences.getString("userLatitude","");
             longitude = sharedPreferences.getString("userLongitude","");
             email = sharedPreferences.getString("userEmail","");

            //Setup info into text view
            fullNameTv.setText(fullName);
            userNameTv.setText(name);
            userTypeTv.setText(type);
            emailTv.setText("Email : "+email);
            genderTv.setText("Gender : "+gender);
            phoneTv.setText("Phone : "+phone);
            addressTv.setText("Address : "+address);
            ratingTv.setText("Rating : "+rating);


        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_corner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.aboutus:
                Intent intentAboutUs = new Intent(getApplicationContext(), aboutUs.class);
                startActivity(intentAboutUs);
                break;
            case R.id.workInProgress:

                break;
            case R.id.Appfeedback:
                Intent intentFeedBack = new Intent(getApplicationContext(), Appfeedback.class);
                startActivity(intentFeedBack);
                break;
            case R.id.signout:
                Toast.makeText(getApplicationContext(),"signout Successfully", Toast.LENGTH_LONG).show();
                SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedin",false);
                editor.commit();
                Intent intentSignout = new Intent(getApplicationContext(),SplashScreen.class);
                finish();
                startActivity(intentSignout);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editProfileButton:
                Intent intentEditProfile = new Intent(getApplicationContext(), editProfile.class);
                startActivity(intentEditProfile);
                break;
        }
    }
}
