package com.ahmdkhled.chatto.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ahmdkhled.chatto.R;
import com.ahmdkhled.chatto.adapters.ChatsAdapter;
import com.ahmdkhled.chatto.model.Chat;
import com.ahmdkhled.chatto.model.Message;
import com.ahmdkhled.chatto.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessagesListActivity extends AppCompatActivity {

    ArrayList<Chat> chatList;
    RecyclerView chatRecycler;
    ChatsAdapter chatAdapter;
    DatabaseReference root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);

        chatRecycler=findViewById(R.id.messagesListRecycler);
        chatList=new ArrayList<>();
        root= FirebaseDatabase.getInstance().getReference().getRoot();

        fetchChats();
    }

    void fetchChats(){
        DatabaseReference chatsRef=root.child("chats");
        chatsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Chat chat = new Chat();
                chat.setUid(dataSnapshot.getKey());
                String user1 = dataSnapshot.child("user1").getValue(String.class);
                String user2 = dataSnapshot.child("user2").getValue(String.class);
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
                            User user = dataSnapshot.getValue(User.class);
                            user.setUid(dataSnapshot.getKey());
                            chat.setUser(user);
                            chatList.add(chat);
                            chatAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                Query messageRef=root.child("messages").child(chat.getUid()).orderByChild("date")
                        .limitToLast(1);
                messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data:dataSnapshot.getChildren()){
                            Log.d("MSSGS",data.toString());
                            Message message=data.getValue(Message.class);
                            chat.setLastMessage(message.getContent());
                            chatAdapter.notifyDataSetChanged();
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
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
        showChats(chatList);
    }

    private void showChats(ArrayList<Chat> chatList) {
        chatAdapter =new ChatsAdapter(this,chatList);
        chatRecycler.setAdapter(chatAdapter);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    String getcurrentUserUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


}
