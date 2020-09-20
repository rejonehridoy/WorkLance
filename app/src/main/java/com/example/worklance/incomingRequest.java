package com.example.worklance;

import androidx.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

public class incomingRequest extends AppCompatActivity {


    private ListView listView;
    private customListViewForIncomingRequest customAdapter;
    private List<RequestInfo> info = new ArrayList<>();
    private List<ServiceRequest> serviceRequestsInfo = new ArrayList<>();
    private String userName, uid, userType;
    private TextView text;
    String data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_request);
        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbarIncomingRequest);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setTitle("Incoming Request");
        myChildToolbar.setTitleTextColor(0xFFFFFFFF);
        listView = (ListView) findViewById(R.id.list_view_incomingRequest);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        text = findViewById(R.id.testTextView);

        load_user_info();
        if (userType.equals("User")){
            Toast.makeText(this, "This activity is not available for serviceman", Toast.LENGTH_SHORT).show();
            finish();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = serviceRequestsInfo.get(position).getRid();
                //Toast.makeText(getApplicationContext(),value,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(incomingRequest.this,servicemanPriceConfirmation.class);
                intent.putExtra("Rid",value);
                startActivity(intent);
            }
        });
        //init();

    }

    @Override
    protected void onStart() {
        super.onStart();
        fetch_request_info();

    }

    public void init() {
        //Collections.reverse(serviceRequestsInfo);
        //Toast.makeText(getApplicationContext(),"init method called",Toast.LENGTH_LONG).show();

        customAdapter = new customListViewForIncomingRequest(incomingRequest.this, R.layout.incoming_request_list_view, serviceRequestsInfo);
        listView.setAdapter(customAdapter);
    }

    private void fetch_request_info() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection("Requests");
        collection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                //String data = "";
                serviceRequestsInfo.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String rid = documentSnapshot.getString("rid");
                    String subject = documentSnapshot.getString("subject");
                    String description = documentSnapshot.getString("description");
                    String allocatedServiceman = documentSnapshot.getString("allocatedServiceMan");
                    String comment = documentSnapshot.getString("comment");
                    String endTime = documentSnapshot.getString("endTime");
                    String notificationStatus = documentSnapshot.getString("notificationStatus");
                    String price = documentSnapshot.getString("price");
                    String rating = documentSnapshot.getString("rating");
                    String requestedTime = documentSnapshot.getString("requestedTime");
                    String startTime = documentSnapshot.getString("startTime");
                    String status = documentSnapshot.getString("status");
                    String userName = documentSnapshot.getString("userName");
                    String workType = documentSnapshot.getString("workerType");
                    RequestInfo requestInfo = new RequestInfo(rid, subject, description, userName, requestedTime, startTime, endTime, workType
                            , price, allocatedServiceman, status, rating, comment, notificationStatus);
                    info.add(requestInfo);
                    check_firestore_data(rid,subject,description);
                    //Toast.makeText(getApplicationContext(), "IR : " + rid, Toast.LENGTH_LONG).show();
                }
                //text.setText(data);
                //Toast.makeText(getApplicationContext(),"Size : "+serviceRequestsInfo.size(),Toast.LENGTH_LONG).show();
                //init();

            }
        });



    }

    private void check_firestore_data(String rid, final String subject, final String description) {
        final String Rid = rid;

        //fetch_request_info();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection("ServiceRequestDetails").document("Details").collection(rid);
        DocumentReference doc = collection.document(uid);

        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String dateTime = documentSnapshot.getString("dateTime");
                    String notificationStatus = documentSnapshot.getString("notificationStatus");
                    String requestPrice = documentSnapshot.getString("requestPrice");
                    //Toast.makeText(getApplicationContext(), "inside method : " + Rid, Toast.LENGTH_LONG).show();
                    //data += "Rid : " + Rid + "\nStatus : " + notificationStatus + "\nDate: " + requestPrice + "\n\n";
                    if (!notificationStatus.equals("Cancelled") && !notificationStatus.equals("Started") && !notificationStatus.equals("Completed")){
                        ServiceRequest request = new ServiceRequest(Rid, dateTime, requestPrice, notificationStatus,subject,description);
                        serviceRequestsInfo.add(request);
                    }


                }

                //Toast.makeText(getApplicationContext(),"size : "+serviceRequestsInfo.size(),Toast.LENGTH_LONG).show();
                /*if (serviceRequestsInfo.size() > 0){

                    init();
                }*/
                if (serviceRequestsInfo.size()>0){
                    Collections.sort(serviceRequestsInfo, new Comparator<ServiceRequest>() {
                        @Override
                        public int compare(ServiceRequest o1, ServiceRequest o2) {
                            return o2.getDateTime().compareTo(o1.getDateTime());
                        }
                    });
                    init();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        /*for (ServiceRequest i : serviceRequestsInfo) {
            data += "Rid : " + i.getRid() + "\nStatus : " + i.getNotificationStatus() + "\nDate: " + i.getDateTime() + "\n\n";
        }
        text.setText(data);*/


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

    private void load_user_info() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Boolean isloggedin = sharedPreferences.getBoolean("isLoggedin", false);
        if (isloggedin && sharedPreferences.contains("userName") && sharedPreferences.contains("userPassword")) {

            userName = sharedPreferences.getString("userName", "");
            uid = sharedPreferences.getString("userId", "");
            userType = sharedPreferences.getString("userType", "");

        }
    }
}
