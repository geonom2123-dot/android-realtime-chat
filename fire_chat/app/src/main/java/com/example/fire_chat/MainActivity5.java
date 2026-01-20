package com.example.fire_chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
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
import java.util.Collections;
import java.util.List;

public class MainActivity5 extends AppCompatActivity {

    RecyclerView rvChats;
    ChatAdapter chatAdapter;
    List<ChatModel> chatList;
    DatabaseReference ref1, ref2;
    FirebaseAuth auth;
    FirebaseDatabase db;
    TextView textView;
    ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        getWindow().setStatusBarColor(Color.parseColor("#FFB347"));

        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        setContentView(R.layout.activity_main5);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textView = findViewById(R.id.textView18);
        imageView2 = findViewById(R.id.imageView2);
        rvChats = findViewById(R.id.rvChats);
        rvChats.setLayoutManager(new LinearLayoutManager(this));

        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatList);
        rvChats.setAdapter(chatAdapter);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        String myUid = auth.getCurrentUser().getUid();
        ref1 = db.getReference("chats").child(myUid);
        ref2 = db.getReference("users").child(myUid);

        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("username").getValue(String.class);
                    textView.setText(name);

                    String pfp = snapshot.child("profilePic").getValue(String.class);

                    // ΔΙΟΡΘΩΣΗ: Έλεγχος αν η φωτό είναι Base64 ή URL
                    if (pfp != null && !pfp.equals("default") && !pfp.isEmpty()) {
                        try {
                            if (pfp.startsWith("http")) {
                                // Παλιό στυλ με URL
                                Glide.with(MainActivity5.this).load(pfp).circleCrop().into(imageView2);
                            } else {
                                // Νέο στυλ με Base64 κείμενο
                                byte[] decodedString = Base64.decode(pfp, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                Glide.with(MainActivity5.this).load(decodedByte).circleCrop().into(imageView2);
                            }
                        } catch (Exception e) {
                            imageView2.setImageResource(R.drawable.baseline_account_circle_24);
                        }
                    } else {
                        imageView2.setImageResource(R.drawable.baseline_account_circle_24);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        loadChatsFromFirebase();
    }

    private void loadChatsFromFirebase() {
        ref1.orderByChild("lastTimestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    ChatModel chat = data.getValue(ChatModel.class);
                    if (chat != null) {
                        // receiverid einai to uid)
                        chat.receiverId = data.getKey();

                        db.getReference("users").child(chat.receiverId).child("profilePic")
                                .get().addOnSuccessListener(pfpSnapshot -> {
                                    if (pfpSnapshot.exists()) {
                                        chat.profilePic = pfpSnapshot.getValue(String.class);
                                        chatAdapter.notifyDataSetChanged();
                                    }
                                });
                        chatList.add(chat);
                    }
                }

                Collections.reverse(chatList);
                chatAdapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    public void add(View view) {
        Intent intent = new Intent(this, MainActivity6.class);
        startActivity(intent);
    }

    public void profile(View view) {
        Intent intent = new Intent(this, MainActivity8.class);
        startActivity(intent);
    }
}