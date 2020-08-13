package com.e.chatingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
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
    String mpeuID;
    String deviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();
        RootRef= FirebaseDatabase.getInstance().getReference();
        InitializeField();
        Date myDate = new Date();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
        String date = format1.format(myDate);
        Random r = new Random();
        int n = 100000 + r.nextInt(900000);
         mpeuID = String.valueOf(n);



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

    private void CreateNewAccoutn() {
    String email=UserEmail.getText().toString();
    final String password=UserPassword.getText().toString();
    if(TextUtils.isEmpty(email))
    {
        Toast.makeText(this, "Please enter email....", Toast.LENGTH_SHORT).show();
    }
    if(TextUtils.isEmpty(password))
    {
        Toast.makeText(this, "Please enter password....", Toast.LENGTH_SHORT).show();
    }
    else
    {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {  deviceToken = FirebaseInstanceId.getInstance().getToken();
                    String currentUserID=mAuth.getCurrentUser().getUid();

                RootRef.child("Passwword").child(currentUserID).setValue(password);
                RootRef.child("peuID").child(mpeuID).setValue(currentUserID);

                               //RootRef.child("Users").child(currentUserID).child("device_token").setValue(deviceToken);

                             sendUserToMainActivity();

                    Toast.makeText(RegisterActivity.this, "SuccessFul", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    String message=task.getException().toString();
                    Toast.makeText(RegisterActivity.this, "Error:"+message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    }

    private void sendUserToMainActivity() {
        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
        intent.putExtra("peuID", mpeuID);
        intent.putExtra("device_token", deviceToken);
        startActivity(intent);

    }


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
