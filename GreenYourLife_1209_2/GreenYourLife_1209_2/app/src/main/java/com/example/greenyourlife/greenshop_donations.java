package com.example.greenyourlife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static java.lang.String.valueOf;
//.greenshop_donations
public class greenshop_donations extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    DatabaseReference dbr;
    FirebaseAuth mAuth;
    TextView cpts;
    ImageView don1,don2,don3,don4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greenshop_donations);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        //設定主頁
        bottomNavigationView.setSelectedItemId(R.id.bt_page_greenshop);
        //切換監聽器
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bt_page_qrcode:
                        startActivity(new Intent(getApplicationContext(), QRcode_subpage_m.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bt_page_miles:
                        startActivity(new Intent(getApplicationContext(), login_main_page.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bt_page_treasure:
                        startActivity(new Intent(getApplicationContext(), treasure_subpage_m.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bt_page_friends:
                        startActivity(new Intent(getApplicationContext(), friends_supage_m.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bt_page_greenshop:
                        startActivity(new Intent(getApplicationContext(),greenshop_subpage_m.class));
                        overridePendingTransition(0,0);

                        return true;

                }
                return false;
            }
        });
        Bundle bundle = this.getIntent().getExtras();

        String item= bundle.getString("item");
        GlobalVariable gv = (GlobalVariable)getApplicationContext();
        int b=gv.getb();
        don1=findViewById(R.id.donationimage1);
        don2=findViewById(R.id.donationimage2);
        don3=findViewById(R.id.donationimage3);
        don4=findViewById(R.id.donationimage4);
        cpts=findViewById(R.id.shop_cpts);
        mAuth = FirebaseAuth.getInstance();
        if (b/1000>0.5){
            TextView selling = (TextView)findViewById(R.id.textView_donation1);
            selling.setText("【LUUMI】BOWL 矽膠食物袋 紅色");
            don1.setImageResource(R.drawable.item1);

        }
        if (b % 1000 >= 100){
            TextView selling = (TextView)findViewById(R.id.textView_donation2);
            selling.setText("【ISUKA】方形保冰袋 MINI");
            don2.setImageResource(R.drawable.item2);
        }
        if (b%100>=10){
            TextView selling = (TextView)findViewById(R.id.textView_donation3);
            selling.setText("【福葉茶】旅茶組 – 旅茶冷泡組");
            don3.setImageResource(R.drawable.item3);
        }
        if (b%10==1){
            TextView selling = (TextView)findViewById(R.id.textView_donation4);
            selling.setText("【日常野草】魚腥草青草茶 2盒組");
            don4.setImageResource(R.drawable.item4);
        }
        setpoints();
        getUserinfo();

    }
    public void setpoints() {
        dbr = FirebaseDatabase.getInstance().getReference().child("User");
        dbr.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String point = snapshot.child("carbonPoints").getValue().toString();
                cpts.setText(point);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getUserinfo() {
        ImageView shop_pic;
        shop_pic = findViewById(R.id.shop_pic);
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0){
                    if (snapshot.hasChild("image")){
                        String image = snapshot.child("image").getValue().toString();
                        //set user photo
                        Picasso.get().load(image).into(shop_pic);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}