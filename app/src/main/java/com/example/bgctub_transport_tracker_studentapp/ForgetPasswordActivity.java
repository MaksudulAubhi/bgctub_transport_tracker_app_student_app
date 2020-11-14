package com.example.bgctub_transport_tracker_studentapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText forgotPassEmailEditText;
    private Button forgetPassButton;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();

        forgotPassEmailEditText = findViewById(R.id.forgotPass_email_editText);
        forgetPassButton = findViewById(R.id.forgotPass_button);

        forgetPassButton.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    //sent forgot pass email link
    public void forgotPassRequest() {
        String email = forgotPassEmailEditText.getText().toString().trim();
        //input validation
        if (TextUtils.isEmpty(email)) {
            forgotPassEmailEditText.setError("Please enter your account email address");
            return;
        }
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

       //send email with recover password link
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgetPasswordActivity.this, "Please, Check your email to recover the password", Toast.LENGTH_LONG).show();
                    //create alert dialog message
                    alertDialogBuilder(ForgetPasswordActivity.this);
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });

    }

    //alert dialog create after sent reset password email**
    AlertDialog.Builder alertDialogBuilder(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Forgot Password Message");
        builder.setMessage(R.string.forgetPassInfoMsg);
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
        if (v == forgetPassButton) {
            //sent email
            forgotPassRequest();
        }
    }
}