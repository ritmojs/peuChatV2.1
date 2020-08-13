package com.e.chatingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabAccessAdaptor mTabAccessAdaptor;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    String peuID;
    String deviceToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RootRef = FirebaseDatabase.getInstance().getReference();
        mtoolbar = (Toolbar) findViewById(R.id.app_bar_page);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("puechat");
        mViewPager = findViewById(R.id.pager);
        mTabAccessAdaptor = new TabAccessAdaptor(getSupportFragmentManager(), 0);
        mViewPager.setAdapter(mTabAccessAdaptor);
        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Intent intent=getIntent();
        peuID=intent.getStringExtra("peuID");
        deviceToken=intent.getStringExtra("device_token");
    }

    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            sendUserToLoginActivity();
        }
        else
        {verifyUserExistance();

        }

    }

    private void verifyUserExistance() {
        String currentUserID=mAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("name").exists())
                {

                }
                else
                {  sendUserToSettingActivity();

                  }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToSettingActivity() {
        Intent intent=new Intent(MainActivity.this,SettingActivity.class);
       // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("peuID", peuID);
        intent.putExtra("device_token", deviceToken);
        startActivity(intent);


    }

    private void sendUserToLoginActivity() {

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.Logout) {
            mAuth.signOut();
            sendUserToLoginActivity();

        }
        if (item.getItemId() == R.id.main_find_friends_option) {
            sendUserToFindFriendActivity();

        }
        if (item.getItemId() == R.id.Settings) {
            sendUserToSettingActivity();

        }
        return true;
    }

    private void sendUserToFindFriendActivity() {
        Intent intent = new Intent(MainActivity.this, FindFriends.class);
        startActivity(intent);


    }

    public void RequestNewGroup()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name");
        final EditText groupNameField=new EditText(MainActivity.this);
        groupNameField.setHint("WeBe");
        builder.setView(groupNameField);
        builder.setPositiveButton("create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName=groupNameField.getText().toString();
                if(TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, "Please write Group Name ", Toast.LENGTH_SHORT).show();
                }
                else
                {createNewGroup(groupName);

                }

            }
        });

        builder.show();

    }

    private void createNewGroup(String groupName) {
        RootRef.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this, "group created", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
