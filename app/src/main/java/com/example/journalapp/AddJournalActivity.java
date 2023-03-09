package com.example.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddJournalActivity extends AppCompatActivity {

    private static final int GALLERY_CODE = 1;
    private Button savePostBTN;
    private TextView postUsernameTV,postDateTV;
    private EditText postTitleET,postDescriptionET;
    private ProgressBar postProgressBar;
    private ImageView postAddPhotoIV;

    private String currentUserId;
    private String currentUsername;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

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
        postDateTV = findViewById(R.id.postDateTV);
        postTitleET = findViewById(R.id.postTitleET);
        postDescriptionET = findViewById(R.id.postDescriptionET);
        postAddPhotoIV = findViewById(R.id.postIV);
        savePostBTN = findViewById(R.id.postSaveBTN);

        if(JournalUser.getInstance() != null){
            currentUserId = JournalUser.getInstance().getUserId();
            currentUsername = JournalUser.getInstance().getUsername();

            postUsernameTV.setText(currentUsername);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser!=null){

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
                                }
                            });
                        }
                    });
        }
    }
}