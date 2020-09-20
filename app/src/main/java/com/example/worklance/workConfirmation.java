package com.example.worklance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class workConfirmation extends AppCompatActivity implements View.OnClickListener {

    private Button cancelButton, startButton;
    private TextView subjectTV, descriptionTV, requestedDateTV, addressTV, userNameTV, phoneTV, ratingTV, priceTV, startTimeTV, statusTV;
    private String uid, userName, userType, Rid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_confirmation);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbarWorkConfirmation);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setTitle("Work In Progress");
        myChildToolbar.setTitleTextColor(0xFFFFFFFF);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        load_user_info();
        if (userType.equals("User")){
            Toast.makeText(this, "This activity is not available for User", Toast.LENGTH_SHORT).show();
            finish();
        }

        //find all text views by id
        subjectTV = findViewById(R.id.WC_subject);
        descriptionTV = findViewById(R.id.WC_description);
        requestedDateTV = findViewById(R.id.WC_requestedDate);
        addressTV = findViewById(R.id.WC_address);
        userNameTV = findViewById(R.id.WC_userName);
        phoneTV = findViewById(R.id.WC_phone);
        ratingTV = findViewById(R.id.WC_rating);
        startTimeTV = findViewById(R.id.WC_startTime);
        priceTV = findViewById(R.id.WC_price);
        statusTV = findViewById(R.id.WC_status);

        startButton = findViewById(R.id.WC_startButton);
        cancelButton = findViewById(R.id.WC_cancelButton);
        cancelButton.setOnClickListener(this);
        startButton.setOnClickListener(this);


        // get data from notification intent
        Intent intent = getIntent();
        Rid = intent.getStringExtra("Rid");
        if (Rid != null && !Rid.isEmpty()) {
            //Toast.makeText(getApplicationContext(), "Rid : " + Rid, Toast.LENGTH_LONG).show();
            if (check_offline_data()) {
                set_offline_data();
            } else {
                fetch_online_data(Rid);
            }


        } else {
            //Toast.makeText(this, "from external Activity", Toast.LENGTH_SHORT).show();
            if (check_offline_data()) {
                set_offline_data();
            } else {
                //No work in pogress
                Toast.makeText(getApplicationContext(), "No Work is in Progress right now", Toast.LENGTH_LONG).show();
                finish();
            }

        }

    }

    protected void onStart() {
        super.onStart();
        // check whether the work is still in progress or completed
        if (Rid != null && Rid.isEmpty()){
            check_work_update(Rid);
        }else {
            SharedPreferences sharedPreferences = getSharedPreferences("WorkDetails", Context.MODE_PRIVATE);
            Boolean isProgress = sharedPreferences.getBoolean("isProgress", false);
            if (isProgress){
                String rid = sharedPreferences.getString("rid","");
                check_work_update(rid);
            }

        }
    }

    private void check_work_update(String rid){
        FirebaseFirestore.getInstance().collection("Requests").document(rid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String status = documentSnapshot.getString("status");
                    if (status.equals("Completed")){
                        clear_offline_data();

                    }
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
        Toast.makeText(getApplicationContext(),"Work has finished,Check history",Toast.LENGTH_SHORT).show();
        this.finish();
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
                requestedDateTV.setText(sharedPreferences.getString("requestedTime", ""));
                statusTV.setText(sharedPreferences.getString("status", ""));
                userNameTV.setText(sharedPreferences.getString("fullName", ""));
                phoneTV.setText(sharedPreferences.getString("phone", ""));
                ratingTV.setText(sharedPreferences.getString("rating", ""));
                priceTV.setText(sharedPreferences.getString("price", ""));
                addressTV.setText(sharedPreferences.getString("address",""));
                if (sharedPreferences.getString("startTime", "").isEmpty()) {
                    startTimeTV.setText("N/A");
                } else {
                    startTimeTV.setText(sharedPreferences.getString("startTime", ""));
                    startButton.setVisibility(View.GONE);
                    cancelButton.setVisibility(View.GONE);
                }
            }
        }
    }

    private void fetch_online_data(String rid) {
        FirebaseFirestore.getInstance().collection("Requests").document(rid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String rid = documentSnapshot.getString("rid");
                    String subject = documentSnapshot.getString("subject");
                    String description = documentSnapshot.getString("description");
                    String price = documentSnapshot.getString("price");
                    String requestedTime = documentSnapshot.getString("requestedTime");
                    String status = documentSnapshot.getString("status");
                    String name = documentSnapshot.getString("userName");
                    String startTime = documentSnapshot.getString("startTime");

                    subjectTV.setText(subject);
                    descriptionTV.setText(description);
                    requestedDateTV.setText(requestedTime);
                    if (startTime.isEmpty()) {
                        startTimeTV.setText("N/A");
                    } else {
                        startTimeTV.setText(startTime);
                        startButton.setVisibility(View.GONE);
                        cancelButton.setVisibility(View.GONE);
                    }
                    priceTV.setText(price);
                    statusTV.setText(status);
                    set_user_online_data(rid, subject, description, price, requestedTime, status, name, startTime);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("workConfirmation", "onFailure: " + e.toString());
            }
        });

    }

    private void set_user_online_data(final String rid, final String subject, final String description, final String price,
                                      final String requestedTime, final String status, String name, final String startTime) {

        FirebaseFirestore.getInstance().collection("Users").whereEqualTo("userName", name)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot snapshot : snapshots) {
                        String fullName = snapshot.getString("fullName");
                        String rating = snapshot.getString("rating");
                        String phone = snapshot.getString("phone");
                        String address = snapshot.getString("address");

                        //set up data in text views
                        ratingTV.setText(rating);
                        phoneTV.setText(phone);
                        addressTV.setText(address);
                        userNameTV.setText(fullName);

                        // save data in sharedPreference if it doesnot contain any data
                        SharedPreferences sp = getSharedPreferences("WorkDetails", Context.MODE_PRIVATE);
                        Boolean isProgress = sp.getBoolean("isProgress", false);
                        if (!isProgress) {
                            SharedPreferences.Editor editor;
                            SharedPreferences sharedPreferences = getSharedPreferences("WorkDetails", Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();
                            editor.putBoolean("isProgress", true);
                            editor.putString("subject", subject);
                            editor.putString("description", description);
                            editor.putString("requestedTime", requestedTime);
                            editor.putString("status", status);
                            editor.putString("price", price);
                            editor.putString("startTime", startTime);
                            editor.putString("fullName", fullName);
                            editor.putString("phone", phone);
                            editor.putString("rating", rating);
                            editor.putString("user", userName);
                            editor.putString("rid", rid);
                            editor.putString("address", address);
                            editor.commit();
                        }


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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.WC_cancelButton:
                /*Intent intentFeedBack = new Intent(getApplicationContext(), serviceFeedback.class);
                startActivity(intentFeedBack);*/
                //lots of work have to be done here
                cancel_button_Dialog();

                break;
            case R.id.WC_startButton:
                //lots of work have to be done here
                start_button_Dialog();
                break;
        }
    }

    private void start_button_Dialog() {
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle("Start Work")
                .setMessage("Are you sure to start this work?")
                .setIcon(R.drawable.question)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Yes button clicked

                        //get Rid from sharedPreference
                        SharedPreferences sharedPreferences = getSharedPreferences("WorkDetails", Context.MODE_PRIVATE);
                        Boolean isProgress = sharedPreferences.getBoolean("isProgress", false);
                        if (isProgress && sharedPreferences.contains("rid")) {
                            String rid = sharedPreferences.getString("rid", "");
                            update_start_work(rid);
                            //update info in userNotificaiton table
                            //Cloud Firestore
                            FirebaseFirestore.getInstance().collection("UserNotification").document(rid).update("startNotification","Pending");
                            FirebaseFirestore.getInstance().collection("UserNotification").document(rid).update("servicemanName",userName);
                            FirebaseFirestore.getInstance().collection("UserNotification").document(rid).update("sid",uid);
                            //Firebase realtime datbase
                            FirebaseDatabase.getInstance().getReference("UserNotification").child(rid).child("startNotification").setValue("Pending");
                            FirebaseDatabase.getInstance().getReference("UserNotification").child(rid).child("servicemanName").setValue(userName);
                            FirebaseDatabase.getInstance().getReference("UserNotification").child(rid).child("sid").setValue(uid);
                            Toast.makeText(workConfirmation.this, "Work has started successfully", Toast.LENGTH_SHORT).show();
                        }


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

    private void cancel_button_Dialog() {
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle("Cancel Work")
                .setMessage("Are you sure to cancel this work?")
                .setIcon(R.drawable.cancel)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Yes button clicked
                        //get Rid from sharedPreference
                        SharedPreferences sharedPreferences = getSharedPreferences("WorkDetails", Context.MODE_PRIVATE);
                        Boolean isProgress = sharedPreferences.getBoolean("isProgress", false);
                        if (isProgress && sharedPreferences.contains("rid")) {
                            String rid = sharedPreferences.getString("rid", "");
                            cancel_work(rid);
                            //update info in userNotificaiton table
                            //Cloud Firestore
                            FirebaseFirestore.getInstance().collection("UserNotification").document(rid).update("cancelNotification","Pending");
                            FirebaseFirestore.getInstance().collection("UserNotification").document(rid).update("servicemanName",userName);
                            FirebaseFirestore.getInstance().collection("UserNotification").document(rid).update("sid",uid);
                            //Firebase realtime datbase
                            FirebaseDatabase.getInstance().getReference("UserNotification").child(rid).child("cancelNotification").setValue("Pending");
                            FirebaseDatabase.getInstance().getReference("UserNotification").child(rid).child("servicemanName").setValue(userName);
                            FirebaseDatabase.getInstance().getReference("UserNotification").child(rid).child("sid").setValue(uid);

                            Toast.makeText(workConfirmation.this, "Work has cancelled successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }

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

    private void update_start_work(String rid) {
        //update start work time in online
        //using Cloud Firestore
        FirebaseFirestore.getInstance().collection("Requests").document(rid).update("startTime", get_current_Date());
        FirebaseFirestore.getInstance().collection("ServiceRequestDetails").document("Details")
                .collection(rid).document(uid).update("notificationStatus", "Started");

        //using Firebase realtime Database
        FirebaseDatabase.getInstance().getReference("Requests").child(rid).child("startTime").setValue(get_current_Date());
        FirebaseDatabase.getInstance().getReference("ServiceRequestDetails").child(rid).child(uid).child("notificationStatus")
                .setValue("Started");

        //update start work time in offline
        //save in sharedPreference
        SharedPreferences.Editor editor;
        SharedPreferences sharedPreferences = getSharedPreferences("WorkDetails", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("startTime", get_current_Date());
        editor.commit();
        //set up in startTime Text Field
        startTimeTV.setText(get_current_Date());
        //disable start work button and cancel work button
        startButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);

    }

    private void cancel_work(String rid) {
        //update value in request table
        //using cloud firestore
        DocumentReference docRequest = FirebaseFirestore.getInstance().collection("Requests").document(rid);
        docRequest.update("status", "Requested");
        docRequest.update("allocatedServiceMan", "");
        docRequest.update("price", "");

        FirebaseFirestore.getInstance().collection("ServiceRequestDetails")
                .document("Details").collection(rid).document(uid).update("notificationStatus", "Cancelled");

        //using Firebase realtime Database
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Requests").child(rid);
        db.child("status").setValue("Requested");
        db.child("allocatedServiceMan").setValue("");
        db.child("price").setValue("");

        FirebaseDatabase.getInstance().getReference("ServiceRequestDetails").child(rid).child(uid).child("notificationStatus")
                .setValue("Cancelled");

        //clear work in progress data in sharedpreference
        SharedPreferences.Editor editor;
        SharedPreferences sharedPreferences = getSharedPreferences("WorkDetails", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean("isProgress", false);
        editor.commit();

    }

    public void openDialog() {
        DialogBox exampleDialog = new DialogBox();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }


    /*public class FireMissilesDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Cancel")
                    .setMessage("Are you sure you want to cancel this work?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }*/
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
