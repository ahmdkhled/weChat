package com.ahmdkhled.chatto.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.ahmdkhled.chatto.R;
import com.ahmdkhled.chatto.utils.Encryption;
import com.ahmdkhled.chatto.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText nameET, emailET, passwordET;
    TextInputLayout nameIL, emailIL, passwordIL;
    Button signUp, gotoLogin;
    FirebaseAuth mAuth;
    DatabaseReference rootRef;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameET = findViewById(R.id.fullName_ET);
        emailET = findViewById(R.id.email_ET);
        passwordET = findViewById(R.id.password_ET);
        nameIL = findViewById(R.id.fullName_IL);
        emailIL = findViewById(R.id.email_IL);
        passwordIL = findViewById(R.id.password_IL);
        signUp = findViewById(R.id.signUp);
        gotoLogin = findViewById(R.id.gotoLogIn_BU);
        progressBar = findViewById(R.id.signUp_PB);
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference().getRoot();

        getSupportActionBar().setElevation(0);

        FirebaseUser user=mAuth.getCurrentUser();
        if (user!=null){
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = nameET.getText().toString();
                final String email = emailET.getText().toString();
                final String password = passwordET.getText().toString();
                validateInput(name, email, password);
                if (!Utils.isEmpty(name, email, password)) {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if (user != null) {
                                            saveUser(user.getUid(), name, email, password);
                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", e.getMessage());
                            Toast.makeText(getApplicationContext(), R.string.failled+" "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

            }
        });


        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });





    }

    void validateInput(String name, String mail, String pass) {
        if (Utils.isEmpty(name)) {
            nameIL.setError(getString(R.string.enter_your_name_please));
        }
        if (Utils.isEmpty(mail)) {
            emailIL.setError(getString(R.string.enter_your_mail_please));
        }
        if (Utils.isEmpty(pass)) {
            passwordIL.setError(getString(R.string.enter_your_password_please));
        }
    }



    void saveUser(String userId, String name, String email, String password) {
        DatabaseReference uidRef = rootRef.child("users").child(userId);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("password", Encryption.encrypt(userId,password));
        userMap.put("profileImg", "");
        userMap.put("bio", "");
        String token= FirebaseInstanceId.getInstance().getToken();
        userMap.put("notification_token", token);
        uidRef.updateChildren(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), R.string.signed_up, Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), R.string.failled+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }



}
