package com.example.greenyourlife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class chatroom extends AppCompatActivity {

    ListView lvDiscussTopics;
    ArrayList<String> listOfDiscussion = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    String UserName;

    Button backFriendPage,createNewChat;

    //realtime database root
    FirebaseAuth mAuth;
    private DatabaseReference dbr_user = FirebaseDatabase.getInstance().getReference().child("User");
    private DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("chat");

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        backFriendPage = findViewById(R.id.back_friend);
        createNewChat = findViewById(R.id.new_chat);

        mAuth = FirebaseAuth.getInstance();

        //get bottonNavigationView
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        //set main page
        bottomNavigationView.setSelectedItemId(R.id.bt_page_friends);
        //change listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.bt_page_qrcode:
                        startActivity(new Intent(getApplicationContext(),QRcode_subpage_m.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.bt_page_miles:
                        startActivity(new Intent(getApplicationContext(),login_main_page.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.bt_page_treasure:
                        startActivity(new Intent(getApplicationContext(),treasure_subpage_m.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.bt_page_friends:
                        return true;
                    case R.id.bt_page_greenshop:
                        startActivity(new Intent(getApplicationContext(),greenshop_subpage_m.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });

        //set adapter to show topic
        lvDiscussTopics = findViewById(R.id.lv_discussTopics);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listOfDiscussion);
        lvDiscussTopics.setAdapter(arrayAdapter);

        //get user name
        getUserName();

        //keep track database data change
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //set provide a non duplicate collection
                Set<String> set = new HashSet<String>();
                //iterator enable to cycle throw a collection
                //data snapshot instance contain data from firebase location
                Iterator i = snapshot.getChildren().iterator();

                //add firebase data to set
                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }

                //add set to arrayAdapter
                arrayAdapter.clear();
                arrayAdapter.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        //click list view
        lvDiscussTopics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getApplicationContext(),DiscussionActivity.class);
                //send user name and chat topic
                intent.putExtra("Selected_Topic",((TextView)view).getText().toString());
                intent.putExtra(("UserName"),UserName);
                startActivity(intent);
            }
        });

        //back to friend page
        backFriendPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(chatroom.this,friends_supage_m.class);
                startActivity(intent);
            }
        });

        //create new chat room
        createNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(chatroom.this,createNewChat.class);
                startActivity(intent);
            }
        });
    }

    //send user name
    private void getUserName(){
        //get realtime data base information
        dbr_user.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0 ){
                    //set shop name
                    UserName = snapshot.child("userName").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}