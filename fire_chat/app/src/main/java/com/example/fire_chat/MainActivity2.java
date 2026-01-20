package com.example.fire_chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity2 extends AppCompatActivity {

    EditText usernameText,emailText,passwordText;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        reference=db.getReference("users");
        usernameText=findViewById(R.id.editTextText3);
        emailText=findViewById(R.id.editTextTextEmailAddress);
        passwordText=findViewById(R.id.editTextTextPassword);



    }

    public void buttonclicked(View view){

        String email= emailText.getText().toString().trim();
        String password= passwordText.getText().toString();
        String username= usernameText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()){
            showmessage3("Please fill all the fields");
            return;
        }

        if (username.length()>=8 || username.length()<=2) {
            showmessage3("Username must be less than 8 characters and bigger than 2 characters");
            return;
        }

        String usernameToCheck = username.toLowerCase();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean usernameExists = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String existingUsername =
                            userSnapshot.child("username").getValue(String.class);

                    if (existingUsername != null &&
                            existingUsername.equalsIgnoreCase(usernameToCheck)) {

                        usernameExists = true;
                        break;
                    }
                }

                if (!usernameExists) {
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String uid = auth.getCurrentUser().getUid();
                                    reference.child(uid).child("username").setValue(username);
                                    reference.child(uid).child("email").setValue(email);
                                    reference.child(uid).child("profilePic").setValue("default");
                                    showmessagesuc("The registration is successful!");

                                } else {
                                    Exception e = task.getException();
                                    emailText.setText("");
                                    passwordText.setText("");
                                    usernameText.setText("");
                                  if (e instanceof FirebaseAuthUserCollisionException) {
                                        showmessage3("This email is already in use.");
                                    } else if (e instanceof FirebaseAuthWeakPasswordException) {
                                        showmessage3(e.getLocalizedMessage());
                                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                        showmessage3("This email is invalid.");
                                    } else {
                                        showmessage3("Registration failed. Please try again.");
                                    }
                                }
                            });
                }else{showmessage3("The username is already taken. Please choose a different one.");}


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showmessage3("Database error: " + error.getMessage());
                startActivity(new Intent(MainActivity2.this, MainActivity.class));
            }
        });




    }
    public void showmessage3(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .show();

    }
    public void showmessagesuc(String message){
        new AlertDialog.Builder(MainActivity2.this)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton("OK", (d, which) -> {
                    startActivity(new Intent(MainActivity2.this, MainActivity3.class));
                    finish();
                })
                .show();
    }

}