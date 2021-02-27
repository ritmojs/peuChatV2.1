package com.e.chatingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {
    private Button CreateAccoutnt;
    private EditText UserEmail,UserPassword;
    private TextView AlreadyHaveAccount;
    private FirebaseAuth mAuth;
    private ProgressBar LoadingBar;
    private DatabaseReference RootRef;
    private String mpeuID;
    private String deviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth=FirebaseAuth.getInstance();
        RootRef= FirebaseDatabase.getInstance().getReference();


        //Calling initialize method
        InitializeField();
        //Creating peuID
        Date myDate = new Date();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
        String date = format1.format(myDate);
        Random r = new Random();
        int n = 100000 + r.nextInt(900000);
        mpeuID = String.valueOf(n);


//OnClickListners
        AlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLoginActivity();
            }
        });
        CreateAccoutnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccoutn();
            }
        });
    }


    //Create Account Method
    private void CreateNewAccoutn()
    {
    String email=UserEmail.getText().toString();
    final String password=UserPassword.getText().toString();

        if(email.isEmpty()){
            UserEmail.setError("Email is required");
            UserEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            UserEmail.setError("Please enter a valid email");
            UserEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            UserPassword.setError("Password is required");
            UserPassword.requestFocus();
            return;
        }
        if(password.length()<6)
        {
            UserPassword.setError("Minimum length of password is 6");
            UserPassword.requestFocus();
            return;
        }
    else
    {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    //Profile Build for FirebaseAuths.
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName("peuChater")
                        .build();
                    mAuth.getCurrentUser().updateProfile(profileUpdates);


                    //Toast Email Register
                    Toast.makeText(RegisterActivity.this, "Email Register Successful", Toast.LENGTH_SHORT).show();


                    //Sending Verification Code to Email
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {

                                deviceToken = FirebaseInstanceId.getInstance().getToken();
                                String currentUserID=mAuth.getCurrentUser().getUid();

                                RootRef.child("Passwword").child(currentUserID).setValue(password);
                                RootRef.child("peuID").child(mpeuID).setValue(currentUserID);

                                //Getting Current Time Date For Online Status
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                                String saveCurrentDate = currentDate.format(calendar.getTime());
                                SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
                                String saveCurrentTime = currentTime.format(calendar.getTime());
                                //End Of Online Status Code

                                //Updating DataBase With Details
                                RootRef.child("Users").child(currentUserID).child("device_token").setValue(deviceToken);
                                RootRef.child("Users").child(currentUserID).child("peuID").setValue(mpeuID);
                                RootRef.child("Users").child(currentUserID).child("userState").child("state").setValue("online");
                                RootRef.child("Users").child(currentUserID).child("userState").child("date").setValue(saveCurrentDate);
                                RootRef.child("Users").child(currentUserID).child("userState").child("time").setValue(saveCurrentTime);
                                //End Of Details Updations



                                Toast.makeText(RegisterActivity.this, "Check Your Email for Verification Link", Toast.LENGTH_SHORT).show();


                                sendUserToLoginActivity();


                            }

                        }
                    });
                }
                else
                {
                    String message=task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    }


    /*
    private void sendUserToMainActivity() {
        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
        intent.putExtra("peuID", mpeuID);
        intent.putExtra("device_token", deviceToken);
        startActivity(intent);

    }*/



    private void sendUserToLoginActivity() {
        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }



    private void InitializeField() {
    CreateAccoutnt=findViewById(R.id.Register_btn);
    UserEmail=findViewById(R.id.Register_email);
    UserPassword=findViewById(R.id.Register_password);
    AlreadyHaveAccount=findViewById(R.id.Alreagy_Have_Account);
    LoadingBar= new ProgressBar(this);
    }
}
