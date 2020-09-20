package com.example.worklance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SplashScreen extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout relativeLayout01,relativeLayout02;
    private Button loginButton,signupButton;
    private ProgressBar progressBarSignin;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            relativeLayout01.setVisibility(View.VISIBLE);
            relativeLayout02.setVisibility(View.VISIBLE);


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);



        relativeLayout01 = findViewById(R.id.rellay1);
        relativeLayout02 = findViewById(R.id.rellay2);


        loginButton = findViewById(R.id.loginButton);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signUpButton);
        progressBarSignin=findViewById(R.id.signInProgressBar);


        loginButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);

        handler.postDelayed(runnable,1000);

        check_previous_login();



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginButton:
                Intent intentLogin = new Intent(getApplicationContext(), Login.class);
                finish();
                startActivity(intentLogin);
                break;

            case R.id.signUpButton:
                Intent intentSignin = new Intent(getApplicationContext(), Signin.class);
                finish();
                startActivity(intentSignin);
                break;


        }

    }

    public void check_previous_login(){
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
    }
}
