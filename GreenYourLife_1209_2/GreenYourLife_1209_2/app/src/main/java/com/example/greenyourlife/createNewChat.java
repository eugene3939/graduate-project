package com.example.greenyourlife;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class createNewChat extends AppCompatActivity {

    Button back_chat,send,clear;
    ImageView picture;
    EditText title,content;

    //connect chat
    DatabaseReference dbr_chat;

    Uri imageUri;
    String myUri = "";
    String pictureId;

    StorageTask uploadTask;
    StorageReference storageProfileRef;

    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_chat);

        back_chat = findViewById(R.id.back_chat);
        picture = findViewById(R.id.to_chat_gra);

        send = findViewById(R.id.send_new_chat);
        clear = findViewById(R.id.clear_new_chat);

        title = findViewById(R.id.new_event_title);
        content = findViewById(R.id.new_event_content);

        dbr_chat = FirebaseDatabase.getInstance().getReference().child("chat");
        storageProfileRef = FirebaseStorage.getInstance().getReference().child("Chat Pic");

        //back to chat room
        back_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(createNewChat.this,chatroom.class);
                startActivity(intent);
            }
        });

        //set image
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //select successful from gallery
                mGetContent.launch("image/*");
            }
        });

        //clear all content
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText(null);
                content.setText(null);
            }
        });

        //send new chat
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create connection with chat
                Map<String,Object> map = new HashMap<String, Object>();
                //set name as key
                dbr_chat.updateChildren(map);

                DatabaseReference dbr2 = dbr_chat.child(title.getText().toString()).child("content");
                Map<String,Object> updateMap = new HashMap<String, Object>();
                updateMap.put("title",title.getText().toString());
                updateMap.put("content",content.getText().toString());
                upLoadImage(title.getText().toString());

                dbr2.updateChildren(updateMap);

                //complete massage
                Toast.makeText(createNewChat.this, "更新完成", Toast.LENGTH_SHORT).show();
                //back to event page
                Intent intent = new Intent(createNewChat.this,chatroom.class);
                startActivity(intent);
            }
        });
    }

    //upload image
    private void upLoadImage(String s) {
        //show progress
        ProgressDialog progressDialog = new ProgressDialog(this);
        //progress dialog
        progressDialog.setTitle("設定您的相片");
        progressDialog.setMessage("請等待，正在儲存您的變更");
        progressDialog.show();

        //choose not null image
        if (imageUri != null){
            pictureId = UUID.randomUUID().toString();
            final StorageReference fileRef = storageProfileRef.child(pictureId);

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
                        userMap.put("chatImage",myUri);

                        dbr_chat.child(s).child("content").updateChildren(userMap);
                        progressDialog.dismiss();

                        //back to login page
                        Intent intent = new Intent(createNewChat.this,chatroom.class);
                        startActivity(intent);
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
                picture.setImageURI(result);
                //result save in a uri
                imageUri = result;
            }
        }
    });

}