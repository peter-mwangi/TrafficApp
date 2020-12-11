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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import javax.security.auth.login.LoginException;

public class RegisterActivity extends AppCompatActivity
{
    private EditText mFullName, mEmail, mPassword, mConfirmPassword;
    private TextView mLoginLink;
    private Button registerBtn;

    private ProgressDialog mDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        mDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    private void initViews()
    {
        mFullName = findViewById(R.id.register_first_name);
        mEmail = findViewById(R.id.register_email);
        mPassword = findViewById(R.id.register_password);
        mConfirmPassword = findViewById(R.id.register_confirm_password);
        mLoginLink = findViewById(R.id.register_login_link);
        registerBtn = findViewById(R.id.register_btn);


        registerBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               verifyInputValues();

            }
        });

        mLoginLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent loginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(loginIntent);

            }
        });
    }

    private void verifyInputValues()
    {
        mDialog.setTitle("Register");
        mDialog.setMessage("Please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        String fullName = mFullName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String confirmPassword = mConfirmPassword.getText().toString().trim();

        if (!fullName.isEmpty())
        {
            if (!email.isEmpty())
            {
                if (!password.isEmpty())
                {
                    if (!confirmPassword.isEmpty())
                    {
                        if (password.equals(confirmPassword))
                        {
                            createNewUser(fullName,email,password);
                        }
                        else
                        {
                            mPassword.setError("Password do not match");
                            mPassword.requestFocus();
                            mConfirmPassword.setError("Password do not match");
                            mConfirmPassword.requestFocus();
                            mDialog.dismiss();
                        }
                    }
                    else
                    {
                        mConfirmPassword.setError("This field is required");
                        mConfirmPassword.requestFocus();
                        mDialog.dismiss();
                    }
                }
                else
                {
                    mPassword.setError("Password is required!");
                    mPassword.requestFocus();
                    mDialog.dismiss();
                }
            }
            else
            {
                mEmail.setError("Email is required!");
                mEmail.requestFocus();
                mDialog.dismiss();
            }
        }
        else
        {
            mFullName.setError("Full Name is required!");
            mFullName.requestFocus();
            mDialog.dismiss();
        }
    }

    private void createNewUser(final String fullName, final String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            String uid = mAuth.getCurrentUser().getUid();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("uid", uid);
                            hashMap.put("fullName", fullName);
                            hashMap.put("email", email);

                            usersRef.child(uid).updateChildren(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                        Intent loginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(loginIntent);
                                        mDialog.dismiss();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    String error = e.getMessage();
                                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
                                    mDialog.dismiss();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String error = e.getMessage();
                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
                mDialog.dismiss();

            }
        });
    }
}
