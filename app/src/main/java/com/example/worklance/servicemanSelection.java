package com.example.worklance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class servicemanSelection extends AppCompatActivity {


    private ListView listView;
    private CustomAdapterforButtonListView customAdapter;
    private double circleRadius=5000;
    private float distance;
    private String uid,userName,userType;
    private List<RequestInfo> info = new ArrayList<>();
    private List<ServiceRequest> service_info = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serviceman_selection);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbarServicemanSelection);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setTitle("Serviceman Selection");
        myChildToolbar.setTitleTextColor(0xFFFFFFFF);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        load_user_info();




    }

    @Override
    protected void onStart() {
        super.onStart();
        fetch_request_info();
    }

    private void fetch_request_info()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference requestCollection = db.collection("Requests");
        requestCollection.whereEqualTo("status","Requested")
                .whereEqualTo("userName",userName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                info.clear();
                service_info.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String userName = documentSnapshot.getString("userName");
                    String status = documentSnapshot.getString("status");
                    String rid = documentSnapshot.getString("rid");
                    String allocatedServiceMan = documentSnapshot.getString("allocatedServiceMan");
                    String comment = documentSnapshot.getString("comment");
                    String description = documentSnapshot.getString("description");
                    String endTime = documentSnapshot.getString("endTime");
                    String notificationStatus = documentSnapshot.getString("notificationStatus");
                    String price = documentSnapshot.getString("price");
                    String rating = documentSnapshot.getString("rating");
                    String requestedTime = documentSnapshot.getString("requestedTime");
                    String startTime = documentSnapshot.getString("startTime");
                    String subject = documentSnapshot.getString("subject");
                    String workerType = documentSnapshot.getString("workerType");
                    RequestInfo requestInfo = new RequestInfo(rid,subject,description,userName,requestedTime,startTime,endTime
                            ,workerType,price,allocatedServiceMan,status,rating,comment,notificationStatus);
                    info.add(requestInfo);

                }
                for (RequestInfo r : info){
                    //Toast.makeText(getApplicationContext(),"rid : "+r.Rid+" userName : "+r.getUserName()+" status : "+r.Status,Toast.LENGTH_LONG).show();
                    load_service_request_by_price_confrimation(r.Rid,r.Subject,r.Description);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void load_service_request_by_price_confrimation(final String rid, final String subject, final String description){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ServiceRequestDetails").document("Details").collection(rid).whereEqualTo("notificationStatus","Requested")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String servicemanId = documentSnapshot.getId();
                    String notificationStatus = documentSnapshot.getString("notificationStatus");
                    String price = documentSnapshot.getString("requestPrice");
                    String dateTime = documentSnapshot.getString("dateTime");
                    ServiceRequest request = new ServiceRequest(rid,servicemanId,dateTime,price,notificationStatus,subject,description);
                    service_info.add(request);

                }
                /*for (ServiceRequest s : service_info){
                    Toast.makeText(getApplicationContext(),"Rid : "+s.getRid()+" Sid : "+s.getSid()+" status : "+s.getNotificationStatus(),Toast.LENGTH_LONG).show();
                }*/
                if (service_info.size() >0){
                    Collections.sort(service_info, new Comparator<ServiceRequest>() {
                        @Override
                        public int compare(ServiceRequest o1, ServiceRequest o2) {
                            return o2.getDateTime().compareTo(o1.getDateTime());
                        }
                    });
                    init(service_info);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ACTIVITY",e.toString());
            }
        });
    }
    public void init(List<ServiceRequest> requests){

        /*Location location = new Location("servicemanLocation");
        location.setLatitude(23.758429);
        location.setLongitude(90.373821);

        Location locationB = new Location("userLocation");
        locationB.setLatitude(23.751191);
        locationB.setLongitude(90.390505);

        distance = locationB.distanceTo(location);  //find distance between user and serviceman
        String user = null;

        if(distance > circleRadius){               // checking serviceman is within circle radius or not
            user = "Outside Circle";
        }
        else
            user = "inside circle";*/

        listView = (ListView)findViewById(R.id.list_view_servicemanSelection);
        customAdapter = new CustomAdapterforButtonListView(servicemanSelection.this,R.layout.row,requests);
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
                SharedPreferences.Editor editor;
                Toast.makeText(getApplicationContext(),"signout Successfully", Toast.LENGTH_LONG).show();
                SharedPreferences sp = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                editor = sp.edit();
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

    private void load_user_info()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Boolean isloggedin = sharedPreferences.getBoolean("isLoggedin", false);
        if (isloggedin && sharedPreferences.contains("userName") && sharedPreferences.contains("userPassword")) {

            userName = sharedPreferences.getString("userName", "");
            uid = sharedPreferences.getString("userId", "");
            userType = sharedPreferences.getString("userType", "");

        }
    }
}
