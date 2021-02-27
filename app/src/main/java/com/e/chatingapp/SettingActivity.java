package com.e.chatingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private Button UpdateAccountSettings;
    private EditText userName,userStatus;
    private CircleImageView userProfileImage;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference Rootref;
    private TextView mpeuID;
    String peuID;
    String deviceToken;
    private static final int GALLERY_CODE = 1;




    DatabaseReference reference;
    ProgressDialog progressDialog;
    StorageReference storageReference;

    private Uri mImageUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            userProfileImage.setImageURI(mImageUri);


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Intent myIntent = getIntent();

        deviceToken=myIntent.getStringExtra("device_token");
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        InitializeFiels();
        RetriveUserInfo();



// Load the image using Glide


        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });
        storageReference= FirebaseStorage.getInstance().getReference().child("User_images");
        Rootref.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("peuID").exists()) {
                    peuID=dataSnapshot.child("peuID").getValue().toString();
                  deviceToken=dataSnapshot.child("device_token").getValue().toString();

                }
                else
                {


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void InitializeFiels() {
        UpdateAccountSettings=findViewById(R.id.update);
        userName=findViewById(R.id.Username);
        userStatus=findViewById(R.id.Status);
        userProfileImage=findViewById(R.id.profile_image);
        mpeuID=findViewById(R.id.peuID);
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        Rootref= FirebaseDatabase.getInstance().getReference();
    }

    private void UpdateSettings() {
      //  HashMap<String,String> profileMap=new HashMap<>();
      String setUserName=userName.getText().toString();
        String setStatus=userStatus.getText().toString();
        Intent myIntent=getIntent();

        if(TextUtils.isEmpty(setStatus))
        {
            Toast.makeText(this, "Please write status..", Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(setUserName))
        {
            Toast.makeText(this, "Please write useName..", Toast.LENGTH_SHORT).show();

        }
        else
        {

            StorageReference sr = storageReference.child(currentUserID + ".jpg");
            if(mImageUri!=null) {
                sr.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //Uri url=taskSnapshot.getDownloadUrl();
                                //  String url= taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!urlTask.isSuccessful()) ;

                                String url = urlTask.getResult().toString();

                                Rootref.child("Users").child(currentUserID).child("imageuri").setValue(url);
                            }
                        });
            }








            Rootref.child("Users").child(currentUserID).child("uid").setValue(currentUserID);
            Rootref.child("Users").child(currentUserID).child("device_token").setValue(deviceToken);
            Rootref.child("Users").child(currentUserID).child("status").setValue(setStatus);
            Rootref.child("Users").child(currentUserID).child("name").setValue(setUserName).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(SettingActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        sendToMainActivity();
                    }
                    else
                    {
                        String messege=task.getException().toString();
                        Toast.makeText(SettingActivity.this, "Error:"+messege, Toast.LENGTH_SHORT).show();
                    }

                }
            });




          /*  profileMap.put("uid",currentUserID);
            profileMap.put("peuID",peuID);
            profileMap.put("device_token",deviceToken);
            profileMap.put("name",setUserName);
            profileMap.put("status",setStatus);

            Rootref.child("Users").child(currentUserID).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    { Toast.makeText(SettingActivity.this, "Profile Updated Successful", Toast.LENGTH_SHORT).show();

                        sendToMainActivity();

                    }
                    else
                    {
                    }
                }
            });*/
        }

    }
    public  void RetriveUserInfo()
    {Rootref.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")&& dataSnapshot.hasChild("status")&& dataSnapshot.hasChild("imageuri")))
            {String imageuri=dataSnapshot.child("imageuri").getValue().toString();
                Picasso.get().load(imageuri).into(userProfileImage);
                String retrivename=dataSnapshot.child("name").getValue().toString();
                String retrivestatus=dataSnapshot.child("status").getValue().toString();
                String retrivepueId=dataSnapshot.child("peuID").getValue().toString();
                userName.setText(retrivename);
                userStatus.setText(retrivestatus);
                mpeuID.setText(retrivepueId);

            }
            if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")&& dataSnapshot.hasChild("status") && !dataSnapshot.hasChild("imageuri")))
            {
                String retrivename=dataSnapshot.child("name").getValue().toString();
                String retrivestatus=dataSnapshot.child("status").getValue().toString();
                String retrivepueId=dataSnapshot.child("peuID").getValue().toString();
                userName.setText(retrivename);
                userStatus.setText(retrivestatus);
                mpeuID.setText(retrivepueId);
            }


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

    }
    private void sendToMainActivity() {
        Intent intent=new Intent(SettingActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }





    }
