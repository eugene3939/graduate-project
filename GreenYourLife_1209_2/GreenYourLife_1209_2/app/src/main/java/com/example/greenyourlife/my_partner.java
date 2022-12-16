package com.example.greenyourlife;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class my_partner extends AppCompatActivity {

    FirebaseDatabase mDatabase;
    DatabaseReference mRef, dbr_friend;
    FirebaseStorage mStorage;
    RecyclerView recyclerView;
    friendAdapter fAdapter;
    List<friendModel> friendModelList;
    FirebaseAuth mAuth;
    Button backFriend_m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_partner);

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("User");
        mStorage = FirebaseStorage.getInstance();
        recyclerView = findViewById(R.id.friend_recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();

        backFriend_m = findViewById(R.id.bt_backFriend);

        friendModelList = new ArrayList<friendModel>();
        fAdapter = new friendAdapter(my_partner.this,friendModelList);
        recyclerView.setAdapter(fAdapter);

        //set provide a non duplicate collection
        Set<String> set = new HashSet<String>();

        //find current user friends
        dbr_friend = FirebaseDatabase.getInstance().getReference().child("User_Friends");
        dbr_friend.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //iterator enable to cycle throw a collection
                //data snapshot instance contain data from firebase location
                Iterator i = snapshot.getChildren().iterator();

                //add firebase data to set
                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                    //Log.i(String.valueOf(friendAdapter.this),"朋友表: " + ((DataSnapshot)i.next()).getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                friendModel fr = snapshot.getValue(friendModel.class);
                for (String name: set){
                    if (fr.getUserName().equals(name)){
                        friendModelList.add(fr);
                    }
                }
                fAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //back previous page
        backFriend_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(my_partner.this,friends_supage_m.class);
                startActivity(intent);
            }
        });

    }
}