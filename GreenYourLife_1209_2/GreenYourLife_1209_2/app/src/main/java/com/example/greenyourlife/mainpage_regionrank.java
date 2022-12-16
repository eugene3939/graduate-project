package com.example.greenyourlife;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class mainpage_regionrank extends android.app.Activity {

    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    RecyclerView recyclerView;
    friendAdapter2 fAdapter;
    List<friendModel> friendModelList;
    FirebaseAuth mAuth;
    Button backFriend_m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage_regionrank);

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("User");
        mStorage = FirebaseStorage.getInstance();
        recyclerView = findViewById(R.id.friend_recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();

        backFriend_m = findViewById(R.id.bt_backFriend);

        friendModelList = new ArrayList<friendModel>();
        fAdapter = new friendAdapter2(mainpage_regionrank.this,friendModelList);
        recyclerView.setAdapter(fAdapter);

        mRef.addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                friendModel fr = snapshot.getValue(friendModel.class);
                friendModelList.add(fr);

                friendModelList.sort(new Comparator<friendModel>() {
                    @Override
                    public int compare(friendModel t1, friendModel t2) {
                        return t2.getSteps().compareTo(t1.getSteps());
                    }
                });
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
                Intent intent = new Intent(mainpage_regionrank.this,friends_supage_m.class);
                startActivity(intent);
            }
        });

    }
}