package com.example.greenyourlife;

import android.annotation.SuppressLint;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class userCar {
    private int image;
    private String name;

    public userCar(int image, String name){
        this.image = image;
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
