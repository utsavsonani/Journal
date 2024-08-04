package com.example.journal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.journal.util.JournalApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText userNameEditText,emailEditText,passwordEditText;
    private ProgressBar progressBar;
    private Button createAccountButton ;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

//    firebase conneation
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();

        userNameEditText = findViewById(R.id.userName);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        createAccountButton = findViewById(R.id.crate_account_button);
        progressBar = findViewById(R.id.Login_progress);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(emailEditText.getText().toString())
                    && !TextUtils.isEmpty(passwordEditText.getText().toString())
                    && !TextUtils.isEmpty(userNameEditText.getText().toString())) {

                    String email =  emailEditText.getText().toString();
                    String password =  passwordEditText.getText().toString();
                    String userName  = userNameEditText.getText().toString();



                    createUserEmailAccount(email,password,userName);
                } else {
                    Toast.makeText(getApplicationContext(),"Empty filed is not allowed",Toast.LENGTH_LONG).show();
                }

            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if(currentUser != null){

                }else {

                }
            }
        };
    }

    private void createUserEmailAccount(String email,String password,String userName) {
        if(!TextUtils.isEmpty(email)
            && !TextUtils.isEmpty(password)
            && !TextUtils.isEmpty(userName)) {

            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            Toast.makeText(getApplicationContext(),"enter in onComplete",Toast.LENGTH_LONG).show();
                            if(task.isSuccessful()){
//                                Toast.makeText(getApplicationContext(),"enter in oncomplete if staement",Toast.LENGTH_LONG).show();
//                                we take user to add jounral activity
                                currentUser = firebaseAuth.getCurrentUser();
                                String currentUserId = currentUser.getUid();



//                                creating a user map so we can create user  in the user collection
                                Map<String,String> userObj = new HashMap<>();
                                userObj.put("userId",currentUserId);
                                userObj.put("userName",userName);

//                                save to our firestore database
                                collectionReference.add(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
//                                                Toast.makeText(getApplicationContext(),"enter in colleationreference",Toast.LENGTH_LONG).show();
                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if(task.getResult().exists()) {
//                                                                    Toast.makeText(getApplicationContext(),"enter in onComplete last task",Toast.LENGTH_LONG).show();
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                        String name =  task.getResult().getString("userName");

                                                                    JournalApi journalApi = JournalApi.getInstance();
                                                                    journalApi.setUserName(name);
                                                                    journalApi.setUserId(currentUserId);

                                                                        Intent intent = new Intent(CreateAccountActivity.this,postjournalActivity.class);
//                                                                        intent.putExtra("userId",currentUserId);
//                                                                        intent.putExtra("userName",name);
                                                                        startActivity(intent);


                                                                } else {
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    Toast.makeText(getApplicationContext(),"something went wrong",Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(getApplicationContext(),"something went wrong" + e.getMessage(),Toast.LENGTH_LONG).show();
                                            }
                                        });

                            } else {
//                                something went wrong
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(),"something went wrong" ,Toast.LENGTH_LONG).show();

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(),"something went wrong" + e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

        } else {

            Toast.makeText(getApplicationContext(),"something went wrong" ,Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}