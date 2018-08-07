package com.ahmdkhled.wechat.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.adapters.ChatAdapter;
import com.ahmdkhled.wechat.model.Chat;
import com.ahmdkhled.wechat.model.Message;
import com.ahmdkhled.wechat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    ArrayList<Chat> chatList;
    RecyclerView chatRecycler;
    ChatAdapter chatAdapter;
    DatabaseReference root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecycler=findViewById(R.id.chatRecycler);
        chatList=new ArrayList<>();
        root= FirebaseDatabase.getInstance().getReference().getRoot();

        fetchChats();
    }

    void fetchChats(){
        DatabaseReference chatsRef=root.child("chats");
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    final Chat chat = new Chat();
                    chat.setUid(data.getKey());
                    String user1 = data.child("user1").getValue(String.class);
                    String user2 = data.child("user2").getValue(String.class);
                    String chatUser = null;
                    if (user1.equals(getcurrentUserUid())) {
                        chatUser = user2;
                    } else if (user2.equals(getcurrentUserUid())) {
                        chatUser = user1;
                    }
                    if (chatUser != null) {
                        DatabaseReference userRef = root.child("users").child(chatUser);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d("MSSGS",dataSnapshot.toString());
                                User user = dataSnapshot.getValue(User.class);
                                chat.setUser(user);
                                chatList.add(chat);
                                chatAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                    Query messageRef=root.child("messages").child(chat.getUid())
                            .limitToFirst(1);
                    messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Message message=dataSnapshot.getValue(Message.class);
                            chat.setLastMessage(message.getContent());
                            chatAdapter.notifyDataSetChanged();
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
        showChats(chatList);
    }

    private void showChats(ArrayList<Chat> chatList) {
        chatAdapter =new ChatAdapter(this,chatList);
        chatRecycler.setAdapter(chatAdapter);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    String getcurrentUserUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


}
