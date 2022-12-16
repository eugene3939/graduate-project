package com.example.greenyourlife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class greenshop_subpage_m extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    TextView cpts;
    Boolean check = false;

    DatabaseReference dbr;
    FirebaseAuth mAuth;
    CircleImageView btn1,btn2,btn3,btn4,shopImage;
    Button btn5,btn6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greenshop_subpage_m);

        //以下要複製 宣告
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        //設定主頁
        bottomNavigationView.setSelectedItemId(R.id.bt_page_greenshop);
        //切換監聽器
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
                        startActivity(new Intent(getApplicationContext(),friends_supage_m.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.bt_page_greenshop:
                        return true;

                }
                return false;
            }
        });

        dbr = FirebaseDatabase.getInstance().getReference().child("User");
        mAuth = FirebaseAuth.getInstance();
        shopImage = findViewById(R.id.shop_pic);

        cpts=findViewById(R.id.shop_cpts);
        btn1=findViewById(R.id.shop_item1);
        btn1.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opensubpage1();
            }
        }));
        btn2=findViewById(R.id.shop_item2);
        btn2.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opensubpage2();
            }
        }));
        btn3=findViewById(R.id.shop_item3);
        btn3.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opensubpage3();
            }
        }));
        btn4=findViewById(R.id.shop_item4);
        btn4.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opensubpage4();
            }
        }));


        //get c point
        setPoints();
        //get user photo
        getUserinfo();

        //plus(5);
    }

    private void opensubpage6() {
        Intent intent= new Intent(this,greenshop_donations.class);
        startActivity(intent);
    }


    public void opensubpage5() {
        Intent intent= new Intent(this,greenshop_coupons.class);
        startActivity(intent);
    }


    public void opensubpage1() {
        Intent intent= new Intent(this,greenshop_subpage1.class);
        Bundle bundle = new Bundle();
        String one="【LUUMI】BOWL 矽膠食物袋 紅色";//item name
        bundle.putString("item",one);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public void opensubpage2() {
        Intent intent= new Intent(this,greenshop_subpage1.class);
        Bundle bundle = new Bundle();
        String one="【ISUKA】方形保冰袋 MINI";//item name
        bundle.putString("item",one);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public void opensubpage3() {
        Intent intent= new Intent(this,greenshop_subpage1.class);
        Bundle bundle = new Bundle();
        String one="【福葉茶】旅茶組 – 旅茶冷泡組";//item name
        bundle.putString("item",one);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public void opensubpage4() {
        Intent intent = new Intent(this,greenshop_subpage1.class);
        Bundle bundle = new Bundle();
        String one="【日常野草】魚腥草青草茶 2盒組";//item name
        bundle.putString("item",one);
        intent.putExtras(bundle);
        startActivity(intent);
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
                //Log.i(String.valueOf(greenshop_main.this),"速材料"); //debug
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setPoints() {
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

    //get user photo file
    private void getUserinfo() {
        dbr.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0){
                    if (snapshot.hasChild("image")){
                        String image = snapshot.child("image").getValue().toString();
                        //set user photo
                        Picasso.get().load(image).into(shopImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}