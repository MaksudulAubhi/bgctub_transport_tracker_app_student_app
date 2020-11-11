package com.example.bgctub_transport_tracker_studentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText signInEmailEditText,signInPasswordEditText;
    private Button signInButton;
    private TextView forgotPassTextView, signUpReqTextView;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        signInEmailEditText=findViewById(R.id.signin_email_editText);
        signInPasswordEditText=findViewById(R.id.signin_password_editText);
        signInButton=findViewById(R.id.signin_button);
        forgotPassTextView=findViewById(R.id.forgetPasswordRequestTextView);
        signUpReqTextView=findViewById(R.id.signupRequestTextView);

        signInButton.setOnClickListener(this);
        forgotPassTextView.setOnClickListener(this);
        signUpReqTextView.setOnClickListener(this);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();

        //check user logon or not
        if(mUser!=null && mUser.isEmailVerified()){
            startActivity(new Intent(this,AppMainActivity.class));
            finish();
        }

        progressDialog=new ProgressDialog(this);

    }

    //user sign in method**
    public void userSignIn(){
        String email=signInEmailEditText.getText().toString().trim();
        String password=signInPasswordEditText.getText().toString().trim();

        //input validation**
        if(TextUtils.isEmpty(email)){
            signInEmailEditText.setError("Please enter your account email address.");
            return;
        }
        if(TextUtils.isEmpty(password)){
            signInPasswordEditText.setError("please enter your password.");
            return;
        }
        if(password.length()<6){
            signInPasswordEditText.setError("please enter correct password.");
            return;
        }


        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        //sign in with email and password
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //check verified email or not
                    if(mAuth.getCurrentUser().isEmailVerified()){
                        startActivity(new Intent(SigninActivity.this,AppMainActivity.class));
                        finish();
                        Toast.makeText(SigninActivity.this,"Welcome "+mAuth.getCurrentUser().getEmail(),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SigninActivity.this,"Please verify your email address.",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(SigninActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });


    }

    @Override
    public void onClick(View v) {
        if(v==signInButton){
            //user sign in after click
            userSignIn();
        }
        if(v==forgotPassTextView){
            //forgot pass activity open
            startActivity(new Intent(this,ForgetPasswordActivity.class));
        }
        if(v==signUpReqTextView){
            //sign up activity open
            startActivity(new Intent(this,SignupActivity.class));
        }
    }
}