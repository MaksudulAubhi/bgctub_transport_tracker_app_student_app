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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText signUpEmailEditText,signUpPasswordEditText,signUpConfirmPasswordEditText;
    private Button signUpButton;
    private TextView helpReqTextView;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        signUpEmailEditText=findViewById(R.id.signup_email_editText);
        signUpPasswordEditText=findViewById(R.id.signup_password_editText);
        signUpConfirmPasswordEditText=findViewById(R.id.signup_confirm_password_editText);
        signUpButton=findViewById(R.id.signup_button);
        helpReqTextView=findViewById(R.id.helpRequestTextView);


        signUpButton.setOnClickListener(this);
        helpReqTextView.setOnClickListener(this);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        //check user logon or not
        if(mUser!=null && mUser.isEmailVerified()){
            startActivity(new Intent(this,AppMainActivity.class));
            finish();
        }

        progressDialog=new ProgressDialog(this);

    }

    //user signUp method**
    public void userSignUp(){
        String email=signUpEmailEditText.getText().toString().trim();
        String password=signUpPasswordEditText.getText().toString().trim();
        String conPassword=signUpConfirmPasswordEditText.getText().toString().trim();

        //input validation**
        if(TextUtils.isEmpty(email)){
            signUpEmailEditText.setError("Please, enter a valid email address");
            return;
        }
        if(TextUtils.isEmpty(password)){
            signUpPasswordEditText.setError("please enter a password.");
            return;
        }
        if(password.length()<6){
            signUpPasswordEditText.setError("please enter a 6 characters password.");
            return;
        }
        if(TextUtils.isEmpty(conPassword)){
            signUpConfirmPasswordEditText.setError("please enter confirm password.");
            return;
        }
        if(conPassword.length()<6 || !conPassword.equals(password)){
            signUpConfirmPasswordEditText.setError("confirm password not matched.");
            return;
        }

        progressDialog.setMessage("Please wait...");
        progressDialog.show();


        //SignUp with email and password**

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //sent verification email**
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SignupActivity.this,R.string.verify_account_info_msg,Toast.LENGTH_LONG).show();
                                //create alert dialog message
                                alertDialogBuilder(SignupActivity.this);
                            }else{
                                Toast.makeText(SignupActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(SignupActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }

        });
    }

    //alert dialog create after sent verification email**
    AlertDialog.Builder alertDialogBuilder(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Verification Account Message");
        builder.setMessage(R.string.verify_account_info_msg);
        builder.setIcon(R.drawable.ic_action_info);
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
    public void onClick(View v) {
        if(v==signUpButton){
            //user sign up method
            userSignUp();
        }
    }
}