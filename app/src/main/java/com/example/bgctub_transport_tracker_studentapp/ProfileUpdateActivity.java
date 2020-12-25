package com.example.bgctub_transport_tracker_studentapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bgctub_transport_tracker_studentapp.model.StudentInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileUpdateActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText nameEdiText, idEdiText, departmentEditText, busStoppageEditText, programEditText, semesterEdiText;
    private RadioGroup genderRadioGroup;
    private RadioButton genderRadioButton,maleRadioButton,femaleRadioButton,othersRadioButton;
    private Button profileUpdateButton;
    private TextView genderErrorTextView;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference studentInfoDatabaseRef;
    private ProgressDialog progressDialog;
    private String gender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        nameEdiText = findViewById(R.id.student_name_editText);
        idEdiText = findViewById(R.id.student_id_editText);
        departmentEditText = findViewById(R.id.student_department_editText);
        busStoppageEditText = findViewById(R.id.student_Bus_Stoppage_editText);
        programEditText = findViewById(R.id.student_program_editText);
        semesterEdiText = findViewById(R.id.student_semester_editText);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        maleRadioButton=findViewById(R.id.radioMaleText);
        femaleRadioButton=findViewById(R.id.radioFemaleText);
        othersRadioButton=findViewById(R.id.radioOthersText);
        profileUpdateButton = findViewById(R.id.profile_update_button);
        genderErrorTextView = findViewById(R.id.genderErrorTextView);
        profileUpdateButton.setOnClickListener(this);
        progressDialog=new ProgressDialog(this);

        //firebase authentication and database reference**
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String userId = mUser.getUid();
        final String DATABASE_PATH = "student_app" + "/" +"student_profile"+"/"+ userId + "/" + "student_information";
        studentInfoDatabaseRef = FirebaseDatabase.getInstance().getReference(DATABASE_PATH);

        //check user logon or not, if not go to signIn activity
        if (mUser == null) {
            startActivity(new Intent(this, SigninActivity.class));
            finish();
        }
        //add data to editText if available
        addDataInField();
    }

    //add data to respective editText if data available**
    public void addDataInField(){
        studentInfoDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    String name=snapshot.child("name").getValue().toString();
                    String id=snapshot.child("id").getValue().toString();
                    String gender=snapshot.child("gender").getValue().toString();
                    String department=snapshot.child("department").getValue().toString();
                    String bus_stoppage=snapshot.child("bus_stoppage").getValue().toString();
                    String program=snapshot.child("program").getValue().toString();
                    String semester=snapshot.child("semester").getValue().toString();

                    nameEdiText.setText(name);
                    idEdiText.setText(id);
                    departmentEditText.setText(department);
                    busStoppageEditText.setText(bus_stoppage);
                    programEditText.setText(program);
                    semesterEdiText.setText(semester);

                    //for gender
                    if(gender.equals("Male")){
                        genderRadioGroup.check(R.id.radioMaleText);
                    }
                    if(gender.equals("Female")){
                        genderRadioGroup.check(R.id.radioFemaleText);
                    }
                    if(gender.equals("Others")){
                        genderRadioGroup.check(R.id.radioOthersText);
                    }

                }catch (Exception exception){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileUpdateActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //student information update methods with input validation**
    public void studentInfoUpdate() {
        //other options
        String userId = mUser.getUid();
        String name = nameEdiText.getText().toString().trim();
        String id = idEdiText.getText().toString().trim();
        String department = departmentEditText.getText().toString().trim();
        String email = mUser.getEmail();
        String busStoppage = busStoppageEditText.getText().toString().trim();
        String program = programEditText.getText().toString().trim();
        String semester = semesterEdiText.getText().toString().trim();


        //input validation**
        if (TextUtils.isEmpty(name)) {
            nameEdiText.setError("Please enter your full name");
            return;
        }
        if (TextUtils.isEmpty(id)) {
            idEdiText.setError("Please enter your registration id");
            return;
        }
        if (TextUtils.isEmpty(department)) {
            departmentEditText.setError("Please enter department name");
            return;
        }
        if (TextUtils.isEmpty(program)) {
            programEditText.setError("Please enter your program");
            return;
        }
        if (TextUtils.isEmpty(semester)) {
            semesterEdiText.setError("Please enter your semester");
            return;
        }
        //For gender radio button input validation and get text
        int genderID = genderRadioGroup.getCheckedRadioButtonId();
        if (genderID == -1) {
            genderErrorTextView.setVisibility(View.VISIBLE);
            return;
        } else {
            genderRadioButton = (RadioButton) findViewById(genderID);
            gender = genderRadioButton.getText().toString().trim();
        }

        if (TextUtils.isEmpty(busStoppage)) {
            busStoppageEditText.setError("Please enter your bus stoppage name");
            return;
        }





        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        //data upload**
        StudentInformation studentInformation=new StudentInformation(userId,name,id,gender,department,email,busStoppage,program,semester);
        try {
            studentInfoDatabaseRef.setValue(studentInformation);

            Toast.makeText(this,"Profile Updated Successfully", Toast.LENGTH_LONG).show();
        }catch(Exception exception){

            Toast.makeText(this,"Please try again later",Toast.LENGTH_LONG).show();
        }
        progressDialog.dismiss();

    }



    @Override
    public void onClick(View v) {
        if(v==profileUpdateButton){
            //update student information
            studentInfoUpdate();
        }
    }
}