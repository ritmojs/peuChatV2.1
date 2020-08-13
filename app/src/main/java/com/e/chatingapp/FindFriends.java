package com.e.chatingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindFriends extends AppCompatActivity {
    private EditText peuID;
    private Button mbtn;
    private DatabaseReference RootRef;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        peuID = findViewById(R.id.enter_peuID);
        RootRef = FirebaseDatabase.getInstance().getReference();
        mbtn = findViewById(R.id.search);

        mbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = peuID.getText().toString();
                verifyUserExistance();


            }
        });


    }

    private void verifyUserExistance() {

        RootRef.child("peuID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(text).exists()) {
                    Intent intent=new Intent(FindFriends.this,ProfileActivity.class);
                    intent.putExtra("peuID",text);
                    
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(FindFriends.this, "No User With this peuID..Try Again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}

