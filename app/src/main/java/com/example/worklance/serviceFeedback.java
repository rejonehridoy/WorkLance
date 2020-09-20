package com.example.worklance;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;

public class serviceFeedback extends AppCompatActivity implements View.OnClickListener {

    Button submitButton;
    private TextView fullNameTV,phoneTV,ratingTV;
    private EditText ratingET,feedbackMessageET;
    String Rid,Uname,Sname,Rating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_feedback);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbarServiceFeedback);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setTitle("Service Feedback");
        myChildToolbar.setTitleTextColor(0xFFFFFFFF);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        submitButton = findViewById(R.id.SF_submitButton);
        fullNameTV = findViewById(R.id.SF_name);
        phoneTV = findViewById(R.id.SF_phone);
        ratingTV = findViewById(R.id.SF_rating);
        ratingET = findViewById(R.id.SF_ratingET);
        feedbackMessageET = findViewById(R.id.SF_feedbackET);

        Intent intent = getIntent();
        Rid = intent.getStringExtra("Rid");
        Uname = intent.getStringExtra("uname");
        Sname = intent.getStringExtra("sname");
        if(Rid != null && !Rid.isEmpty()){
            load_serviceman_info();
        }


        submitButton.setOnClickListener(this);

    }

    private void load_serviceman_info(){
        FirebaseFirestore.getInstance().collection("Users").whereEqualTo("userName",Sname)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                        String fullName = snapshot.getString("fullName");
                        String phone = snapshot.getString("phone");
                        Rating = snapshot.getString("rating");
                        //set up data in textviews
                        fullNameTV.setText(fullName);
                        phoneTV.setText(phone);
                        ratingTV.setText(Rating);
                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
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
        switch (v.getId()) {
            case R.id.SF_submitButton:
                if (ratingET.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Rating field should not be empty",Toast.LENGTH_SHORT).show();
                }else{
                    update_info();
                    Intent home = new Intent(getApplicationContext(), userHome.class);
                    finish();
                    startActivity(home);
                }

                break;
        }
    }

    private void update_info(){
        final Double rating = Double.parseDouble(Rating);
        final Double currentRating = Double.parseDouble(ratingET.getText().toString());
        FirebaseFirestore.getInstance().collection("UserLog").document(Sname)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String noOfWorkCompleted = documentSnapshot.getString("noOfWorkCompleted");
                String id = documentSnapshot.getString("id");
                Double no = Double.parseDouble(noOfWorkCompleted);
                DecimalFormat df = new DecimalFormat("#.##");
                Double finalRating = ((rating*(no-1)) + currentRating)/no;
                String finalRating_string = df.format(finalRating);

                //update rating in Users table
                //cloud Firestore
                FirebaseFirestore.getInstance().collection("Users").document(id).update("rating",finalRating_string);
                //Firebase realtime database
                FirebaseDatabase.getInstance().getReference("Users").child(id).child("rating").setValue(finalRating_string);

                //update rating and comment in Request table

                FirebaseFirestore.getInstance().collection("Requests").document(Rid).update("rating",ratingET.getText().toString());
                FirebaseDatabase.getInstance().getReference("Requests").child(Rid).child("rating").setValue(ratingET.getText().toString());
                if (!feedbackMessageET.getText().toString().isEmpty()){
                    FirebaseFirestore.getInstance().collection("Requests").document(Rid).update("comment",feedbackMessageET.getText().toString().trim());
                    FirebaseDatabase.getInstance().getReference("Requests").child(Rid).child("comment").setValue(feedbackMessageET.getText().toString().trim());
                }
            }
        });
    }
}
