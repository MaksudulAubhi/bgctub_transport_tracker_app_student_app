package com.example.bgctub_transport_tracker_studentapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MapsActivityAllVehicle extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private FloatingActionButton mapReloadFab, backBtnFab, reloadFab;
    FirebaseAuth mAuth;
    DatabaseReference busLocationDatabaseRef;
    private HashMap<String, Marker> mMarkers = new HashMap<String, Marker>();
    private static final int PERMISSION_REQUEST = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_all_vehicle);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
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


        //database references**
        String database_path = "driver_app" + "/" + "transport_info_location";
        busLocationDatabaseRef = FirebaseDatabase.getInstance().getReference(database_path);


        //for floating action button
        mapReloadFab = findViewById(R.id.fabAllBusMapReload);
        backBtnFab = findViewById(R.id.fabAllBusBackBtn);
        reloadFab = findViewById(R.id.fabAllBusActivityReload);
        mapReloadFab.setOnClickListener(this);
        backBtnFab.setOnClickListener(this);
        reloadFab.setOnClickListener(this);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(16);
        mMap.setOnInfoWindowClickListener(this);

        //update student locations if phone GPS is on**
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            requestLocationUpdates();
        } else {
            Toast.makeText(this, "Please turn on your phone location service to get your current location.", Toast.LENGTH_LONG).show();
        }
        //fetch bus locations
        subscribeToUpdate();
    }

    //Get location callback for student location

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                LatLng studentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMarkers.put("my_location", mMap.addMarker(new MarkerOptions()
                        .position(studentLocation)
                        .title("My Location")
                        .snippet("")));
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
    public void subscribeToUpdate() {
        busLocationDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {


                    for (DataSnapshot userIdSnapshot : snapshot.getChildren()) {
                        String userId = userIdSnapshot.getKey();

                        //if status path is present and status is active then others information will get and fetch data
                        if (userIdSnapshot.hasChild("status")) {
                            if (userIdSnapshot.child("status").getValue().toString().equals("active")) {
                                setMarker(snapshot, userId);
                            }
                        }
                    }
                } catch (Exception exception) {
                    Toast.makeText(MapsActivityAllVehicle.this, "Sorry no driver is active now.", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //set Markers on position**
    private void setMarker(DataSnapshot dataSnapshot, String userId) {
        //userId is key

        //icon for buses**
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.bus_map);

        //for vehicle information**
        //if status true: active then others information will get and fetch data
        //if child available show transport information data else show empty


        String vehicle_info1 = "Sorry information not available.", vehicle_info2 = "Information not given by driver.";

        if (dataSnapshot.child(userId).hasChild("transport_information")) {

            String vehicle_name = dataSnapshot.child(userId).child("transport_information").child("vehicle_name").getValue().toString();
            String vehicle_number = dataSnapshot.child(userId).child("transport_information").child("vehicle_number").getValue().toString();
            String road = dataSnapshot.child(userId).child("transport_information").child("travel_road").getValue().toString();
            String startLoc = dataSnapshot.child(userId).child("transport_information").child("start_location").getValue().toString();
            String destination = dataSnapshot.child(userId).child("transport_information").child("destinition").getValue().toString();
            String time = dataSnapshot.child(userId).child("transport_information").child("start_time_schedule").getValue().toString();
            String date = dataSnapshot.child(userId).child("transport_information").child("start_date_schedule").getValue().toString();
            vehicle_info1 = "Company Name: \n" + vehicle_name + "  \n\nVehicle Number: \n" + vehicle_number + "  \n\nRoad: \n" + road;
            vehicle_info2 = "Starting Place: \n" + startLoc + "  \n\nStart Time: \n" + time + "  \n\nStart Date: \n" + date + "  \n\nDestination:  \n" + destination;

        }
        //upload location data to hashMap and get from hashMap**

        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.child(userId).child("locations").getValue();
        double lat = Double.parseDouble(value.get("latitude").toString());
        double lng = Double.parseDouble(value.get("longitude").toString());


        //set location latitude and longitude**
        LatLng location = new LatLng(lat, lng);

        //if not already data in hashMap add data else get data and set new position
        if (!mMarkers.containsKey(userId)) {
            mMarkers.put(userId, mMap.addMarker(new MarkerOptions()
                    .title(vehicle_info1).position(location)
                    .snippet(vehicle_info2)
                    .icon(icon)
            ));
        } else {
            mMarkers.get(userId).setPosition(location);
        }

        //area boundary builder**
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));


    }

    @Override
    public void onClick(View v) {
        if (v == mapReloadFab) {
            //reload map to click
            onMapReady(mMap);
        }
        if (v == backBtnFab) {
            //back to parent activity
            onBackPressed();
        }
        if (v == reloadFab) {
            //recreate activity to click
            recreate();
        }
    }

    //alert dialog create with vehicle info after click marker info window verification**
    AlertDialog.Builder alertDialogBuilder(Context context, String information) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Vehicle Information");
        builder.setMessage(information);
        builder.setIcon(R.drawable.bus_map);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

        return builder;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //after window click open alert window**
        String information = marker.getTitle() + "\n\n" + marker.getSnippet();

        alertDialogBuilder(this, information);

    }
}