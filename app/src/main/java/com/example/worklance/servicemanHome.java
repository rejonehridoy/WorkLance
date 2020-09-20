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
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class servicemanHome extends AppCompatActivity implements View.OnClickListener {

    Button incomingRequestButton, profileButton, historyButton, workConfirmation,changeLocation;
    TextView addTextviewUserHome;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String userName, uid, userType;
    private String address,latitude,longitude,location;
    private List<Address> add=new ArrayList<>();
    List<String> rid = new ArrayList<>();
    List<ServiceRequest> serviceman = new ArrayList<>();
    private static final String CHANNEL_ID = "CHANNEL_1";
    private static final String CHANNEL_NAME = "NOTIFICATION";
    public static final String CHANNEL_DESC = "Request Notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serviceman_home);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbarServicemanHome);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setTitle("Serviceman Home");
        myChildToolbar.setTitleTextColor(0xFFFFFFFF);
        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);

        load_user_info();
        if (userType.equals("User")){
            Toast.makeText(this, "This activity is not available for serviceman", Toast.LENGTH_SHORT).show();
            finish();
        }

        get_user_data();

        incomingRequestButton = findViewById(R.id.incomingRequstButton);
        profileButton = findViewById(R.id.profileButton);
        historyButton = findViewById(R.id.historyButton);
        workConfirmation = findViewById(R.id.workConfirmation);
        changeLocation = findViewById(R.id.changeLocation);
        addTextviewUserHome = findViewById(R.id.addTextviewUserHome);

        incomingRequestButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        historyButton.setOnClickListener(this);
        workConfirmation.setOnClickListener(this);
        changeLocation.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_corner, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetch_all_request_id();
        check_work_confirmation();
    }
    protected void onResume() {
        super.onResume();

        double latitudeChange = getIntent().getDoubleExtra("latitude", 0);
        latitude= String.valueOf(latitudeChange);
        Log.d("latitude   ",latitude);
        double longitudeChange = getIntent().getDoubleExtra("longitude", 0);
        longitude= String.valueOf(longitudeChange);
        Log.d("longitude   ",longitude);

        Geocoder geocoder= new Geocoder(servicemanHome.this);
        location = "Address not found";
        //add.clear();
        try {
            add = geocoder.getFromLocation(latitudeChange,longitudeChange,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0;i<add.size();i++){
            location=add.get(i).getAddressLine(i);
        }


        //Toast.makeText(servicemanHome.this, "Current Location in signin"+location, Toast.LENGTH_SHORT).show();
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
       // Toast.makeText(getApplicationContext(),"get_user_data()",Toast.LENGTH_LONG).show();
        FirebaseFirestore.getInstance().collection("Users").document(uid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                address = documentSnapshot.getString("address");
                addTextviewUserHome.setText(address);
                Log.i("add",address);
                //Toast.makeText(getApplicationContext(),"address            "+address,Toast.LENGTH_LONG).show();
                latitude = documentSnapshot.getString("latitude");
                //Toast.makeText(getApplicationContext(),"latitude  "+latitude,Toast.LENGTH_LONG).show();
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
    private void fetch_all_request_id() {
        // Getting Request id from Firebase Database
        /*DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Requests");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    rid.clear();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        String id = snapshot.child("rid").getValue().toString();
                        //Toast.makeText(getApplicationContext(),id,Toast.LENGTH_LONG).show();
                        check_notification_status(id);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        //getting Request id from Cloud Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection("Requests");
        collection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                //String data = "";
                //serviceRequestsInfo.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String rid = documentSnapshot.getString("rid");
                    check_notification_status(rid);
                }

            }
        });


    }

    private void check_notification_status(String rid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notifcationInfo = db.collection("ServiceRequestDetails");
        DocumentReference docService = notifcationInfo.document("Details").collection(rid).document(uid);
        final String id = rid;
//        docService.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    String status = documentSnapshot.getString("notificationStatus");
//                    if (status.equals("Pending")) {
//                        fetch_subject_description(id, status);
//                    }
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
//            }
//        });

        docService.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e !=null){
                    return;
                }
                if (documentSnapshot.exists()) {
                    String status = documentSnapshot.getString("notificationStatus");

                    if (status.equals("Pending")) {
                        fetch_subject_description(id, status);
                    }
                }

            }
        });
    }


    private void fetch_subject_description(final String rid, String status) {
        //fetch subject and description from Resquest table in firebase real time database
        /*Query db = FirebaseDatabase.getInstance().getReference("Requests").orderByChild("rid")
                .equalTo(rid);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        String subject = snapshot.child("subject").getValue().toString();
                        String description = snapshot.child("description").getValue().toString();
                        String requestTime = snapshot.child("requestedTime").getValue().toString();
                        String workerType = snapshot.child("workerType").getValue().toString();
                        String status = snapshot.child("status").getValue().toString();
                        String userName = snapshot.child("userName").getValue().toString();

                        send_notification(rid,subject,description);
                        update_notification_status(rid,subject,description);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        // fetching data using cloud Firestore
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference doc = database.collection("Requests").document(rid);
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String subject = documentSnapshot.getString("subject");
                    String description = documentSnapshot.getString("description");
                    String requestTime = documentSnapshot.getString("requestedTime");
                    String workerType = documentSnapshot.getString("workerType");
                    String status = documentSnapshot.getString("status");
                    String userName = documentSnapshot.getString("userName");

                    //new added
                    if (status.equals("Requested")){
                        send_notification(rid, subject, description);
                        update_notification_status(rid, subject, description);
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        });

//        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                if (e!=null){
//                    return;
//                }
//                if (documentSnapshot.exists()) {
//                    String subject = documentSnapshot.getString("subject");
//                    String description = documentSnapshot.getString("description");
//                    String requestTime = documentSnapshot.getString("requestedTime");
//                    String workerType = documentSnapshot.getString("workerType");
//                    String status = documentSnapshot.getString("status");
//                    String userName = documentSnapshot.getString("userName");
//
//                    send_notification(rid, subject, description);
//                    update_notification_status(rid, subject, description);
//                }
//            }
//        });
    }

    int C = 0;

    private void send_notification(String rid, String subject, String description) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, incomingRequest.class);
        //intent.putExtra("Rid", rid);
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
                .setContentText(description)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(501, nofiticationBuilder.build());
        C++;
    }

    private void update_notification_status(String rid, String subject, String description) {
        // Ipdate in firebase realtime database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("ServiceRequestDetails").child(rid).child(uid);
        ServiceRequest serviceRequest = new ServiceRequest(get_current_Date(), "", "Sent");
        databaseReference.setValue(serviceRequest);

        //Update in cloud Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference notification_info = db.collection("ServiceRequestDetails").document("Details").collection(rid).document(uid);
        notification_info.update("dateTime", get_current_Date());
        notification_info.update("notificationStatus", "Sent");


    }

    private void check_work_confirmation() {
        FirebaseFirestore.getInstance().collection("Requests")
                .whereEqualTo("allocatedServiceMan", userName)
                .whereEqualTo("status", "In Progress")
                .whereEqualTo("notificationStatus", "Pending")
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
                                String subject = snapshot.getString("subject");
                                String description = snapshot.getString("description");
                                String price = snapshot.getString("price");
                                String requestedTime = snapshot.getString("requestedTime");
                                String status = snapshot.getString("status");
                                String name = snapshot.getString("userName");
                                String startTime = snapshot.getString("startTime");
                                //Toast.makeText(getApplicationContext(),"Rid : "+rid+" subject : "+subject,Toast.LENGTH_LONG).show();
                                // send notification
                                send_work_confirmation_notification(rid, subject, description);

                                //saved data in sharedPreference
                                save_work_data_offline(rid, subject, description, price, requestedTime, status, name, startTime);
                            }
                        }
                    }
                });
    }

    private void send_work_confirmation_notification(String rid, String subject, String description) {
        Intent intent = new Intent(this, workConfirmation.class);
        intent.putExtra("Rid", rid);
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
                .setContentTitle("Work Confirmation")
                .setContentText(subject)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(101, nofiticationBuilder.build());
        C++;
    }

    private void save_work_data_offline(final String rid, final String subject, final String description, final String price,
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

                        // save data in sharedPreference
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
                        editor.putString("address", address);
                        editor.putString("rid", rid);
                        editor.commit();

                    }
                }
            }
        });

        //update Request table to understand that confirmation notification is sent to serviceman
        //Cloud Firestore
        FirebaseFirestore.getInstance().collection("Requests").document(rid)
                .update("notificationStatus", "Sent");
        //Firebase real time database
        FirebaseDatabase.getInstance().getReference("Requests").child(rid).child("notificationStatus")
                .setValue("Sent");
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
                Toast.makeText(getApplicationContext(), "signout Successfully", Toast.LENGTH_LONG).show();
                SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedin", false);
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
            case R.id.incomingRequstButton:
                Intent incominRequestIntent = new Intent(getApplicationContext(), incomingRequest.class);
                startActivity(incominRequestIntent);
                break;
            case R.id.profileButton:
                Intent profileIntent = new Intent(getApplicationContext(), profile.class);
                startActivity(profileIntent);

                break;
            case R.id.historyButton:
                Intent hitoryIntent = new Intent(getApplicationContext(), servicemanHistory.class);
                startActivity(hitoryIntent);
                break;
            case R.id.workConfirmation:
                Intent workConfirmationIntent = new Intent(getApplicationContext(), workConfirmation.class);
                startActivity(workConfirmationIntent);
                break;
            case R.id.changeLocation:
                Intent intentMap = new Intent(getApplicationContext(), MapActivityServiceman.class);
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

    private String get_current_Date() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        Date date = new Date();
        return formatter.format(date);

    }
}
