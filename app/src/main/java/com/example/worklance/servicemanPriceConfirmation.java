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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import javax.annotation.Nullable;

public class servicemanPriceConfirmation extends AppCompatActivity implements View.OnClickListener {
    String rid;
    // this is a checking string of work has started by another user or not,its value might be changed
    private final String WORK_IN_PROGRESS_STATUS = "in Progress";
    private TextView subjectTV,descriptionTV,userNameTV,contactTV,addressTV,requestDateTimeTV;
    private EditText priceET;
    private Button confrimPriceBtn;
    private List<Users> user_info = new ArrayList<>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String uid,userName,userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serviceman_price_confirmation);


        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbarServicemanPriceConfirmation);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setTitle("Serviceman Price Confirmation");
        myChildToolbar.setTitleTextColor(0xFFFFFFFF);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //find text views, edit text and button
        subjectTV = findViewById(R.id.SPC_subject);
        descriptionTV = findViewById(R.id.SPC_description);
        addressTV = findViewById(R.id.SPC_address);
        contactTV = findViewById(R.id.SPC_contactNo);
        requestDateTimeTV = findViewById(R.id.SPC_requestedTime);
        userNameTV = findViewById(R.id.SPC_userName);
        priceET = findViewById(R.id.SPC_price);
        confrimPriceBtn = findViewById(R.id.SPC_confrimBtn);

        load_user_info();

        rid = getIntent().getStringExtra("Rid");
        //Toast.makeText(getApplicationContext(),rid,Toast.LENGTH_LONG).show();

        check_valid_rid();
        confrimPriceBtn.setOnClickListener(this);
    }
    private void check_valid_rid()
    {
        // this method will check if the work has already started by other serviceman or not,if the work has started allready by other serviceman
        //then this page will not show anything ,it will just show a toast
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRequest = db.collection("Requests").document(rid);
        docRequest.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e!=null){
                    return;
                }
                if (documentSnapshot.exists()){
                    String allcoatedServiceman = documentSnapshot.getString("allocatedServiceMan");
                    String StartTime = documentSnapshot.getString("startTime");
                    String status = documentSnapshot.getString("status");
                    String subject = documentSnapshot.getString("subject");
                    String description = documentSnapshot.getString("description");
                    String uName = documentSnapshot.getString("userName");
                    String requestedTime = documentSnapshot.getString("requestedTime");

                    if (allcoatedServiceman != null && !allcoatedServiceman.isEmpty()){
                        if (allcoatedServiceman.equals(userName)){
                            Intent workInProgress = new Intent(getApplicationContext(),workConfirmation.class);
                            workInProgress.putExtra("Rid",rid);
                            finish();
                            startActivity(workInProgress);
                        }else{
                            Toast.makeText(getApplicationContext(),"Work has started by another serviceman",Toast.LENGTH_LONG).show();
                            Intent previous_page = new Intent(getApplicationContext(),incomingRequest.class);
                            finish();
                            startActivity(previous_page);
                        }

                    }
                    // this part of code is commented because of possibility of ambigous string value of 'status'
                    /*if (status.equals(WORK_IN_PROGRESS_STATUS)){
                        Toast.makeText(getApplicationContext(),"Work has started by another serviceman",Toast.LENGTH_LONG).show();
                        Intent previous_page = new Intent(getApplicationContext(),incomingRequest.class);
                        finish();
                        startActivity(previous_page);
                    }*/

                    //set up values in the text fields
                    subjectTV.setText(subject);
                    descriptionTV.setText(description);
                    requestDateTimeTV.setText(requestedTime);
                    set_address(uName);
                    load_user_info(uName);

                }
            }
        });

    }

    private void load_user_info(final String userName){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String user = documentSnapshot.getString("userName");
                    String contact = documentSnapshot.getString("phone");
                    String address = documentSnapshot.getString("address");
                    String fullName = documentSnapshot.getString("fullName");

                    if (user.equals(userName)){
                        userNameTV.setText(fullName);
                        contactTV.setText(contact);
                        addressTV.setText(address);
                        break;
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void set_address(String user){
        FirebaseFirestore.getInstance().collection("Users").whereEqualTo("userName",user)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                        addressTV.setText(snapshot.getString("address"));
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
        if (v.getId() == R.id.SPC_confrimBtn){
            if (priceET.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Price should not be empty", Toast.LENGTH_SHORT).show();
            }else{
                store_price(priceET.getText().toString());
                store_info_priceRequestNotification(priceET.getText().toString());
                Toast.makeText(getApplicationContext(),"Successfully Requested",Toast.LENGTH_LONG).show();
                Intent home = new Intent(this,servicemanHome.class);
                finish();
                startActivity(home);
            }
        }
    }
    private void store_price(String price)
    {
        // store price
        // Cloud Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference doc_uid = db.collection("ServiceRequestDetails").document("Details")
                .collection(rid).document(uid);
        doc_uid.update("requestPrice",price);
        doc_uid.update("notificationStatus","Requested");
        doc_uid.update("dateTime",get_current_Date());

        //Firebase Realtime database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("ServiceRequestDetails").child(rid).child(uid);
        ServiceRequest serviceRequest = new ServiceRequest(get_current_Date(),price,"Requested");
        dbRef.setValue(serviceRequest);
    }

    private void store_info_priceRequestNotification(final String price){
        //Fetch subject,description,username from request table and store it with proper information in price request notification table
        FirebaseFirestore.getInstance().collection("Requests").document(rid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String uname = documentSnapshot.getString("userName");
                            String subject = documentSnapshot.getString("subject");
                            String description = documentSnapshot.getString("description");

                            //Store the info in priceRequestNotification

                            //Firebase realtime database
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PriceRequestNotification");
                            String id = databaseReference.push().getKey();
                            PriceRequestNotification notification = new PriceRequestNotification(description,"Pending",id,price,rid,userName,uid,subject,uname);
                            databaseReference.child(id).setValue(notification);

                            //Cloud Firestore
                            FirebaseFirestore.getInstance().collection("PriceRequestNotification").document(id).set(notification);

                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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
    private String get_current_Date(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        Date date = new Date();
        return formatter.format(date);

    }

}
