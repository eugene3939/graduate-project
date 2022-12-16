package com.example.greenyourlife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class createNewEvent extends AppCompatActivity {

    EditText name,selectDate,location,people,information,other;
    Button backEvent,sentEvent,clearEvent;

    String date;

    //hardness select setting
    Spinner hard_spinner;
    int select_hardness;
    String select_name;
    final String[] hardList = {"A", "B", "C","D"};

    DatabaseReference dbr,dbr_User;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_event);

        mAuth = FirebaseAuth.getInstance();
        dbr = FirebaseDatabase.getInstance().getReference().child("event");
        dbr_User = FirebaseDatabase.getInstance().getReference().child("User");

        //fill in information
        name = findViewById(R.id.et_name);
        selectDate = findViewById(R.id.et_date);
        location = findViewById(R.id.et_location);
        people = findViewById(R.id.et_people);
        information = findViewById(R.id.et_information);
        other = findViewById(R.id.et_other);
        hard_spinner = findViewById(R.id.sp_hard);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DATE);

        //back to event page
        backEvent = findViewById(R.id.back_toEvent);
        sentEvent = findViewById(R.id.sent_newEvent);
        clearEvent = findViewById(R.id.clear_newEvent);

        //set mission hard adapter
        hard_spinner.setAdapter(new ArrayAdapter<>(createNewEvent.this, android.R.layout.simple_spinner_dropdown_item, hardList));
        hard_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                select_hardness = i;    //choose location of hard list  //preset is 0 ("A")
                //Log.i(String.valueOf(createNewEvent.this),"難度:" + select_hardness);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //back to event page
        backEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(createNewEvent.this,event.class);
                startActivity(intent);
            }
        });

        //select date
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(createNewEvent.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        date = year + "/" + month + "/" + day;
                        selectDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        //clear event
        clearEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setText(null);
                selectDate.setText(null);
                location.setText(null);
                information.setText(null);
                people.setText(null);
                other.setText(null);
            }
        });

        //sent event
        sentEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> map = new HashMap<String, Object>();
                //set name as key
                dbr.updateChildren(map);

                //get now user name
                //getUserNow();

                DatabaseReference dbr2 = dbr.child(name.getText().toString());
                Map<String,Object> updateMap = new HashMap<String, Object>();
                updateMap.put("name",name.getText().toString());
                updateMap.put("time",selectDate.getText().toString());
                updateMap.put("location",location.getText().toString());
                updateMap.put("number",people.getText().toString());
                updateMap.put("information",information.getText().toString());
                updateMap.put("else",other.getText().toString());
                updateMap.put("hard",hardList[hard_spinner.getSelectedItemPosition()]);
                updateMap.put("prover",select_name);
                dbr2.updateChildren(updateMap);

                Toast.makeText(createNewEvent.this, "更新完成", Toast.LENGTH_SHORT).show();
                //back to event page
                Intent intent = new Intent(createNewEvent.this,event.class);
                startActivity(intent);
            }
        });

        //get user name
        getUserNow();
    }

    public void getUserNow() {
        dbr_User.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if (snapshot.child("userName").exists()){
                    select_name = snapshot.child("userName").getValue().toString();
                    //Log.i(String.valueOf(createNewEvent.this),"使用者:" + select_name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}