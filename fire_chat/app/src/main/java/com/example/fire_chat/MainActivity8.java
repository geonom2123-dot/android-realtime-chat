package com.example.fire_chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity8 extends AppCompatActivity {

    TextView email, username;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseDatabase db;
    ImageView imageView;
    String base64Image = null; // Εδώ θα αποθηκεύουμε την εικόνα ως κείμενο

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageView.setImageURI(uri);
                    base64Image = uriToBase64(uri); // Μετατροπή σε κείμενο αμέσως
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main8);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.textView14);
        username = findViewById(R.id.textView8);
        imageView = findViewById(R.id.imageView6);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        String uid = auth.getCurrentUser().getUid();
        reference = db.getReference("users").child(uid);

        imageView.setOnClickListener(v -> change(v));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    username.setText(snapshot.child("username").getValue(String.class));
                    email.setText(snapshot.child("email").getValue(String.class));
                    String pfp = snapshot.child("profilePic").getValue(String.class);

                    // Το Glide διαβάζει Base64 keimeno
                    if (pfp != null && !pfp.equals("default") && base64Image == null) {
                        byte[] decodedString = Base64.decode(pfp, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        Glide.with(MainActivity8.this).load(decodedByte).circleCrop().into(imageView);
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void change(View view){
        mGetContent.launch("image/*");
    }


    public void save(View view) {
        if (base64Image != null) {
            reference.child("profilePic").setValue(base64Image).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                finish();
            });
        } else {
            finish();
        }
    }

    public void logout(View view) {
        // Δημιουργία του Alert Dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    auth.signOut();
                    Intent intent = new Intent(MainActivity8.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss(); // κλεινει το παράθυρο με το νο
                })
                .show();
    }

    //Μέθοδος για τη μετατροπη της εικόνας
    private String uriToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // Συμπιέζουμε την εικόνα για να μην είναι πολύ μεγάλη για τη βάση
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    public void finish(View view){ finish(); }


}