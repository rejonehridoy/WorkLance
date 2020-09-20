package com.example.worklance;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Appfeedback extends AppCompatActivity implements View.OnClickListener{
    private EditText subjectET,messageET;
    private Button submitBtn;
    private String userName,uid,userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appfeedback);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbarFeedback);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setTitle("Feedback");
        myChildToolbar.setTitleTextColor(0xFFFFFFFF);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        load_user_info();

        subjectET = findViewById(R.id.AF_subject);
        messageET = findViewById(R.id.AF_message);
        submitBtn = findViewById(R.id.AF_submitbtn);
        submitBtn.setOnClickListener(this);



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

                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.AF_submitbtn){
            if (subjectET.getText().toString().isEmpty() || messageET.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(),"Field must not be empty",Toast.LENGTH_SHORT).show();
            }else{
                store_info();

            }
        }
    }

    private void store_info(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Feedback");

        String id = databaseReference.push().getKey();

        FeedbackInfo feedback = new FeedbackInfo(get_current_Date(),id,messageET.getText().toString().trim(),subjectET.getText().toString().trim(),
                userName);
        //Firebase realtime database
        databaseReference.child(id).setValue(feedback);
        //Cloud Firestore
        FirebaseFirestore.getInstance().collection("Feedback").document(id).set(feedback);

        Toast.makeText(getApplicationContext(),"Feedback sent successfully",Toast.LENGTH_SHORT).show();
    }

    private void load_user_info() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Boolean isloggedin = sharedPreferences.getBoolean("isLoggedin", false);
        if (isloggedin && sharedPreferences.contains("userName") && sharedPreferences.contains("userPassword")) {

            userName = sharedPreferences.getString("userName", "");
            uid = sharedPreferences.getString("userId", "");
            userType = sharedPreferences.getString("userType", "");

        }
    }

    private String get_current_Date() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        Date date = new Date();
        return formatter.format(date);

    }
}
