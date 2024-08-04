package com.example.journal;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.journal.model.Journal;
import com.example.journal.util.JournalApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import UI.JournalRecyclerAdapter;

public class JournalListActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
//    private FirebaseStorage firebaseStorage;

//    conneation firebase

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

//    use for recycleview and adpater
    private List<Journal> journalList;
    private RecyclerView recyclerView;
    private JournalRecyclerAdapter journalRecyclerAdapter ;

    private CollectionReference collectionReference = db.collection("Journal");
    private TextView NoJournalEnrty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_journal_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        NoJournalEnrty = findViewById(R.id.journal_time_list);

        journalList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this) );


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getMenuInflater().inflate(R.menu.menu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        if(item.getItemId() == R.id.action_app) {
//            take user to add journal
            if(user != null && firebaseAuth != null) {
                startActivity(new Intent(JournalListActivity.this,postjournalActivity.class));
//                finish();
            }

        }
        if(item.getItemId() == R.id.signout){
//            signout user
            if(user != null && firebaseAuth != null) {
                firebaseAuth.signOut();
                startActivity(new Intent(JournalListActivity.this,MainActivity.class));
//                finish();
            }
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
            collectionReference.whereEqualTo("userId", JournalApi.getInstance().getUserId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(!queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    Journal journal = snapshot.toObject(Journal.class);
                                    journalList.add(journal);
                                }

                                journalRecyclerAdapter = new JournalRecyclerAdapter(JournalListActivity.this, journalList);
                                recyclerView.setAdapter(journalRecyclerAdapter);
                                journalRecyclerAdapter.notifyDataSetChanged();
                            } else {
                                NoJournalEnrty.setVisibility(View.VISIBLE);
                            }
                        }
                    });

    }
}