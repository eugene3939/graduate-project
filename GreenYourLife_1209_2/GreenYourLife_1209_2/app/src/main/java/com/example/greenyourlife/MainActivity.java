package com.example.greenyourlife;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //initialize variable
    Button bt_mlogin,bt_glogin;
    //firebase check
    FirebaseAuth mAuth;
    //user email
    String email;
    //activity
    Activity content = this;

    //close drawer
    public static void closeDrawer(DrawerLayout drawerLayout) {
        //check condition
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            //when drawer is open
            //clear drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign variable
        bt_mlogin = findViewById(R.id.bt_mlogin);
        bt_glogin = findViewById(R.id.bt_glogin);

        //get new account and password
        Intent getNewPasEvent = getIntent();
        String newAcc = getNewPasEvent.getStringExtra("InputAcc");

        //member login
        bt_mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create bottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
                bottomSheetDialog.setCanceledOnTouchOutside(false);

                //initialize and assign variables
                EditText etUsername = bottomSheetDialog.findViewById(R.id.et_username);
                EditText etPassword = bottomSheetDialog.findViewById(R.id.et_password);
                Button btSubmit  = bottomSheetDialog.findViewById(R.id.bt_Reg);
                Button btGoRegister = bottomSheetDialog.findViewById(R.id.bt_Goregister);

                if (newAcc != null){
                    etUsername.setText(newAcc);
                }

                mAuth= FirebaseAuth.getInstance();

                //login part
                btSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //user Authentication check
                        mAuth.signInWithEmailAndPassword(etUsername.getText().toString(),etPassword.getText().toString()).addOnCompleteListener(content, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (task.isSuccessful()){
                                    email = user.getEmail();

                                    //create alter dialog
                                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                    builder.setTitle(email+"登入成功");
                                    builder.setMessage("歡迎使用 Green Your Life");
                                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                                    AlertDialog alertDialog = builder.create();
                                    //show alter dialog
                                    alertDialog.show();

                                    saveData(email);

                                    //on success
                                    Intent userlogin = new Intent(MainActivity.this,login_main_page.class);
                                    startActivity(userlogin);

                                }else {
                                    //on failed
                                    Toast.makeText(view.getContext(),"登入失敗!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                //show button sheet dialog
                bottomSheetDialog.show();

                //go to register page
                btGoRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent gotoRegister = new Intent(MainActivity.this,register.class);
                        startActivity(gotoRegister);
                    }
                });
            }
        });

        //guest login
        bt_glogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //in the develop part go login page
                Intent userlogin = new Intent(MainActivity.this,login_main_page.class);
                //design path can go to main quickly
                saveData("a@gmail.com");    //illegal login auth is false
                startActivity(userlogin);
            }
        });

        //svae map back ground size
        SharedPreferences shareRef = getSharedPreferences("background", MODE_PRIVATE);
        shareRef.edit().putInt("size",0).commit();
    }

    private void saveData(String s) {
        //provide xml file User List for this app to load
        SharedPreferences sharedPreferences = getSharedPreferences("userList",MODE_PRIVATE);
        //add data to shared preference by editor
        sharedPreferences.edit().putString("User",s).commit();   //file tag User
    }
}