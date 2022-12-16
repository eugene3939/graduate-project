package com.example.greenyourlife;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class friendAdapter2 extends RecyclerView.Adapter<friendAdapter2.ViewHolder> {

    Context context;
    List<friendModel> friendModelList;
    String nowUser;

    FirebaseAuth mAuth;
    DatabaseReference dbr,dbr_friend;

    String getUserName; //user name past to gallery

    int rank = 0;

    //set provide a non duplicate collection
    Set<String> set = new HashSet<String>();

    public friendAdapter2(Context context, List<friendModel> friendModelList) {
        this.context = context;
        this.friendModelList = friendModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_for_regionrank_recycleview,parent,false);
        //design row connectivity
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //find current user
        mAuth = FirebaseAuth.getInstance();
        dbr = FirebaseDatabase.getInstance().getReference().child("User");
        dbr.child(mAuth.getCurrentUser().getUid()).child("userName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nowUser = snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //find current user friends
        dbr_friend = FirebaseDatabase.getInstance().getReference().child("User_Friends");
        dbr_friend.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //iterator enable to cycle throw a collection
                //data snapshot instance contain data from firebase location
                Iterator i = snapshot.getChildren().iterator();

                //add firebase data to set
                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                    //Log.i(String.valueOf(friendAdapter.this),"朋友表: " + ((DataSnapshot)i.next()).getKey());
                }

                for (String name: set){
                    Log.i(String.valueOf(friendAdapter2.this),"朋友表: " + name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //here we will bind it
        friendModel fm = friendModelList.get(position);

        if (fm.getUserName().equals(nowUser)){     // check now user
            holder.tvName.setText(fm.getUserName() + "\n (me)");
            holder.tvpoints.setText(fm.getSteps() + "步");
            holder.tvRank.setText(String.valueOf("第 "+ (position+1) +" 名"));
            holder.tvName.setTextColor(Color.parseColor("#677456"));
        }else{
            holder.tvName.setText(fm.getUserName());
            holder.tvpoints.setText(fm.getSteps() + "步");
            holder.tvRank.setText(String.valueOf("第 "+ (position+1) +" 名"));
        }

        holder.friends = fm;    //get click userNmae

        String imageUri = null;
        imageUri = fm.getImage();
        Picasso.get().load(imageUri).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return friendModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //here declare design
        CircleImageView imageView;
        TextView tvName;
        TextView tvpoints;
        TextView tvRank;

        friendModel friends;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.recycle_image);
            tvName = itemView.findViewById(R.id.recycle_name);
            tvpoints=itemView.findViewById(R.id.recycle_steps);
            tvRank = itemView.findViewById(R.id.recycle_rank);

        }
    }
}