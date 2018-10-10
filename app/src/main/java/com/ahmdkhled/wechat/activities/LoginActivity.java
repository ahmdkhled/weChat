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
import android.widget.ProgressBar;
import android.widget.Toast;
import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    Button loginBU,gotoSignup;
    TextInputLayout emailIL,passwordIL;
    EditText emailET,passwordET;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBU=findViewById(R.id.login_BU);
        gotoSignup=findViewById(R.id.gotoSignup_BU);
        emailIL=findViewById(R.id.loginEmail_IL);
        emailET=findViewById(R.id.loginEmail_ET);
        passwordIL=findViewById(R.id.loginPass_IL);
        passwordET=findViewById(R.id.loginPass_ET);
        progressBar=findViewById(R.id.login_PB);
        mAuth=FirebaseAuth.getInstance();


        gotoSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });


        loginBU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailET.getText().toString();
                String password=passwordET.getText().toString();
                validateInput(email,password);
                if (!Utils.isEmpty(email,password)){
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    String token= FirebaseInstanceId.getInstance().getToken();
                                    Log.d("FCMM", "login token: " + token);
                                    if (token!=null){
                                        updateToken(token);
                                    }

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(),getString(R.string.failed_to_login)+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
                }
            }
        });
    }

    void validateInput( String mail, String pass) {

        if (Utils.isEmpty(mail)) {
            emailIL.setError(getString(R.string.enter_your_mail_please));
        }
        if (Utils.isEmpty(pass)) {
            passwordIL.setError(getString(R.string.enter_your_password_please));
        }
    }


    void updateToken(String token){
        DatabaseReference root= FirebaseDatabase.getInstance().getReference().getRoot();
        DatabaseReference tokenRef=root.child("users")
                .child(getCurrentUserUid())
                .child("notification_token");
        tokenRef.setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    String getCurrentUserUid(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            return user.getUid();
        }
        return null;
    }

}
