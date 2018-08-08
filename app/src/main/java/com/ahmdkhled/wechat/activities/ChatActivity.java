package com.ahmdkhled.wechat.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.adapters.MessagesAdapter;
import com.ahmdkhled.wechat.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class ChatActivity extends AppCompatActivity {

    EditText writeMesage;
    ImageView sendMessage;
    RecyclerView chatRecycler;
    DatabaseReference root;
    ArrayList<Message> messagesList;
    MessagesAdapter messagesAdapter;
    String chatUid=null;
    String receiverUid;
    public static final String  RECEIVER_Uid_TAG="receiver_uid";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        writeMesage=findViewById(R.id.writeMessage);
        sendMessage=findViewById(R.id.sendMessage);
        chatRecycler=findViewById(R.id.chatRecycler);
        messagesList=new ArrayList<>();
        root= FirebaseDatabase.getInstance().getReference().getRoot();

        if (getIntent()!=null&&getIntent().hasExtra(RECEIVER_Uid_TAG)){
            receiverUid=getIntent().getStringExtra(RECEIVER_Uid_TAG);
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
                        chatMap.put("user2", receiverUid);
                        chatRef.updateChildren(chatMap);
                    }
                    DatabaseReference messagesRef = root.child("messages").child(chatUid).push();
                    Message message = new Message(messageContent, getcurrentUserUid(), System.currentTimeMillis(), false);
                    messagesRef.setValue(message);

                }
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
                    if ( (user1.equals(getcurrentUserUid())&&user2.equals(receiverUid))||
                         (user2.equals(getcurrentUserUid())&&user1.equals(receiverUid)) ){
                        chatUid=data.getKey();
                        break;
                    }
                }

                if (chatUid==null){
                    Toast.makeText(getApplicationContext(),"you have never messaged .send message now ",Toast.LENGTH_SHORT).show();
                }else {
                    Query messagesRef=root.child("messages").child(chatUid).orderByChild("date");
                    messagesRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Message message=dataSnapshot.getValue(Message.class);
                            messagesList.add(message);
                            messagesAdapter.notifyDataSetChanged();
                            chatRecycler.smoothScrollToPosition(messagesList.size()-1);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        messagesAdapter=new MessagesAdapter(this,messagesList);
        chatRecycler.setAdapter(messagesAdapter);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));

    }

    String getcurrentUserUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }



}
