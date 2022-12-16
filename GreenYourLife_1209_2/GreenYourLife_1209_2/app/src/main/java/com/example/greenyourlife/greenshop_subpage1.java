package com.example.greenyourlife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class greenshop_subpage1 extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Button btn1,btn2;
    DatabaseReference dbr;
    FirebaseAuth mAuth;
    TextView cpts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greenshop_subpage1);

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
                        return true;

                }
                return false;
            }
        });

        dbr = FirebaseDatabase.getInstance().getReference().child("User");
        mAuth = FirebaseAuth.getInstance();
        cpts=findViewById(R.id.shop_cpts);
        Bundle bundle = this.getIntent().getExtras();
        String item= bundle.getString("item");
        TextView selling = (TextView)findViewById(R.id.textView4);
        selling.setText(item);
        btn1=findViewById(R.id.shop_confirm1);
        btn1.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openconfirmpage(item);
            }
        }));
        btn2=findViewById(R.id.shop_confirm2);
        btn2.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openconfirmpage2(item);
            }
        }));

        setpoints();

        getUserinfo();
    }

    public void setpoints() {
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

    private void openconfirmpage2(String item) {//exchange confirm
        Intent intent= new Intent(this,greenshop_exchange_confirm.class);
        Bundle bundle = new Bundle();
        // String one="【LUUMI】BOWL 矽膠食物袋 紅色";//item name
        bundle.putString("item",item);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void openconfirmpage(String item) {//donate confirm
        Intent intent= new Intent(this,shop_donate_confirm.class);
        Bundle bundle = new Bundle();
        //String one="【LUUMI】BOWL 矽膠食物袋 紅色";//item name
        bundle.putString("item",item);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //get user photo file
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