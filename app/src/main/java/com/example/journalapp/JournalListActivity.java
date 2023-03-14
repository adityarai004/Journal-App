package com.example.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.journalapp.model.Journal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class JournalListActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;

    private StorageReference storageReference;

    private List<Journal> journalList;
    private RecyclerView recyclerView;
//    private JournalRecyclerAdapter journalRecyclerAdapter;

    private CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Journal");
    private TextView noPosts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //widgets
        noPosts = findViewById(R.id.list_no_posts);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //posts arraylist
        journalList = new ArrayList<>();
    }

    //Adding menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ((item.getItemId())){
            case R.id.action_add:
                    if(user!=null && firebaseAuth != null ){
                        startActivity(new Intent(JournalListActivity.this,AddJournalActivity.class));
                    }
                    break;
            case R.id.action_signOut:
                if(user!=null && firebaseAuth != null ){
                    firebaseAuth.signOut();
                    startActivity(new Intent(JournalListActivity.this,
                            MainActivity.class));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //getting all the posts
}