package com.example.greenyourlife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class eventTopic extends AppCompatActivity {

    Button bt_back;
    String SelectTopic;
    TextView name,time,location,people,information,hardness,prover,other;

    private DatabaseReference dbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_topic);

        //back button
        bt_back = findViewById(R.id.bt_backEvent);

        //set details
        name = findViewById(R.id.event_name);
        time = findViewById(R.id.event_time);
        location = findViewById(R.id.event_location);
        people = findViewById(R.id.event_people);
        information = findViewById(R.id.event_information);
        hardness = findViewById(R.id.event_hardness);
        prover = findViewById(R.id.event_prover);
        other = findViewById(R.id.event_else);

        //set event details

        SelectTopic = getIntent().getExtras().get("Selected_Event").toString();

        dbr = FirebaseDatabase.getInstance().getReference().child("event").child(SelectTopic);

        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //more than one child
                if (snapshot.exists() && snapshot.getChildrenCount() > 0 ){
                    //set shop name
                    name.setText(snapshot.child("name").getValue().toString());
                    location.setText("地點 ｜ " + snapshot.child("location").getValue().toString());
                    information.setText("活動資訊 ｜ " + snapshot.child("information").getValue().toString());
                    time.setText("時間 ｜ " + snapshot.child("time").getValue().toString());
                    people.setText("人數 ｜ " + snapshot.child("number").getValue().toString());
                    hardness.setText("難易度 ｜ " + snapshot.child("hard").getValue().toString());
                    other.setText("備註 ｜ " + snapshot.child("else").getValue().toString());
                    prover.setText("發起人 ｜ " + snapshot.child("prover").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(eventTopic.this,event.class);
                startActivity(intent);
            }
        });
    }

}