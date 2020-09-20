package com.example.worklance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
//import com.google.firebase.database.DatabaseReference;

public class Signin extends AppCompatActivity implements View.OnClickListener {

    private Button registerButton;
    private Spinner spinnerWorkerType;
    private Button getLocationonMap;
    DatabaseReference databaseReference;
    private EditText userNameTv,fullNameTv,emailTv,passwordTv,retypePasswordTv,phoneTv;
    private TextView addressTextView;
    int typeFlag=0;
    int genderFlag = 0;
    private List<Boolean> isFoundDuplicate = new ArrayList<>();
    private RadioButton userRB,servicemanRB;
    List<Users> users;
    String Type="";
    private boolean is_duplicate_username = true;
    private List<Address> add = new ArrayList<>();
    private double lat=0,log=0;
    private String loc="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        //set up references for user table in firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbarSignin);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setTitle("Sign Up");
        myChildToolbar.setTitleTextColor(0xFFFFFFFF);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        final Spinner spinnerWorkerType = (Spinner) findViewById(R.id.work_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.work_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkerType.setAdapter(adapter);
        //TextView find
        userNameTv = findViewById(R.id.usernameEditTextview);
        fullNameTv = findViewById(R.id.userfullnameEditTextview);
        emailTv = findViewById(R.id.emailEditTextview);
        passwordTv = findViewById(R.id.passwordEditTextview);
        retypePasswordTv = findViewById(R.id.repasswordEditTextview);
        phoneTv = findViewById(R.id.phoneEditTextview);
        addressTextView= findViewById(R.id.addTextview);
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group_userType);

        getLocationonMap = findViewById(R.id.getlocationOnmap);
        getLocationonMap.setOnClickListener(this);
        users = new ArrayList<>();

        spinnerWorkerType.setVisibility(View.GONE);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (R.id.radio_user == checkedId){
                    //user checked
                    //Toast.makeText(getApplicationContext(),"user Checked",Toast.LENGTH_LONG).show();
                    spinnerWorkerType.setVisibility(View.GONE);
                }else if (R.id.radio_serviceman == checkedId){
                    //serviceman checked
                    //Toast.makeText(getApplicationContext(),"serviceman Checked",Toast.LENGTH_LONG).show();
                    spinnerWorkerType.setVisibility(View.VISIBLE);
                }
            }
        });
        // create item click listener of spinner
        spinnerWorkerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Type = spinnerWorkerType.getSelectedItem().toString();
                Toast.makeText(getApplicationContext(),Type+" is selected",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences receive = getSharedPreferences("Location",MODE_PRIVATE);

        double latitude = getIntent().getDoubleExtra("latitude", 0);
        lat = latitude;
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        log = longitude;
        Geocoder geocoder= new Geocoder(Signin.this);
        String location = null;
        //add.clear();
        try {
            add = geocoder.getFromLocation(latitude,longitude,1);
            //location=String.valueOf(add.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0;i<add.size();i++){
            location=add.get(i).getAddressLine(i);
        }
        /*try {
            location= String.valueOf(geocoder.getFromLocation(latitude,longitude,1));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //add.clear();
        loc= location;
        Toast.makeText(Signin.this, "Current Location in signin"+location, Toast.LENGTH_SHORT).show();
        //if(!location.equals(null))
        addressTextView.setText(location);
        //else
            //addressTextView.setText("Address");

        SharedPreferences.Editor editor = getSharedPreferences("Location",MODE_PRIVATE).edit();
        editor.putString("location", location);

    }

    //check username is already exist
    private boolean check_duplicate_userName(String username)
    {
        isFoundDuplicate.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").whereEqualTo("userName",username).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()){
                    //isFoundDuplicate.add(false);
                    is_duplicate_username = false;
                    //Toast.makeText(getApplicationContext(),"Duplicate not found",Toast.LENGTH_LONG).show();
                    registration();
                    clear_fields();

                }else{
                    //isFoundDuplicate.add(true);
                    //is_duplicate_username = true;
                    Toast.makeText(getApplicationContext(),"this username has already taken",Toast.LENGTH_LONG).show();

                }
            }
        });

        return is_duplicate_username;
    }

    // registration code here
    public void registration(){
        String id = databaseReference.push().getKey();
        String gender = "",userType="";

        // *** This three variables must be get from user. for simplicity it is taken as a default *** ///
        String Address = loc;
        String Latitude = String.valueOf(lat);
        String Longitude = String.valueOf(log);
        String rating = "5.0";
        //*** Required ***///


        // getting gender
        if (genderFlag == 1){
            gender = "Male";
        }
        else if (genderFlag == 2) {
            gender = "Female";
        }
        // getting user type
        if (typeFlag == 1) {
            userType = "User";
        }
        else if (typeFlag == 2) {
            userType = Type;
        }

        //Store data in Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference user = db.collection("Users");
        DocumentReference docUser = user.document(id);

        Users users = new Users(id,userNameTv.getText().toString().trim(),fullNameTv.getText().toString().trim(),
                emailTv.getText().toString().trim(),passwordTv.getText().toString(),phoneTv.getText().toString(),
                gender,userType, Address, Latitude, Longitude,get_current_Date(),rating);
        // saved data in firebase realtime database
        databaseReference.child(id).setValue(users);
        //saved data in cloud firestore
        docUser.set(users);

        //create userLog profile
        UserLog userLog = new UserLog(id,get_current_Date(),"0","0",userNameTv.getText().toString().trim());
        //Cloud Firestore
        FirebaseFirestore.getInstance().collection("UserLog").document(userNameTv.getText().toString().trim()).set(userLog);
        // Firebase realtime database
        FirebaseDatabase.getInstance().getReference("UserLog").child(userNameTv.getText().toString().trim()).setValue(userLog);


        Toast.makeText(this,"Account Created Successfully. Go to Login Page",Toast.LENGTH_LONG).show();

    }
    // this function will check all required field is fill uped or not
    public boolean check_form_fillup(){
        if (userNameTv.getText().toString().equals("") || fullNameTv.getText().toString().equals("") || emailTv.getText().toString().equals("")
        || passwordTv.getText().toString().equals("") || retypePasswordTv.getText().toString().equals("") || phoneTv.getText().toString().equals("")
        || typeFlag == 0 || genderFlag == 0){
            return true;
        }
        return false;
    }


    public void clear_fields(){
        userNameTv.setText("");
        fullNameTv.setText("");
        emailTv.setText("");
        passwordTv.setText("");
        retypePasswordTv.setText("");
        phoneTv.setText("");

    }


    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_male:
                if (checked)
                    genderFlag = 1;
                    break;
            case R.id.radio_female:
                if (checked)
                    genderFlag = 2;
                    break;
            case R.id.radio_user:
                if (checked){
                    typeFlag=1;
                    //spinnerWorkerType.setVisibility(View.GONE);
                }

                    break;
            case R.id.radio_serviceman:
                if (checked){
                    typeFlag=2;
                    //spinnerWorkerType.setVisibility(View.VISIBLE);
                }

                    break;
        }
    }

String intentName;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerButton:
                //user Registration code here
                if (check_form_fillup()){
                    Toast.makeText(this,"No field should be empty",Toast.LENGTH_LONG).show();
                }else{
                    if (passwordTv.getText().toString().equals(retypePasswordTv.getText().toString())){
                        //check duplicate username
                        check_duplicate_userName(userNameTv.getText().toString().trim());
                    }
                    else{
                        Toast.makeText(this,"Password mismatched",Toast.LENGTH_LONG).show();
                    }

                }
                break;

            case R.id.getlocationOnmap:
                Intent intentMap = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intentMap);
                finish();

                break;
        }
    }

    public String get_current_Date(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return formatter.format(date);

    }
}
