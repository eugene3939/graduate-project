package com.example.greenyourlife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.UserDataReader;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.firestore.core.UserData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class register extends AppCompatActivity {

    Activity content = this;
    Button createA,clear;
    TextView msg;
    EditText sAccount,sPassword,sName;
    private FirebaseFirestore db;

    Map<String,Object> user = new HashMap<>();

    //help login successful user
    //no need to input again
    boolean SignState = false;
    String newAcc;

    FirebaseAuth mAuth;
    DatabaseReference dbr;
    private Object Tag = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createA = findViewById(R.id.create_acc);
        clear = findViewById(R.id.clear_d);

        dbr = FirebaseDatabase.getInstance().getReference().child("User");

        sAccount = findViewById(R.id.sign_acc);
        sPassword = findViewById(R.id.sign_pas);
        sName = findViewById(R.id.sign_nickName);

        msg = findViewById(R.id.text_message);

        mAuth = FirebaseAuth.getInstance();

        createA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.createUserWithEmailAndPassword(sAccount.getText().toString(),sPassword.getText().toString()).addOnCompleteListener(content, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            msg.setText("結果:"+ firebaseUser.getEmail() + "註冊成功!");
                            SignState = true;
                            newAcc = firebaseUser.getEmail();

                            //upload to realtime database
                            Map<String,Object> map = new HashMap<String, Object>();
                            //set name as key
                            dbr.updateChildren(map);

                            DatabaseReference dbr2 = dbr.child(mAuth.getCurrentUser().getUid());
                            Map<String,Object> updateMap = new HashMap<String, Object>();
                            updateMap.put("uid",newAcc);
                            updateMap.put("userName",sName.getText().toString());
                            updateMap.put("carbonPoints",0);
                            updateMap.put("route",0);
                            updateMap.put("steps",0);
                            updateMap.put("speed",0);
                            updateMap.put("transport","走路");

                            dbr2.updateChildren(updateMap);

                            Toast.makeText(register.this, "註冊完成", Toast.LENGTH_SHORT).show();
                        }else {
                            msg.setText("結果:註冊失敗!" + task.getException());
                        }
                    }
                });
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sAccount.setText(null);
                sPassword.setText(null);
            }
        });
    }
}