package com.example.journal;

import java.util.Date;
import com.google.firebase.Timestamp;

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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.journal.model.Journal;
import com.example.journal.util.JournalApi;
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

//import java.sql.Date;
import java.sql.Time;

public class postjournalActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_CODE =  1;
    //     private ActivityPostjournalBinding binding;
    private Button saveButton;
    private EditText titleEditText,thoughtaEditText;
    private ProgressBar progressBar;
    private ImageView addPhotoButton;
    private TextView currentUserTextview;
    private ImageView imageView;

    private String currentUserId;
    private String currentUserName;

    private Uri imageUrl;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser User;


//    connection to Firestore

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//    colleaction reference

    private CollectionReference collectionReference = firebaseFirestore.collection("Journal");

    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_postjournal);
//        binding = DataBindingUtil.setContentView(this,R.layout.activity_postjournal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        saveButton = findViewById(R.id.post_save_journal_button);
        titleEditText = findViewById(R.id.post_Title);
        thoughtaEditText = findViewById(R.id.post_description);
        progressBar = findViewById(R.id.progress);
        addPhotoButton = findViewById(R.id.post_Add_Image);
        imageView = findViewById(R.id.imageView);
        currentUserTextview = findViewById(R.id.post_username_textview);

        saveButton.setOnClickListener(this);
        addPhotoButton.setOnClickListener(this);

        progressBar.setVisibility(View.INVISIBLE);

        if(JournalApi.getInstance() != null) {
            currentUserId = JournalApi.getInstance().getUserId();
            currentUserName = JournalApi.getInstance().getUserName();

            currentUserTextview.setText(currentUserName);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    User = firebaseAuth.getCurrentUser();
                    if(User != null) {

                    }else {

                    }
            }
        };

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.post_save_journal_button) {
//            save journal
            saveJournal();
        }
        if(v.getId() == R.id.post_Add_Image) {
//            get image from gallery/phone
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY_CODE);
        }
    }

    private void saveJournal() {

        progressBar.setVisibility(View.VISIBLE);

        String title = titleEditText.getText().toString().trim();
        String thoughts = thoughtaEditText.getText().toString().trim();

//        Toast.makeText(getApplicationContext(),"enter in save button mmethode  ",Toast.LENGTH_SHORT).show();

        if(!TextUtils.isEmpty(title)
            && !TextUtils.isEmpty(thoughts)
            && imageUrl != null) {

//            Toast.makeText(getApplicationContext(),"check it work fast or slow",Toast.LENGTH_SHORT).show();

            StorageReference filepath = storageReference // ../ journal_imagess/our_images.jpeg
                     .child("journal_images")
                     .child("my_image_" + Timestamp.now().getSeconds());

//            Toast.makeText(getApplicationContext(),"enter in save button mmethode  ",Toast.LENGTH_SHORT).show();

            filepath.putFile(imageUrl)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(getApplicationContext(),"image uploaded",Toast.LENGTH_SHORT).show();

                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

//                                    Toast.makeText(getApplicationContext(),"enter in onSuccess  ",Toast.LENGTH_SHORT).show();

                                    String imageurl  =uri.toString();

                                    Journal journal = new Journal();
                                    journal.setTitle(title);
                                    journal.setThought(thoughts);
                                    journal.setImageUrl(imageurl);
                                    journal.setTimeAdd(new Timestamp(new Date()));
                                    journal.setUserName(currentUserName);
                                    journal.setUserId(currentUserId);

                                    collectionReference.add(journal)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    progressBar.setVisibility(View.INVISIBLE);
//                                                    Toast.makeText(getApplicationContext(),"enter in onSuccess  last",Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(postjournalActivity.this,JournalListActivity.class));
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(),"onFailure " + e.getMessage(),Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"onFailure " + e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(),"something worng",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        Toast.makeText(getApplicationContext(),"onActitvity",Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == GALLERY_CODE) && (resultCode == RESULT_OK)) {
            if(data != null) {
                 imageUrl = data.getData();

                 imageView.setImageURI(imageUrl);

//                Toast.makeText(getApplicationContext(),"images uploaded from onactivity",Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(getApplicationContext(),"images not uploaded",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        User = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}