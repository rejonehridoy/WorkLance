package com.example.worklance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Nullable;

public class workInProgress extends AppCompatActivity implements View.OnClickListener {

    private Button completeButton;
    private TextView subjectTV, descriptionTV, requestDateTV, servicemanNameTV, contactTV, ratingTV, priceTV, startDateTV, statusTV;
    String uid, userName, userType, Rid, Sid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_in_progress);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbarWorkInProgress);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setTitle("Work In Progress");
        myChildToolbar.setTitleTextColor(0xFFFFFFFF);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        load_user_info();
        if (!userType.equals("User")) {
            Toast.makeText(this, "This activity is not available for serviceman", Toast.LENGTH_SHORT).show();
            finish();
        }

        //find view by id
        subjectTV = findViewById(R.id.WIP_subject);
        descriptionTV = findViewById(R.id.WIP_description);
        requestDateTV = findViewById(R.id.WIP_requestDateTime);
        servicemanNameTV = findViewById(R.id.WIP_servicemanName);
        contactTV = findViewById(R.id.WIP_ContactNo);
        ratingTV = findViewById(R.id.WIP_rating);
        priceTV = findViewById(R.id.WIP_price);
        startDateTV = findViewById(R.id.WIP_startTime);
        statusTV = findViewById(R.id.WIP_status);

        completeButton = findViewById(R.id.WIP_CompleteButton);
        completeButton.setOnClickListener(this);

        //get data from intent... get from CustomAdapterforButtonListView.java
        Intent intent = getIntent();
        Rid = intent.getStringExtra("Rid");
        Sid = intent.getStringExtra("Sid");
        String Price = intent.getStringExtra("Price");
        if (Rid != null && !Rid.isEmpty()) {

            SharedPreferences sharedPreferences = getSharedPreferences("WorkDetails", Context.MODE_PRIVATE);
            Boolean isProgress = sharedPreferences.getBoolean("isProgress", false);
            if (!isProgress) {
                fetch_online_data(Rid, Sid);
            }
            if (check_offline_data()) {
                set_offline_data();
            } else {
                fetch_online_data(Rid, Sid);
            }

        } else {
            //Toast.makeText(this, "from external Activity", Toast.LENGTH_SHORT).show();
            if (check_offline_data()) {
                set_offline_data();
                if (check_internet_connection()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("WorkDetails", Context.MODE_PRIVATE);
                    String rid = sharedPreferences.getString("rid", "");
                    update_work_info(rid);
                }
            } else {
                //no work in progress
                Toast.makeText(getApplicationContext(), "No Work is in Progress right now", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    private boolean check_offline_data() {
        SharedPreferences sharedPreferences = getSharedPreferences("WorkDetails", Context.MODE_PRIVATE);

        if (sharedPreferences.contains("isProgress")) {
            Boolean isProgress = sharedPreferences.getBoolean("isProgress", false);
            String user = sharedPreferences.getString("user", "");

            if (isProgress && user.equals(userName)) {
                return true;
            } else
                return false;
        }
        return false;

    }

    private void set_offline_data() {
        SharedPreferences sharedPreferences = getSharedPreferences("WorkDetails", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("isProgress")) {
            Boolean isProgress = sharedPreferences.getBoolean("isProgress", false);
            if (isProgress) {
                subjectTV.setText(sharedPreferences.getString("subject", ""));
                descriptionTV.setText(sharedPreferences.getString("description", ""));
                requestDateTV.setText(sharedPreferences.getString("requestedTime", ""));
                statusTV.setText(sharedPreferences.getString("status", ""));
                servicemanNameTV.setText(sharedPreferences.getString("fullName", ""));
                contactTV.setText(sharedPreferences.getString("phone", ""));
                ratingTV.setText(sharedPreferences.getString("rating", ""));
                priceTV.setText(sharedPreferences.getString("price", ""));
                String rid = sharedPreferences.getString("rid", "");
                if (sharedPreferences.getString("startTime", "").isEmpty()) {
                    if (!rid.isEmpty()) {
                        check_update_startTime(rid);
                    }
                } else {
                    startDateTV.setText(sharedPreferences.getString("startTime", ""));
                    completeButton.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void check_update_startTime(String rid) {
        FirebaseFirestore.getInstance().collection("Requests").document(rid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String st = documentSnapshot.getString("startTime");
                    if (!st.isEmpty()) {
                        startDateTV.setText(st);
                        completeButton.setVisibility(View.VISIBLE);
                    } else {
                        startDateTV.setText("N/A");
                        completeButton.setVisibility(View.GONE);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                startDateTV.setText("N/A");
                completeButton.setVisibility(View.GONE);
            }
        });
    }

    private void fetch_online_data(String rid, String sid) {
        // Fetch data from Requests Table
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("Requests").document(rid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    String subject = documentSnapshot.getString("subject");
//                    String description = documentSnapshot.getString("description");
//                    String requestedTime = documentSnapshot.getString("requestedTime");
//                    String status = documentSnapshot.getString("status");
//                    String startTime = documentSnapshot.getString("startTime");
//                    String price = documentSnapshot.getString("price");
//                    String user = documentSnapshot.getString("userName");
//                    String rid = documentSnapshot.getString("rid");
//
//                    //Saved Data in sharedPreference
//                    SharedPreferences.Editor editor;
//                    SharedPreferences sharedPreferences = getSharedPreferences("WorkDetails", Context.MODE_PRIVATE);
//                    editor = sharedPreferences.edit();
//                    editor.putBoolean("isProgress", true);
//                    editor.putString("subject", subject);
//                    editor.putString("description", description);
//                    editor.putString("requestedTime", requestedTime);
//                    editor.putString("status", status);
//                    editor.putString("startTime", startTime);
//                    editor.putString("price", price);
//                    editor.putString("user", user);
//                    editor.putString("rid", rid);
//                    editor.commit();
//
//                    //Set data in the text Views
//                    subjectTV.setText(subject);
//                    descriptionTV.setText(description);
//                    requestDateTV.setText(requestedTime);
//                    statusTV.setText(status);
//                    priceTV.setText(price);
//                    if (startTime.isEmpty()) {
//                        startDateTV.setText("N/A");
//                        completeButton.setVisibility(View.GONE);
//                    } else {
//                        startDateTV.setText(startTime);
//                    }
//
//                } else {
//
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
//            }
//        });

        //new add
        // Fetch data from Requests Table
        FirebaseFirestore.getInstance().collection("Requests").document(rid).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (documentSnapshot.exists()) {
                    String subject = documentSnapshot.getString("subject");
                    String description = documentSnapshot.getString("description");
                    String requestedTime = documentSnapshot.getString("requestedTime");
                    String status = documentSnapshot.getString("status");
                    String startTime = documentSnapshot.getString("startTime");
                    String price = documentSnapshot.getString("price");
                    String user = documentSnapshot.getString("userName");
                    String rid = documentSnapshot.getString("rid");

                    //Saved Data in sharedPreference
                    SharedPreferences.Editor editor;
                    SharedPreferences sharedPreferences = getSharedPreferences("WorkDetails", Context.MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("isProgress", true);
                    editor.putString("subject", subject);
                    editor.putString("description", description);
                    editor.putString("requestedTime", requestedTime);
                    editor.putString("status", status);
                    editor.putString("startTime", startTime);
                    editor.putString("price", price);
                    editor.putString("user", user);
                    editor.putString("rid", rid);
                    editor.commit();

                    //Set data in the text Views
                    subjectTV.setText(subject);
                    descriptionTV.setText(description);
                    requestDateTV.setText(requestedTime);
                    statusTV.setText(status);
                    priceTV.setText(price);
                    if (startTime.isEmpty()) {
                        startDateTV.setText("N/A");
                        completeButton.setVisibility(View.GONE);
                    } else {
                        startDateTV.setText(startTime);
                    }

                } else {

                }

            }
        });

        //new added


        //Fetch data from Users Table
        // get serviceman info

//        FirebaseFirestore dbref = FirebaseFirestore.getInstance();
//        dbref.collection("Users").document(sid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    String serviceManName = documentSnapshot.getString("userName");
//                    String fullName = documentSnapshot.getString("fullName");
//                    String phone = documentSnapshot.getString("phone");
//                    String rating = documentSnapshot.getString("rating");
//
//                    //Saved Data in sharedPreference
//                    SharedPreferences.Editor editor;
//                    SharedPreferences sharedPreferences = getSharedPreferences("WorkDetails", Context.MODE_PRIVATE);
//                    editor = sharedPreferences.edit();
//                    editor.putBoolean("isProgress", true);
//                    editor.putString("fullName", fullName);
//                    editor.putString("phone", phone);
//                    editor.putString("rating", rating);
//                    editor.commit();
//
//                    //Set data in the text Views
//                    servicemanNameTV.setText(fullName);
//                    contactTV.setText(phone);
//                    ratingTV.setText(rating);
//
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
//            }
//        });


        //new added
        FirebaseFirestore.getInstance().collection("Users").document(sid)
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e !=null){
                            return;
                        }
                        if (documentSnapshot.exists()){
                            String serviceManName = documentSnapshot.getString("userName");
                            String fullName = documentSnapshot.getString("fullName");
                            String phone = documentSnapshot.getString("phone");
                            String rating = documentSnapshot.getString("rating");

                            //Saved Data in sharedPreference
                            SharedPreferences.Editor editor;
                            SharedPreferences sharedPreferences = getSharedPreferences("WorkDetails", Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();
                            editor.putBoolean("isProgress", true);
                            editor.putString("fullName", fullName);
                            editor.putString("phone", phone);
                            editor.putString("rating", rating);
                            editor.commit();

                            //Set data in the text Views
                            servicemanNameTV.setText(fullName);
                            contactTV.setText(phone);
                            ratingTV.setText(rating);
                        }
                    }
                });
    }

    private void update_work_info(String rid) {
        //this method will update price,startdate,status
        FirebaseFirestore.getInstance().collection("Requests").document(rid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            startDateTV.setText(documentSnapshot.getString("startTime"));
                            priceTV.setText(documentSnapshot.getString("price"));
                            statusTV.setText(documentSnapshot.getString("status"));
                        }
                    }
                });
    }

    @Override
    public boolean enterPictureInPictureMode(@NonNull PictureInPictureParams params) {
        return super.enterPictureInPictureMode(params);
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
                SharedPreferences.Editor editor;
                Toast.makeText(getApplicationContext(), "signout Successfully", Toast.LENGTH_LONG).show();
                SharedPreferences sp = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                editor = sp.edit();
                editor.putBoolean("isLoggedin", false);
                editor.remove("userName");
                editor.remove("userType");
                editor.remove("userId");
                editor.commit();
                Intent intentSignout = new Intent(getApplicationContext(), SplashScreen.class);
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
            case R.id.WIP_CompleteButton:
                complete_button_Dialog();

                break;
        }
    }

    private void complete_button_Dialog() {
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle("Complete Work")
                .setMessage("Are you sure to complete this work?")
                .setIcon(R.drawable.complete)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Yes button clicked
                        //update work completion info in request table
                        update_work_completion();


                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //No button clicked
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void update_work_completion() {
        final String id;
        SharedPreferences sharedPreferences = getSharedPreferences("WorkDetails", Context.MODE_PRIVATE);
        Boolean isProgress = sharedPreferences.getBoolean("isProgress", false);
        if (isProgress && sharedPreferences.contains("rid")) {
            id = sharedPreferences.getString("rid", "");
            //update status and endTime in the Request Table
            //cloud Firestore
            FirebaseFirestore.getInstance().collection("Requests").document(id).update("status", "Completed");
            FirebaseFirestore.getInstance().collection("Requests").document(id).update("endTime", get_current_Date());
            //Firebase Realtime Database
            FirebaseDatabase.getInstance().getReference("Requests").child(id).child("status").setValue("Completed");
            FirebaseDatabase.getInstance().getReference("Requests").child(id).child("endTime").setValue(get_current_Date());

            //getting username and serviceman name from request table using rid
            FirebaseFirestore.getInstance().collection("Requests").document(id)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String uName = documentSnapshot.getString("userName");
                        String sName = documentSnapshot.getString("allocatedServiceMan");
                        load_userLog_data_and_update(uName);
                        load_userLog_data_and_update(sName);
                        update_serviceRequestDetails(id, uName, sName);
                        go_new_activity(id, uName, sName);

                    }

                }
            });

        }
    }

    private void load_userLog_data_and_update(final String name) {
        FirebaseFirestore.getInstance().collection("UserLog").document(name)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    int noOfWorkCompleted = Integer.parseInt(documentSnapshot.getString("noOfWorkCompleted"));
                    int totalPrice = Integer.parseInt(documentSnapshot.getString("totalAmount"));

                    //update data back to UserLog table
                    //Cloud Firestore
                    String no = String.valueOf(noOfWorkCompleted + 1);
                    FirebaseFirestore.getInstance().collection("UserLog").document(name).update("noOfWorkCompleted", no);
                    FirebaseFirestore.getInstance().collection("UserLog").document(name).update("totalAmount",
                            String.valueOf(Integer.parseInt(priceTV.getText().toString()) + totalPrice));

                    // Firebase realtime database
                    FirebaseDatabase.getInstance().getReference("UserLog").child(name).child("noOfWorkCompleted").setValue(no);
                    FirebaseDatabase.getInstance().getReference("UserLog").child(name).child("totalAmount").setValue(String.valueOf(Integer.parseInt(priceTV.getText().toString()) + totalPrice));
                }
            }
        });
    }

    private void clear_offline_data() {
        //clear all work details info in sharedPreference
        SharedPreferences.Editor editor;
        SharedPreferences sharedPreferences = getSharedPreferences("WorkDetails", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean("isProgress", false);
        editor.remove("subject");
        editor.remove("description");
        editor.remove("requestedTime");
        editor.remove("status");
        editor.remove("price");
        editor.remove("startTime");
        editor.remove("fullName");
        editor.remove("phone");
        editor.remove("rating");
        editor.remove("user");
        editor.remove("rid");
        editor.remove("address");
        editor.commit();
    }

    private void update_serviceRequestDetails(final String rid, String uname, String sname) {
        FirebaseFirestore.getInstance().collection("UserLog").document(sname)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String id = documentSnapshot.getString("id");
                    //update notification status in serviceRequestDetails table,make it "Completed"
                    //Cloud Firestore
                    FirebaseFirestore.getInstance().collection("ServiceRequestDetails").document("Details")
                            .collection(rid).document(id).update("notificationStatus", "Completed");
                    //Firebase Realtime database
                    FirebaseDatabase.getInstance().getReference("ServiceRequestDetails").child(rid).child(id).child("notificationStatus")
                            .setValue("Completed");
                }
            }
        });
    }

    private void go_new_activity(String rid, String uname, String sname) {
        //clear all offline data in sharedpreference
        clear_offline_data();
        //going to feedback activity
        Intent intentFeedBack = new Intent(getApplicationContext(), serviceFeedback.class);
        intentFeedBack.putExtra("Rid", rid);
        intentFeedBack.putExtra("uname", uname);
        intentFeedBack.putExtra("sname", sname);
        finish();
        startActivity(intentFeedBack);
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

    public String get_current_Date() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        Date date = new Date();
        return formatter.format(date);

    }

    public boolean check_internet_connection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                //Toast.makeText(this,"Wifi Connected",Toast.LENGTH_LONG).show();

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                //Toast.makeText(this,"Mobile Network Connected",Toast.LENGTH_LONG).show();
            }
            return true;
        } else {
            //Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
