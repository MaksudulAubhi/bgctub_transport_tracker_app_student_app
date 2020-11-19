package com.example.bgctub_transport_tracker_studentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TransportInfoDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private String userId;
    private TextView locationReqTextView;
    private TextView statusTextView;
    private TextView driverNameTextView;
    private TextView transportNameTextView, transportNumberTextView;
    private TextView schedule_time_TextView,schedule_day_TextView,scheduleRoadTextView, start_loc_TextView,destinitionTextView;
    private DatabaseReference transportInfoDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_info_details);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //get userId from transport information fragment
        userId=getIntent().getStringExtra("userId");

        locationReqTextView=findViewById(R.id.openLocationReqTextView);
        statusTextView=findViewById(R.id.driver_status_textview);

        driverNameTextView=findViewById(R.id.driver_name_textview);


        transportNameTextView=findViewById(R.id.transport_name_textview);
        transportNumberTextView=findViewById(R.id.transport_number_textview);

        schedule_time_TextView=findViewById(R.id.schedule_time_textview);
        schedule_day_TextView=findViewById(R.id.schedule_day_textview);
        scheduleRoadTextView=findViewById(R.id.shedule_road_textview);
        start_loc_TextView=findViewById(R.id.schedule_start_loc_textview);
        destinitionTextView=findViewById(R.id.schedule_destinition_textview);

        locationReqTextView.setOnClickListener(this);

        //database path and references
        final String DATABASE_PATH= "driver_app" + "/" +"transport_info_location"+"/"+ userId;

        transportInfoDatabaseRef= FirebaseDatabase.getInstance().getReference(DATABASE_PATH);

        //add information method
        addInformation();

    }

    //if data available data add to textView**
    public void addInformation(){

        transportInfoDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {

                    String name = snapshot.child("transport_information").child("driver_name").getValue().toString();
                    String transName = snapshot.child("transport_information").child("vehicle_name").getValue().toString();
                    String transNumber = snapshot.child("transport_information").child("vehicle_number").getValue().toString();
                    String schTime = snapshot.child("transport_information").child("start_time_schedule").getValue().toString();
                    String schDate = snapshot.child("transport_information").child("start_date_schedule").getValue().toString();
                    String schRoad = snapshot.child("transport_information").child("travel_road").getValue().toString();
                    String startLoc = snapshot.child("transport_information").child("start_location").getValue().toString();
                    String destination = snapshot.child("transport_information").child("destinition").getValue().toString();

                    driverNameTextView.setText(name);
                    transportNameTextView.setText(transName);
                    transportNumberTextView.setText(transNumber);
                    schedule_time_TextView.setText(schTime);
                    schedule_day_TextView.setText(schDate);
                    scheduleRoadTextView.setText(schRoad);
                    start_loc_TextView.setText(startLoc);
                    destinitionTextView.setText(destination);

                    //for driver status **
                    try {
                        String status = snapshot.child("status").getValue().toString();
                        if (status.equals("active")) {
                            //if active change text color to green and set text active
                            statusTextView.setText(status);
                            statusTextView.setTextColor(Color.GREEN);
                        }
                        if (status.equals("inactive")) {
                            statusTextView.setText(status);
                            statusTextView.setTextColor(Color.RED);
                        }
                    }catch (Exception exception){

                    }

                }catch(Exception exception){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TransportInfoDetailsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        if(v==locationReqTextView){
            //if status is active then userId sent to map activity
            //and open mapsActivity
            if(statusTextView.getText().toString().trim().equals("active")){
                Intent intent = new Intent(TransportInfoDetailsActivity.this, MapsActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);

            }else{
                Toast.makeText(this,"Sorry, Driver is not active",Toast.LENGTH_LONG).show();
            }

        }
    }
}