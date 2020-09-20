package com.example.worklance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class servicemanHistory extends AppCompatActivity {

    private ListView listView;
    private CustomAdapter customAdapter;
    DatabaseReference databaseReference;
    String userName,userType,uid;

    List<RequestInfo> info;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serviceman_history);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbarServicemanHistory);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setTitle("Serviceman History");
        myChildToolbar.setTitleTextColor(0xFFFFFFFF);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.list_view_servicemanHistory);
        info = new ArrayList<RequestInfo>();
        load_user_info();
        // Select * from Requests where userName = @userName
        Query query = FirebaseDatabase.getInstance().getReference("Requests")
                .orderByChild("allocatedServiceMan")
                .equalTo(userName);

        query.addListenerForSingleValueEvent(valueEventListener);

        //init();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = info.get(position).Rid;
                //Toast.makeText(getApplicationContext(),value,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(servicemanHistory.this,HistoryDetails.class);
                intent.putExtra("Rid",value);
                startActivity(intent);
            }
        });

    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            info.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
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
                    RequestInfo requestInfo = new RequestInfo(rid,subject,decription,userName,requestedTime,startTime,
                            endTime,workerType,price,allocatedServiceMan,status,rating,comment,notificationStatus);
                    info.add(requestInfo);
                }
                //adapter.notifyDataSetChanged();
            }
            Collections.reverse(info);
            init();
            //Toast.makeText(getApplicationContext(),"Size inside loop : "+info.size(),Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void load_user_info() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Boolean isloggedin = sharedPreferences.getBoolean("isLoggedin", false);
        if (isloggedin && sharedPreferences.contains("userName") && sharedPreferences.contains("userPassword")) {

            userName = sharedPreferences.getString("userName", "");
            uid = sharedPreferences.getString("userId", "");
            userType = sharedPreferences.getString("userType", "");

        }
    }


    public void init(){
        //listView = (ListView)findViewById(R.id.list_view);
        customAdapter = new CustomAdapter(servicemanHistory.this,R.layout.custom_list_view,info,userType);
        listView.setAdapter(customAdapter);
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


}

