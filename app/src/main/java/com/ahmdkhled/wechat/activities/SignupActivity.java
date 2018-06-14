package com.ahmdkhled.wechat.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.model.Post;
import com.ahmdkhled.wechat.model.User;
import com.ahmdkhled.wechat.utils.Encryption;
import com.ahmdkhled.wechat.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText nameET, emailET, passwordET;
    TextInputLayout nameIL, emailIL, passwordIL;
    Button signUp, gotoLogin;
    FirebaseAuth mAuth;
    DatabaseReference rootRef;
    ProgressDialog progressDialog;

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
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference().getRoot();
        progressDialog = new ProgressDialog(this);

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
                    showDialog();
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
                            progressDialog.dismiss();
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
            nameIL.setError("enter your name please");
        }
        if (Utils.isEmpty(mail)) {
            emailIL.setError("enter your mail please");
        }
        if (Utils.isEmpty(pass)) {
            passwordIL.setError("enter your password please");
        }
    }

    void showDialog() {
        progressDialog.setTitle("please wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    void saveUser(String userId, String name, String email, String password) {

        DatabaseReference uidRef = rootRef.child("users").child(userId);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("password", Encryption.encrypt(userId,password));
        userMap.put("profileImg", "");
        userMap.put("bio", "");
        uidRef.updateChildren(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "signed up  ", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "failed  ", Toast.LENGTH_SHORT).show();

            }
        });


    }



}
