package com.example.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    EditText signUpPasswordET,signUpEmailET,signUpUsernameET;
    Button signUpBTN;

    //Firebase authentication
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firebase connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();

        signUpEmailET = findViewById(R.id.signUpEmailET);
        signUpPasswordET = findViewById(R.id.signUpPasswordET);
        signUpBTN = findViewById(R.id.signUpBTN);
        signUpUsernameET = findViewById(R.id.signUpUsernameET);


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null){
                    //Already logged in
                }
                else{

                }
            }
        };

        signUpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(signUpEmailET.getText().toString()) && !TextUtils.isEmpty(signUpPasswordET.getText().toString())
                        && !TextUtils.isEmpty(signUpUsernameET.getText().toString())){
                    String email = signUpEmailET.getText().toString().trim();
                    String password = signUpPasswordET.getText().toString().trim();
                    String username = signUpUsernameET.getText().toString().trim();
                    CreateNewUser(email,password,username);
                }
                else{
                    Toast.makeText(SignUpActivity.this, "Fields should not be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void CreateNewUser(String email, String password,final String username) {
        if(!TextUtils.isEmpty(signUpEmailET.getText().toString()) && !TextUtils.isEmpty(signUpPasswordET.getText().toString())){
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isComplete()){
                        //we will take user to next activity : (Add Journal Activity)
                        currentUser = firebaseAuth.getCurrentUser();
                        assert currentUser != null;
                        final String currentUserId = currentUser.getUid();

                        Map<String,String> userObj = new HashMap<>();
                        userObj.put("userId", currentUserId);
                        userObj.put("username", username);

                        //Adding users to firestore
                        collectionReference.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(Objects.requireNonNull(task.getResult().exists())){
                                                String name = task.getResult().getString("username");
                                                //if user is registered successfully then move to AddJournalActivity
                                                Intent i = new Intent(SignUpActivity.this,AddJournalActivity.class);
                                                i.putExtra("username", name);
                                                i.putExtra("userId", currentUserId);
                                                startActivity(i);
                                            }
                                            else{

                                            }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}