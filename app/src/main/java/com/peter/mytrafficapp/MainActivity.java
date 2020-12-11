package com.peter.mytrafficapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
{
    private EditText mEmail, mPassword;
    private TextView forgotPasswordLink, registerLink;
    private Button loginBtn;

    private ProgressDialog mDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        mDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
    }

    private void initViews()
    {
        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);
        forgotPasswordLink = findViewById(R.id.login_forgot_password_link);
        registerLink = findViewById(R.id.login_create_account_link);
        loginBtn = findViewById(R.id.login_btn);

        loginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                verifyInputValues();
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    private void verifyInputValues()
    {
        mDialog.setTitle("Login");
        mDialog.setMessage("Please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (!email.isEmpty())
        {
            if (!password.isEmpty())
            {
                allowUserToLogin(email, password);
            }
            else
            {
                mPassword.setError("Password is required");
                mPassword.requestFocus();
                mDialog.dismiss();
            }
        }
        else
        {
            mEmail.setError("Email is required");
            mEmail.requestFocus();
            mDialog.dismiss();
        }
    }

    private void allowUserToLogin(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                            Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(homeIntent);
                            mDialog.dismiss();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String error = e.getMessage();
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                mDialog.dismiss();
            }
        });
    }
}
