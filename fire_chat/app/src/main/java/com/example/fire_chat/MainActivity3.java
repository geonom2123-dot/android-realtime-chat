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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity3 extends AppCompatActivity {

    FirebaseAuth auth;
    EditText emailText,passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        emailText=findViewById(R.id.editTextTextEmailAddress2);
        passwordText=findViewById(R.id.editTextTextPassword2);

    }

    public void signin2(View view){

        String email=emailText.getText().toString().trim();
        String password=passwordText.getText().toString();

        if (email.isEmpty() || password.isEmpty()){
            new AlertDialog.Builder(this)
                    .setTitle("User Input")
                    .setMessage("Please put email and password")
                    .show();
            passwordText.setText("");
            return;
        }

        auth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Intent intent =new Intent(MainActivity3.this,MainActivity5.class);
                        startActivity(intent);

                        finish();

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showmesssage("Login failed. Please check your credentials.");

                        passwordText.setText("");
                    }
                });


    }

    public void showmesssage(String message){

        new AlertDialog.Builder(this)
                .setTitle("Sign in")
                .setMessage(message)
                .show();

    }

    public void forgot(View view){

        Intent intent =new Intent(this,MainActivity4.class);
        startActivity(intent);


    }


}