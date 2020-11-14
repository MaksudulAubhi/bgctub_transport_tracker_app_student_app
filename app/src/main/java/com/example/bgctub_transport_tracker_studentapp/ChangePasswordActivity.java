package com.example.bgctub_transport_tracker_studentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText currentPasswordEditText,newPasswordEditText,newConfirmPasswordEditText;
    private Button changePassButton;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        currentPasswordEditText=findViewById(R.id.current_pass_editText);
        newPasswordEditText=findViewById(R.id.new_password_editText);
        newConfirmPasswordEditText=findViewById(R.id.confirm_new_password_editText);
        changePassButton=findViewById(R.id.change_password_button);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        //check user logon or not, if not go sign up activity
        if (mUser == null) {
            startActivity(new Intent(this, SigninActivity.class));
            finish();
        }

        mProgressDialog = new ProgressDialog(this);

        changePassButton.setOnClickListener(this);
    }

    //change user password method**
    public void changePassword(String newPassword ){

        //change user password**
        mUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ChangePasswordActivity.this,"Changed password Successfully",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(ChangePasswordActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    //re-authentic user if re-authenticated then user account will deleted**
    public void reAuthenticUser(){
        String email=mUser.getEmail().toString().trim();
        String currentPassword=currentPasswordEditText.getText().toString().trim();
        String newPassword=newPasswordEditText.getText().toString().trim();
        String confirmNewPassword=newConfirmPasswordEditText.toString().trim();

        //input validation**
        if(TextUtils.isEmpty(currentPassword)){
            currentPasswordEditText.setError("Please enter your current password");
            return;
        }
        if(currentPassword.length()<6){
            currentPasswordEditText.setError("Please enter password in correct format.");
            return;
        }
        if(TextUtils.isEmpty(newPassword)){
            newPasswordEditText.setError("please enter a new password.");
            return;
        }
        if(newPassword.length()<6){
            newPasswordEditText.setError("please enter a 6 characters password.");
            return;
        }
        if(TextUtils.isEmpty(confirmNewPassword)){
            newConfirmPasswordEditText.setError("please enter confirm password.");
            return;
        }
        if(confirmNewPassword.length()<6 || newPassword.equals(confirmNewPassword)){
            newConfirmPasswordEditText.setError("confirm password not matched.");
            return;
        }

        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();

        //re-authenticate user
        AuthCredential authCredential= EmailAuthProvider.getCredential(email,currentPassword);
        mUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //call user change password method if task success
                   changePassword(newPassword);
                }else{
                    //set error for current password error
                    currentPasswordEditText.setError("Please check your password");
                    Toast.makeText(ChangePasswordActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

                mProgressDialog.dismiss();
            }
        });
    }


    @Override
    public void onClick(View v) {
        if(v==changePassButton){
            //user re-authentication and change password
            reAuthenticUser();
        }
    }
}