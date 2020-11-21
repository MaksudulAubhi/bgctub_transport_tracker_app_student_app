package com.example.bgctub_transport_tracker_studentapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bgctub_transport_tracker_studentapp.MapsActivityAllVehicle;
import com.example.bgctub_transport_tracker_studentapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private TextView totalUserCountTextView, activeUserCountTextView;
    private FloatingActionButton fabLocationButton;
    private long totalUser = 0, activeUser = 0;
    private FirebaseAuth mAuth;
    private DatabaseReference vehicleListDatabaseRef;


    private HomeViewModel mViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        totalUserCountTextView = root.findViewById(R.id.totalBusCount);
        activeUserCountTextView = root.findViewById(R.id.activeBusCount);
        fabLocationButton = root.findViewById(R.id.fabHomeLocationBtn);

        fabLocationButton.setOnClickListener(this);

        //check user and databaseReferences
        mAuth = FirebaseAuth.getInstance();

        final String DATABASE_PATH = "driver_app" + "/" + "transport_info_location";
        vehicleListDatabaseRef = FirebaseDatabase.getInstance().getReference(DATABASE_PATH);

        //get total user and active users
        getUserInfo();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }


    //count all user and active user **
    public void getUserInfo() {
        vehicleListDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                activeUser=0;
                try {
                    //count total userID
                    totalUser = snapshot.getChildrenCount();
                    for (DataSnapshot userIdSnapShot : snapshot.getChildren()) {
                        //check 'status' path have if then check active or not
                        if (userIdSnapShot.hasChild("status")) {
                            String status = userIdSnapShot.child("status").getValue().toString();
                            if (status.equals("active")) {
                                activeUser = activeUser + 1;
                            }
                        }
                    }
                } catch (Exception exception) {
                    Toast.makeText(getActivity(), "Sorry no information available.", Toast.LENGTH_LONG).show();
                }
                activeUserCountTextView.setText(String.valueOf(activeUser));
                totalUserCountTextView.setText(String.valueOf(totalUser));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }


    @Override
    public void onClick(View v) {
        if (v == fabLocationButton) {
            //open all buses map to click
            startActivity(new Intent(getActivity(), MapsActivityAllVehicle.class));
        }
    }
}