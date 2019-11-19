package com.example.cityfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.cityfit.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity {

    private Button mButtonSignIn, mButtonSignUp;
    private EditText mTextEmail, mTextPassword;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        mButtonSignIn = findViewById(R.id.buttonLogin);
        mButtonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextEmail = findViewById(R.id.textEmail);
                mTextPassword = findViewById(R.id.textPassword);
                mAuth = FirebaseAuth.getInstance();
                if(!mTextEmail.getText().toString().isEmpty()) {
                    mAuth.signInWithEmailAndPassword(mTextEmail.getText().toString(), mTextPassword.getText().toString()).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        Intent intent = new Intent(LoginActivity.this, HomeScreen.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                    );
                }

            }
        });


        mButtonSignUp = findViewById(R.id.buttonSignUpIntent);
        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

//    private void onAuthSuccess(FirebaseUser user) {
//
////        String username = usernameFromEmail(user.getEmail());
//
//        // Write new user
//        writeNewUser(user.getUid(), user.getEmail());
//
//        // Go to MainActivity

    }
//    private String usernameFromEmail(String email) {
//        if (email.contains("@")) {
//            return email.split("@")[0];
//        } else {
//            return email;
//        }
//    }

//    private void writeNewUser(String userId, String email) {
//        User user = new User();
//        user.setEmail(email);
//        user.setId(userId);
//        DocumentReference openRef = db
//                .collection("users")
//                .document();
//
//        openRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    Log.d("Created new note","message");
//                }
//                else{
//                    Log.d("Created new note","message");
//                }
//            }
//        });
//
//    }





