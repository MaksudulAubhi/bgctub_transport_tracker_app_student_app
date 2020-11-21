package com.example.bgctub_transport_tracker_studentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView deleteReqTextView,changePassReqTextView;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        deleteReqTextView=findViewById(R.id.deleteAccountTextView);
        changePassReqTextView=findViewById(R.id.changePasswordReqTextView);

        deleteReqTextView.setOnClickListener(this);
        changePassReqTextView.setOnClickListener(this);
        //check user**
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(SettingsActivity.this,SigninActivity.class));
            finish();
        }
    }

    //used for check user again when user back previous session or resume of activity

    @Override
    protected void onPostResume() {
        super.onPostResume();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            finish();
        }
    }


    @Override
    public void onClick(View v) {
        if(v==deleteReqTextView){
            startActivity(new Intent(SettingsActivity.this,DeleteAccountActivity.class));
        }
        if(v==changePassReqTextView){
            startActivity(new Intent(SettingsActivity.this,ChangePasswordActivity.class));
        }
    }
}