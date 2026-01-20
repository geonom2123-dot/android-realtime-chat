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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity6 extends AppCompatActivity {

    EditText editText;
    FirebaseDatabase db;
    DatabaseReference ref, ref2;
    FirebaseAuth auth;
    ImageView imgResult, myProfileImg; // imageView3 και imageView2
    TextView txt, txtResult;

    String foundUid;
    String foundUsername;
    String foundPfp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(Color.parseColor("#FFB347"));
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        setContentView(R.layout.activity_main6);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        editText = findViewById(R.id.editTextText4);
        imgResult = findViewById(R.id.imageView3); // Αποτέλεσμα αναζήτησης
        myProfileImg = findViewById(R.id.imageView2); // Το προφιλτου χρηστη(Top Bar)
        txtResult = findViewById(R.id.textView13);
        txt = findViewById(R.id.textView18);

        String myuid = auth.getCurrentUser().getUid();
        ref = db.getReference("users").child(myuid);

        // 1. ΦΟΡΤΩΣΗ ΤΩΝ ΔΙΚΩΝ ΣΟΥ ΣΤΟΙΧΕΙΩΝ (Top Bar)
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    txt.setText(snapshot.child("username").getValue(String.class));
                    String myPfp = snapshot.child("profilePic").getValue(String.class);

                    // Διόρθωση Glide για το imageView2 (Εσύ)
                    if (myPfp != null && !myPfp.equals("default")) {
                        loadBase64OrUrl(myPfp, myProfileImg);
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void search(View view){
        String searchName = editText.getText().toString().trim();
        if (searchName.isEmpty()){
            message("Please enter a username");
            return;
        }

        ref2 = db.getReference("users");
        ref2.orderByChild("username").equalTo(searchName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String usernameFromDB = child.child("username").getValue(String.class);

                        if(searchName.equalsIgnoreCase(txt.getText().toString())){
                            imgResult.setVisibility(View.GONE);
                            txtResult.setVisibility(View.VISIBLE);
                            txtResult.setText("You cannot search yourself");
                        } else {
                            foundUid = child.getKey();
                            foundUsername = usernameFromDB;
                            foundPfp = child.child("profilePic").getValue(String.class);

                            imgResult.setVisibility(View.VISIBLE);
                            txtResult.setVisibility(View.VISIBLE);
                            txtResult.setText("Found: " + foundUsername + "\nClick icon to chat");

                            // 2. Διόρθωση Glide για το imageView3 (Αποτέλεσμα)
                            if(foundPfp != null && !foundPfp.equals("default")) {
                                loadBase64OrUrl(foundPfp, imgResult);
                            } else {
                                imgResult.setImageResource(R.drawable.baseline_account_circle_24);
                            }
                        }
                    }
                } else {
                    imgResult.setVisibility(View.GONE);
                    txtResult.setVisibility(View.VISIBLE);
                    txtResult.setText("User not found");
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void loadBase64OrUrl(String pfpData, ImageView targetIv) {
        try {
            if (pfpData.startsWith("http")) {
                Glide.with(this).load(pfpData).circleCrop().into(targetIv);
            } else {
                byte[] decodedString = Base64.decode(pfpData, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Glide.with(this).asBitmap().load(decodedByte).circleCrop().into(targetIv);
            }
        } catch (Exception e) {
            targetIv.setImageResource(R.drawable.baseline_account_circle_24);
        }
    }

    public void startConversation(View view) {
        if (foundUid != null) {
            Intent intent = new Intent(this, MainActivity7.class);
            intent.putExtra("uid", foundUid);
            intent.putExtra("username", foundUsername);
            intent.putExtra("pfp", foundPfp);
            startActivity(intent);
            finish();
        }
    }

    public void message(String message){
        new AlertDialog.Builder(this).setTitle("Error").setMessage(message).show();
    }

    public void change(View view){
        Intent intent= new Intent(this, MainActivity8.class);
        startActivity(intent);
    }
}