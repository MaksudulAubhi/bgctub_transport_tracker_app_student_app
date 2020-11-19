package com.example.bgctub_transport_tracker_studentapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

//maps activity for single vehicle location
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    FirebaseAuth mAuth;
    private GoogleMap mMap;
    String userId;
    DatabaseReference busLocationDatabaseRef;
    private HashMap<String, Marker> mMarkers = new HashMap<String, Marker>();
    private static final int PERMISSION_REQUEST = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //for student device location**
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        client = LocationServices.getFusedLocationProviderClient(this);

        //user Checker**
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, SigninActivity.class));
            finish();
        }

        //get userId string for transport Information Details activity**
        userId = getIntent().getStringExtra("userId");

        //database references**
        String database_path = "driver_app" + "/" + "transport_info_location" + "/" + userId;
        busLocationDatabaseRef = FirebaseDatabase.getInstance().getReference(database_path);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //   LatLng sydney = new LatLng(-34, 151);
        //  mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //  mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMaxZoomPreference(16);

        //update student locations if phone GPS is on**
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            requestLocationUpdates();
        }
        else{
            Toast.makeText(this, "Please turn on your phone location service to get your current location.", Toast.LENGTH_LONG).show();
        }
        //fetch bus locations
        subscribeToUpdate();
    }


    //fetches student locations and set to map**
    //check student current location and add marker**
    //check permission**
    /*
    //another method to get student location
    public void studentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng studentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMarkers.put("my location", mMap.addMarker(new MarkerOptions().position(studentLocation).title("My Location")));
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
        }
    }
    */
    //Get location callback for student location

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                LatLng studentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMarkers.put("my location", mMap.addMarker(new MarkerOptions()
                        .position(studentLocation)
                        .title("My Location")));
            }
        }
    };

    // request location check location in time period continously for student location**

    private void requestLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }


    //update markers**
    private void subscribeToUpdate() {
        busLocationDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setMarker(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //set Markers on position**
    private void setMarker(DataSnapshot dataSnapshot) {
        String key = userId;

        //icon for buses**
        BitmapDescriptor icon= BitmapDescriptorFactory.fromResource(R.drawable.ic_action_bus);

        //for vehicle information**

        String vehicle_name = dataSnapshot.child("transport_information").child("vehicle_name").getValue().toString();
        String vehicle_number = dataSnapshot.child("transport_information").child("vehicle_number").getValue().toString();
        String road = dataSnapshot.child("transport_information").child("travel_road").getValue().toString();
        String startLoc=dataSnapshot.child("transport_information").child("start_location").getValue().toString();
        String destination=dataSnapshot.child("transport_information").child("destinition").getValue().toString();
        String time=dataSnapshot.child("transport_information").child("start_time_schedule").getValue().toString();
        String date=dataSnapshot.child("transport_information").child("start_date_schedule").getValue().toString();
        String vehicle_info1 = "[Name: " + vehicle_name + "] [Number: " + vehicle_number + "] [Start Time: " + time + "]";
        String vehicle_info2=  "[Date: " + date + "] [Road: " + road + "] [From: " + startLoc + "]"+" [Destination: " + destination + "]";

        //upload location data to hashMap and get from hashMap**

        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.child("locations").getValue();
        double lat = Double.parseDouble(value.get("latitude").toString());
        double lng = Double.parseDouble(value.get("longitude").toString());


        //set location latitude and longitude**
        LatLng location = new LatLng(lat, lng);

        //if not already data in hashMap add data else get data and set new position
        if (!mMarkers.containsKey(key)) {
            mMarkers.put(key, mMap.addMarker(new MarkerOptions()
                    .title(vehicle_info1).position(location)
                    .snippet(vehicle_info2)
                    .icon(icon)
            ));
        } else {
            mMarkers.get(key).setPosition(location);
        }

        //area boundary builder**
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));


    }
}