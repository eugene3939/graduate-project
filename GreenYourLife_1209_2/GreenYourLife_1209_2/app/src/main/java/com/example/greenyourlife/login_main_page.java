package com.example.greenyourlife;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class login_main_page extends AppCompatActivity implements SensorEventListener {
    private TextView textViewStepDetector;
    private SensorManager sensorManager;
    private Sensor mStepCounter;
    private boolean isCounterSensorPresent;
    int stepCount=0;
    int preStep = 0;
    boolean firstStepLock = false;
    //database c point
    Integer db_step = 0;

    BottomNavigationView bottomNavigationView;
    Button to_rank;
    Button btntgos,btnregionrank;
    TextView tv_Step,tv_Tra,tv_Speed,tv_C_point,tv_topTitle;

    String uid; //now login email

    CircleImageView imageView;

    //user information
    String transport,userName,cPoint,route,steps,speed;

    FirebaseAuth mAuth;
    FirebaseStorage storage;

    DatabaseReference databaseReference;
    DatabaseReference dbr_user;

    Uri imageUri;
    String myUri = "";

    StorageTask uploadTask;
    StorageReference storageProfileRef;

    @SuppressLint("WrongViewCast")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main_page);

        GlobalVariable gv = (GlobalVariable)getApplicationContext();
        gv.seta(0);
        imageView = findViewById(R.id.ic_shuba);
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        tv_Step = findViewById(R.id.tv_mySteps);
        tv_C_point = findViewById(R.id.tv_myCpoint);
        tv_topTitle = findViewById(R.id.tv_topTitle);
        btnregionrank=findViewById(R.id.region_rank);
        btnregionrank.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(login_main_page.this,mainpage_regionrank.class);
                startActivity(intent);
            }
        }));

        //reset step vale from 45 to 0
        resetvalue();

        //new add
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        storageProfileRef = FirebaseStorage.getInstance().getReference().child("Profile Pic");
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
            mStepCounter=sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorPresent=true;
        }else{
            tv_Step.setText("CounterSensor is not present");
            isCounterSensorPresent=false;
        }

        //change user photo
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent changePhoto = new Intent(login_main_page.this,change_pro.class);
                //startActivity(changePhoto);

                //select successful from gallery
                mGetContent.launch("image/*");
                //connect to firebase
            }
        });

        //copy declare
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

        //get firstIn step in database
        databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() &&snapshot.getChildrenCount() > 0){
                    //database c point
                    db_step = Integer.valueOf(snapshot.child("steps").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Intent gotoRegister = new Intent(login_main_page.this,mainpage_regionrank.class);
                startActivity(gotoRegister);
            }
        });

        //go to rank


        //get the user now using the app
        getUserNameAccount();

        //set user information
        setUserInf();

        //check user has their own photo
        //checkUserImage();
        getUserinfo();
    }

    private void showButtonDialoge() {
        //create bottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(login_main_page.this);
        bottomSheetDialog.setContentView(R.layout.login_upload_check);
        bottomSheetDialog.setCanceledOnTouchOutside(true);

        //initialize and assign variables
        Button lo_upload  = bottomSheetDialog.findViewById(R.id.login_upload);
        Button lo_cancel  = bottomSheetDialog.findViewById(R.id.login_cancel);

        lo_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upLoadImage();
                //back to login page
                Intent intent = new Intent(login_main_page.this,login_main_page.class);
                startActivity(intent);
            }
        });

        lo_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myUri = null;
                //back to login page
                Intent intent = new Intent(login_main_page.this,login_main_page.class);
                startActivity(intent);
            }
        });

        //show button sheet dialog
        bottomSheetDialog.show();
    }

    //reset foot steps
    public void resetvalue(){
        int reset1=0;
        stepCount =0;
        tv_Step.setText(String.valueOf(reset1));
        //Log.i(String.valueOf(login_main_page.this),"重設");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor == mStepCounter){
            stepCount=(int) sensorEvent.values[0];
            tv_Step.setText(String.valueOf(stepCount));

            addPoints(stepCount);
        }
    }

    public void addPoints(int stepCount) {
        DatabaseReference dbr_User1 = databaseReference.child(mAuth.getCurrentUser().getUid());
        //update user c point
        //add to firebase
        dbr_User1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0 ){
                    //add c point and update preStep
                    if (preStep == 0){ //must bigger than step in database
                        preStep = stepCount;
                        //update c point
                        Map<String,Object> updateMap1 = new HashMap<String, Object>();
                        updateMap1.put("steps",stepCount);
                        dbr_User1.updateChildren(updateMap1);

                        Log.i(String.valueOf(login_main_page.this),"第一次 preStep"+ preStep + "步");
                        Log.i(String.valueOf(login_main_page.this),"第一次 step"+ stepCount + "步");
                        Log.i(String.valueOf(login_main_page.this),"第一次 dbStep"+ db_step + "步");

                    }else {
                        //only bigger than preStep add to database
                        if (stepCount > preStep){
                            //update c point (exclude last part had be added)
                            Integer number2 = Integer.valueOf(snapshot.child("carbonPoints").getValue().toString()) + stepCount - preStep;
                            Map<String,Object> updateMap2 = new HashMap<String, Object>();
                            //updateMap2.put("carbonPoints",number2);
                            updateMap2.put("steps",stepCount);
                            dbr_User1.updateChildren(updateMap2);

                            Log.i(String.valueOf(login_main_page.this),"第二次 preStep"+ preStep + "步");
                            //update preStep
                            preStep = stepCount;

                            Log.i(String.valueOf(login_main_page.this),"第二次 step"+ stepCount + "步");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
            sensorManager.registerListener(this,mStepCounter,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
            sensorManager.unregisterListener(this,mStepCounter);
        }
    }

    //get user account from shared Reference
    private void getUserNameAccount() {
        //get uid from mainActivity
        uid = getSharedPreferences("userList",MODE_PRIVATE).getString("User","");
    }

    //insert user inf
    private void setUserInf() {
        //get realtime data base information
        dbr_user = databaseReference.child(mAuth.getCurrentUser().getUid());

        dbr_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0 ){
                    //set shop name
                    userName = snapshot.child("userName").getValue().toString();
                    cPoint = snapshot.child("carbonPoints").getValue().toString();
                    steps = snapshot.child("steps").getValue().toString();

                    //show message
                    tv_topTitle.setText("Welcome back, "+userName+" !");
                    tv_Step.setText(steps +" 步");
                    tv_C_point.setText(cPoint + " C");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    //get user photo file
    private void getUserinfo() {
        databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0){
                    //user photo
                    if (snapshot.hasChild("image")){
                        String image = snapshot.child("image").getValue().toString();
                        //set user photo
                        Picasso.get().load(image).into(imageView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    //upload image
    private void upLoadImage() {
        //show progress
        ProgressDialog progressDialog = new ProgressDialog(this);
        //progress dialog
        progressDialog.setTitle("設定您的相片");
        progressDialog.setMessage("請等待，正在儲存您的變更");
        progressDialog.show();

        //choose not null image
        if (imageUri != null){
            final StorageReference fileRef = storageProfileRef.child(mAuth.getCurrentUser().getUid() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = (Uri) task.getResult();
                        myUri = downloadUri.toString();

                        HashMap<String,Object> userMap = new HashMap<>();
                        userMap.put("image",myUri);

                        databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);

                        progressDialog.dismiss();
                    }
                }
            });
        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "並未選擇相片", Toast.LENGTH_SHORT).show();
        }

    }


    //put image on activity
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            //result is the result uri
            if (result != null){
                imageView.setImageURI(result);
                //result save in a uri
                imageUri = result;

                showButtonDialoge();
            }
        }
    });

}