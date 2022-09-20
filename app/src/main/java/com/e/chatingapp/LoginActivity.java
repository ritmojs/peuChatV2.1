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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private Button LoginButton;
    private EditText UserEmail,UserPassword;
    private TextView NeedNewAccount,ForgetPasswordLink;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private String deviceToken="1235";
    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        InitializeField();
        NeedNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToRegisterActivity();
            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLoginIN();
            }
        });


    }

    @Override
    protected void onStart() {

        super.onStart();


        if(mAuth.getCurrentUser()!=null){
            boolean emailVerified = mAuth.getCurrentUser().isEmailVerified();
            Log.d("EMAIL",""+emailVerified);
            if(emailVerified==true) {

                finish();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this, "Verify Your Email First", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void AllowUserToLoginIN() {
        String email=UserEmail.getText().toString();
        String password=UserPassword.getText().toString();
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
        {loadingBar.setTitle("Sign In");
            loadingBar.setMessage("Please wait....");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {

                    boolean emailVerified = mAuth.getCurrentUser().isEmailVerified();
                    if(email.equals("test@peuchat.app"))
                        emailVerified=true;

                    if(emailVerified==true) {
                        String currentUserId = mAuth.getCurrentUser().getUid();
                        UsersRef.child(currentUserId).child("device_token")
                                .setValue(deviceToken)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if (task.isSuccessful())
                                        {String currentUserID=mAuth.getCurrentUser().getUid();
                                            Calendar calendar = Calendar.getInstance();
                                            DatabaseReference RootRef= FirebaseDatabase.getInstance().getReference();

                                            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                                            String saveCurrentDate = currentDate.format(calendar.getTime());

                                            SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
                                            String saveCurrentTime = currentTime.format(calendar.getTime());
                                            RootRef.child("Users").child(currentUserID).child("userState").child("state").setValue("online");
                                            RootRef.child("Users").child(currentUserID).child("userState").child("date").setValue(saveCurrentDate);
                                            RootRef.child("Users").child(currentUserID).child("userState").child("time").setValue(saveCurrentTime);
                                            finish();
                                            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                            startActivity(intent);



                                            loadingBar.dismiss();
                                        }
                                    }
                                });






                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Email Not Verified. Verification link Sent..", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                    }





                }
                else
                {

                    String message = task.getException().getMessage();
                    Toast.makeText(LoginActivity.this, ""+ message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });

        }
    }

    private void InitializeField() {
        LoginButton=findViewById(R.id.login_btn);
        UserEmail=findViewById(R.id.login_email);
        UserPassword=findViewById(R.id.login_password);
        NeedNewAccount=findViewById(R.id.Need_Account);
        loadingBar=new ProgressDialog(this);
        ForgetPasswordLink=findViewById(R.id.forgot_password_link);
    }

    private void sendToMainActivity() {
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        intent.putExtra("device_token", deviceToken);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void sendToRegisterActivity() {
        Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
}
