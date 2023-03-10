package com.example.journalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journalapp.model.Journal;
import com.example.journalapp.util.JournalUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class AddJournalActivity extends AppCompatActivity {

    private static final int GALLERY_CODE = 1;
    private Button savePostBTN;
    private TextView postUsernameTV;
    private EditText postTitleET,postDescriptionET;
    private ProgressBar postProgressBar;
    private ImageView postAddPhotoIV,imageView;

    private String currentUserId;
    private String currentUsername;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Journal");
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        postProgressBar = findViewById(R.id.postProgressBar);
        postUsernameTV = findViewById(R.id.postUsernameTV);
        postTitleET = findViewById(R.id.postTitleET);
        postDescriptionET = findViewById(R.id.postDescriptionET);
        postAddPhotoIV = findViewById(R.id.postCameraBTN);
        imageView = findViewById(R.id.postIV);
        savePostBTN = findViewById(R.id.postSaveBTN);

        postProgressBar.setVisibility(View.INVISIBLE);

        if(JournalUser.getInstance() != null){
            currentUserId = JournalUser.getInstance().getUserId();
            currentUsername = JournalUser.getInstance().getUsername();
            Log.i("TAG", currentUserId + "        " + currentUsername);
            postUsernameTV.setText(currentUsername);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user !=null){

                }
                else{

                }
            }
        };

        savePostBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveJournal();
            }
        });

        postAddPhotoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting image from gallery
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });
    }

    private void SaveJournal() {
        final String title = postTitleET.getText().toString().trim();
        final String description = postDescriptionET.getText().toString().trim();
        postProgressBar.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && imageUri != null){
            //the saving path of the images in storage database
            // ..../journal_images/our_image.png
            final StorageReference filepath = storageReference.child("journal_images").child("my_image_" + Timestamp.now().getSeconds());
            //uploading the image
            filepath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    //creating object of Journal
                                    //Journal model class here
                                    Journal journal = new Journal();
                                    journal.setTitle(title);
                                    journal.setDescription(description);
                                    journal.setImageUrl(imageUrl);
                                    journal.setTimeAdded(new Timestamp(new Date()));
                                    journal.setUsername(currentUsername);
                                    journal.setUserId(currentUserId);

                                    //Collection reference Invoke
                                    collectionReference.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            postProgressBar.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(AddJournalActivity.this,JournalListActivity.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Failed : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            postProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        }else{
            postProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    //used for getting result from other activity here we are using for choosing image from the gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            if(data != null){
                imageUri = data.getData();           // getting the actual image path
                postAddPhotoIV.setImageURI(imageUri);// showing the image
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}