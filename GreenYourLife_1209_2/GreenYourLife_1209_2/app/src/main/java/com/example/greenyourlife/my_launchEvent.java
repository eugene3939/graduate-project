package com.example.greenyourlife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class my_launchEvent extends AppCompatActivity {

    Button toLastPage;
    ImageView picture;

    BottomNavigationView bottomNavigationView;

    ListView lvEventTopics;
    TextView inf;
    ArrayList<String> listOfEvent = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    ArrayList<String> getEventName = new ArrayList<String>();
    ArrayList<String> getEventHard = new ArrayList<String>();

    //realtime database root
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("event");
    DatabaseReference dbr_User = FirebaseDatabase.getInstance().getReference().child("User");

    String nowUser;
    Boolean userEventState;
    int event_num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_launch_event);

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

        toLastPage = findViewById(R.id.back_to_event);
        picture = findViewById(R.id.iv_generateImage);
        inf = findViewById(R.id.empty_text);

        userEventState = false;

        //set adapter to show topic
        lvEventTopics = findViewById(R.id.lv_my_events);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listOfEvent);
        lvEventTopics.setAdapter(arrayAdapter);

        //back to home page
        toLastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(my_launchEvent.this,event.class);
                startActivity(intent);
            }
        });

        //get now user name
        getUserNow();

        //keep track database data change
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                arrayAdapter.clear();

                //set provide a non duplicate collection
                Set<String> set = new HashSet<String>();

                //data snapshot instance contain data from firebase location
                Iterator i = snapshot.getChildren().iterator();

                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }

                //get all children from hotel file name set
                for (String filename: set){
                    //get child file name
                    DatabaseReference dbr2 = dbr.child(filename);
                    dbr2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            //the event now user launched
                            if (snapshot.exists() && snapshot.child("prover").getValue().toString().equals(nowUser)){
                                //set  event
                                arrayAdapter.add(filename);
                                //event information
                                String hard = snapshot.child("hard").getValue().toString();
                                getEventName.add(filename);
                                getEventHard.add(hard);
                                userEventState = true;//check null or not
                                Log.i(String.valueOf(my_launchEvent.this),filename);

                                inf.setText("點擊下方活動內容產生QR Code");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });
                }

                //add set to arrayAdapter
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
                //get in formation
                Log.i(String.valueOf(my_launchEvent.this),"取得" + getEventName.get(position));
                Log.i(String.valueOf(my_launchEvent.this),"難度" + getEventHard.get(position));

                createQRcode(getEventHard.get(position));
            }
        });
    }

    private void createQRcode(String hardness) {
        //initialize format writer
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            //initialize bit matrix
            BitMatrix matrix = writer.encode("*" + hardness, BarcodeFormat.QR_CODE,350,350);
            //initialize barcode encoder
            BarcodeEncoder encoder = new BarcodeEncoder();
            //initialize bit map
            Bitmap bitmap = encoder.createBitmap(matrix);
            //set bit map  on image view
            picture.setImageBitmap(bitmap);
            //initialize input manger
            InputMethodManager manager = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE
            );
            //hide soft keyboard
            //manager.hideSoftInputFromWindow(etInput.getApplicationWindowToken(),0);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void getUserNow() {
        dbr_User.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if (snapshot.child("userName").exists()){
                    nowUser = snapshot.child("userName").getValue().toString();
                    Log.i(String.valueOf(my_launchEvent.this),"使用者:" + nowUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}