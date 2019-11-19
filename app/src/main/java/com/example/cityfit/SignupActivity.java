package com.example.cityfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cityfit.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    private Button mSignIn, mSignUp;
    private FirebaseAuth mAuth;
    private EditText mTextName, mTextEmail, mTextPassword;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        mSignUp = findViewById(R.id.buttonSignUp);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                mTextName = findViewById(R.id.textNameSignUp);
                mTextEmail = findViewById(R.id.textEmailSignUp);
                mTextPassword = findViewById(R.id.textPasswordSignUp);

                mAuth.createUserWithEmailAndPassword(mTextEmail.getText().toString(),mTextPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("het", mTextEmail.toString());
                            startActivity(new Intent(SignupActivity.this, HomeScreen.class));
                            createNewUser(mTextName.getText().toString(),mTextEmail.getText().toString());
                            finish();
                        }
                        else {
                            Log.v("het", mTextEmail.toString());
                            Toast.makeText(SignupActivity.this, "sign in error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });


        mSignIn = findViewById(R.id.buttonSignInIntent);
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    public void createNewUser(String name,String email){

        DocumentReference newNoteRef = db
                .collection("users")
                .document();




        User user = new User();
       user.setName(name);
       user.setEmail(email);


        newNoteRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("Created new note","message");
                }
                else{
                    Log.d("Created new note","message");
                }
            }
        });
    }
}
