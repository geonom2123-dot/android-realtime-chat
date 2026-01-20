package com.example.fire_chat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
public class MainActivity4 extends AppCompatActivity {

    EditText emailText;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main4);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        emailText=findViewById(R.id.editTextTextEmailAddress8);

    }
    public void forgot(View view){
        String email=emailText.getText().toString().trim();

        if (email.isEmpty()){
            showmesssage2("User Input","Please put email");
            return;
        }
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Το email στάλθηκε επιτυχώς
                        showmesssage2("Success", "If an account exists with email "+email+", you will receive a password reset link.");
                        emailText.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Υπήρξε κάποιο σφάλμα (π.χ. το email δεν υπάρχει)
                        showmesssage2("Error", e.getLocalizedMessage());
                        emailText.setText("");
                    }
                });



    }


    public void showmesssage2(String input,String message){

        new AlertDialog.Builder(this)
                .setTitle(input)
                .setMessage(message)
                .show();

    }

    public void menu(View view){
        Intent intent =new Intent(this,MainActivity.class);
        startActivity(intent);

    }
}