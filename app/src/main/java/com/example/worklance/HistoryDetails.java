package com.example.worklance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryDetails extends AppCompatActivity {
    String rid,serviceman="";
    List<RequestInfo> info;
    private TextView userORServicemanTv,subjectTv,descriptionTv,requestedDateTv,startDateTv,endDateTv,serviceManTv,contactTv,ratingTv,priceTv,commentTv,statusTv;
    DatabaseReference databaseReference;
    Query query,query1;
    private String userName,uid,userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbarWorkInProgress);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setTitle("History Details");
        myChildToolbar.setTitleTextColor(0xFFFFFFFF);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //finding view by id
        subjectTv = findViewById(R.id.historysubject);
        descriptionTv = findViewById(R.id.historyDescription);
        requestedDateTv = findViewById(R.id.historyRequest);
        startDateTv = findViewById(R.id.historyStartWorkDate);
        endDateTv = findViewById(R.id.historyEndWorkDate);
        serviceManTv = findViewById(R.id.historyAllocatedServiceMan);
        contactTv = findViewById(R.id.historyWorkerContactNo);
        priceTv = findViewById(R.id.historyPrice);
        ratingTv = findViewById(R.id.HistoryRating);
        commentTv = findViewById(R.id.HistoryComment);
        statusTv = findViewById(R.id.HistoryStatus);
        userORServicemanTv = findViewById(R.id.HistoryDetailsUserorServiceMan);
        info = new ArrayList<RequestInfo>();
        rid = getIntent().getStringExtra("Rid");
        //Toast.makeText(getApplicationContext(),rid,Toast.LENGTH_LONG).show();
        load_user_info();
        query = FirebaseDatabase.getInstance().getReference("Requests")
                .orderByChild("rid")
                .equalTo(rid);

        load_history_info();
    }

    private void load_user_info(String Serviceman) {
        query1 = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("userName")
                .equalTo(Serviceman);

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String phone = snapshot.child("phone").getValue().toString();
                    //Toast.makeText(getApplicationContext(),phone,Toast.LENGTH_LONG).show();
                    contactTv.setText(phone);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    private void load_history_info() {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                info.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String allocatedServiceMan = snapshot.child("allocatedServiceMan").getValue().toString();
                    String comment = snapshot.child("comment").getValue().toString();
                    String decription = snapshot.child("description").getValue().toString();
                    String endTime = snapshot.child("endTime").getValue().toString();
                    String notificationStatus = snapshot.child("notificationStatus").getValue().toString();
                    String price = snapshot.child("price").getValue().toString();
                    String rating = snapshot.child("rating").getValue().toString();
                    String requestedTime = snapshot.child("requestedTime").getValue().toString();
                    String rid = snapshot.child("rid").getValue().toString();
                    String startTime = snapshot.child("startTime").getValue().toString();
                    String status = snapshot.child("status").getValue().toString();
                    String subject = snapshot.child("subject").getValue().toString();
                    String user = snapshot.child("userName").getValue().toString();
                    String workerType = snapshot.child("workerType").getValue().toString();
                    //Toast.makeText(getApplicationContext(),"Rid : "+rid,Toast.LENGTH_LONG).show();
                    RequestInfo requestInfo = new RequestInfo(rid,subject,decription,user,requestedTime,startTime,
                            endTime,workerType,price,allocatedServiceMan,status,rating,comment,notificationStatus);
                    info.add(requestInfo);

                }
                // set up in the text fields

                for(RequestInfo list: info){
                    subjectTv.setText(list.Subject);
                    descriptionTv.setText(list.Description);
                    requestedDateTv.setText(list.RequestedTime);

                    if(userType.equals("User")){
                        if(list.AllocatedServiceMan.isEmpty() && list.Rating.isEmpty()){
                            startDateTv.setText("N/A");
                            endDateTv.setText("N/A");
                            serviceManTv.setText("N/A");
                            //contactTv.setText(list.Description);
                            contactTv.setText("N/A");
                            priceTv.setText("N/A");
                            ratingTv.setText("N/A");
                            commentTv.setText("N/A");
                            statusTv.setText("N/A");
                        }
                        else{
                            startDateTv.setText(list.StartTime);
                            endDateTv.setText(list.EndTime);
                            serviceManTv.setText(list.AllocatedServiceMan);
                            serviceman = list.AllocatedServiceMan;
                            load_user_info(list.AllocatedServiceMan);
                            priceTv.setText(list.Price);
                            ratingTv.setText(list.Rating);
                            commentTv.setText(list.Comment);
                            statusTv.setText(list.Status);
                        }
                    }
                    else{
                        userORServicemanTv.setText("Client Name:");
                        startDateTv.setText(list.StartTime);
                        endDateTv.setText(list.EndTime);
                        serviceManTv.setText(list.UserName);
                        serviceman = list.UserName;
                        load_user_info(list.UserName);
                        priceTv.setText(list.Price);
                        ratingTv.setText(list.Rating);
                        commentTv.setText(list.Comment);
                        statusTv.setText(list.Status);
                    }


                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
