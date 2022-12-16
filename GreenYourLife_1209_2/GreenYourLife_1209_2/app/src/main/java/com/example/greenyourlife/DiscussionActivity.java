package com.example.greenyourlife;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DiscussionActivity extends AppCompatActivity {

    TextView title,content;
    ImageView chat_photo;

    Button btnSendMsg;
    EditText etMsg;
    ListView lvDiscussion;
    ArrayList<String> listConversation = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    private DatabaseReference dbr;
    DatabaseReference dbr_msg;
    DatabaseReference dbr_content;

    String UserName,SelectTopic,user_msg_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        btnSendMsg = findViewById(R.id.btnSendMsg);
        etMsg = findViewById(R.id.etMessage);

        lvDiscussion = (ListView)findViewById(R.id.lvConversation);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,listConversation);
        lvDiscussion.setAdapter(arrayAdapter);

        title = findViewById(R.id.chat_title);
        content = findViewById(R.id.chat_content);
        chat_photo = findViewById(R.id.chat_photo);

        UserName =getIntent().getExtras().get("UserName").toString();
        SelectTopic = getIntent().getExtras().get("Selected_Topic").toString();
        setTitle("Topic: " + SelectTopic);

        dbr = FirebaseDatabase.getInstance().getReference().child("chat").child(SelectTopic);
        dbr_msg = dbr.child("chatMsg");

        //send msg and update database
        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> map = new HashMap<String, Object>();
                //store msg in key
                user_msg_key = dbr_msg.push().getKey();
                dbr_msg.updateChildren(map);

                DatabaseReference dbr2 = dbr_msg.child(user_msg_key);
                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put("msg",etMsg.getText().toString());
                map2.put("user",UserName);
                dbr2.updateChildren(map2);

                //clear text box
                etMsg.setText(null);
            }
        });

        //realtime update
        dbr_msg.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot,String s) {
                updateConversation(snapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot,String s) {
                updateConversation(snapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot,String s) {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        //fill in choose topic content
        setChatContent();
    }

    //set content to page
    private void setChatContent() {
        //get realtime data base information
        dbr_content = dbr.child("content");

        dbr_content.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0 ){
                    //set shop name
                    String title1 = snapshot.child("title").getValue().toString();
                    String content1 = snapshot.child("content").getValue().toString();
                    //get picture
                    getChatPicture();

                    title.setText(title1);
                    content.setText(content1);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    //update conversation to chat room
    private void updateConversation(DataSnapshot snapshot) {
        String msg, user, conversation;
        Iterator i = snapshot.getChildren().iterator();
        while (i.hasNext()){
            msg = (String) ((DataSnapshot)i.next()).getValue();
            user = (String) ((DataSnapshot)i.next()).getValue();

            conversation = user + ": " + msg;
            arrayAdapter.insert(conversation, 0);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    //get chat photo
    private void getChatPicture() {
        dbr_content.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0){
                    if (snapshot.hasChild("chatImage")){
                        String image = snapshot.child("chatImage").getValue().toString();
                        Picasso.get().load(image).into(chat_photo);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}