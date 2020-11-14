package com.example.bgctub_transport_tracker_studentapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bgctub_transport_tracker_studentapp.ProfileUpdateActivity;
import com.example.bgctub_transport_tracker_studentapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private ProfileViewModel mViewModel;
    private TextView nameTextView, idTextView, genderTextView, departmentTextView, emailTextView, busStoppageTextView, programTextView, semesterTextView;
    private ImageButton editProfileButton;
    private FirebaseAuth mAuth;
    private DatabaseReference studentInfoDatabaseRef;
    private FirebaseUser mUser;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.profile_fragment, container, false);
        nameTextView = root.findViewById(R.id.student_name_textview);
        idTextView = root.findViewById(R.id.student_id_textview);
        genderTextView = root.findViewById(R.id.student_gender_textview);
        departmentTextView = root.findViewById(R.id.student_department_textview);
        emailTextView = root.findViewById(R.id.student_email_textview);
        busStoppageTextView = root.findViewById(R.id.student_Bus_Stoppage_textview);
        programTextView = root.findViewById(R.id.student_program_textview);
        semesterTextView = root.findViewById(R.id.student_semester_textview);
        editProfileButton = root.findViewById(R.id.profile_View_edit_imgBtn);

        editProfileButton.setOnClickListener(this);


        //firebase authentication and database reference**
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String userId = mUser.getUid();
        final String DATABASE_PATH = "student_app" + "/" +"student_profile"+"/"+ userId + "/" + "student_information";
        studentInfoDatabaseRef = FirebaseDatabase.getInstance().getReference(DATABASE_PATH);

        //add data to respected field
        addDataInField();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }

    //add data to respective textView if data available**
    public void addDataInField() {
        studentInfoDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String name = snapshot.child("name").getValue().toString();
                    String id = snapshot.child("id").getValue().toString();
                    String gender = snapshot.child("gender").getValue().toString();
                    String department = snapshot.child("department").getValue().toString();
                    String bus_stoppage = snapshot.child("bus_stoppage").getValue().toString();
                    String program = snapshot.child("program").getValue().toString();
                    String semester = snapshot.child("semester").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();

                    nameTextView.setText(name);
                    idTextView.setText(id);
                    genderTextView.setText(gender);
                    departmentTextView.setText(department);
                    busStoppageTextView.setText(bus_stoppage);
                    programTextView.setText(program);
                    semesterTextView.setText(semester);
                    emailTextView.setText(email);

                } catch (Exception exception) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void onClick(View v) {
        if (v == editProfileButton) {
            startActivity(new Intent(getActivity(), ProfileUpdateActivity.class));
        }
    }
}