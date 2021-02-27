package com.e.chatingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.FocusFinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private  String receiverUserID,Current_State,senderUserID;
    private TextView userProfileName,userProfileStatus,userpeuID;
    private CircleImageView userProfileImage;
    private Button sendMessageRequestButton,declineMessageRequestButton;
    private DatabaseReference RootRef,ChatRequestRef,ContactRef,NotificationRef,peuRef;
    FirebaseAuth mAuth;
    String peuID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth=FirebaseAuth.getInstance();
        senderUserID=mAuth.getCurrentUser().getUid();
       //receiverUserID=getIntent().getStringExtra("visit_user_id");
        peuID=getIntent().getStringExtra("peuID");
        receiverUserID="";
        userpeuID=findViewById(R.id.visit_user_peuID);
        userpeuID.setText(peuID);

        userProfileImage=findViewById(R.id.visit_profile_image);
        userProfileName=findViewById(R.id.visit_user_name);
        userProfileStatus=findViewById(R.id.visit_user_status);
        sendMessageRequestButton=findViewById(R.id.send_messege_request_button);
        RootRef= FirebaseDatabase.getInstance().getReference().child("Users");

        ChatRequestRef= FirebaseDatabase.getInstance().getReference().child("ChatRequest");
        ContactRef= FirebaseDatabase.getInstance().getReference().child("Contacts");
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        declineMessageRequestButton=findViewById(R.id.decline_messege_request_button);

        Current_State="new";
        peuRef= FirebaseDatabase.getInstance().getReference().child("peuID");
        peuRef.child(peuID).addValueEventListener(new ValueEventListener() {
                                                      @Override
                                                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                          if ((dataSnapshot.exists())) {
                                                              receiverUserID=dataSnapshot.getValue().toString();
                                                              RetriveUserInfo();


                                                          }
                                                      }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
                                                  });




        //ManageChatRequest();


    }



    private void RetriveUserInfo() {
       RootRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")&& dataSnapshot.hasChild("status")&& dataSnapshot.hasChild("imageuri")))
               {String userImage=dataSnapshot.child("imageuri").getValue().toString();
                String userName=dataSnapshot.child("name").getValue().toString();
                String userStatus=dataSnapshot.child("status").getValue().toString();
                String userID=dataSnapshot.child("peuID").getValue().toString();
               Picasso.get().load(userImage).placeholder(R.drawable.profile).into(userProfileImage);
              userProfileName.setText(userName);
              userProfileStatus.setText(userStatus);
                   ManageChatRequest();
               }
               if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")&& dataSnapshot.hasChild("status") && !dataSnapshot.hasChild("imageuri")))
               {
                   String userName=dataSnapshot.child("name").getValue().toString();
                   String userStatus=dataSnapshot.child("status").getValue().toString();
                   String userID=dataSnapshot.child("peuID").getValue().toString();
                   userProfileName.setText(userName);
                   userProfileStatus.setText(userStatus);
                   ManageChatRequest();

               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });


    }
    private void ManageChatRequest() {
        ChatRequestRef.child((senderUserID))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUserID))
                        {String request_type=dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();
                                             if(request_type.equals("sent"))
                                             {
                                                 Current_State="request_sent";
                                                 sendMessageRequestButton.setText("Cancel request");
                                             }
                                             else if(request_type.equals("received"))
                                             {Current_State="request_received";
                                             sendMessageRequestButton.setText("Accept Chat request");
                                             declineMessageRequestButton.setEnabled(true);
                                             declineMessageRequestButton.setVisibility(View.VISIBLE);
                                             declineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     CancelChatRequest();
                                                 }
                                             });

                                             }

                        }
                        else
                        {
                            ContactRef.child(senderUserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(receiverUserID))
                                            {
                                                Current_State="friends";
                                                sendMessageRequestButton.setText("Remove");

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        if(!senderUserID.equals(receiverUserID))
        {
            sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessageRequestButton.setEnabled(false);
                    if(Current_State.equals("new"))
                    {
                        sendChatRequest();
                    }
                    if(Current_State.equals("request_sent"))
                    {
                        CancelChatRequest();
                    }
                    if(Current_State.equals("request_received"))
                    {
                        AcceptChatRequest();
                    }
                    if (Current_State.equals("friends"))
                    {
                        RemoveSpecificContact();
                    }
                }
            });

        }
        else
        {
          sendMessageRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveSpecificContact() {
        ContactRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ContactRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendMessageRequestButton.setEnabled(true);
                                                Current_State = "new";
                                                sendMessageRequestButton.setText("Send Message");

                                                declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                declineMessageRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptChatRequest() {
        ContactRef.child((senderUserID)).child(receiverUserID)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            ContactRef.child((receiverUserID)).child(senderUserID)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                ChatRequestRef.child(senderUserID).child(receiverUserID).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    ChatRequestRef.child(receiverUserID).child(senderUserID).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    sendMessageRequestButton.setEnabled(true);
                                                                                    Current_State="friends";
                                                                                    sendMessageRequestButton.setText("Remove");
                                                                                    declineMessageRequestButton.setVisibility(View.INVISIBLE);

                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }

                                        }
                                    });
                        }
                    }
                });

    }

    private void CancelChatRequest() {
        ChatRequestRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {  ChatRequestRef.child(receiverUserID).child(senderUserID)
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        sendMessageRequestButton.setEnabled(true);
                                        Current_State = "new";
                                        sendMessageRequestButton.setText("Send Request");
                                        declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                        declineMessageRequestButton.setEnabled(false);
                                    }
                                }
                                );

                        }
                    }
                });

    }

    private void sendChatRequest() {
        ChatRequestRef.child(senderUserID).child(receiverUserID).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {

                            ChatRequestRef.child(receiverUserID).child(senderUserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                HashMap<String, String> chatNotificationMap = new HashMap<>();
                                                chatNotificationMap.put("from", senderUserID);
                                                chatNotificationMap.put("type", "request");

                                                NotificationRef.child(receiverUserID).push()
                                                        .setValue(chatNotificationMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    sendMessageRequestButton.setEnabled(true);
                                                                    Current_State = "request_sent";
                                                                    sendMessageRequestButton.setText("Cancel Chat Request");
                                                                }
                                                            }
                                                        });

                                            }
                                        }
                                    });
                        }

                    }
                });
    }
}
