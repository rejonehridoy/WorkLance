package com.example.worklance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.worklance.MapActivity.GPS_REQUEST_CODE;

public class request extends AppCompatActivity implements View.OnClickListener{

    private EditText subjectEt,messageEt;
    private List<String> servicemanId;
    private Button submitbtn;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String Type="",userName,uid,userType,location;
    DatabaseReference databaseReference;
    private double userLat,userLog;
    private static final String CHANNEL_ID = "CHANNEL_1";
    private static final String CHANNEL_NAME = "NOTIFICATION";
    public static final String CHANNEL_DESC = "Request Notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);


        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbarRequest);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setTitle("Request");
        myChildToolbar.setTitleTextColor(0xFFFFFFFF);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        final Spinner spinner = (Spinner) findViewById(R.id.work_spinner_request);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.work_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //get user info form sharedpreference
        load_user_info();
        servicemanId = new ArrayList<>();

        //finding view by id
        subjectEt = findViewById(R.id.subjectRequest);
        messageEt = findViewById(R.id.messageRequest);
        submitbtn = findViewById(R.id.submitRequest);
        submitbtn.setOnClickListener(this);
        // create item click listener of spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Type = spinner.getSelectedItem().toString();
                load_Specific_serviceMan();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        location = getIntent().getStringExtra("add");
        String latitude = getIntent().getStringExtra("lat");
        userLat= Double.parseDouble(latitude);
        String longitude = getIntent().getStringExtra("log");
        userLog= Double.parseDouble(longitude);
        //Toast.makeText(getApplicationContext(),"add   "+location,Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(),"lat   "+latitude,Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(),"log   "+longitude,Toast.LENGTH_LONG).show();
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

    private boolean check_empty_field()
    {
        if (subjectEt.getText().toString().isEmpty() || messageEt.getText().toString().isEmpty() || Type.isEmpty()){
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submitRequest:
                if (check_empty_field())
                {
                    Toast.makeText(getApplicationContext(),"No field should empty",Toast.LENGTH_LONG).show();
                }
                else{
                    // Store request in the database

                                    store_request();


                    //send_notification();
                }

                break;
        }
    }
    private double parseDouble(String s){
        if(s == null || s.isEmpty())
            return 0.0;
        else
            return Double.parseDouble(s);
    }

    private void load_Specific_serviceMan() {
        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("userType")
                .equalTo(Type);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                servicemanId.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String serviceManId = snapshot.child("id").getValue().toString();

                        String serviceManlat = snapshot.child("latitude").getValue().toString();
                        double servicemanLa,servicemanLo;
                        servicemanLa = parseDouble(serviceManlat);

                        String serviceManlog = snapshot.child("longitude").getValue().toString();
                        servicemanLo = parseDouble(serviceManlog);

                        //Toast.makeText(getApplicationContext(),"Fetching Add"+servicemanLa,Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(),"Fetching name"+servicemanLo,Toast.LENGTH_SHORT).show();
                        int radius=6000;
                        float[] distance =new float[2];
                        Location.distanceBetween(userLat,userLog,servicemanLa,servicemanLo,distance);


                        //Toast.makeText(getApplicationContext(),"Rid : "+rid,Toast.LENGTH_LONG).show();
                        if(radius >= distance[0]){
                            servicemanId.add(serviceManId);
                            Log.d("RADIUS","Inside Radius");
                            //Toast.makeText(request.this, "Inside radius", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //Toast.makeText(request.this, "Outside radius", Toast.LENGTH_SHORT).show();
                            Log.d("RADIUS","Outside Radius");
                            continue;
                        }

                        //Toast.makeText(getApplicationContext(),"Fetching Id",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void store_notification_info(String rid) {
        //storing info in the firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("ServiceRequestDetails");

        // storing data in the firebase cloud firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notifcationInfo = db.collection("ServiceRequestDetails").document("Details").collection(rid);


        String dateTime = "";
        String requestPrice = "";
        String notificationStatus = "Pending";
        Map<String, Object> notification = new HashMap<>();
        notification.put("dateTime", dateTime);
        notification.put("requestPrice", requestPrice);
        notification.put("notificationStatus", notificationStatus);

        Toast.makeText(getApplicationContext(),servicemanId.size()+" serviceman found nearby",Toast.LENGTH_LONG).show();
        for (String id : servicemanId){
            Toast.makeText(getApplicationContext(),"ServiceRequestTable : "+id,Toast.LENGTH_LONG).show();
            ServiceRequest serviceRequest = new ServiceRequest("","","Pending");
            //store in the realtime firebase database
            dbRef.child(rid).child(id).setValue(serviceRequest);
            //store in the cloud firestore
            notifcationInfo.document(id).set(notification);
        }

    }

    private void send_notification() {

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, aboutUs.class);
        intent.putExtra("Rid","requestId");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder nofiticationBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ractangle_shape)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.background_login))
                .setContentTitle("This is a title")
                .setContentText("This is a text")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(196,nofiticationBuilder.build());

        /*NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ractangle_shape)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.background_login))
                .setContentTitle("This is a title")
                .setContentText("This is a text")
                .setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationManagerCompat mNotification = NotificationManagerCompat.from(this);
        mNotification.notify(1,mbuilder.build());*/
    }

    private void store_request() {
        if (check_internet_connection()){
            // Declaration of Firebase Database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference("Requests");
            // Decalaration of Cloud Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collection = db.collection("Requests");


            String id = databaseReference.push().getKey();

            RequestInfo requestInfo = new RequestInfo(id,subjectEt.getText().toString().trim(),messageEt.getText().toString().trim(),
                    userName,get_current_Date(),"","",Type,"","","Requested","",
                    "","Pending");
            //saved in firebase database
            databaseReference.child(id).setValue(requestInfo);
            //saved in cloud firestore
            collection.document(id).set(requestInfo);

            //Store information in userNotification Table
            UserNotificaiton usernotificaiton = new UserNotificaiton("",id,"","","",userName,uid);
            //Firestore
            FirebaseFirestore.getInstance().collection("UserNotification").document(id).set(usernotificaiton);
            //Firebase real time database
            FirebaseDatabase.getInstance().getReference("UserNotification").child(id).setValue(usernotificaiton);

            store_notification_info(id);
            Toast.makeText(getApplicationContext(),"Requested Successfully",Toast.LENGTH_LONG).show();
            Intent userHomeintent = new Intent(getApplicationContext(),userHome.class);
            finish();
            startActivity(userHomeintent);


        }
        else{
            Toast.makeText(getApplicationContext(),"No internet Connection",Toast.LENGTH_LONG).show();
        }

    }
    public boolean check_internet_connection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                //Toast.makeText(this,"Wifi Connected",Toast.LENGTH_LONG).show();

            }else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                //Toast.makeText(this,"Mobile Network Connected",Toast.LENGTH_LONG).show();
            }
            return true;
        }else{
            //Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();
            return false;
        }
    }
    public String get_current_Date(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        Date date = new Date();
        return formatter.format(date);

    }
}
