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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class event extends AppCompatActivity {
    Button backFriendPage,createNewEvent,myEvent;

    BottomNavigationView bottomNavigationView;

    ListView lvEventTopics;
    ArrayList<String> listOfEvent = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    //realtime database root
    private DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("event");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

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
//                        startActivity(new Intent(getApplicationContext(),event.class));
//                        overridePendingTransition(0,0);
                        return true;
                    case R.id.bt_page_greenshop:
                        startActivity(new Intent(getApplicationContext(),greenshop_subpage_m.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });

        backFriendPage = findViewById(R.id.back_to_friend);
        createNewEvent = findViewById(R.id.bt_create_event);
        myEvent = findViewById(R.id.bt_my_event);

        //set adapter to show topic
        lvEventTopics = findViewById(R.id.lv_eventTopics);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listOfEvent);
        lvEventTopics.setAdapter(arrayAdapter);

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
        lvEventTopics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getApplicationContext(),eventTopic.class);
                //send user name and chat topic
                intent.putExtra("Selected_Event",((TextView)view).getText().toString());
                //intent.putExtra(("UserName"),UserName);
                startActivity(intent);
            }
        });

        //back to home page
        backFriendPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(event.this,friends_supage_m.class);
                startActivity(intent);
            }
        });

        //create new event page
        createNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(event.this,createNewEvent.class);
                startActivity(intent);
            }
        });

        //go to my event
        myEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(event.this,my_launchEvent.class);
                startActivity(intent);
            }
        });
    }
}