package com.example.firebasesocialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity  {
 private EditText edtUsername,edtPassword,edtEmail;
 private Button btnSignUp,BtnLogin;
    private FirebaseAuth mAuth;
 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        edtEmail   =findViewById(R.id.email);
        edtUsername=findViewById(R.id.Username);
        edtPassword=findViewById(R.id.Password);
        btnSignUp=findViewById(R.id.signUp);
        BtnLogin=findViewById(R.id.Login);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            signuP();
        }
    });
        BtnLogin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
         SignIn();
        }
    });
 }



    private void SignIn() {
 mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
     @Override
     public void onComplete(@NonNull Task<AuthResult> task) {
    if(task.isSuccessful()){
        Toast.makeText(MainActivity.this, "Sign in Successful", Toast.LENGTH_SHORT).show();
            FirebaseDatabase.getInstance()
                    .getReference().child("my_Users")
                    .child(task.getResult().getUser()
                            .getUid()).child("userame")
                    .setValue(edtUsername.getText().toString());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(edtUsername.getText().toString())
                .build();

        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "update Successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                     transistion();
     }else{
        Toast.makeText(MainActivity.this, "Sign in Failed", Toast.LENGTH_SHORT).show();
    }

     }
 });
 }



    private void transistion() {
        startActivity(new Intent(MainActivity.this,SociallMediaApp.class));
  }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
              if(currentUser!=null){
                 transistion();
              }
      }
public void signuP(){
    mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString())
            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
        if(task.isSuccessful()){
            Toast.makeText(MainActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
            FirebaseDatabase.getInstance()
                    .getReference().child("my_Users")
                    .child(task.getResult().getUser()
                            .getUid()).child("userame")
                    .setValue(edtUsername.getText().toString());
            transistion();
        }else{
            Toast.makeText(MainActivity.this, "SignUp failed", Toast.LENGTH_SHORT).show();
        }
        }
    });
}
}
