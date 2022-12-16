package com.example.greenyourlife;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class qr_build_page extends AppCompatActivity {

    //initialize variable
    ImageView ivOutput;

    //fire base data base
    FirebaseAuth mAuth;
    DatabaseReference databaseReference,dbr_friend;

    BottomNavigationView bottomNavigationView;
    Button qr_scan;

    //username
    String userName,getName;
    String userUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_build_page);

        //assign variable
        ivOutput = findViewById(R.id.iv_output);
        qr_scan = findViewById(R.id.QR_scan_friend);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        dbr_friend = FirebaseDatabase.getInstance().getReference().child("User_Friends");

        setUserInf();

        //bottom view
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        //setting main page
        bottomNavigationView.setSelectedItemId(R.id.bt_page_miles);
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

        //go to QR code scan page
        qr_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //initialize intent integrator
                IntentIntegrator intentIntegrator = new IntentIntegrator(qr_build_page.this);
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

    //insert user inf to qr code
    public void setUserInf() {
        Log.i(String.valueOf(qr_build_page.this),"uid名稱: " + mAuth.getCurrentUser().getUid());
        String getUser = mAuth.getCurrentUser().getUid();

        //create current user qr code
        userName = "#" + getUser;
        createQrcode(userName);
    }

    //create QR code with user name
    public void createQrcode(String sText) {
        //initialize format writer
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            //initialize bit matrix
            BitMatrix matrix = writer.encode(sText, BarcodeFormat.QR_CODE,350,350);
            //initialize barcode encoder
            BarcodeEncoder encoder = new BarcodeEncoder();
            //initialize bit map
            Bitmap bitmap = encoder.createBitmap(matrix);
            //set bit map  on image view
            ivOutput.setImageBitmap(bitmap);
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

    //after scan
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

    private void containCheck(String str) {
        if (str.contains("#")){  //check for add friend not other use
            userUuid = str.replace("#","");  //delete #

            //friend doesn't user to next step
            if (userUuid != mAuth.getCurrentUser().getUid()){

                Map<String,Object> map = new HashMap<String, Object>();
                //set name as key
                dbr_friend.updateChildren(map);

                //create friend name as key
                databaseReference.child(userUuid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getChildrenCount() > 0 ){
                            //get friend name
                            getName = snapshot.child("userName").getValue().toString();
                            //Log.i(String.valueOf(qr_build_page.this),"取得名稱: " +getName);

                            //save in data base
                            DatabaseReference dbr2 = dbr_friend.child(mAuth.getCurrentUser().getUid());
                            Map<String,Object> updateMap = new HashMap<String, Object>();
                            updateMap.put(getName,userUuid);
                            dbr2.updateChildren(updateMap);

                            //show success message
                            //initialize alert dialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(qr_build_page.this);
                            //set title
                            builder.setTitle("提示");
                            //set message
                            builder.setMessage("成功加入好友");
                            //set positive button
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //dismiss dialog
                                    dialogInterface.dismiss();
                                }
                            });
                            //show alert dialog
                            builder.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

            }else {
                Toast.makeText(getApplicationContext(),"你不能加自己為好友",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"加入失敗",Toast.LENGTH_SHORT).show();
        }
    }

}