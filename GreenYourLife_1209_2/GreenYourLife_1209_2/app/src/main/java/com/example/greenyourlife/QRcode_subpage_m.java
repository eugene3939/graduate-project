package com.example.greenyourlife;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

public class QRcode_subpage_m extends AppCompatActivity {

    //fire base data base
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    DatabaseReference dbr_user;

    //username
    String userName;
    Integer userPoint;

    TextView show_cpoint;
    Button qr_scan,subpage_build_friend;
    Boolean alter = false;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_subpage_m);

        subpage_build_friend = findViewById(R.id.QR_build_page);

        //get bottonNavigationView
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        //set main page
        bottomNavigationView.setSelectedItemId(R.id.bt_page_qrcode);
        //change listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.bt_page_qrcode:
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
                        startActivity(new Intent(getApplicationContext(),greenshop_subpage_m.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        //assign variable
        show_cpoint = findViewById(R.id.show_Cpoint);
        qr_scan = findViewById(R.id.QR_scan_point);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");

        setUserInf();

        //go to add friend QR code page
        subpage_build_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sub_build = new Intent(QRcode_subpage_m.this,qr_build_page.class);
                startActivity(sub_build);
            }
        });

        //go to QR code scan page
        qr_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //initialize intent integrator
                IntentIntegrator intentIntegrator = new IntentIntegrator(QRcode_subpage_m.this);
                //set prompt text
                intentIntegrator.setPrompt("請掃描");
                //set beep
                intentIntegrator.setBeepEnabled(true);
                //lock orientation
                intentIntegrator.setOrientationLocked(true);
                //set capture activity
                intentIntegrator.setCaptureActivity(Capture.class);
                //initialize scan
                intentIntegrator.initiateScan();
            }
        });
    }

    private void setUserInf() {
        Log.i(String.valueOf(QRcode_subpage_m.this),"uid名稱: " + mAuth.getCurrentUser().getUid());
        String getUser = mAuth.getCurrentUser().getUid();
        dbr_user = databaseReference.child(getUser);

        dbr_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0 ){
                    //set user name
                    userName = snapshot.child("userName").getValue().toString();
                    userPoint = Integer.valueOf(snapshot.child("carbonPoints").getValue().toString());

                    //Log.i(String.valueOf(qr_build_point.this),"你是: " +userName);
                    //Log.i(String.valueOf(qr_build_point.this),"點數: " +userPoint);

                    show_cpoint.setText(userPoint + " C");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //initialize intent result
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        //check condition
        if (intentResult.getContents()!= null){
            //check contain to proceed
            containCheck(intentResult.getContents());

        }else {
            //when result content is null
            //display toast
            Toast.makeText(getApplicationContext(),"你沒有掃描喔",Toast.LENGTH_SHORT).show();
        }
    }

    //scan contain check
    private void containCheck(String str) {
        //check add friend or add point
        if (str.contains("*")){   //check for add point not other use
            //Toast.makeText(getApplicationContext(),"集點成功",Toast.LENGTH_SHORT).show();

            String hard = str.replace("*",""); //delete *

            //add value
            Integer plus = 0;
            switch (hard){
                case "A":
                    plus = 100;
                    Toast.makeText(getApplicationContext(),"完成A級任務!",Toast.LENGTH_SHORT).show();
                    break;
                case "B":
                    plus = 80;
                    Toast.makeText(getApplicationContext(),"完成B級任務!",Toast.LENGTH_SHORT).show();
                    break;
                case "C":
                    plus = 60;
                    Toast.makeText(getApplicationContext(),"完成C級任務!",Toast.LENGTH_SHORT).show();
                    break;
                case "D":
                    plus = 40;
                    Toast.makeText(getApplicationContext(),"完成D級任務!",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    plus = 0;
                    Toast.makeText(getApplicationContext(),"不要掃奇怪的內容喔!",Toast.LENGTH_SHORT).show();
                    break;
            }

            //when result content is not fill
            //initialize alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(QRcode_subpage_m.this);
            //set title
            builder.setTitle("任務完成");
            //set message
            builder.setMessage("完成" + hard + "級任務");
            //set positive button
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alter = false;  //no worry about infinite loop
                    //dismiss dialog
                    dialogInterface.dismiss();
                }
            });
            //show alert dialog
            builder.show();

            updatePoints(plus);

        }else {
            Toast.makeText(getApplicationContext(),"集點失敗",Toast.LENGTH_SHORT).show();
        }
    }

    //update mission points
    private void updatePoints(Integer plus) {

        databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0 ){
                    if (alter.equals(false)){   //prevent infinite loop
                        //update user point
                        userPoint = Integer.valueOf(snapshot.child("carbonPoints").getValue().toString()) + plus;
                        Map<String,Object> updateMap = new HashMap<String, Object>();
                        updateMap.put("carbonPoints",userPoint);
                        databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(updateMap);

                        show_cpoint.setText(userPoint + " C");
                        alter = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}