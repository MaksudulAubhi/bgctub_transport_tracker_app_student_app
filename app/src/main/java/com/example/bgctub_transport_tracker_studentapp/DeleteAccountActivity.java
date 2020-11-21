package com.example.bgctub_transport_tracker_studentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText reAuthenticPassEditText;
    private Button deleteAccountButton;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference userIdDatabaseRef;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        reAuthenticPassEditText=findViewById(R.id.reAuthentic_pass_editText);
        deleteAccountButton=findViewById(R.id.delete_Account_button);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String userId = mUser.getUid();

        //user information database path
        final String DATABASE_PATH = "student_app" + "/"+"student_profile"+"/"+ userId;
        userIdDatabaseRef = FirebaseDatabase.getInstance().getReference(DATABASE_PATH);

        //check user logon or not, if not go sign up activity
        if (mUser == null) {
            startActivity(new Intent(this, SigninActivity.class));
            finish();
        }

        mProgressDialog = new ProgressDialog(this);
        deleteAccountButton.setOnClickListener(this);
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

    //delete a user and user information**

    public void deleteAccountAndInfo() {
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.show();

        assert mUser != null;
        if (mUser != null) {
            mUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //remove user info
                        userIdDatabaseRef.removeValue();
                        startActivity(new Intent(DeleteAccountActivity.this, SigninActivity.class));
                        finish();
                        Toast.makeText(DeleteAccountActivity.this, "Your account deleted correctly", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(DeleteAccountActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }

                    mProgressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(DeleteAccountActivity.this, "Please, try again later.", Toast.LENGTH_LONG).show();
                    Toast.makeText(DeleteAccountActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
            });

        }
    }


    //re-authentic user if re-authenticated then user account will deleted**
    public void reAuthenticUser(){
        String email=mUser.getEmail().toString().trim();
        String password=reAuthenticPassEditText.getText().toString().trim();

        //input validation
        if(TextUtils.isEmpty(password)){
            reAuthenticPassEditText.setError("Please enter your password");
            return;
        }
        if(password.length()<6){
            reAuthenticPassEditText.setError("Please enter password in correct format.");
            return;
        }

        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();
        //re-authenticate user
        AuthCredential authCredential= EmailAuthProvider.getCredential(email,password);
        mUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //if task success then user account and information will deleted and open sign in activity
                    deleteAccountAndInfo();
                }else{
                    Toast.makeText(DeleteAccountActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
                mProgressDialog.dismiss();
            }
        });
    }



    @Override
    public void onClick(View v) {
        if(v==deleteAccountButton){
            //call user re-authentication method
            reAuthenticUser();
        }
    }
}