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

import java.util.HashMap;
import java.util.Map;

public class greenshop_exchange_confirm extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    DatabaseReference dbr;
    FirebaseAuth mAuth;
    TextView cpts;
    Button btn1;
    Boolean check = false;
    Integer a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greenshop_exchange_confirm);
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
        TextView selling = (TextView)findViewById(R.id.textView6);
        selling.setText(item);
        GlobalVariable gv = (GlobalVariable)getApplicationContext();
        a=gv.geta();

        if(item.equals("【LUUMI】BOWL 矽膠食物袋 紅色")){
            a=a+1000;

        }
        if(item.equals("【ISUKA】方形保冰袋 MINI")){
            a=a+100;

        }
        if(item.equals("【福葉茶】旅茶組 – 旅茶冷泡組")){
            a=a+10;

        }
        if(item.equals("【日常野草】魚腥草青草茶 2盒組")){
            a=a+1;

        }
        setpoints();
        getUserinfo();
        btn1=findViewById(R.id.shop_confirm_final);
        btn1.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plus(-20);
                gv.seta(a);
                Intent intent= new Intent(greenshop_exchange_confirm.this,greenshop_coupons.class);
                Bundle bundle = new Bundle();

                bundle.putString("item",item);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }));

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
    public void plus(int i) {
        dbr.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (check == false){
                    Integer number1 = Integer.valueOf(snapshot.child("carbonPoints").getValue().toString()) + i;
                    Map<String,Object> updateMap = new HashMap<String, Object>();
                    updateMap.put("carbonPoints",number1);
                    dbr.child(mAuth.getCurrentUser().getUid()).updateChildren(updateMap);
                    //cpts.setText(number1);

                    check =true;
                }
               // Log.i(String.valueOf(greenshop_main.this),"速材料"); //debug
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