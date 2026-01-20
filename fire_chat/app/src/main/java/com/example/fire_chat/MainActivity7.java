package com.example.fire_chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity7 extends AppCompatActivity {

    EditText message;
    RecyclerView recyclerView;
    MessageAdapter adapter;
    List<ModelOfMesseging> messageList;

    FirebaseDatabase db;
    FirebaseAuth auth;
    String receiverId, myUid, chatRoomId;
    DatabaseReference chatRef, refCurrentUser, refReceiver;

    TextView currentUserNameTxt, topUserNameReceiver;
    ImageView imageView, imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main7);

        getWindow().setStatusBarColor(Color.parseColor("#FFB347"));
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        //Μηδενίζει το bottom padding για το πληκτρολόγιο
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Σύνδεση Views
        message = findViewById(R.id.editMessage);
        recyclerView = findViewById(R.id.recyclerview2);
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        currentUserNameTxt = findViewById(R.id.textView9);
        topUserNameReceiver = findViewById(R.id.textView18);

        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        myUid = auth.getCurrentUser().getUid();
        receiverId = getIntent().getStringExtra("uid");

        // Listener για αυτόματο σκρολάρισμα όταν ανοίγει το πληκτρολόγιο
        recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom && messageList != null && messageList.size() > 0) {
                recyclerView.postDelayed(() ->
                        recyclerView.smoothScrollToPosition(messageList.size() - 1), 100);
            }
        });

        // Chat Room Logic
        if (myUid.compareTo(receiverId) > 0) {
            chatRoomId = myUid + "_" + receiverId;
        } else {
            chatRoomId = receiverId + "_" + myUid;
        }

        chatRef = db.getReference("messages").child(chatRoomId);
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(this, messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        loadUserData();
        loadMessages();
    }

    private void loadUserData() {
        // Φόρτωση Συνομιλητή
        db.getReference("users").child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    topUserNameReceiver.setText(snapshot.child("username").getValue(String.class));
                    loadBase64OrUrl(snapshot.child("profilePic").getValue(String.class), imageView);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Φόρτωση Δικών σου στοιχείων
        db.getReference("users").child(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUserNameTxt.setText(snapshot.child("username").getValue(String.class));
                    loadBase64OrUrl(snapshot.child("profilePic").getValue(String.class), imageView2);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    messageList.add(data.getValue(ModelOfMesseging.class));
                }
                adapter.notifyDataSetChanged();
                if(messageList.size() > 0) recyclerView.scrollToPosition(messageList.size() - 1);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadBase64OrUrl(String pfpData, ImageView targetIv) {
        if (pfpData == null || pfpData.equals("default")) {
            targetIv.setImageResource(R.drawable.baseline_account_circle_24);
            return;
        }
        try {
            byte[] decodedString = Base64.decode(pfpData, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Glide.with(this).load(bitmap).circleCrop().into(targetIv);
        } catch (Exception e) {
            targetIv.setImageResource(R.drawable.baseline_account_circle_24);
        }
    }

    public void send(View view){
        String msg = message.getText().toString().trim();
        if (!msg.isEmpty()) {
            long time = System.currentTimeMillis();
            ModelOfMesseging model = new ModelOfMesseging(msg, myUid, time);
            chatRef.push().setValue(model);

            // Update Chats for Main5
            DatabaseReference myChat = db.getReference("chats").child(myUid).child(receiverId);
            myChat.child("lastMessage").setValue(msg);
            myChat.child("lastTimestamp").setValue(time);
            myChat.child("username").setValue(topUserNameReceiver.getText().toString());
            myChat.child("receiverId").setValue(receiverId);

            DatabaseReference hisChat = db.getReference("chats").child(receiverId).child(myUid);
            hisChat.child("lastMessage").setValue(msg);
            hisChat.child("lastTimestamp").setValue(time);
            hisChat.child("username").setValue(currentUserNameTxt.getText().toString());
            hisChat.child("receiverId").setValue(myUid);

            message.setText("");
        }
    }
}