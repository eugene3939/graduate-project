package com.example.greenyourlife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.collect.Iterators;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class friends_supage_m extends AppCompatActivity {

    Button eventPage,chatPage,my_partner;
    private Button button;
    Button btntgos;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_supage_m);
        eventPage = findViewById(R.id.go_event_page);
        chatPage = findViewById(R.id.go_chat_page);
        my_partner = findViewById(R.id.my_partner);
//        btntgos=findViewById(R.id.TGOS);
//        btntgos.setOnClickListener((new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Uri uriUrl = Uri.parse("https://www.tgos.tw/MapSites/Web/Map/MS_Map.aspx?themeid=24404&type=edit&visual=point");
//                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
//                startActivity(launchBrowser);
//            }
//        }));
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

        //goto to event page
        eventPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(friends_supage_m.this,event.class);
                startActivity(intent);
            }
        });

        //goto to chat page
        chatPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(friends_supage_m.this,chatroom.class);
                startActivity(intent);
            }
        });

        //go to my partner page
        my_partner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(friends_supage_m.this,my_partner.class);
                startActivity(intent);
            }
        });
    }
}