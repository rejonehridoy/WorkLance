package com.example.worklance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class userHome extends AppCompatActivity implements View.OnClickListener {

    private Button requestButton, profileButton, historyButton, servicemanselectionButton,changeLocation;
    private TextView requestTextView,addTextviewUserHome;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String address,latitude,longitude,location;
    private String userName,uid,userType;
    private List<Address> add=new ArrayList<>();
    private static final String CHANNEL_ID = "CHANNEL_1";
    private static final String CHANNEL_NAME = "NOTIFICATION";
    public static final String CHANNEL_DESC = "Request Notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbarUserHome);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setTitle("Home");
        myChildToolbar.setTitleTextColor(0xFFFFFFFF);
        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);

        requestButton = findViewById(R.id.requstButton);
        profileButton = findViewById(R.id.profileButton);
        historyButton = findViewById(R.id.historyButton);
        changeLocation = findViewById(R.id.changeLocation);
        addTextviewUserHome=findViewById(R.id.addTextviewUserHome);
        servicemanselectionButton = findViewById(R.id.servicemanSelectionButton);

        load_user_info();

        if (!userType.equals("User")){
            Toast.makeText(this, "This activity is not available for serviceman", Toast.LENGTH_SHORT).show();
            finish();
        }

        get_user_data();

        requestButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        historyButton.setOnClickListener(this);
        changeLocation.setOnClickListener(this);
        servicemanselectionButton.setOnClickListener(this);

        requestTextView = findViewById(R.id.requestTextView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //get_user_data();
        //addTextviewUserHome.setText(address);
        check_priceRequest_notificaton();
        check_startWork_notification();
        check_cancelWork_notification();
    }

    @Override
    protected void onResume() {
        super.onResume();

        double latitudeChange = getIntent().getDoubleExtra("latitude", 0);
        latitude= String.valueOf(latitudeChange);
        double longitudeChange = getIntent().getDoubleExtra("longitude", 0);
        longitude= String.valueOf(longitudeChange);

        Geocoder geocoder= new Geocoder(userHome.this);
        location = "Address not found";
        try {
            add = geocoder.getFromLocation(latitudeChange,longitudeChange,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0;i<add.size();i++){
            location=add.get(i).getAddressLine(i);
        }
        //Toast.makeText(userHome.this, "Current Location in signin"+location, Toast.LENGTH_SHORT).show();
        if(!location.equals("Address not found")){
            FirebaseFirestore.getInstance().collection("Users").document(uid).update("latitude",latitude);
            FirebaseFirestore.getInstance().collection("Users").document(uid).update("longitude",longitude);
            FirebaseFirestore.getInstance().collection("Users").document(uid).update("address",location);

            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("latitude").setValue(latitude);
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("longitude").setValue(longitude);
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("address").setValue(location);
        }
        addTextviewUserHome.setText(address);
    }

    private void get_user_data(){
        //Toast.makeText(getApplicationContext(),"get_user_data()",Toast.LENGTH_LONG).show();
        FirebaseFirestore.getInstance().collection("Users").document(uid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                 address = documentSnapshot.getString("address");
                addTextviewUserHome.setText(address);
                 Log.i("add",address);
                //Toast.makeText(getApplicationContext(),"address            "+address,Toast.LENGTH_LONG).show();
                 latitude = documentSnapshot.getString("latitude");
               // Toast.makeText(getApplicationContext(),"latitude  "+latitude,Toast.LENGTH_LONG).show();
                 longitude = documentSnapshot.getString("longitude");
                //Toast.makeText(getApplicationContext(),"longitude  "+longitude,Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void check_priceRequest_notificaton(){
        FirebaseFirestore.getInstance().collection("PriceRequestNotification")
                .whereEqualTo("username", userName)
                .whereEqualTo("notification", "Pending")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("ERROR", "onEvent: " + e.toString());
                            return;
                        }
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot snapshot : snapshots) {
                                String pid = snapshot.getString("pid");
                                String subject = snapshot.getString("subject");
                                String description = snapshot.getString("description");
                                String price = snapshot.getString("price");


                                // send notification
                                send_PriceRequest_notification(pid, subject, description,price);

                                //update data in PriceRequestNotificationTable
                                //Cloud Firestore
                                FirebaseFirestore.getInstance().collection("PriceRequestNotification").document(pid).update("notification","Sent");
                                //Firebase Realtime Database
                                FirebaseDatabase.getInstance().getReference("PriceRequestNotification").child(pid).child("notification").setValue("Sent");

                            }
                        }
                    }
                });

    }

    private void send_PriceRequest_notification(String pid,String subject,String description,String price){
        Intent intent = new Intent(this, servicemanSelection.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder nofiticationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.worklance_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.worklance_logo))
                .setContentTitle(subject)
                .setContentText("Price Requested : "+price)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(101, nofiticationBuilder.build());
    }

    private void check_startWork_notification(){
        FirebaseFirestore.getInstance().collection("UserNotification")
                .whereEqualTo("userName", userName)
                .whereEqualTo("startNotification", "Pending")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("ERROR", "onEvent: " + e.toString());
                            return;
                        }
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot snapshot : snapshots) {
                                String rid = snapshot.getString("rid");
                                String sid = snapshot.getString("sid");
                                String servicemanName = snapshot.getString("servicemanName");

                                // send notification
                                send_startWork_notification(rid,sid,servicemanName);

                                //update data in PriceRequestNotificationTable
                                //Cloud Firestore
                                FirebaseFirestore.getInstance().collection("UserNotification").document(rid).update("startNotification","Sent");
                                //Firebase Realtime Database
                                FirebaseDatabase.getInstance().getReference("UserNotification").child(rid).child("startNotification").setValue("Sent");

                                //Update offline data in sharedpreference
                                update_offline_data(rid);
                            }
                        }
                    }
                });

    }

    private void send_startWork_notification(String rid,String sid,String servicemanName){
        Intent intent = new Intent(this, workInProgress.class);
        intent.putExtra("Rid", rid);
        intent.putExtra("Sid", sid);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder nofiticationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.worklance_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.worklance_logo))
                .setContentTitle("Start Work Confirmation")
                .setContentText("Work has started by username : "+servicemanName)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(102, nofiticationBuilder.build());
    }

    private void update_offline_data(final String rid){
        FirebaseFirestore.getInstance().collection("Requests").document(rid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String startTime = documentSnapshot.getString("startTime");
                    String price = documentSnapshot.getString("price");
                    String status = documentSnapshot.getString("status");

                    //save data in sharedpreference
                    SharedPreferences.Editor editor;
                    SharedPreferences sharedPreferences = getSharedPreferences("WorkDetails",Context.MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putString("status",status);
                    editor.putString("price",price);
                    editor.putString("startTime",startTime);
                    editor.putString("rid", rid);
                    editor.commit();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void check_cancelWork_notification(){
        FirebaseFirestore.getInstance().collection("UserNotification")
                .whereEqualTo("userName", userName)
                .whereEqualTo("cancelNotification", "Pending")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("ERROR", "onEvent: " + e.toString());
                            return;
                        }
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot snapshot : snapshots) {
                                String rid = snapshot.getString("rid");
                                //String sid = snapshot.getString("sid");
                                String servicemanName = snapshot.getString("servicemanName");

                                // send notification
                                send_cancelWork_notification(servicemanName);

                                //update data in PriceRequestNotificationTable
                                //Cloud Firestore
                                FirebaseFirestore.getInstance().collection("UserNotification").document(rid).update("cancelNotification","Sent");
                                //Firebase Realtime Database
                                FirebaseDatabase.getInstance().getReference("UserNotification").child(rid).child("cancelNotification").setValue("Sent");

                            }
                        }
                    }
                });

    }

    private void send_cancelWork_notification(String serviceman){
        Intent intent = new Intent(this, userHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder nofiticationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.worklance_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.worklance_logo))
                .setContentTitle("Cancelation of Work")
                .setContentText("Work has cancelled by : "+serviceman)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(102, nofiticationBuilder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.userhome_menu, menu);
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
                Intent intentWorkInOrigress = new Intent(getApplicationContext(), workInProgress.class);
                startActivity(intentWorkInOrigress);
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
                editor.remove("userName");
                editor.remove("userType");
                editor.remove("userId");
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
            case R.id.requstButton:
                Intent intentRequest = new Intent(getApplicationContext(), request.class);
                intentRequest.putExtra("add",address);
                intentRequest.putExtra("lat",latitude);
                intentRequest.putExtra("log",longitude);
                startActivity(intentRequest);
                break;

            case R.id.profileButton:
                Intent intentProfile = new Intent(getApplicationContext(), profile.class);
                startActivity(intentProfile);
                break;
            case R.id.historyButton:
                Intent intentHistory = new Intent(getApplicationContext(), history.class);
                startActivity(intentHistory);
                break;
            case R.id.servicemanSelectionButton:
                Intent intentservicemanSelection = new Intent(getApplicationContext(), servicemanSelection.class);
                startActivity(intentservicemanSelection);
                break;

            case R.id.changeLocation:
                Intent intentMap = new Intent(getApplicationContext(), MapActivity2.class);
                finish();
                startActivity(intentMap);


        }
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
