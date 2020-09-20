package com.example.worklance;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class editProfile extends AppCompatActivity implements View.OnClickListener{

    private TextView fullNameTv,passwordTv,retypePasswordTv,addressTv,phoneTv,emailTv;
    private Button applyChangesbtn;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String name,pass,type,id,fullName,gender,phone,address,rating,createdDate,email,longitude,latitude;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbarEditProfile);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setTitle("Edit Profile");
        myChildToolbar.setTitleTextColor(0xFFFFFFFF);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //finding view by id
        fullNameTv = findViewById(R.id.fullnameEditProfile);
        passwordTv = findViewById(R.id.passwordEditProfile);
        retypePasswordTv = findViewById(R.id.repasswordEditProfile);
        phoneTv = findViewById(R.id.phoneEditProfile);
        applyChangesbtn = findViewById(R.id.applyChangesButton);
        //emailTv = findViewById(R.id.emailEditProfile);

        //load user info
        load_user_info();
        set_user_info_inEditText();

        applyChangesbtn.setOnClickListener(this);


    }

    private void set_user_info_inEditText() {
        fullNameTv.setText(fullName);
        phoneTv.setText(phone);
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

    public void update_user_info()
    {
        //getting the specified user reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Users").child(id);

        // online updated using firebase realtime database
        Users users = new Users(id,name,fullName,email,pass,phone,gender,type,address,latitude,longitude,createdDate,rating);
        dR.setValue(users);

        //online update using cloud firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection("Users");
        DocumentReference doc = collection.document(id);
        doc.update("fullName",fullName);
        doc.update("phone",phone);
        doc.update("password",pass);


        //offline update of sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("userFullName",fullName);
        editor.putString("userPhone",phone);
        editor.putString("userPassword",pass);
        //editor.putString("userEmail",Uemail);
        //editor.putString("userLongitude",Ulongtitude);
        //editor.putString("userLatitude",Ulatitude);
        editor.commit();

    }

    public void load_user_info()
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

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.applyChangesButton:
                if (check_empty_fields()){
                    Toast.makeText(getApplicationContext(),"Fields should not be empty",Toast.LENGTH_LONG).show();
                }else if(check_mismatched_password()){
                    Toast.makeText(getApplicationContext(),"Mismatched Password",Toast.LENGTH_LONG).show();
                }else{
                    if (check_internet_connection())
                    {
                        //get updated value from user
                        fullName = fullNameTv.getText().toString().trim();
                        if (!passwordTv.getText().toString().isEmpty()){
                            pass = passwordTv.getText().toString();
                        }
                        //address = addressTv.getText().toString().trim();
                        phone = phoneTv.getText().toString().trim();
                        //email = emailTv.getText().toString().trim();

                        //update user info
                        update_user_info();
                        Toast.makeText(getApplicationContext(),"Profile Updated Successfully",Toast.LENGTH_LONG).show();
                        Intent intentProfile = new Intent(getApplicationContext(), profile.class);
                        startActivity(intentProfile);
                        editProfile.this.finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"No internet Connection",Toast.LENGTH_LONG).show();
                    }

                }



                Intent intentEditProfile = new Intent(getApplicationContext(), editProfile.class);
                startActivity(intentEditProfile);
                break;
        }
    }

    public boolean check_empty_fields(){
        if (fullNameTv.getText().toString().isEmpty()   || phoneTv.getText().toString().isEmpty()){
            return true;
        }
        return false;
    }

    public boolean check_mismatched_password()
    {
        if (passwordTv.getText().toString().equals(retypePasswordTv.getText().toString())){
            return false;
        }
        return true;
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
}
