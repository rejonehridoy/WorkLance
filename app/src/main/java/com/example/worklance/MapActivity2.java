package com.example.worklance;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapActivity2 extends FragmentActivity implements OnMapReadyCallback{

    private List<Address> address;
    private ArrayAdapter<String> adapter;
    private static final int Threshold = 1;
    private GoogleMap gMap;
    private Geocoder geocoder, geocoder1;
    private LinearLayout locationLayout;
    private Button locationButton,confirmButton;
    private ImageView removeImageView,locationImageView;
    private FusedLocationProviderClient mLocationClient;
    private static final int PLAY_SERVICES_ERROR_CODE = 9002;
    public static final int GPS_REQUEST_CODE = 9003;
    public static final int PERMISSION_REQUEST_CODE = 9001;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Location mLastKnownLocation;
    private static final int DEFAULT_ZOOM = 15;
    private static final String TAG = MapActivity.class.getSimpleName();
    private LatLng currentLoc;
    private Marker marker = null;
    private String userName,uid,userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);

        locationLayout = findViewById(R.id.locationLayout);
        locationButton = findViewById(R.id.locationButton);
        removeImageView = findViewById(R.id.ic_cancel_icon);
        locationImageView = findViewById(R.id.ic_mylocation);
        confirmButton = findViewById(R.id.confirmButton);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>());

        final AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        actv.setThreshold(Threshold);
        actv.setAdapter(adapter);
        actv.setTextColor(Color.RED);

        mLocationClient = LocationServices.getFusedLocationProviderClient(this);
        removeImageView.setVisibility(View.INVISIBLE);
        load_user_info();
        initials();




        actv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.clear();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() != 0) {
                    removeImageView.setVisibility(View.VISIBLE);
                    removeImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            actv.setText("");
                        }
                    });
                } else
                    removeImageView.setVisibility(View.INVISIBLE);

                Log.i("char", charSequence.toString());
                check(getBaseContext(), charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //removeImageView.setVisibility(View.INVISIBLE);
                if(marker!=null){
                    marker.remove();
                }
                String selected = (String) adapterView.getItemAtPosition(position);
                Log.i("selected", selected);
                int pos = Arrays.asList(adapter).indexOf(selected);
                Log.i("pos", String.valueOf(pos));
                List<Address> place = null;
                geocoder1 = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    place = geocoder1.getFromLocationName(selected, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LatLng loc = new LatLng(place.get(0).getLatitude(), address.get(0).getLongitude());
                marker = gMap.addMarker(new MarkerOptions().position(loc));
                float zoomLevel = 17.5f;
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,zoomLevel));
            }
        });
        locationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(marker == null) {
                    initials();
                    getDeviceLocation();

                    Intent confirmLocationIntent = new Intent(getBaseContext(), userHome.class);
                    confirmLocationIntent.putExtra("latitude",currentLoc.latitude);
                    confirmLocationIntent.putExtra("longitude",currentLoc.longitude);
                    startActivity(confirmLocationIntent);
                }
                else{
                    Intent confirmLocationIntent = new Intent(getBaseContext(), userHome.class);
                    confirmLocationIntent.putExtra("latitude",marker.getPosition().latitude);
                    confirmLocationIntent.putExtra("longitude",marker.getPosition().longitude);
                    startActivity(confirmLocationIntent);
                }

                finish();


            }
        });


    }

    protected void onResume() {
        super.onResume();
        initials();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        gMap = googleMap;

        gMap.getUiSettings().setZoomControlsEnabled(true);

        getLocationPermission();
        getDeviceLocation();

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                if(marker!=null){
                    marker.remove();
                }
                LatLng touchLoc = new LatLng(latLng.latitude,latLng.longitude);
                float zoomLevel = 17.5f;
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(touchLoc,zoomLevel));
                marker = gMap.addMarker(new MarkerOptions().position(touchLoc));
            }
        });
    }

    private void initials() {
        if (isServicesOk()) {
            if (isGPSEnabled()) {
                getLocationPermission();
                if (mLocationPermissionGranted) {
                    Toast.makeText(this, "Ready to Map", Toast.LENGTH_SHORT).show();

                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);

                    supportMapFragment.getMapAsync(this);
                } else {
                    requestLocationPermission();
                }

            }
        }
    }
    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            locationLayout.setVisibility(View.INVISIBLE);
            return true;
        } else {

            locationLayout.setVisibility(View.VISIBLE);
            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(locationIntent, GPS_REQUEST_CODE);
                }
            });
        }
        return false;
    }


    private boolean isServicesOk() {

        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();

        int result = googleApi.isGooglePlayServicesAvailable(this);

        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApi.isUserResolvableError(result)) {
            Dialog dialog = googleApi.getErrorDialog(this, result, PLAY_SERVICES_ERROR_CODE, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface task) {
                    Toast.makeText(MapActivity2.this, "Dialog is cancelled by User", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();


        } else {
            Toast.makeText(this, "Play services are required by this application", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

            }
        }
    }





    private void check(Context context, String location) {

        geocoder = new Geocoder(context, Locale.getDefault());


        try {
            address = geocoder.getFromLocationName(location, 15);

            Log.i("address", String.valueOf(address.size()));
            Log.i("address", address.toString());

            for (int i = 0; i <= address.size(); i++) {
                String city = address.get(i).getLocality();
                /*String country = address.get(i).getCountryName();*/
                String add = address.get(i).getAddressLine(i);
                String ex = address.get(i).getFeatureName();
                Log.i("city+country+add", add + " " + ex + " " + city);

                adapter.add(add);


                Log.i("adapter", adapter.toString());
            }
        } catch (Exception e) {

        }
    }



    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }



    private void getDeviceLocation() {
        if (gMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                gMap.setMyLocationEnabled(true);
                gMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                gMap.setMyLocationEnabled(false);
                gMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }

        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                currentLoc = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                float zoomLevel = 17.5f;
                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,zoomLevel));
                                //marker = gMap.addMarker(new MarkerOptions().position(currentLoc));

                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            gMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
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

