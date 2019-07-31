package com.ahmdkhled.chatto.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmdkhled.chatto.R;
import com.ahmdkhled.chatto.adapters.MessagesAdapter;
import com.ahmdkhled.chatto.model.Message;
import com.ahmdkhled.chatto.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;


public class ChatActivity extends AppCompatActivity {

    EditText writeMesage;
    ImageView sendMessage,importImage;
    RecyclerView chatRecycler;
    DatabaseReference root;
    ArrayList<Message> messagesList;
    MessagesAdapter messagesAdapter;
    String chatUid=null;
    User user;
    Toolbar toolbar;
    TextView toolbarTitle;
    Uri imageUri=null;
    public static final String USER_TAG ="user_tag";
    private final int PICK_IMAGE_CODE=73;
    private int STORAGE_PERMISSION_CODE=74;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        writeMesage=findViewById(R.id.writeMessage);
        sendMessage=findViewById(R.id.sendMessage);
        importImage=findViewById(R.id.importImage);
        chatRecycler=findViewById(R.id.chatRecycler);
        toolbar=findViewById(R.id.chatToolbar);
        toolbarTitle=findViewById(R.id.chatTitle);
        messagesList=new ArrayList<>();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        root= FirebaseDatabase.getInstance().getReference().getRoot();

        if (getIntent()!=null&&getIntent().hasExtra(USER_TAG)){
            user =getIntent().getParcelableExtra(USER_TAG);
            toolbarTitle.setText(user.getName());


        }else {
            Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
            finish();
        }

        fetchMessages();

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageContent = writeMesage.getText().toString();
                writeMesage.setText("");
                if (!TextUtils.isEmpty(messageContent)) {
                    if (chatUid == null) {
                        DatabaseReference chatRef = root.child("chats");
                        chatUid = chatRef.push().getKey();
                        chatRef = chatRef.child(chatUid);
                        HashMap<String, Object> chatMap = new HashMap<>();
                        chatMap.put("user1", getcurrentUserUid());
                        chatMap.put("user2", user.getUid());
                        chatRef.updateChildren(chatMap);
                    }
                    DatabaseReference messagesRef = root.child("messages").child(chatUid).push();
                    Message message = new Message(messageContent,"", getcurrentUserUid(), System.currentTimeMillis(), false);
                    messagesRef.setValue(message);

                }
            }
        });

        importImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grantPermission();
            }
        });

    }

    private void fetchMessages(){
        DatabaseReference chatRef=root.child("chats");
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    String user1=data.child("user1").getValue(String.class);
                    String user2=data.child("user2").getValue(String.class);
                    if ( (user1.equals(getcurrentUserUid())&&user2.equals(user.getUid()))||
                         (user2.equals(getcurrentUserUid())&&user1.equals(user.getUid())) ){
                        chatUid=data.getKey();
                        break;
                    }
                }

                if (chatUid==null){
                    Toast.makeText(getApplicationContext(),"you have never messaged.send message now ",Toast.LENGTH_SHORT).show();
                }else {
                    Query messagesRef=root.child("messages").child(chatUid).orderByChild("date");
                    messagesRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Message message=dataSnapshot.getValue(Message.class);
                            message.setUid(dataSnapshot.getKey());
                            messagesList.add(message);
                            if (!message.isSeen()&&!message.getSenderUid().equals(getcurrentUserUid())){
                                message.setSeen(true);
                                setMessageSeen(message.getUid());
                            }
                            messagesAdapter.notifyDataSetChanged();
                            chatRecycler.smoothScrollToPosition(messagesList.size()-1);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            Message message=dataSnapshot.getValue(Message.class);
                            message.setUid(dataSnapshot.getKey());
                            int index=messagesList.indexOf(message);
                            messagesList.get(index).setSeen(message.isSeen());
                            messagesAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        populateMessages(messagesList);
    }

    void populateMessages(ArrayList<Message> messagesList){
        messagesAdapter=new MessagesAdapter(this,messagesList,user);
        chatRecycler.setAdapter(messagesAdapter);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    void setMessageSeen(String messageUid){
        DatabaseReference messageRef=root.child("messages")
                .child(chatUid).child(messageUid).child("seen");
        messageRef.setValue(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_CODE &&data!=null){
            imageUri=data.getData();
            uploadprofileImg(imageUri);
            imageUri=null;
        }
    }

    void uploadprofileImg(final Uri imageUri){
        final StorageReference storageRef= FirebaseStorage.getInstance().getReference();
        storageRef.child("chatImages/"+chatUid+System.currentTimeMillis()).putFile(imageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            String url=task.getResult().getDownloadUrl().toString();

                            DatabaseReference messagesRef = root.child("messages").child(chatUid).push();
                            Message message = new Message("",url, getcurrentUserUid()
                                    , System.currentTimeMillis(), false);
                            messagesRef.setValue(message);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("STORAGEEE",""+e.getMessage());
            }
        });
    }

    void pickImage(){
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,PICK_IMAGE_CODE);
    }

    void grantPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
                    alertDialog.setTitle(R.string.Storage_Permission);
                    alertDialog.setMessage(R.string.permission_body);
                    alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                            }
                        }
                    });
                    alertDialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(), R.string.ok_thanks,Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.show();
                }else{
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                }
            }else {
                pickImage();
            }
        }else {
            pickImage();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==STORAGE_PERMISSION_CODE){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                pickImage();
            }
        }
    }

    String getcurrentUserUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }



}
